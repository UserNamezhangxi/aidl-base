package com.zx.aidldemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.annotation.Nullable;

/**
 * Created by Administrator on 2018/9/11.
 */

public class IRemoteService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    private IBinder mBinder = new IMyAidlInterface.Stub(){

        @Override
        public int add(int num1, int num2) throws RemoteException {
            return num1+num2;
        }
    };
}
