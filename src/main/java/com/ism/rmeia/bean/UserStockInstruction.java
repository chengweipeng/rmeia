package com.ism.rmeia.bean;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * messageID唯一标识
 *
 * @author wx
 */
@SuppressWarnings("serial")
public class UserStockInstruction implements Serializable{
    String msgId;
    String ism;
    String uid;
    ArrayList<StockInstruction> orders = new ArrayList<>();
    int source;
    public UserStockInstruction(){
    }
    public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String messageID) {
        this.msgId = messageID;
    }

    public String getIsm() {
        return ism;
    }

    public void setIsm(String ism) {
        this.ism = ism;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ArrayList<StockInstruction> getOrders() {
        return orders;
    }

    public void setOrders(ArrayList<StockInstruction> orders) {
        this.orders = orders;
    }
    
    public int getSource() {
        return source;
    }

    public void setSource(int source) {
        this.source = source;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((msgId == null) ? 0 : msgId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserStockInstruction other = (UserStockInstruction) obj;
        if (msgId == null) {
            if (other.msgId != null)
                return false;
        } else if (!msgId.equals(other.msgId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("UserStockInstruction [msgId=").append(msgId)
                .append(", ism=").append(ism).append(", uid=").append(uid)
                .append(", orders=").append(orders).append(", source=")
                .append(source).append("]");
        return builder.toString();
    }
}
