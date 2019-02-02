package com.feiliks.dashboard;

public class NotifierMessage<T> {
    private String cmd;
    private String key;
    private T data;

    public NotifierMessage() {
    }

    public NotifierMessage(String cmd, String key, T data) {
        this.cmd = cmd;
        this.key = key;
        this.data = data;
    }

    public String getCmd() {
        return cmd;
    }

    public String getKey() {
        return key;
    }

    public T getData() {
        return data;
    }
}
