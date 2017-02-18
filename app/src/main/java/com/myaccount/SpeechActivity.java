package com.myaccount;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Created by wangchaohu on 2017/2/15.
 */

public class SpeechActivity extends AppCompatActivity {

    private SpeechRecognizer mIat = null;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    private EditText resultTv;
    private TextView btn;

    private String today;
    private boolean isSameDay = false;
    String json = "";

    RecognizerDialog mDialog = null;

    JsonUtils jsonUtils;

    private String lastStr = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_speech);

        //初始化即创建语音配置对象，只有初始化后才可以使用MSC的各项服务。建议将初始化放在程序入口处（如Application、Activity的onCreate方法),
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=58a43717");

        createDialog();
        initIat();
        init();

    }

    private void init() {
        resultTv = (EditText) findViewById(R.id.tv);
        btn = (TextView) findViewById(R.id.start);
        btn.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    mIat.startListening(mRecoListener);
                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    mIat.stopListening();
                }
                return true;
            }
        });


        Intent intent = getIntent();
        today = intent.getStringExtra("today");
    }

    private void initIat() {
        mIat = SpeechRecognizer.createRecognizer(this, null);
        //2.设置听写参数，详见《科大讯飞MSC API手册(Android)》SpeechConstant类
        mIat.setParameter(SpeechConstant.DOMAIN, "iat");
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin ");
    }

    InitListener mInitListener = new InitListener() {
        @Override
        public void onInit(int i) {
            Log.d("wch", "onInit: " + i);
        }
    };

    public void createDialog() {
        //1.创建RecognizerDialog对象
        mDialog = new RecognizerDialog(this, mInitListener); //2.设置accent、language等参数
        mDialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT, "mandarin");
//3.设置回调接口
        mDialog.setListener(mRecognizerDialogListener);
    }

    RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        @Override
        public void onResult(RecognizerResult results, boolean b) {
            printResult(results);
        }

        @Override
        public void onError(SpeechError speechError) {

        }
    };

    RecognizerListener mRecoListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {

        }

        @Override
        public void onError(SpeechError speechError) {

        }

        //开始录音
        @Override
        public void onBeginOfSpeech() {
            lastStr = resultTv.getText().toString();
            if (mDialog != null) {
                mDialog.show();
            }
        }

        //结束录音
        @Override
        public void onEndOfSpeech() {
            if (mDialog != null) {
                mDialog.dismiss();
            }
        }

        //扩展用接口
        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {

        }
    };

    StringBuffer resultBuffer = null;

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }


        resultTv.setText(lastStr + resultBuffer.toString());
        resultTv.setSelection(resultTv.length());
        toJsonString();
    }

    private void toJsonString(){
        if (!(resultTv.getText().toString().equals(""))) {
            Date date = new Date();
            //转化成json
            jsonUtils = new JsonUtils();
            //判断是否是同一天
            if (null == today) {   //第一次
                json = "{\"jsonData\":[{\"dayDate\":\"" + jsonUtils.toDay(date) + "\",\"data\":[{\"hourDate\":\"" + jsonUtils.toHour(date)
                        + "\",\"content\":\"" + resultTv.getText().toString() + "\"}]}]}";

            } else if (jsonUtils.toDay(date).equals(today)) {  //不是第一次，是同一天
                isSameDay = true;
                json = ",{\"hourDate\":\"" + jsonUtils.toHour(date) + "\",\"content\":\"" + resultTv.getText().toString() + "\"}]}]}";
            } else {  //不是第一次，不是同一天
                json = ",{\"dayDate\":\"" + jsonUtils.toDay(date) + "\",\"data\":[{\"hourDate\":\"" + jsonUtils.toHour(date)
                        + "\",\"content\":\"" + resultTv.getText().toString() + "\"}]}]}";
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("json", json);
        intent.putExtra("isSameDay", isSameDay);
        setResult(0x123, intent);
        json = "";
        super.onBackPressed();
    }

}


