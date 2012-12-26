package com.feelyou.ui;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.feelyou.R;
import com.feelyou.vo.UserInfo;

public class NearListAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
//	public List<Map<String, Object>> mData;
	public List<UserInfo> mData;
	
	public NearListAdapter(Context context, List<UserInfo> mData) {
		mInflater = LayoutInflater.from(context);
		this.mData = mData;
	}
	
//	public NearListAdapter(Context context, List<Map<String, Object>> mData) {
//		mInflater = LayoutInflater.from(context);
//		this.mData = mData;
//	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
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
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.near_list_item, null);
			holder.ivIcon = (ImageView) convertView.findViewById(R.id.ivIcon);
			holder.tvName = (TextView) convertView.findViewById(R.id.tvName);
			holder.tvDistance = (TextView) convertView.findViewById(R.id.tvDistance);
			holder.tvSignature = (TextView) convertView.findViewById(R.id.tvSignature);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

/*		holder.ivIcon.setImageResource(Integer.parseInt(mData.get(position).get("icon").toString()));
		holder.tvName.setText(mData.get(position).get("name").toString());
		holder.tvDistance.setText(mData.get(position).get("distance").toString());
		holder.tvSignature.setText(mData.get(position).get("signature").toString());*/
		
//		holder.ivIcon.setImageResource(resId)
		UserInfo userInfo = mData.get(position);
		holder.tvName.setText(userInfo.getUname());
		holder.tvDistance.setText(userInfo.getUdistance() + "รื");
		holder.tvSignature.setText(userInfo.getUsignature());
		if (Integer.parseInt(userInfo.getUsex()) == 1) {
			holder.ivIcon.setImageResource(R.drawable.male);
		} else {
			holder.ivIcon.setImageResource(R.drawable.female);
		}
		
		return convertView;
	}
	
	public final class ViewHolder {
		public ImageView ivIcon;
		public TextView tvName;
		public TextView tvDistance;
		public TextView tvSignature;
	}

}
