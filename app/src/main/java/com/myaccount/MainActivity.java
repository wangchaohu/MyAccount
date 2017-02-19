package com.myaccount;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.iflytek.autoupdate.IFlytekUpdate;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

import java.util.List;


/**
 * 思路：
 * 将读取，写入都使用线程来完成，
 * 写一个接口，在写入和读取完成时调用
 * <p>
 * 将读取，写入操作都放到MainActivity中完成，
 * 在写入读取的同时，加入刷新动画
 */


public class MainActivity extends AppCompatActivity {


    private RecyclerView mRecycleView;
    private String today = null;
    private RecycleViewAdapter adapter = null;
    private LinearLayoutManager llm;
    private String data;

    private WriteUtils writeRead;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
    }

    @Override
    protected void onResume() {
        setPermission();
        super.onResume();
    }



    private void initView() {
        setContentView(R.layout.activity_main);
        mRecycleView = (RecyclerView) findViewById(R.id.main_Rv);
        findViewById(R.id.ll).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SpeechActivity.class);
                intent.putExtra("today", today);
                startActivityForResult(intent, 0x123);
            }
        });


        writeRead = new WriteUtils(new DoFile() {
            @Override
            public void writeSuccess() {
                mHandler.sendEmptyMessage(0x124);
            }

            @Override
            public void readSuccess(String jsonData) {
                data = jsonData;
                mHandler.sendEmptyMessage(0x125);
            }
        });
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x124) {
                writeRead.readData();
            }
            if (msg.what == 0x125) {
                setRvAdapter();
            }
        }
    };


    @Override
    protected void onDestroy() {
        data = "";
        adapter = null;
        llm = null;
        writeRead = null;
        super.onDestroy();
    }

    private void setRvAdapter() {
        adapter = new RecycleViewAdapter(MainActivity.this, data);
        today = adapter.getDate();
        llm = new LinearLayoutManager(MainActivity.this);
        mRecycleView.setLayoutManager(llm);
        mRecycleView.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == 0x123) {
            Boolean isSameDay = data.getBooleanExtra("isSameDay", false);
            String json = data.getStringExtra("json");
            if (!json.equals("")) {
                writeRead.writeTxtToFile(json, isSameDay);
            } else {
                writeRead.readData();
            }
        }

    }

    private long firstTime = 0;

    @Override
    public void onBackPressed() {

        Log.d("wch", "onBackPressed: ");
        long secondTime = System.currentTimeMillis();
        if (secondTime - firstTime > 2000) {
            firstTime = secondTime;
            Toast.makeText(MainActivity.this, "再按一次退出程序", Toast.LENGTH_SHORT).show();
        } else {
            System.exit(0);   //正常退出程序
        }
    }



    private void setPermission() {
        /**23以上定位权限申请*/
        if (Build.VERSION.SDK_INT >= 23) {
            int i = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i == PackageManager.PERMISSION_GRANTED) {  //授权
                writeRead.readData();
            } else if (i == PackageManager.PERMISSION_DENIED) {
                //拒绝
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 100);
            }
        } else {
            writeRead.readData();
        }
    }

    /**
     * 定位权限返回处理
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                writeRead.readData();
            } else {
                Toast.makeText(MainActivity.this, "没有获得权限，将无法正常使用", Toast.LENGTH_LONG).show();
            }
        }
    }
}
