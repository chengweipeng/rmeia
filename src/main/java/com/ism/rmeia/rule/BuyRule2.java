package com.ism.rmeia.rule;

import com.ism.rmeia.bean.IAStock;
import com.ism.rmeia.bean.StockSignal;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
/**
 * 
 * @author wx
 *买入规则2，目前的阈值是设为1
 */
public class BuyRule2 {

    public static float sharpRatioThreshold = 1f;

    /**
     * sharpRatio价格的sharp ratio是否与股票相关,目前暂定为1
     *
     * @param stocks
     * @param map
     * @return
     */
    public static List<IAStock> getCanBuyStocks(Collection<IAStock> stocks, Map<String, StockSignal> map) {
        ArrayList<IAStock> ret = new ArrayList<IAStock>();
        for (IAStock stock : stocks) {
            StockSignal sig = map.get(stock.getStockCode());
            if (sig != null) {
                double sharpRatio = (sig.pred_close - stock.getCurPrice()) / sig.pred_close_std;
                if (sharpRatio > sharpRatioThreshold) {
                    ret.add(stock);
                }
            }
        }
        return ret;
    }
    public static void setSharpRatioThreshold(float sharpRatio){
        sharpRatioThreshold = sharpRatio;
    }
    public static float getSharpRatioThreshold(){
        return sharpRatioThreshold;
    }
}
