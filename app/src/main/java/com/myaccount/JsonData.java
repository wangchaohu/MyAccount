package com.myaccount;

import java.util.List;

/**
 * Created by wangchaohu on 2017/2/16.
 */

public class JsonData {


    private List<JsonDataBean> jsonData;

    public List<JsonDataBean> getJsonData() {
        return jsonData;
    }

    public void setJsonData(List<JsonDataBean> jsonData) {
        this.jsonData = jsonData;
    }

    public static class JsonDataBean {
        /**
         * dayDate : 2017-2-14
         * hourDate : 22:24:24
         * content : 你好
         */

        private String dayDate;
        private String hourDate;
        private String content;

        public String getDayDate() {
            return dayDate;
        }

        public void setDayDate(String dayDate) {
            this.dayDate = dayDate;
        }

        public String getHourDate() {
            return hourDate;
        }

        public void setHourDate(String hourDate) {
            this.hourDate = hourDate;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }
}
