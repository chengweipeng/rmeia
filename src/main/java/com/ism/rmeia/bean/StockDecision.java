package com.ism.rmeia.bean;

import java.sql.Timestamp;

public class StockDecision {
    String ism_id;
    String user_id;
    String stock_code;
    float pred_15;
    float pred_close;
    float pre_close;
    int diff_build_day;
    int close_day;
    Timestamp sig_time;
    String msg_id;
 }
