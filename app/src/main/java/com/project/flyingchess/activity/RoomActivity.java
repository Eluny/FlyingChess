package com.project.flyingchess.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.orhanobut.logger.Logger;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDataCallback;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;
import com.project.flyingchess.R;
import com.project.flyingchess.utils.SalutHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

//这个类暂时没有什么卵用。//房间这个东西...因为salut的一些特性只能写在
public class RoomActivity extends BaseActivity implements View.OnClickListener,SalutDataCallback {
    private static final String TAG = "ConfigueActivity";

    private ListView lv_device;
    private List<SalutDevice> mList;
    private DeviceAdpater deviceAdpater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_room);

        EventBus.getDefault().register(this);

        findview();
        init();
    }

    private void init() {
        mList = new ArrayList<>();

        mList.add(new SalutDevice("测试一号~"));
        mList.add(new SalutDevice("测试二号~"));
        mList.add(new SalutDevice("测试三号~"));

        deviceAdpater = new DeviceAdpater(this);
        lv_device.setAdapter(deviceAdpater);

        SalutDataReceiver mDataReceiver = new SalutDataReceiver(this, this);
        SalutServiceData mServiceData = new SalutServiceData("server", 23334, "测试一号");
        SalutHolder.getInstance().init(mDataReceiver, mServiceData, new SalutCallback() {
            @Override
            public void call() {
                System.out.println("你麻痹你炸了~");
            }
        });
    }

    private void findPeers(){
        SalutHolder.getInstance().discoverWithTimeout(
                new SalutCallback() {
                    @Override
                    public void call() {
                        Logger.d(SalutHolder.getInstance().getmSalut().foundDevices.toString());
                        EventBus.getDefault().post(SalutHolder.getInstance().getmSalut().foundDevices);
                    }
                }, new SalutCallback() {
                    @Override
                    public void call() {
                        System.out.println("你麻痹~失败了");
                    }
                });
    }

    private void findview() {
        lv_device = (ListView) findViewById(R.id.lv_device);
        ImageView iv_create = (ImageView) findViewById(R.id.iv_create);

        iv_create.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_create:
                startActivity(new Intent(RoomActivity.this,GameActivity.class)
                        .putExtra(GameActivity.KEY_MODE,GameActivity.NET_SERVER));
                break;
            default:
                break;
        }
    }

    @Subscribe
    public void onEventMainThread(ArrayList<SalutDevice> devices) {
        mList.addAll(devices);
        deviceAdpater.notifyDataSetChanged();
    }

    @Override
    public void onDataReceived(Object data) {

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
                    Toast.makeText(RoomActivity.this, salutDevice.deviceName + "~", Toast.LENGTH_SHORT).show();
                    SalutHolder.getInstance().setSalutDevice(salutDevice);
                }
            });

            return convertView;
        }

        class ViewHolder {
            TextView tv_device;
        }
    }

    public String getTag() {
        return TAG;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
