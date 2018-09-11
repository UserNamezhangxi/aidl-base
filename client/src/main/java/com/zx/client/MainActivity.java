package com.zx.client;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.zx.aidldemo.IMyAidlInterface;

public class MainActivity extends AppCompatActivity {
    private EditText ed1,ed2,ed3;
    private Button btn;
    private IMyAidlInterface aidl;
    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // 得到相应的我们定义的AIDL接口的代理对象, 然后可以执行相应的方法
            aidl = IMyAidlInterface.Stub.asInterface(service);
            Log.d("TAG","onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("TAG","onServiceDisconnected");
            aidl = null;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        autoBindService();
        initView();

    }

    private void autoBindService() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName("com.zx.aidldemo","com.zx.aidldemo.IRemoteService"));
        bindService(intent,conn, Context.BIND_AUTO_CREATE);
    }

    private void initView() {
        ed1 = (EditText) findViewById(R.id.ed1);
        ed2 = (EditText) findViewById(R.id.ed2);
        ed3 = (EditText) findViewById(R.id.ed3);
        btn = (Button) findViewById(R.id.btn);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n1 = Integer.parseInt(ed1.getText().toString());
                int n2 = Integer.parseInt(ed2.getText().toString());
                int n3=0;
                try {
                    n3 = aidl.add(n1,n2);
                    ed3.setText(n3+"");
                } catch (RemoteException e) {
                    e.printStackTrace();
                    ed3.setText("ERR");
                }
            }
        });
    }
}
