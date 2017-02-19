package com.myaccount;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import com.iflytek.autoupdate.IFlytekUpdate;
import com.iflytek.autoupdate.IFlytekUpdateListener;
import com.iflytek.autoupdate.UpdateConstants;
import com.iflytek.autoupdate.UpdateErrorCode;
import com.iflytek.autoupdate.UpdateInfo;
import com.iflytek.autoupdate.UpdateType;
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
    private IFlytekUpdate updManager;
    private String APP_ID = "58a43717";
    private Thread mUiThread;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this.getApplicationContext();
//初始化即创建语音配置对象，只有初始化后才可以使用MSC的各项服务。建议将初始化放在程序入口处（如Application、Activity的onCreate方法),
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=" + APP_ID);
        autoUpdate();
    }

    private void autoUpdate() {
        //初始化自动更新对象
        updManager = IFlytekUpdate.getInstance(mContext);
        //开启调试模式，默认不开启
        updManager.setDebugMode(true);
        //开启wifi环境下检测更新，仅对自动更新有效，强制更新则生效
        updManager.setParameter(UpdateConstants.EXTRA_WIFIONLY, "true");
        //设置通知栏使用应用icon，详情请见示例
        updManager.setParameter(UpdateConstants.EXTRA_NOTI_ICON, "true");
        //设置更新提示类型，默认为通知栏提示
        updManager.setParameter(UpdateConstants.EXTRA_STYLE, UpdateConstants.UPDATE_UI_NITIFICATION);
        updManager.autoUpdate(mContext, updateListener);
    }

    IFlytekUpdateListener updateListener = new IFlytekUpdateListener() {
        @Override
        public void onResult(int errorCode, UpdateInfo result) {

            if(errorCode == UpdateErrorCode.OK && result!= null) {
                if(result.getUpdateType() == UpdateType.NoNeed) {
//                    Toast.makeText(mContext, "已经是最新版本！", Toast.LENGTH_SHORT).show();
//                    return;
                }
                updManager.showUpdateInfo(mContext, result);
            }
            else
            {
                Toast.makeText(mContext, "请求更新失败！\n更新错误码：" + errorCode , Toast.LENGTH_SHORT).show();
            }
        }
    };
}
