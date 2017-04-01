package com.ism.rmeia.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 每条记录是一个IA当前信息，获取IA发送过来的消息
 *
 * @author wx
 */
public class InputIAInfo implements Serializable{

    private String ismId;
    private String uid;
    private short afterBuildDay;
    private byte isCloseDay;
    private double totalMoney;
    private double totalFreeMoney;
    //增加市值（停牌股票rmeia本身没法计算）
    private double totalMacketValue;
    private String msgId;
    private ArrayList<IAInputStock> stock = new ArrayList<IAInputStock>();
    int source;

    public InputIAInfo(){
        source=1;
    }
    public String getIsm() {
        return ismId;
    }

    public void setIsm(String ism) {
        this.ismId = ism;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public short getAfterBuildDay() {
        return afterBuildDay;
    }

    public void setAfterBuildDay(short afterBuildDay) {
        this.afterBuildDay = afterBuildDay;
    }

    public byte getIsCloseDay() {
        return isCloseDay;
    }

    public void setIsCloseDay(byte isCloseDay) {
        this.isCloseDay = isCloseDay;
    }

    public double getTotoalMoney() {
        return totalMoney;
    }

    public void setTotoalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    public double getTotalFreeMoney() {
        return totalFreeMoney;
    }

    public void setTotalFreeMoney(double totalFreeMoney) {
        this.totalFreeMoney = totalFreeMoney;
    }


    public String getIsmId() {
        return ismId;
    }

    public void setIsmId(String ismId) {
        this.ismId = ismId;
    }

    public double getTotalMoney() {
        return totalMoney;
    }

    public void setTotalMoney(double totalMoney) {
        this.totalMoney = totalMoney;
    }

    /**
     * @return 返回IA生成的消息ID
     */
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public double getTotalMacketValue() {
        return totalMacketValue;
    }
    public void setTotalMacketValue(double totalMacketValue) {
        this.totalMacketValue = totalMacketValue;
    }
    
    /**
     * @return 
     */
    public ArrayList<IAInputStock> getStock() {
        return stock;
    }

    public void setStock(ArrayList<IAInputStock> stock) {
        this.stock = stock;
    }

    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    @Override
    public String toString() {
        return "InputIAInfo [ismId=" + ismId + ", uid=" + uid + ", afterBuildDay=" + afterBuildDay + ", isCloseDay="
                + isCloseDay + ", totalMoney=" + totalMoney + ", totalFreeMoney=" + totalFreeMoney + ",totalMacketValue=" + totalMacketValue 
                +", msgId=" + msgId + ", stock=" + stock + "]";
    }

    public static void main(String args[]) {
        // performance test
        System.out.println(System.currentTimeMillis());
        for (int i = 0; i < 1000; i++) {

        }
        System.out.println(System.currentTimeMillis());
        for (int i = 0; i < 1000; i++) {

        }
        System.out.println(System.currentTimeMillis());
    }
}
