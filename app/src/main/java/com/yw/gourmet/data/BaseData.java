package com.yw.gourmet.data;

/**
 * Created by LYW on 2017/11/13.
 */

public class BaseData<T> {
    private int status;
    private String message;
    private T data;

    public int getStatus() {
        return status;
    }

    public BaseData setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public BaseData setMessage(String message) {
        this.message = message;
        return this;
    }

    public T getData() {
        return data;
    }

    public BaseData setData(T data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "BaseData{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
