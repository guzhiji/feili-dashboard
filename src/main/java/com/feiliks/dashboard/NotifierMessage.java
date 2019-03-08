package com.feiliks.dashboard;

public class NotifierMessage<T> {
    private String cmd;
    private String key;
    private T data;

    public NotifierMessage() {
    }

    public NotifierMessage(String cmd, String key, T data) {
        this.setCmd(cmd);
        this.setKey(key);
        this.setData(data);
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

    public void setCmd(String cmd) {
        this.cmd = cmd;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setData(T data) {
        this.data = data;
    }
}
