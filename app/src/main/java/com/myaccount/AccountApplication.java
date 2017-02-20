package com.myaccount;

import android.app.Application;
import android.content.Context;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

/**
 * Created by wangchaohu on 2017/2/19.
 * <p>
 * <p>
 * 自动更新功能
 */

public class AccountApplication extends Application {

    Context mContext;

    private String APP_ID = "58a43717";

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
//初始化即创建语音配置对象，只有初始化后才可以使用MSC的各项服务。建议将初始化放在程序入口处（如Application、Activity的onCreate方法),
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + APP_ID);
    }


}
