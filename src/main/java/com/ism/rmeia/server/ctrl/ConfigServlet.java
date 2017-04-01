package com.ism.rmeia.server.ctrl;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.ism.rmeia.day.strategy.AbstractStrategy;
import org.apache.commons.io.IOUtils;
import org.apache.http.entity.InputStreamEntity;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ism.market.config.ConfigConstants;
import com.ism.market.timer.HbaseGetKLinerTimer;
import com.ism.rmeia.GlobalTemplateConfig;
import com.ism.rmeia.day.strategy.RunDayStrategy;
import com.ism.rmeia.rule.SoldRule1;
import com.ism.rmeia.rule.SoldRule1.RuleValue;
import com.ism.util.Config;

/**
 * 读取IA发送过来的交易数据
 *
 * @author wx
 */
@SuppressWarnings("serial")
public class ConfigServlet extends HttpServlet {
    private final static Logger logger = LoggerFactory.getLogger(ConfigServlet.class);

    public ConfigServlet() {
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/json");
        PrintWriter wr = response.getWriter();
        InputStream is = request.getInputStream();
        try {
            String json = EntityUtils.toString(new InputStreamEntity(is), StandardCharsets.UTF_8);
            JSONObject obj = JSON.parseObject(json);
            if (obj != null) {
                Config cfg = Config.getInstance();
                for (Entry<String, Object> e : obj.entrySet()) {
                    String key = e.getKey();
                    Object value = e.getValue();
                    if (value == null) {
                        cfg.put(key, "");
                    } else {
                        cfg.put(key, e.getValue().toString());
                    }
                }
                new GlobalTemplateConfig(cfg).init();
                wr.write(printConfig());
            } else {
                // 输入为空
                wr.write(RenderJsonUtil.getStatusJson(500, "no config set.") + "\n old config:\n" + printConfig());
            }
        } catch (Throwable t) {
            logger.info(t.getMessage());
        } finally {
            IOUtils.closeQuietly(is);
            wr.close();
        }
    }

    /**
     * 修改系统参数
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        logger.info("config parameter,params={}",request.getParameterMap());
        response.setContentType("text/json");
        PrintWriter wr = response.getWriter();
   
        try {
            Config cfg = Config.getInstance();//bugfix，need to changed the default parameter
            Map<String, String[]> parameters = request.getParameterMap();
            parameters.forEach((key, params) -> {
                if (key.equals(ConfigConstants.RULE_X_KEY)){ 
                    SetFloatParam(cfg, key, params);
                }
                else if (key.equals(ConfigConstants.RULE_Y1_KEY)){ 
                    SetFloatParam(cfg, key, params);
                }
                else if (key.equals(ConfigConstants.RULE_Y2_KEY)){ 
                   SetFloatParam(cfg, key, params);
                }
                else if (key.equals(ConfigConstants.RULE_Z_KEY)){ 
                    SetFloatParam(cfg, key, params);
                }
            });
            RunDayStrategy.configure(cfg);
            AbstractStrategy.configure(cfg);

            HbaseGetKLinerTimer.getInstance().setX(cfg.getInt("Rule.X",30));
            
            StringBuilder sb = new StringBuilder();
            
            String configString = printConfig();
            sb.append(configString);
            wr.write(sb.toString());
        } finally {
            wr.close();
        }
    }

    /**
     * 设置浮点参数
     * @param cfg
     * @param key
     * @param params
     */
    private void SetFloatParam(Config cfg,String key,String... params) {
        
            if (params!=null && params.length > 0) {
                try {
                    //Float X = Float.parseFloat(params[params.length - 1]);
                    String X = params[params.length - 1];
                    cfg.put(key, X);
                } catch (NumberFormatException nfe) {
                }
            }
        
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/json");
        PrintWriter wr = response.getWriter();
        try {
            String configString = printConfig();
            wr.write(configString);
        } finally {
            wr.close();
        }
    }

    /**
     * @return config 的字符串表示
     */
    private String printConfig() {
        Map<Object, Object> prop = Config.getInstance().imutableMap();
        StringBuilder sb = new StringBuilder();
        sb.append("--------config--------\n");
        for (java.util.Map.Entry<? extends Object, ? extends Object> e : prop.entrySet()) {
            String key = e.getKey().toString();
            String value = e.getValue().toString();
            int idx = value.indexOf("password");
            if (idx == 0) {
                value = "";
            } else if (idx > 0) {
                value = value.substring(0, idx + "password".length());
            }
            sb.append(key + "=" + value);
            sb.append("\n");
        }
        sb.append("--------rule1.txt-------\n");
        for (RuleValue e : SoldRule1.getRules()) {
            sb.append(e.toCSVString());
            sb.append("\n");
        }
        return sb.toString();
    }
}
