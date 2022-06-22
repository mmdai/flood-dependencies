package cn.flood.websocket.utils;

import com.alibaba.fastjson2.JSON;

import java.io.Serializable;
import java.util.Date;

public class ResponseData implements Serializable {

    private String _code;//-1:处理失败;0:处理中;1:处理成功
    private String _type;
    private String _msg;
    private Object _data;
    private long _count;
    private Date _time;

    public ResponseData() {
    }

    public ResponseData(long resCount, Object resData) {
        _code = "000000";
        _msg = "success";
        _count = resCount;
        _data = resData;
        _time = new Date();
    }

    public ResponseData(String resType, Object resData) {
        _type = resType;
        _data = resData;
        _time = new Date();
    }

    public ResponseData(String resCode, String resMsg, Object resData) {
        _code = resCode;
        _msg = resMsg;
        _data = resData;
        _time = new Date();
    }

    public ResponseData(Exception ex) {
        _code = "S00000";
        _msg = ex.getMessage();
        _data = ex.toString();
        _time = new Date();
    }

    public String get_code() {
        return _code;
    }

    public void set_code(String _code) {
        this._code = _code;
    }

    public String get_type() {
        return _type;
    }

    public void set_type(String _type) {
        this._type = _type;
    }

    public String get_msg() {
        return _msg;
    }

    public void set_msg(String _msg) {
        this._msg = _msg;
    }

    public Object get_data() {
        return _data;
    }

    public void set_data(Object _data) {
        this._data = _data;
    }

    public long get_count() {
        return _count;
    }

    public void set_count(long _count) {
        this._count = _count;
    }

    public Date get_time() {
        return _time;
    }

    public void set_time(Date _time) {
        this._time = _time;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }
}
