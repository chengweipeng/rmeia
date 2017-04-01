package com.ism.rmeia.output;

import com.ism.util.HttpClientUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Consumer;

/**
 * Created by wx on 2016/10/14.
 * @since 1.0.1
 * @version 1.0.1
 */
public class CombineHttpSendJson<E> implements Consumer<Collection<E>> {
    private final static Logger logger = LoggerFactory.getLogger(CombineHttpSendJson.class);
    String url;
    public CombineHttpSendJson(){
    }



    /**
     * 对于null对象，会被过滤掉
     * @param c 输入集合
     */
    @Override
    public void accept(Collection<E> c) {
       String content =  joins(c);
        try {
            HttpClientUtils.getInstance().postAndForget(url, 10000, content, "application/json", StandardCharsets.UTF_8, false,true,null);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("[CombineHttpSendJson] error,url={},ex={}",url,e);
        }
    }

    public String joins(Collection<E> u) {
        char separator = ',';
        if(u == null || u.isEmpty()){
            return "";
        }
        Iterator<E> iterator = u.iterator();
        Object first = iterator.next();
        String value = (first== null?"":first.toString());

        StringBuilder buf = new StringBuilder(Math.max(256,u.size()*value.length()));
        buf.append('[');

        if (first != null) {
            buf.append(first);
        }
        while (iterator.hasNext()) {
            buf.append(separator);
            Object obj = iterator.next();
            if (obj != null) {
                buf.append(obj);
            }
        }
        buf.append(']');
        return buf.toString();
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
}