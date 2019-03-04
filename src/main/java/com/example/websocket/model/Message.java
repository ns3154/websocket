package com.example.websocket.model;

/**
 * <pre>
 *
 * </pre>
 * @author 杨帮东 (qq:397827222)
 * @version 1.0
 * @date 2019/03/01 09:25
 **/
public class Message<T> {

    private int repType;

    private T data;

    private String msg;

    public Message<T> ok (int repType, T data, String msg) {
        Message<T> m = new Message<>();
        m.setData(data);
        m.setMsg(msg);
        m.setRepType(repType);
        return m;
    }

    public Message<T> fail (int repType, T data, String msg) {
        Message<T> m = new Message<>();
        m.setRepType(repType);
        m.setData(data);
        m.setMsg(msg);
        return m;
    }

    public int getRepType() {
        return repType;
    }

    public void setRepType(int repType) {
        this.repType = repType;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
