package com.ism.rmeia.bean;

import java.util.ArrayList;
import java.util.List;

public class UserInfo {
    IABasic basic;
    List<IAStock> stocks = new ArrayList<IAStock>();

    public IABasic getBasic() {
        return basic;
    }

    public void setBasic(IABasic basic) {
        this.basic = basic;
    }

    public List<IAStock> getStocks() {
        return stocks;
    }

    public void setStocks(List<IAStock> stocks) {
        this.stocks = stocks;
    }
    public int getSource(){
        return basic.getSource();
    }

    @Override
    public String toString() {
        return "UserInfo [basic=" + basic + ", stocks=" + stocks + "]";
    }
}