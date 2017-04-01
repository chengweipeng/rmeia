package com.ism.rmeia.task;

public class GetIsmDetail {
    String url;

    static GetIsmDetail me = new GetIsmDetail();

    public static GetIsmDetail getInstance() {
        return me;
    }

}
