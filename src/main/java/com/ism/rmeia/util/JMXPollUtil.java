package com.ism.rmeia.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.management.*;
import java.lang.management.ManagementFactory;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/*
*
*/

public class JMXPollUtil {

    private static Logger LOG = LoggerFactory.getLogger(JMXPollUtil.class);
    private static MBeanServer mbeanServer = ManagementFactory.getPlatformMBeanServer();

    public static Map<String, Map<String, String>> getAllMBeans() {
        Map<String, Map<String, String>> mbeanMap = new HashMap<>();
        Set<ObjectInstance> queryMBeans = null;
        try {
            queryMBeans = mbeanServer.queryMBeans(null, null);
        } catch (Exception ex) {
            LOG.error("Could not get Mbeans for monitoring", ex);
        }
        for (ObjectInstance obj : queryMBeans) {
            try {
                if (!obj.getObjectName().toString().startsWith("com.ism.rmeia")) {
                    continue;
                }
                MBeanAttributeInfo[] attrs = mbeanServer.getMBeanInfo(obj.getObjectName()).getAttributes();
                String strAtts[] = new String[attrs.length];
                for (int i = 0; i < strAtts.length; i++) {
                    strAtts[i] = attrs[i].getName();
                }
                AttributeList attrList = mbeanServer.getAttributes(obj.getObjectName(), strAtts);
                String component = obj.getObjectName().toString()
                        .substring(obj.getObjectName().toString().indexOf('=') + 1);
                Map<String, String> attrMap = new HashMap<>();

                for (Object attr : attrList) {
                    Attribute localAttr = (Attribute) attr;
                    if (localAttr.getName().equalsIgnoreCase("type")) {
                        component = localAttr.getValue() + "." + component;
                    }
                    attrMap.put(localAttr.getName(), localAttr.getValue().toString());
                }
                mbeanMap.put(component, attrMap);
            } catch (Exception e) {
                LOG.error("Unable to poll JMX for metrics.", e);
            }
        }
        return mbeanMap;
    }
}
