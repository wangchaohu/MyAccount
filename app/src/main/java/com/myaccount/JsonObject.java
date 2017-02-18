package com.myaccount;

import java.util.List;

/**
 * Created by wangchaohu on 2017/2/16.
 */

public class JsonObject {

    private List<JsonDataBean> jsonData;

    public List<JsonDataBean> getJsonData() {
        return jsonData;
    }

    public void setJsonData(List<JsonDataBean> jsonData) {
        this.jsonData = jsonData;
    }

    public static class JsonDataBean {
        /**
         * dayDate : 2017-2-12
         * data : [{"hourDate":"22:24:24","content":"你好"},{"hourDate":"22:25:24","content":"你好"}]
         */

        private String dayDate;
        private List<DataBean> data;

        public String getDayDate() {
            return dayDate;
        }

        public void setDayDate(String dayDate) {
            this.dayDate = dayDate;
        }

        public List<DataBean> getData() {
            return data;
        }

        public void setData(List<DataBean> data) {
            this.data = data;
        }

        public static class DataBean {
            /**
             * hourDate : 22:24:24
             * content : 你好
             */

            private String hourDate;
            private String content;

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
}
