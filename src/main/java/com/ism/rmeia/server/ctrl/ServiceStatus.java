package com.ism.rmeia.server.ctrl;

public class ServiceStatus {
    //    OK(0),
    public static final int PARAMETER_ERROR = 600;
    public static final int SERVICE_ERROR = 500;
    public static int OK = 0;
    //    SERVICE_ERROR(500);
    int status;
    String msg;

    public ServiceStatus() {
    }

    public ServiceStatus(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

}
