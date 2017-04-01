package com.ism.rmeia.bean;

public class IABasic implements Cloneable {
    String msgId;
    long ia_id;// 对应数据库中的id序号，主要用于查询加速
    double totalMoney;// 用户投入的钱数额,单位元
    double totalFreeMoney;// 空闲钱,可用资金
    double totalMacketValue;//总市值
    byte ia_type;
    byte ia_status;
    boolean closeDay;
    int diffOpenDay;//距离建仓日多少天

    String ism_id;
    String user_id;
    String order_id;
    String start_date;
    String end_date;
    int source;

    public IABasic() {
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public double getTotalFreeMoney() {
        return totalFreeMoney;
    }

    @Override
    public IABasic clone() {
        IABasic newIA = new IABasic();
        newIA.ia_id = ia_id;// 对应数据库中的id序号，主要用于查询加速
        newIA.totalMoney = totalMoney;// 用户投入的钱数额,单位元
        newIA.totalFreeMoney = totalFreeMoney;// 空闲钱,可用资金
        newIA.ia_type = ia_type;
        newIA.ia_status = ia_status;
        newIA.ism_id = ism_id;
        newIA.user_id = user_id;
        newIA.order_id = order_id;
        newIA.start_date = start_date;
        newIA.end_date = end_date;
        newIA.diffOpenDay = diffOpenDay;
        newIA.closeDay = closeDay;
        newIA.msgId = msgId;
        newIA.totalMacketValue = totalMacketValue;
        return newIA;
    }

    public boolean isOpenDay() {
        return diffOpenDay == 0;
    }

    public boolean isRunDay() {
        return diffOpenDay > 0 && !isCloseDay();
    }
    
    public long getIa_id() {
        return ia_id;
    }

    public void setIa_id(long ia_id) {
        this.ia_id = ia_id;
    }

    public byte getIa_type() {
        return ia_type;
    }

    public void setIa_type(byte ia_type) {
        this.ia_type = ia_type;
    }

    public byte getIa_status() {
        return ia_status;
    }

    public void setIa_status(byte ia_status) {
        this.ia_status = ia_status;
    }

    public boolean isCloseDay() {
        return closeDay;
    }

    public void setCloseDay(boolean closeDay) {
        this.closeDay = closeDay;
    }

    public int getDiffOpenDay() {
        return diffOpenDay;
    }

    public void setDiffOpenDay(int diffOpenDay) {
        this.diffOpenDay = diffOpenDay;
    }

    public String getIsm_id() {
        return ism_id;
    }

    public void setIsm_id(String ism_id) {
        this.ism_id = ism_id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getStart_date() {
        return start_date;
    }

    public void setStart_date(String start_date) {
        this.start_date = start_date;
    }

    public String getEnd_date() {
        return end_date;
    }

    public void setEnd_date(String end_date) {
        this.end_date = end_date;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public void setTotalFreeMoney(double totalFreeMoney) {
        this.totalFreeMoney = totalFreeMoney;
    }

    
    
    public double getTotalMacketValue() {
        return totalMacketValue;
    }

    public void setTotalMacketValue(double totalMacketValue) {
        this.totalMacketValue = totalMacketValue;
    }

    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "IABasic [messageID=" + msgId + ", ia_id=" + ia_id + ", totalMoney=" + totalMoney
                + ", totalFreeMoney=" + totalFreeMoney + ", totalMacketValue=" + totalMacketValue + ", ia_type=" + ia_type + ", ia_status=" + ia_status
                + ", closeDay=" + closeDay + ", diffOpenDay=" + diffOpenDay + ", ism_id=" + ism_id + ", user_id="
                + user_id + ", order_id=" + order_id + ", start_date=" + start_date + ", end_date=" + end_date + "]";
    }
}
