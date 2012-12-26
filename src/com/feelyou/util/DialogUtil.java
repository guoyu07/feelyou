package com.feelyou.util;

import java.util.HashMap;
import java.util.Iterator;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.DialogInterface.OnMultiChoiceClickListener;

import com.feelyou.R;
import com.feelyou.manager.ContactsManager;

public class DialogUtil implements DialogInterface.OnMultiChoiceClickListener {
	
	private static DialogCallBack okDlgCallback, cancelDlgCallback;
	
	public static Dialog createContactsDialog(Context context, boolean multiChoice, ContactDialogCallBack contactDialogCallBack) {
		HashMap hashMap = ContactsManager.getContacts(context);
		CharSequence[] charSequence = new CharSequence[hashMap.size()];
		final boolean[] arrayOfBoolean = new boolean[charSequence.length];
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle(R.string.m_contacts); // µÁª∞±°
		Iterator<String> iterator = hashMap.keySet().iterator();
		int i = 0;
		while (iterator.hasNext()) {
			String str = iterator.next();
			charSequence[i] = str;
			arrayOfBoolean[i] = false;
			i++;
		}
		if (multiChoice) {
			builder.setMultiChoiceItems(charSequence, null, new OnMultiChoiceClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which, boolean isChecked) {
					arrayOfBoolean[which] = isChecked;
				}
			});
		} else {
			builder.setSingleChoiceItems(charSequence, -1, new OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					arrayOfBoolean[which] = true;
				}
			});
		}
		return builder.create();
	}
	
	public static void showAlertDialog(Context context, int titleId, int contentId, DialogCallBack callBack1, DialogCallBack callBack2) {
		String title = SystemUtil.getString(context, titleId);
		String content = SystemUtil.getString(context, contentId);
		showAlertDialog(context, title, content, callBack1, callBack2);
	}
	
	public static void showAlertDialog(Context context, String title, String content, DialogCallBack callBack1, DialogCallBack callBack2) {
		okDlgCallback = callBack1;
		cancelDlgCallback = callBack2;
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setIcon(R.drawable.application);
		builder.setMessage(content).setTitle(title);
		if (callBack1 != null) {
			builder.setPositiveButton(R.string.confrim, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int arg1) {
					okDlgCallback.callBack(dialogInterface);
				}
			});
		}
		if (callBack2 != null) {
			builder.setNegativeButton(R.string.cancel, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					cancelDlgCallback.callBack(dialog);
				}
			});
		}
		builder.create().show();
	}
	
	public static void showAlertDialog(Context context, String title, String content) {
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(content).setTitle(title);
		builder.setPositiveButton(R.string.confrim, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialogInterface, int arg1) {
						
				}
			}
		);
		builder.create().show();
	}	
	
	public static void showOperateFailureDialog(Context context, int contentId) {
		DialogCallBack callback = new DialogCallBack (){
			@Override
			public void callBack(DialogInterface dialogInterface) {
				dialogInterface.cancel();
				dialogInterface.dismiss();
			}
		};
		showAlertDialog(context,  R.string.op_error_title, contentId, callback, null);	
	}
	
	public static void showOperateFailureDialog(Context context, String content) {
		String title = SystemUtil.getString(context, R.string.op_error_title);
		DialogCallBack callback = new DialogCallBack (){
			@Override
			public void callBack(DialogInterface dialogInterface) {
				dialogInterface.cancel();
				dialogInterface.dismiss();
			}
		};
		showAlertDialog(context, title, content, callback, null);
	}
	
	public static void showOperateSuccessDialog(Context context, int contentId, DialogCallBack callback) {
		showAlertDialog(context, R.string.op_success_title, contentId, callback, null);
	}
	
	public static void showOperateSuccessDialog(Context context, String content, DialogCallBack callback) {
		String title = SystemUtil.getString(context, R.string.op_success_title);
		showAlertDialog(context, title, content, callback, null);
	}
	
	public static Dialog showProgressDialog(Context context, String msg) {
		ProgressDialog progressDialog = new ProgressDialog(context);
		progressDialog.setTitle("∑…”—");
		progressDialog.setIcon(R.drawable.application);
		progressDialog.setMessage(msg);
		progressDialog.show();
		return progressDialog;
	}
	
	public static void showSuccessAndClose(Context context, String content) {
		String title = SystemUtil.getString(context, R.string.op_success_title);

		showAlertDialog(context, title, content, new DialogCallBack () {
			@Override
			public void callBack(DialogInterface dialogInterface) {
				
			}}, null);
	}
	public static void showSuccessAndClose(Context context, int contentId) {
		
		showAlertDialog(context, R.string.op_success_title, contentId, new DialogCallBack() {
			@Override
			public void callBack(DialogInterface dialogInterface) {
			}}, null);
	}
	
	public interface ContactDialogCallBack {
		public void callBack(String strPhone);
	}
	
	public interface DialogCallBack {
		public void callBack(DialogInterface dialogInterface);
	}

	@Override
	public void onClick(DialogInterface dialog, int which, boolean isChecked) {
		
	}
}
