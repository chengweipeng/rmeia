package com.ism.market.hbase;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.junit.After;
import org.junit.Assert;
import org.junit.Test;

import com.ism.market.KLine;

public class KLineGetUtilTest {
    
   // @Before
    public void setUp() throws IOException{
        // System.getenv().put("user.name", "hbase");
        System.getProperties().put("user.name", "hbase");
        System.out.println(System.getenv());
        Properties prop = System.getProperties();
        System.out.println(prop);
        Connection connection = null;
        InputStream is = null;
        try {
            Configuration config = HBaseConfiguration.create();
            // InputStream uri =
            // HbaseTable.class.getResourceAsStream("conf/hbase-site.xml");
            try {
                is = new FileInputStream("conf/hbase-site.xml");
            } catch (Exception e) {
                is = KLineGetUtilTest.class.getClassLoader().getResourceAsStream("conf/hbase-site.xml");
            }
            config.addResource(is);
            // config.addResource("conf/hbase-site.xml");
            connection = ConnectionFactory.createConnection(config);
            Admin admin = (Admin) connection.getAdmin();
            HTableDescriptor htd = new HTableDescriptor(TableName.valueOf("t1"));
            HColumnDescriptor hcd = new HColumnDescriptor("b");
            htd.addFamily(hcd);
            boolean tableExists = admin.isTableEnabled(htd.getTableName());
            if (!tableExists) {
                admin.createTable(htd);
            } else {
                System.out.println("hbase put test OK.");
            }

        } finally {
            IOUtils.closeQuietly(is);
            if (connection != null) {
                connection.close();
            }
        }
    }
    @After
    public void destroy() throws IOException{

    }
    //@Test
    public void testGetAll() throws IOException{
        Map<String, List<KLine>> m = KLineGetUtil.getAll();
        long start = System.currentTimeMillis();
        Collection<KLine> klines = m.get("600001");
        long end = System.currentTimeMillis();
        System.out.println("get all cost=" +(end - start));
        klines.forEach(System.out::println);
    }

    @Test
    public void testUpdateInitParamters() throws URISyntaxException, IOException, CloneNotSupportedException {

        URL url = this.getClass().getClassLoader().getResource("kline.csv");

        List<String> lines =  FileUtils.readLines(new File(url.toURI()));
        List<KLine> kLines = new ArrayList<>();
        int i=0;
        for(i=1;i<lines.size();i++) {
            String line= lines.get(i);
            String [] row = line.split(",");
            int k=0;
            KLine kLine = new KLine();
            kLine.CLOSE = Float.parseFloat(row[k++]);
            kLine.MA =  Float.parseFloat(row[k++]);
            kLine.EMA12 = Float.parseFloat(row[k++]);
            kLine.EMA26 = Float.parseFloat(row[k++]);
            kLine.DIF = Float.parseFloat(row[k++]);
            kLine.DEA = Float.parseFloat(row[k++]);
            kLines.add(kLine);
        }

        List<KLine> expectAns = new ArrayList<>(kLines.size());
        for(int j=0;j<kLines.size();j++){
            expectAns.add((KLine) kLines.get(j).clone());
        }

        Assert.assertEquals(kLines,expectAns);
        KLineGetUtil.updateInitParamters(kLines,35);

        KLineGetUtil.calcAndUpdateParameters(kLines,35);
        Assert.assertEquals(expectAns.size(),kLines.size());
        for(int j=0;j<kLines.size();j++){
            Object src = kLines.get(j);
            Object expect = expectAns.get(j);
            if( !src.equals(expect)) {
                System.err.println(j+":\n");
                System.err.print(src);
                System.err.println("-->");
                System.err.println(expect);
            }
        }
        Assert.assertEquals(expectAns,kLines);
    }
}
