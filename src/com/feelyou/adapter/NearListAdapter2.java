package com.feelyou.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.feelyou.R;
import com.feelyou.view.RemoteImageView;
import com.feelyou.vo.UserInfo;

public class NearListAdapter2 extends BaseAdapter {
	
	public static final String IMGURL_PREFIX = "http://www.feelyou.me/photo/";
	private LayoutInflater layoutInflater;
	private OnClickListener onClickListener;
	public List<UserInfo> mData;
	
	public NearListAdapter2(Context context, List<UserInfo> mData, OnClickListener listener) {
		layoutInflater = LayoutInflater.from(context);
		this.onClickListener = listener;
		this.mData = mData;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData == null ? 0 : mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if (mData != null) {
			return mData.get(position);
		} 
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = layoutInflater.inflate(R.layout.list_item2, null);
			holder = new ViewHolder();
			holder.firstCharHintTextView = (TextView) convertView.findViewById(R.id.text_first_char_hint);
			holder.list_item_img_view = (RemoteImageView) convertView.findViewById(R.id.list_item_img_view);
			holder.list_item_text_name = (TextView) convertView.findViewById(R.id.list_item_text_name);
			holder.list_item_text_distance = (TextView) convertView.findViewById(R.id.list_item_text_distance);
			holder.list_item_text_signature = (TextView) convertView.findViewById(R.id.list_item_text_signature);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		UserInfo userInfo = mData.get(position);
		holder.list_item_text_name.setText(userInfo.getUname());
		
		float distance = 1.0f;
		try {
			distance = Float.parseFloat(userInfo.getUdistance());
		} catch (Exception e) {
			distance = 1.0f;
		}
		if (distance < 1) {
			holder.list_item_text_distance.setText(distance + "米以内");
		} else {
			holder.list_item_text_distance.setText(Math.round(distance) + "千米");
		}
//		holder.list_item_text_distance.setText(userInfo.getUdistance() + "米");
		holder.list_item_text_distance.setTextColor(0xFFFFFF00);
		holder.list_item_text_signature.setText(userInfo.getUsignature());
		
		int sexFlag = 1;
		try {
			sexFlag = Integer.parseInt(userInfo.getUsex());
		} catch (Exception e) {
			sexFlag = 1;
		}
		if (sexFlag == 1) {
			holder.list_item_img_view.setDefaultImage(R.drawable.male);
//			holder.list_item_img_view.setImageResource(R.drawable.male);
		} else {
			holder.list_item_img_view.setDefaultImage(R.drawable.female);
//			holder.list_item_img_view.setImageResource(R.drawable.female);
		}
//		if (Integer.parseInt(userInfo.getUsex()) == 1) {
//			holder.list_item_img_view.setImageResource(R.drawable.male);
//		} else {
//			holder.list_item_img_view.setImageResource(R.drawable.female);
//		}
		// 加载自定义的头像
		String photo = userInfo.getPhoto();
		if ((photo != null) && (photo.length() > 4)) {
			holder.list_item_img_view.setImageUrl(IMGURL_PREFIX + photo);
		}
		
		holder.list_item_img_view.setOnClickListener(onClickListener);
		holder.list_item_img_view.setTag(position);
		int idx = position - 1;
		char previewChar ;
		if ( idx < 0 || mData.get(idx).getUname() == null || "".equals(mData.get(idx).getUname()) )
			previewChar = ' ';
		else 
			previewChar = idx >= 0 ? mData.get(idx).getUname().charAt(0) : ' ';
//		char previewChar = idx >= 0 ? mData.get(idx).getUname().charAt(0) : ' ';
		char currentChar ;
		if (userInfo.getUname() == null || "".equals(userInfo.getUname()))
			currentChar = ' ';
		else
			currentChar = userInfo.getUname().charAt(0);
//		char currentChar = userInfo.getUname().charAt(0);
		if (currentChar != previewChar) {
			holder.firstCharHintTextView.setVisibility(View.VISIBLE);
			holder.firstCharHintTextView.setText(String.valueOf(currentChar));
		} else {
			// 此段代码不可缺：实例化一个CurrentView后，会被多次赋值并且只有最后一次赋值的position是正确
			holder.firstCharHintTextView.setVisibility(View.GONE);
		}
		return convertView;
	}
	
	public final class ViewHolder {
		public TextView firstCharHintTextView;
		public RemoteImageView list_item_img_view;
		public TextView list_item_text_name; //  nameTextView;
		public TextView list_item_text_distance; //  urlTextView;
		public TextView list_item_text_signature; //  ImageView imgView;
	}

}
