package com.ism.rmeia.server.ctrl;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ism.rmeia.util.JMXPollUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Type;
import java.util.Map;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
/**
 * @author wx
 * copy from flume 1.6.0
 */

/**
 * A Monitor service implementation that runs a web server on a configurable
 * port and returns the metrics for components in JSON format.
 * <p>
 * Optional parameters:
 * <p>
 * <tt>port</tt> : The port on which the server should listen to.
 * <p>
 * Returns metrics in the following format:
 * <p>
 * <p>
 * {
 * <p>
 * "componentName1":{"metric1" : "metricValue1","metric2":"metricValue2"}
 * <p>
 * "componentName1":{"metric3" : "metricValue3","metric4":"metricValue4"}
 * <p>
 * }
 */
@SuppressWarnings("serial")
public class HTTPMetricsServlet extends HttpServlet {

    private static Logger LOG = LoggerFactory.getLogger(HTTPMetricsServlet.class);

    Type mapType = new TypeToken<Map<String, Map<String, String>>>() {
    }.getType();
    Gson gson = new Gson();

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
        // /metrics is the only place to pull metrics.
        // If we want to use any other url for something else, we should make
        // sure
        // that for metrics only /metrics is used to prevent backward
        LOG.debug("get mbean metrics");
        response.setContentType("application/json;charset=utf-8");
        response.setStatus(HttpServletResponse.SC_OK);
        Map<String, Map<String, String>> metricsMap = JMXPollUtil.getAllMBeans();
        String json = gson.toJson(metricsMap, mapType);
        PrintWriter pw = response.getWriter();
        pw.write(json);
        pw.close();
        return;
    }
}
