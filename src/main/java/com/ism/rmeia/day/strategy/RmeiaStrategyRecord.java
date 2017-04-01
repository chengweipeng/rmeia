package com.ism.rmeia.day.strategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.ism.enumeration.DELEGATE_METHOD;
import com.ism.rmeia.enumeration.FigureType;
import com.ism.rmeia.enumeration.TradingDayType;
/**
 * 
 * @author wx
 * 股票操作策略
 * @since 0.1.1
 */
public class RmeiaStrategyRecord {
    private final static Logger logger = LoggerFactory.getLogger(RmeiaStrategyRecord.class);
    public static enum OPERATION{
        BUY,
        SEL,
        NOP;
        public static OPERATION parse(int val){ 
            switch(val){
            case 0:
            return BUY;
            case 1:
                return SEL;
            default:
                return NOP;
            }
        }
    }

    int afterOpenDay;
    int isCloseDay;
    FigureType figure;
    TradingDayType dayType;
    DELEGATE_METHOD method = DELEGATE_METHOD.XIAN_JIA;
    float curPrice;
    float pred_15min; 
    float pred_close;
    float entrust_price;
    OPERATION op;
    String ism_id;
    String user_id;
    String stock_code;
    int sig_tm;
    int free_money;
    int entrust_num;
    String msgId;
    float vpin;
    double impact;
    
    public RmeiaStrategyRecord(){
    }
    
    public RmeiaStrategyRecord(int afterOpenDay, int isCloseDay, FigureType figure, TradingDayType dayType,
            float curPrice, float pred_15min, float pred_close, OPERATION oper,DELEGATE_METHOD method,String ismId,String userId,String stockCode,int sigTm) {
        this.afterOpenDay = afterOpenDay;
        this.isCloseDay = isCloseDay;
        this.figure = figure;
        this.dayType = dayType;
        this.curPrice = curPrice;
        this.pred_15min = pred_15min;
        this.pred_close = pred_close;
        this.op = oper;
        this.method = method;
        this.ism_id = ismId;
        this.user_id = userId;
        this.stock_code = stockCode;
        this.sig_tm = sigTm;
        this.free_money = 0;
    }
    public static String formatJSON(RmeiaStrategyRecord src ){
       return JSON.toJSONString(src);
    }
    public static RmeiaStrategyRecord parseJSON(String json){
        return JSON.parseObject(json,RmeiaStrategyRecord.class);
    }
    
    public void parseCSV(String csv){ 
        logger.warn("not supported {}",csv);
        //TODO:
    }
    
    public String formatCSV(){
        return ism_id+","+user_id+","+stock_code+","+afterOpenDay +","+isCloseDay+","+figure+","+dayType+","+(float)curPrice + ","+(float)pred_15min+","+(float)pred_close+","+op+","+method;
    }
    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RmeiaStrategyRecord [afterOpenDay=")
                .append(afterOpenDay).append(", isCloseDay=").append(isCloseDay)
                .append(", figure=").append(figure).append(", dayType=")
                .append(dayType).append(", method=").append(method)
                .append(", curPrice=").append(curPrice).append(", pred_15min=")
                .append(pred_15min).append(", pred_close=").append(pred_close)
                .append(", entrust_price=").append(entrust_price)
                .append(", op=").append(op).append(", ism_id=").append(ism_id)
                .append(", user_id=").append(user_id).append(", stock_code=")
                .append(stock_code).append("]");
        return builder.toString();
    }
    public int getAfterOpenDay() {
        return afterOpenDay;
    }
    public void setAfterOpenDay(int afterOpenDay) {
        this.afterOpenDay = afterOpenDay;
    }
    public int getIsCloseDay() {
        return isCloseDay;
    }
    public void setIsCloseDay(int isCloseDay) {
        this.isCloseDay = isCloseDay;
    }
    public FigureType getFigure() {
        return figure;
    }
    public void setFigure(FigureType figure) {
        this.figure = figure;
    }
    public TradingDayType getDayType() {
        return dayType;
    }
    public void setDayType(TradingDayType dayType) {
        this.dayType = dayType;
    }
    public float getCurPrice() {
        return curPrice;
    }
    public void setCurPrice(float curPrice) {
        this.curPrice = curPrice;
    }
    public float getPred_15min() {
        return pred_15min;
    }
    public void setPred_15min(float pred_15min) {
        this.pred_15min = pred_15min;
    }
    public float getPred_close() {
        return pred_close;
    }
    public void setPred_close(float pred_close) {
        this.pred_close = pred_close;
    }
    public OPERATION getOp() {
        return op;
    }
    /**
     * 设置操作指令 BUY，SEL，NOP
     * @see OPERATION
     * @param op
     */
    public void setOp(OPERATION op) {
        this.op = op;
    }
    public DELEGATE_METHOD getMethod() {
        return method;
    }
    /**
     * 设置委托方式，目前仅支持限价和市价（最优五档）
     * @param method
     */
    public void setMethod(DELEGATE_METHOD method) {
        this.method = method;
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

    public String getStock_code() {
        return stock_code;
    }

    public void setStock_code(String stock_code) {
        this.stock_code = stock_code;
    }

    public float getEntrust_price() {
        return entrust_price;
    }

    public void setEntrust_price(float entrust_price) {
        this.entrust_price = entrust_price;
    }

    public int getSig_tm() {
        return sig_tm;
    }

    public void setSig_tm(int sig_tm) {
        this.sig_tm = sig_tm;
    }

    public int getFree_money() {
        return free_money;
    }

    public void setFree_money(int free_money) {
        this.free_money = free_money;
    }

    public int getEntrust_num() {
        return entrust_num;
    }

    public void setEntrust_num(int entrust_num) {
        this.entrust_num = entrust_num;
    }
    
    public String getMsgId(){
    	return msgId;
    }
    
    public void setMsgId(String msgId){
    	this.msgId = msgId;
    }
    
    public float getVpin(){
    	return vpin;
    }
    public void setVpin(float vpin){
    	this.vpin = vpin;
    }
    
    public double getImpact(){
    	return impact;
    }
    public void setImpact(double impact){
    	this.impact = impact;
    }
 }
