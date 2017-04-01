package com.ism.rmeia.rule;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.ism.rmeia.bean.DelegateStock;
import com.ism.rmeia.bean.IAStock;
import com.ism.rmeia.bean.StockFeature;
import com.ism.rmeia.bean.StockSignal;
import com.ism.rmeia.bean.UserInfo;

//规则1
public class SoldRule1 {

    public static int DEFAULT_COLUMN_NUM = 6;
    static ArrayList<RuleValue> rules = new ArrayList<RuleValue>();

    // public static Cache<String, SoldRule1> hs300Cache =
    // CacheBuilder.newBuilder()
    // .expireAfterWrite(5 * 24,
    // TimeUnit.HOURS).maximumSize(10240).softValues().build();

    public static class RuleValue {
        String expr;
        float curProfitLowRatio;
        float curProfitHighRatio;
        float expectLowRatio;
        float expectHighRatio;
        float sellRatio;

        /**
         * 卖出比例
         *
         * @param sellRatio
         */
        public void setSellRatio(float sellRatio) {
            this.sellRatio = sellRatio;
        }

        @Override
        public String toString() {
            return "[expr=" + expr + ", curProfitLowRatio=" + curProfitLowRatio + ", curProfitHighRatio="
                    + curProfitHighRatio + ", expectLowRatio=" + expectLowRatio + ", expectHighRatio=" + expectHighRatio
                    + ", sellRatio=" + sellRatio + "]";
        }

        public String toCSVString() {
            return expr + "\t" + curProfitLowRatio + "\t" + curProfitHighRatio + "\t" + expectLowRatio + "\t"
                    + expectHighRatio + "\t" + sellRatio;
        }
    }

    static {
        loadRule();
    }

    private static void loadRule() {
        try {
            InputStream is = null;
            try{
                is = SoldRule1.class.getClassLoader().getResourceAsStream("rule1.txt");
            }catch(Exception ioe){
            }
            if(is == null){
               is = new FileInputStream("rule1.txt");
            }
            if(is!=null){
                InputStreamReader ir = null;
                try{
                 ir = new InputStreamReader(is);
                load(ir);
                }finally{
                ir.close();
                }
            }
        } catch (Throwable t) {
           t.printStackTrace();
        }
    }

    /**
     * 根据用户信息，持仓信息，计算卖出的股票委托股数，这里不能得到实际卖出的股价
     *
     * @param userInfo 用户信息
     * @param stocks   持仓信息
     * @return 返回卖出的委托列表
     */
    public static ArrayList<DelegateStock> getNeedSellStock(final UserInfo userInfo, Collection<IAStock> stocks,
                                                            Map<String, StockSignal> sigMap) {
        ArrayList<DelegateStock> delegateList = new ArrayList<DelegateStock>();
        int openDay = userInfo.getBasic().getDiffOpenDay();
        double totalMoney = userInfo.getBasic().getTotalMoney();
        for (IAStock stock : stocks) {
            StockSignal sig = sigMap.get(stock.getStockCode());
            if (sig == null) {
                continue;
            }
            //兼容如果预测价格=0，表明实际上是停牌信息
            if(sig.pred_close == 0){
                //默认为停牌
                continue;
            }
            float ratio = getSellRatio(totalMoney, openDay, stock, sig);
            if (ratio <= 0) {
                ratio = 0;
            } else if (ratio >= 0.9) {
                ratio = 1;
            }
            if (ratio > 0) {
                DelegateStock delegateStock = new DelegateStock(stock);
                //int entrustNum = ((int) (stock.getAvailableNum() * ratio / 100)) * 100;
                int entrustNum = getEntrust(stock.getAvailableNum(),ratio);
                if (entrustNum > 0) {
                    delegateStock.setBuyDir(false);
                    delegateStock.setEntrustNum(entrustNum);
                    delegateList.add(delegateStock);
                }
            }
        }
        return delegateList;
    }

    /**
     * 计算该股票的卖出比列
     *
     * @param totalMoney   总投入
     * @param afterOpenDay 开仓日后多少天
     * @param stock        股票的输入信息
     * @return 卖出比例，0表示不操作
     */

    private static float getSellRatio(double totalMoney, int afterOpenDay, IAStock stock, StockSignal stockSignal) {
        if (stockSignal != null) {
            StockFeature feature = calcStockFeature(stockSignal, stock, totalMoney);

            switch (afterOpenDay) {
                case 1:
                    // 获取卖出比例
                    // getSellRatio;
                    for (int i = 0; i < 4; i++) {
                        if (i >= rules.size()) {
                            return 0;
                        }
                        RuleValue rule = rules.get(i);
                        float ratio = rowSellRatio(feature, rule);
                        if (ratio > 0) {
                            return ratio;
                        }
                    }
                    break;
                case 2:
                    for (int i = 4; i < 8; i++) {
                        if (i >= rules.size()) {
                            return 0;
                        }
                        RuleValue rule = rules.get(i);
                        float ratio = rowSellRatio(feature, rule);
                        if (ratio > 0) {
                            return ratio;
                        }
                    }
                    break;
                case 3:
                    for (int i = 8; i < 12; i++) {
                        if (i >= rules.size()) {
                            return 0;
                        }
                        RuleValue rule = rules.get(i);
                        float ratio = rowSellRatio(feature, rule);
                        if (ratio > 0) {
                            return ratio;
                        }
                    }
                    break;
                default:
                    for (int i = 8; i < 12; i++) {
                        if (i >= rules.size()) {
                            return 0;
                        }
                        RuleValue rule = rules.get(i);
                        float ratio = rowSellRatio(feature, rule);
                        if (ratio > 0) {
                            return ratio;
                        }
                    }
                    break;
            }
        }
        return 0;
    }

    // 判断每一行卖出的比例
    private static float rowSellRatio(StockFeature feature, RuleValue rule) {
        boolean alpha = isBetween(feature.getCurProfit(), rule.curProfitLowRatio, rule.curProfitHighRatio);
        boolean beta = isBetween(feature.getExpectProfit(), rule.expectLowRatio, rule.expectHighRatio);
        if (alpha && beta) {
            return rule.sellRatio;
        }
        return 0;
    }

    public static void load(Reader reader) {
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                if (line.startsWith("#")) {
                    continue;
                }
                String value[] = StringUtils.split(line, "\t ");
                if (value == null || value.length != DEFAULT_COLUMN_NUM) {
                    continue;
                }
                RuleValue rule = new RuleValue();
                rule.expr = value[0];

                rule.curProfitLowRatio = parseFloat(value[1]);
                rule.curProfitHighRatio = parseFloat(value[2]);
                rule.expectLowRatio = parseFloat(value[3]);
                rule.expectHighRatio = parseFloat(value[4]);
                rule.sellRatio = parseFloat(value[5]);
                rules.add(rule);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 计算股票在ISM中的特征
     *
     * @param sig         数据库的SIG&E
     * @param curPrice    股票当前价格
     * @param avgCost     平均成本（可能为负数）
     * @param total_money 总投入
     * @return StockFeature，但不包含股票买卖数
     */
    public static StockFeature calcStockFeature(StockSignal sig, IAStock stock, double total_money) {
        double curPrice = (float) stock.getCurPrice();
        double avgCost = stock.getAvgPrice();
        StockFeature stockFeature = new StockFeature();
        stockFeature.setCurProfit((float) ((curPrice - avgCost) / avgCost));// 当前收益占比
        stockFeature.setExpectProfit((float) ((sig.pred_close - curPrice) * stock.getOwnNum() / total_money));
        stockFeature.setExpectCloseStd(sig.pred_close_std * sig.pre_close);
        stockFeature.setExpectCloseSharpRatio((sig.pred_close - curPrice) / stockFeature.getExpectCloseStd());
        return stockFeature;
    }

    static float parseFloat(String item) {
        if (item.charAt(item.length() - 1) == '%') {
            String floatValue = item.substring(0, item.length() - 1);
            return Float.parseFloat(floatValue) / 100;
        } else {
            return Float.parseFloat(item);
        }
    }

    static boolean isBetween(float cur, float low, float high) {
        return cur >= low && cur <= high;
    }

    public static ArrayList<RuleValue> getRules() {
        return rules;
    }

    //输入股数 5805和卖出系数 0.9
    //
    public static int getEntrust(int num,float ratio){
    	
    	int outnum=0;
    	
    	int intergeNum = num/100; //58
    	
    	int remainder = num%100;  //5
    	
    	int outtmp = (int) (intergeNum*100*ratio);
    	
    	int tempnum = intergeNum*100 - outtmp;
    	
    	outnum = outtmp + tempnum%100 + remainder;
    	
    	return outnum;
    }
    
    public static void main(String args[]) {

        System.out.println(Float.parseFloat("0.5"));
    }
}
