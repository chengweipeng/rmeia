package com.ism.rmeia.server;

import com.ism.db.DBHelper;
import com.ism.rmeia.GlobalTemplateConfig;
import com.ism.rmeia.server.ctrl.AskServlet;
import com.ism.rmeia.server.ctrl.ConfigServlet;
import com.ism.rmeia.server.ctrl.HTTPMetricsServlet;
import com.ism.rmeia.server.ctrl.PredictStockServlet;
import com.ism.rmeia.server.ctrl.RmeiaGetIAServlet;
import com.ism.rmeia.server.ctrl.SyncRmeiaServlet;
import com.ism.util.Config;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Properties;

public class JettyServer {
    private final static Logger logger = LoggerFactory.getLogger(JettyServer.class);
    Server server;
    int port = 80;

    public JettyServer(Map<String, String> cfg) {

    }

    public JettyServer() {
    }

    public JettyServer(Properties prop) {

    }

    public void start() {
        try {
            Config cfg = Config.getInstance().loadSettings("cluster.properties");
            DBHelper.getInstance().configure(cfg);
            GlobalTemplateConfig global = new GlobalTemplateConfig(cfg);
            global.init();
            global.start();
            logger.info("JETTY server start");
            port = cfg.getInt("node.port");
            server = new Server(port);
            server.setAttribute("server", "jetty-rmeia");
            ServletHandler handler = new ServletHandler();
            
            handler.addServletWithMapping(RmeiaGetIAServlet.class, "/rmeia");
            handler.addServletWithMapping(PredictStockServlet.class, "/pred");
            handler.addServletWithMapping(SyncRmeiaServlet.class, "/testrmeia");
            handler.addServletWithMapping(ConfigServlet.class, "/config");
            handler.addServletWithMapping(HTTPMetricsServlet.class, "/metric");
            handler.addServletWithMapping(AskServlet.class, "/ask");
            server.setHandler(handler);
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        JettyServer me = new JettyServer();
        me.start();
        // start server
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void start() {
                super.start();
            }

            @Override
            public void run() {
                System.err.println("server has been shutdown.");
            }
        });
    }
}
