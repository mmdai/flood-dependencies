package cn.flood.websocket.utils;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;
import java.util.Date;

public class ResponseData implements Serializable {

    private String res_code;//-1:处理失败;0:处理中;1:处理成功
    private String res_type;
    private String res_msg;
    private Object res_data;
    private long res_count;
    private Date res_time;

    public ResponseData() {
    }



    public ResponseData(long resCount, Object resData) {
        res_code = "1";
        res_msg = "success";
        res_count = resCount;
        res_data = resData;
        res_time = new Date();
    }

    public ResponseData(String resType, Object resData) {
        res_type = resType;
        res_data = resData;
        res_time = new Date();
    }

    public ResponseData(String resCode, String resMsg, Object resData) {
        res_code = resCode;
        res_msg = resMsg;
        res_data = resData;
        res_time = new Date();
    }



    public ResponseData(Exception ex) {
        res_code = "-1";
        res_msg = ex.getMessage();
        res_data = ex.toString();
        res_time = new Date();
    }


    public String getRes_code() {
        return res_code;
    }

    public void setRes_code(String res_code) {
        this.res_code = res_code;
    }

    public String getRes_type() {
        return res_type;
    }

    public void setRes_type(String res_type) {
        this.res_type = res_type;
    }

    public String getRes_msg() {
        return res_msg;
    }

    public void setRes_msg(String res_msg) {
        this.res_msg = res_msg;
    }

    public Object getRes_data() {
        return res_data;
    }

    public void setRes_data(Object res_data) {
        this.res_data = res_data;
    }

    public long getRes_count() {
        return res_count;
    }

    public void setRes_count(long res_count) {
        this.res_count = res_count;
    }

    public Date getRes_time() {
        return res_time;
    }

    public void setRes_time(Date res_time) {
        this.res_time = res_time;
    }

    public String toString() {
        return JSON.toJSONString(this);
    }
}
