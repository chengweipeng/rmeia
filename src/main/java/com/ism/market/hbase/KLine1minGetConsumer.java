package com.ism.market.hbase;

import com.ism.market.KLine;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.client.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KLine1minGetConsumer implements HbaseGetConsumer {

    Map<String,List<KLine> > kLineMap = new HashMap<>(102400);
    long lastTime;
    @Override
    public void consume(Result r) throws Exception {
        Cell closeCell = r.getColumnLatestCell(KLine1minHbaseTable.family, KLine1minHbaseTable.CLOSE_KEY);
        byte []bb = CellUtil.cloneValue(closeCell);
        //java.nio.ByteBuffer bb = CellUtil.getValueBufferShallowCopy(closeCell);
        String rowKey = new String(r.getRow(),"UTF-8");
        String stockCode = rowKey.substring(0,6);
        long tm = Long.parseLong(rowKey.substring(6));
        lastTime = Math.max(tm,lastTime);
        float close = Float.parseFloat(new String(bb));
        KLine kline = new KLine();
        kline.CLOSE = close;
        List<KLine> l = kLineMap.getOrDefault(stockCode,new ArrayList<>());
        l.add(kline);
        kLineMap.put(stockCode,l);
    }
    public Map<String,List<KLine>> getKLineMap() {
        return kLineMap;
    }
}
