package com.x.doraemon.common.web;

import com.x.doraemon.DateTimes;
import java.io.Serializable;
import java.util.StringJoiner;

/**
 * 统一结果
 *
 * @author AD
 * @date 2022/1/19 13:09
 */
public class Result<T> implements Serializable {

    /**
     * 是否成功
     */
    private boolean success;
    /**
     * 信息
     */
    private String msg;
    /**
     * 当前时间
     */
    private String now;
    /**
     * 结果数据
     */
    private T data;


    public static <T> Result success() {
        return generate(null, "成功", true);
    }

    public static <T> Result success(T data) {
        return generate(data, "成功", true);
    }

    public static <T> Result fail() {
        return generate(null, "失败", false);
    }

    public static <T> Result fail(T data) {
        return generate(data, "失败", false);
    }

    public static <T> Result fail(T data, String error) {
        return generate(data, error, false);
    }

    public static <T> Result generate(T data, String msg, boolean success) {
        Result<T> result = new Result<>();
        result.data = data;
        result.success = success;
        result.msg = msg;
        result.now = DateTimes.now(true);
        return result;
    }

    public String getMsg() {
        return msg;
    }

    public Result setMsg(String msg) {
        this.msg = msg;
        return this;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getNow() {
        return now;
    }

    public void setNow(String now) {
        this.now = now;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Result.class.getSimpleName() + "[", "]").add("success=" + success).add("msg='" + msg + "'")
            .add("now='" + now + "'").add("data=" + data).toString();
    }
}
