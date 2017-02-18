package com.myaccount;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;

import java.util.ArrayList;

/**
 * Created by wangchaohu on 2017/2/16.
 */

public class RecycleViewAdapter extends RecyclerView.Adapter {


    private Context mContext;
    private ArrayList<String> contentLists = new ArrayList<>();   //内容
    private ArrayList<String> dateLists = new ArrayList<>();     //小时时间
    private ArrayList<String> dayDateLists = new ArrayList<>(); //天时间

    public RecycleViewAdapter(Context context, String data) {
        this.mContext = context;
        if (null != data && !(data.equals(""))) {
            initData(data);
        }
    }

    public String getDate() {
        String s;
        if (dayDateLists.size() > 0) {
            s = dayDateLists.get(dayDateLists.size() - 1);
            return s;
        }
        return null;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item_main_layout, parent, false));
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder) holder).contentTv.setText(contentLists.get(contentLists.size() -1- position));
        ((MyViewHolder) holder).dateTv.setText(dateLists.get(contentLists.size() -1 - position));
    }

    @Override
    public int getItemCount() {
        return contentLists.size() == 0 ? 0 : contentLists.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView contentTv;
        TextView dateTv;

        public MyViewHolder(View itemView) {
            super(itemView);
            contentTv = (TextView) itemView.findViewById(R.id.content_Tv);
            dateTv = (TextView) itemView.findViewById(R.id.date_Tv);
        }
    }

    private void initData(String jsonData) {
        //对数据进行解析
        JsonObject jsonObject = JSON.parseObject(jsonData, JsonObject.class);
        for (JsonObject.JsonDataBean jsonDataBean : jsonObject.getJsonData()) {
            dayDateLists.add(jsonDataBean.getDayDate());
            String dayDate = jsonDataBean.getDayDate();   //2017-2-13
            for (JsonObject.JsonDataBean.DataBean dataBean : jsonDataBean.getData()) {
                    contentLists.add(dataBean.getContent());
                    dateLists.add(dayDate + "  " + dataBean.getHourDate());  //2017-2-13  22:13
            }
        }
    }
}
