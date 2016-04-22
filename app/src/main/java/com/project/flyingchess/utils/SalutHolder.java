package com.project.flyingchess.utils;

import com.orhanobut.logger.Logger;
import com.peak.salut.Callbacks.SalutCallback;
import com.peak.salut.Callbacks.SalutDeviceCallback;
import com.peak.salut.Salut;
import com.peak.salut.SalutDataReceiver;
import com.peak.salut.SalutDevice;
import com.peak.salut.SalutServiceData;

/**
 * Created by Administrator on 2016/4/20.
 */
public class SalutHolder {
    private static SalutHolder ourInstance = new SalutHolder();

    public static SalutHolder getInstance() {
        return ourInstance;
    }

    private SalutHolder() {
    }

    private Salut mSalut;
    private SalutDevice salutDevice;
    private boolean isRunningAsHost = false;

    public void init(SalutDataReceiver mDataReceiver, SalutServiceData mServiceData, SalutCallback onFail){
        mSalut = new Salut(mDataReceiver, mServiceData, new SalutCallback() {
            @Override
            public void call() {
                Logger.d("The mobile cannot support the Wifi~connect~ T>T");
            }
        });
    }

    public void uninit(){
        if (mSalut.isRunningAsHost) {
            mSalut.stopNetworkService(false);
        } else {
            mSalut.unregisterClient(false);
        }
    }

    public void startNetWorkService(SalutDeviceCallback salutDeviceCallback, SalutCallback onSuccess, SalutCallback onFail) {
        mSalut.startNetworkService(salutDeviceCallback, onSuccess, onFail);
    }

    public void connectToHost(SalutDevice salutHost, SalutCallback onSuccess, SalutCallback onFail) {
        mSalut.registerWithHost(salutHost, onSuccess, onFail);
    }

    public void discoverWithTimeout(SalutCallback onSuccess, SalutCallback onFail){
        mSalut.discoverWithTimeout(onSuccess, onFail, 6000);
    }

    public SalutDevice getSalutDevice() {
        return salutDevice;
    }

    public void setSalutDevice(SalutDevice salutDevice) {
        this.salutDevice = salutDevice;
    }

    public Salut getmSalut(){
        return mSalut;
    }
}
