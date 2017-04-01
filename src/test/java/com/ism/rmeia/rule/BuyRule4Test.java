package com.ism.rmeia.rule;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Test;

import com.ism.rmeia.day.strategy.OpenDayMarketParameter;
import com.ism.rmeia.util.MathUtil;

/**
 * Created by wx on 2016/9/20.
 */
public class BuyRule4Test{


    @Test
    public void testBuy(){
        OpenDayMarketParameter t = new OpenDayMarketParameter();

        t.ts = System.currentTimeMillis();
        t.ma_4=11.5f;
        t.close_0 = 10.05f;
        t.close_4 = 10;

        t.dif_0=-1;
        t.dif_1=-0.9f;
        t.dif_2=-0.5f;
        t.dif_3=-0.5f;
        t.dif_4=-0.5f;

        t.dea_0=-5;
        t.dea_1=-1;
        t.dea_2=-1;
        t.dea_3=-1;
        t.dea_4=-0.45f;


        Assert.assertTrue("must lt.",(t.close_4 < t.ma_4));
        Assert.assertTrue(t.close_4 < t.close_0);
        Assert.assertTrue(t.dif_4<0 && t.dea_4<0);
        Assert.assertTrue(t.dif_3<0 && t.dea_3<0);
        Assert.assertTrue(t.dif_4< t.dea_4);
        Assert.assertTrue(t.dif_3> t.dea_3);
        Assert.assertTrue(t.dif_2> t.dea_2);
        Assert.assertTrue(t.dif_1> t.dea_1);
        Assert.assertTrue((t.dif_0- t.dea_0) >
            MathUtil.max(
                t.dif_3-t.dea_3,
                t.dif_2-t.dea_2,
                t.dif_1-t.dea_1
            ));
        Assert.assertTrue(BuyRule4.buy(t));
    }
    
    @Test
    public void testBuyFromFile() throws IOException, URISyntaxException{
       URL url = this.getClass().getClassLoader().getResource("buyrule.csv");
       List<String> lines =  FileUtils.readLines(new File(url.toURI()));
     
       for(int i=1;i<lines.size();i++){
           String line = lines.get(i);
           String [] row = line.split(",");
           OpenDayMarketParameter t = new OpenDayMarketParameter();
           int k=0;
           t.close_0 = Float.parseFloat(row[k++]);
           t.close_4 = Float.parseFloat(row[k++]);
           t.ma_4 = Float.parseFloat(row[k++]);
           
           t.dif_0 = Float.parseFloat(row[k++]);
           t.dif_1 = Float.parseFloat(row[k++]);
           t.dif_2 = Float.parseFloat(row[k++]);
           t.dif_3 = Float.parseFloat(row[k++]);
           t.dif_4 = Float.parseFloat(row[k++]);
           
           t.dea_0 = Float.parseFloat(row[k++]);
           t.dea_1 = Float.parseFloat(row[k++]);
           t.dea_2 = Float.parseFloat(row[k++]);
           t.dea_3 = Float.parseFloat(row[k++]);
           t.dea_4 = Float.parseFloat(row[k++]);
           
           float maxValue = Float.parseFloat(row[k++]);
           float diffdea0 = Float.parseFloat(row[k++]);
           boolean ans = Boolean.parseBoolean(row[k++]);
          
           Assert.assertEquals(i+" line error data="+line +" param="+t,ans,BuyRule4.buy(t));
       }
    }

}
