package com.myaccount;

import com.alibaba.fastjson.JSON;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by wangchaohu on 2017/2/16.
 */

public class JsonUtils {
    ArrayList<JsonData.JsonDataBean> jsonDataBeanArrayList;

    public String strToJson(String content){
        jsonDataBeanArrayList = new ArrayList<>();
        JsonData.JsonDataBean jsonDataBean =  new JsonData.JsonDataBean();
        Date date = new Date();
        jsonDataBean.setContent(content);
        jsonDataBean.setDayDate(toDay(date));
        jsonDataBean.setHourDate(toHour(date));

        jsonDataBeanArrayList.add(jsonDataBean);

        JsonData jsonData = new JsonData();
        jsonData.setJsonData(jsonDataBeanArrayList);

        String json = JSON.toJSONString(jsonData);

        return json;
    }

    public  String toDay(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String day = sdf.format(date);
        return day;
    }

    public String toHour(Date date){
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        String hour = sdf.format(date);
        return hour;
    }
}
