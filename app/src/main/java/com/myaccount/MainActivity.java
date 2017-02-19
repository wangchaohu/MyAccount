package com.myaccount;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.iflytek.autoupdate.IFlytekUpdate;


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

    private IFlytekUpdate updManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
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

        writeRead.readData();
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

}
