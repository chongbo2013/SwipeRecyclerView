package com.ferris.holeswipeview;

import java.util.List;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class ApplicationAdapter extends
		RecyclerView.Adapter<ApplicationAdapter.MyViewHolder> {

	private Context mContext;

	private List<ApplicationBeam> mPackagesInfo;

	public ApplicationAdapter(Context context, List<ApplicationBeam> listInfo) {
		mContext = context;
		mPackagesInfo = listInfo;

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public static class MyViewHolder extends RecyclerView.ViewHolder {
		public MyViewHolder(View view) {
			super(view);
		}

		TextView app_name;
	}

	@Override
	public int getItemCount() {
		// TODO Auto-generated method stub
		return mPackagesInfo == null ? 0 : mPackagesInfo.size();
	}

	@Override
	public MyViewHolder onCreateViewHolder(ViewGroup parent, int positon) {
		// TODO Auto-generated method stub
		View convertView = LayoutInflater.from(mContext).inflate(
				R.layout.app_item, parent, false);
		MyViewHolder viewHolder = new MyViewHolder(convertView);
		viewHolder.app_name = (TextView) convertView
				.findViewById(R.id.app_name);
		return viewHolder;
	}

	public void onBindViewHolder(MyViewHolder viewHolder, int position) {
		// TODO Auto-generated method stub
		viewHolder.app_name.setText("app" + position);
	}

}
