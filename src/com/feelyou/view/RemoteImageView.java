package com.feelyou.view;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.WeakHashMap;
import java.util.concurrent.RejectedExecutionException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

public class RemoteImageView extends ImageView {

	private static final String TAG = "RmtImageView";
	private static final int MAX_FAILURES = 3;
	private static final String DEMOS_DIR = "feelyou";
	private static final int MB = 1048576;

	private static ImageCache mImageCache;
	private static int mCacheSize = 150;

	private Context mContext;
	private String mUrl;
	private int mFailure;
	private String mCurrentlyGrabbedUrl;

	public RemoteImageView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	public RemoteImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public RemoteImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		mContext = context;
		init();
	}

	private void init() {
		mImageCache = new ImageCache();
	}

	public void setDefaultImage(int resId) {
		setImageResource(resId);
	}

	public void setImageUrl(String url) {
		if (mUrl != null
				&& mUrl.equals(url)
				&& (mCurrentlyGrabbedUrl == null || (mCurrentlyGrabbedUrl != null && !mCurrentlyGrabbedUrl
						.equals(url)))) {
			mFailure++;
			if (mFailure > MAX_FAILURES) {
				Log.e(TAG, "Failed to download " + url);
				return;
			}
		} else {
			mUrl = url;
			mFailure = 0;
		}

		if (mImageCache.isCached(url)) {
			setImageBitmap(mImageCache.get(url));
		} else {
			String fileName = convertUrlToFileName(url);
			String dir = getDirectory(fileName);
			String pathFileName = dir + "/" + fileName;

			File pathFile = new File(pathFileName);
			if (!pathFile.exists()) {
				try {
					pathFile.createNewFile();
				} catch (IOException e) {
				}
			}

			Bitmap tbmp = BitmapFactory.decodeFile(pathFileName);
			if (tbmp == null) {
				try {
					new DownloadTask().execute(url);
				} catch (RejectedExecutionException e) {
				}
			} else {
				setImageBitmap(tbmp);
			}

			updateCacheSize(pathFileName);
		}
	}

	private String convertUrlToFileName(String url) {
		String filename = url;
		int index = filename.lastIndexOf("/") + 1;
		filename = filename.substring(index);
		filename = filename.replace("jpg", "dat");
		return filename;
	}

	private String getDirectory(String fileName) {
		String extStorageDirectory = Environment.getExternalStorageDirectory()
				.toString();

		String dirPath = extStorageDirectory + "/" + DEMOS_DIR;
		File dirFile = new File(dirPath);
		dirFile.mkdirs();

		dirPath = dirPath + "/Cache";
		dirFile = new File(dirPath);
		dirFile.mkdir();

		return dirPath;
	}

	private void updateCacheSize(String pathFileName) {
		mCacheSize = Integer.parseInt(PreferenceManager
				.getDefaultSharedPreferences(mContext).getString(
						"cache_option", "150"));

		if (isSDCardEnable()) {
			String extStorageDirectory = Environment
					.getExternalStorageDirectory().toString();
			String dirPath = extStorageDirectory + "/" + DEMOS_DIR + "/Cache";
			File dirFile = new File(dirPath);
			File[] files = dirFile.listFiles();
			long dirSize = 0;
			for (File file : files) {
				dirSize += file.length();
			}
			if (dirSize > mCacheSize * MB) {
				clearCache();
			}
		}
	}

	private boolean isSDCardEnable() {
		return Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED);
	}

	private void clearCache() {
		if (isSDCardEnable()) {
			String extStorageDirectory = Environment
					.getExternalStorageDirectory().toString();

			String dirPath = extStorageDirectory + "/" + DEMOS_DIR + "/Cache";
			File dir = new File(dirPath);
			File[] files = dir.listFiles();

			if (files == null) {
				return;
			}

			for (int i = 0; i < files.length; i++) {
				files[i].delete();
			}
		}
	}

	class ImageCache extends WeakHashMap<String, Bitmap> {

		public boolean isCached(String url) {
			return containsKey(url) && get(url) != null;
		}

	}

	class DownloadTask extends AsyncTask<String, Void, String> {

		private String mTaskUrl;
		private Bitmap mBmp = null;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(String... params) {
			mTaskUrl = params[0];
			InputStream stream = null;
			URL imageUrl;
			Bitmap bmp = null;

			try {
				imageUrl = new URL(mTaskUrl);
				try {
					stream = imageUrl.openStream();
					bmp = BitmapFactory.decodeStream(stream);
					try {
						if (bmp != null) {
							mBmp = bmp;
							mImageCache.put(mTaskUrl, bmp);
						} else {
						}
					} catch (NullPointerException e) {
					}
				} catch (IOException e) {
				} finally {
					try {
						if (stream != null) {
							stream.close();
						}
					} catch (IOException e) {
					}
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
			return mTaskUrl;
		}

		@Override
		protected void onPostExecute(String url) {
			super.onPostExecute(url);
			Bitmap bmp = mImageCache.get(url);
			if (bmp == null) {
				setImageUrl(url);
			} else {
				RemoteImageView.this.setImageBitmap(bmp);
				mCurrentlyGrabbedUrl = url;
				saveBmpToSd(mBmp, url);
			}
		}

		private void saveBmpToSd(Bitmap bm, String url) {
			if (bm == null) {
				return;
			}

			if (mCacheSize == 0) {
				return;
			}

			String filename = convertUrlToFileName(url);
			String dir = getDirectory(filename);
			File file = new File(dir + "/" + filename);
			try {
				file.createNewFile();
				OutputStream outStream = new FileOutputStream(file);
				bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
				outStream.flush();
				outStream.close();
			} catch (FileNotFoundException e) {
				Log.w(TAG, "FileNotFoundException");
			} catch (IOException e) {
				Log.w(TAG, "IOException");
			}
		}

	}

}
