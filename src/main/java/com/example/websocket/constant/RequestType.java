package com.example.websocket.constant;

import com.sun.media.sound.FFT;

/**
 * <pre>
 *
 * </pre>
 * @author 杨帮东 (qq:397827222)
 * @version 1.0
 * @date 2019/02/26 16:49
 **/
public enum RequestType {

    /**
     * 登录操作
     */
    LOGIN(10000);

    int value;

    public int getValue() {
        return value;
    }

    public void setValue(int value) {
        this.value = value;
    }

    RequestType(int value) {
        this.value = value;
    }


}
