package com.project.flyingchess.dialog;


import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.orhanobut.logger.Logger;
import com.peak.salut.SalutDevice;
import com.project.flyingchess.R;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

public class PeersDialog extends Dialog {
	private Context mContext;
	private ListView lv_peers;
	private List<SalutDevice> mList;
	private DeviceAdpater deviceAdpater;
	private ProgressBar pb_loading;
	private Button btn_cancel;


	public PeersDialog(Context context, int theme) {
		super(context,theme);

		mContext = context;

		setContentView(R.layout.dialog_peers);

		findview();
		init();
	}

	public void findPeers(){
		mList.clear();
		deviceAdpater.notifyDataSetChanged();
		pb_loading.setVisibility(View.VISIBLE);
	}

	public void updateView(List<SalutDevice> deviceList){
		mList.clear();
		mList.addAll(deviceList);
		pb_loading.setVisibility(View.GONE);

		deviceAdpater.notifyDataSetChanged();
	}

	public void setCancelListener(View.OnClickListener onClickListener){
		btn_cancel.setOnClickListener(onClickListener);
	}

	private void init() {
		mList = new ArrayList<>();

		mList.add(new SalutDevice("测试一号~"));
		mList.add(new SalutDevice("测试二号~"));
		mList.add(new SalutDevice("测试三号~"));

		deviceAdpater = new DeviceAdpater(mContext);
		lv_peers.setAdapter(deviceAdpater);
	}

	private void findview() {
		lv_peers = (ListView) findViewById(R.id.lv_peers);
		pb_loading = (ProgressBar) findViewById(R.id.pb_loading);
		btn_cancel = (Button) findViewById(R.id.btn_cancel);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	class DeviceAdpater extends BaseAdapter {
		private LayoutInflater mInflater;
		public DeviceAdpater(Context ctx) {
			mInflater = LayoutInflater.from(ctx);
		}

		@Override
		public int getCount() {
			return mList == null ? 0 : mList.size();
		}

		@Override
		public Object getItem(int position) {
			return mList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			final ViewHolder holder;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(R.layout.item_device,
						parent, false);
				holder.tv_device = (TextView) convertView.findViewById(R.id.tv_device);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			final SalutDevice salutDevice = mList.get(position);
			holder.tv_device.setText(salutDevice.deviceName);
			holder.tv_device.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Logger.d("加入该" + salutDevice.deviceName +"游戏房间~");
					EventBus.getDefault().post(salutDevice);
				}
			});

			return convertView;
		}

		class ViewHolder {
			TextView tv_device;
		}
	}
}
