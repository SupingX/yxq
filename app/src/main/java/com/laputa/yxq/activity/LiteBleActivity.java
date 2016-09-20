package com.laputa.yxq.activity;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.laputa.yxq.R;
import com.litesuits.bluetooth.LiteBleGattCallback;
import com.litesuits.bluetooth.LiteBluetooth;
import com.litesuits.bluetooth.exception.BleException;
import com.litesuits.bluetooth.exception.ConnectException;
import com.litesuits.bluetooth.exception.GattException;
import com.litesuits.bluetooth.exception.InitiatedException;
import com.litesuits.bluetooth.exception.OtherException;
import com.litesuits.bluetooth.exception.TimeoutException;
import com.litesuits.bluetooth.exception.hanlder.BleExceptionHandler;
import com.litesuits.bluetooth.scan.PeriodScanCallback;

import java.util.ArrayList;
import java.util.List;

import adapter.DeviceAdapter;
import util.DataUtil;
import util.SharedPreferenceUtil;

public class LiteBleActivity extends AppCompatActivity {

    private LiteBluetooth lite;
    private TextView tvBondedDevice;
    private final String ADDRESS = "SHARE_ADDRESS";
    private final long TIME_OUT = 1000L*30;
    private final int DEVICE_FOUND  = 0xf1;
    private ListView lvDevice;
    private List<BluetoothDevice> list;
    private DeviceAdapter adapter;
    private Handler mHandler = new Handler(Looper.getMainLooper()){
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case DEVICE_FOUND:
                    adapter.notifyDataSetChanged();
                    break;

                default:
                    break;
            }
        }
    };

    private BleExceptionHandler bleExceptionHandler;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lite_ble);

        initViews();
        initLite();
    }

    @Override
    protected void onResume() {
        super.onResume();

        int state = lite.getConnectionState();
        if(state == LiteBluetooth.STATE_SERVICES_DISCOVERED){
            tvStatus.setText("已连接");
        }else{
            //
            tvStatus.setText("---");
        }

    }

    private void initLite() {
        lite = new LiteBluetooth(this);
        bleExceptionHandler = new BleExceptionHandler() {

            @Override
            protected void onTimeoutException(TimeoutException arg0) {
                Log.e("LiteBleActivity","--> onTimeoutException()"+arg0.toString());

            }

            @Override
            protected void onOtherException(OtherException arg0) {
                // TODO Auto-generated method stub
                Log.e("LiteBleActivity","--> onOtherException()"+arg0.toString());
            }

            @Override
            protected void onInitiatedException(InitiatedException arg0) {
                // TODO Auto-generated method stub
                Log.e("LiteBleActivity","--> onInitiatedException()"+arg0.toString());

            }

            @Override
            protected void onGattException(GattException arg0) {
                // TODO Auto-generated method stub
                Log.e("LiteBleActivity","--> onGattException()"+arg0.toString());

            }

            @Override
            protected void onConnectException(ConnectException arg0) {
                // TODO Auto-generated method stub
                Log.e("LiteBleActivity","--> onConnectException()"+arg0.toString());
            }
        };
    }

    private void initViews() {
        tvBondedDevice = (TextView) findViewById(R.id.tv_bonded_device);
        tvStatus = (TextView) findViewById(R.id.tv_bonded_device_status);

        String address = (String) SharedPreferenceUtil.get(this, ADDRESS, "没有绑定任何蓝牙");
        tvBondedDevice.setText(address);

        //
        lvDevice = (ListView) findViewById(R.id.lv_device);
        list = new ArrayList<BluetoothDevice>();
        adapter = new DeviceAdapter(list);
        lvDevice.setAdapter(adapter);
        lvDevice.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                BluetoothDevice device = list.get(position);
                lite.refreshDeviceCache();
                Log.i("","开始连接！！！");
                Log.e("","开始连接！！！");
                lite.connect(device, true, new LiteBleGattCallback() {

                    @Override
                    public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                        Log.e("","断开连接！！！");
                        super.onConnectionStateChange(gatt, status, newState);



                        if (status == BluetoothGatt.GATT_SUCCESS){
                            if (newState == BluetoothGatt.STATE_DISCONNECTED){
                                mHandler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        tvStatus.setText("未连接");
                                    }
                                });
                            }
                        }else if (status == BluetoothGatt.GATT_FAILURE){
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvStatus.setText("连接异常");
                                }
                            });
                        } else{
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    tvStatus.setText("连接异常 -- 其他");
                                }
                            });
                        }
                    }

                    @Override
                    public void onConnectSuccess(BluetoothGatt bluetoothGatt, int i) {
                        bluetoothGatt.discoverServices();
                        Log.e("","-->连接成功");
                    }

                    @Override
                    public void onServicesDiscovered(BluetoothGatt gatt, int arg1) {
                        BluetoothDevice deviceConn = gatt.getDevice();
                        final String addressConn = deviceConn.getAddress();
                        final String nameConn = deviceConn.getName();

                        Log.e("","-->查找服务成功");
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                tvBondedDevice.setText(nameConn+"," + addressConn);
                                tvStatus.setText("已连接");
                                SharedPreferenceUtil.put(getApplicationContext(), ADDRESS, addressConn);
                            }
                        });
                    }



                    @Override
                    public void onConnectFailure(BleException e) {
                        Log.e("LiteBleActivity","--> onConnectFailure() 连接失败 !!!!!!!!");
//                        Toast.makeText(getApplicationContext(), "连接失败:" + e.getDescription(), Toast.LENGTH_LONG).show();
                            mHandler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.e("LiteBleActivity","--> 设置未连接状态!!!!!!!!");
                                    tvStatus.setText("未连接");
                                }
                            });
                        bleExceptionHandler.handleException(e);
                    }
                });
            }
        });
    }

    public void startScan(View v){
        scan();
        Log.e("LiteBleActivity", "开始搜索！！！！");
        Log.i("LiteBleActivity", "开始搜索！！！！");
        Log.w("LiteBleActivity", "开始搜索！！！！");
        Log.d("LiteBleActivity", "开始搜索！！！！");
        System.out.println("------------------ahahahahahaaaaa--------------------");
    }

    private void scan(){
        lite.startLeScan(new PeriodScanCallback(TIME_OUT) {
            @Override
            public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
                Log.e("", "--> device : " + device);
                Log.e("", "--> rssi : " + rssi);
                String record = DataUtil.byteToHexString(scanRecord);
                Log.e("", "--> scanRecord : " + record);
                String result = "result : ";
                for(int i=0;i<scanRecord.length;i++){
                    byte b = scanRecord[i];
                    char c = (char) b;
                    Log.e("", "--> c : ["+i+"] :" + c);
                    result += c;
                }
                Log.e("", "--> result : " + result);

                list.add(device);
                mHandler.sendEmptyMessage(DEVICE_FOUND);
            }

            @Override
            public void onScanTimeout() {
//                Toast.makeText(getApplicationContext(), "搜索超时", Toast.LENGTH_LONG).show();
            }
        });
    }
}
