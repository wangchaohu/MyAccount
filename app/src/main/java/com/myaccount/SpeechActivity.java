package com.myaccount;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.Rationale;
import com.yanzhenjie.permission.RationaleListener;

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
                    setPermission();
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

        Log.d("wch", "onBackPressed: ");
        AlertDialog.Builder dialog = new AlertDialog.Builder(SpeechActivity.this);
        dialog.setTitle("重要信息")
                .setMessage("退出后将不能修改、删除，是否退出？")
                .setNegativeButton("退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                toJsonString();
                Intent intent = new Intent();
                intent.putExtra("json", json);
                intent.putExtra("isSameDay", isSameDay);
                setResult(0x123, intent);
                json = "";
                finish();
            }
        }).setPositiveButton("修改", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        dialog.create().show();
    }

    private void setPermission() {
        /**23以上定位权限申请*/
        if (Build.VERSION.SDK_INT >= 23) {
            int i = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i == PackageManager.PERMISSION_GRANTED) {  //授权
                mIat.startListening(mRecoListener);
            } else if (i == PackageManager.PERMISSION_DENIED) {
                //拒绝
                ActivityCompat.requestPermissions(SpeechActivity.this, new String[]{Manifest.permission.RECORD_AUDIO}, 101);
            }
        } else {
            mIat.startListening(mRecoListener);
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
        if (requestCode == 101) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mIat.startListening(mRecoListener);
            } else {
                Toast.makeText(SpeechActivity.this, "没有获得权限，将无法正常使用", Toast.LENGTH_LONG).show();
            }
        }
    }

}


