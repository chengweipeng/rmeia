package com.ism.market.hbase;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.MasterNotRunningException;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.ZooKeeperConnectionException;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Delete;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ism.market.KLine;


/**
 * 
 * @author WX
 *
 * @version 1.0(HBase 1.0)
 */
public class KLine1minHbaseTable {
    private final static Logger logger = LoggerFactory.getLogger(KLine1minHbaseTable.class);
    
    public static final String DEFAULT_TABLE_NAME = "realtime_one_min_price";// "ISM_BLOCK";
    public static final byte[] CLOSE_KEY = "ClosePx".getBytes();

    public static byte[] family = "price".getBytes();// family
    public static KLine1minHbaseTable me = new KLine1minHbaseTable();
    String m_tableName;
    HTableDescriptor htd;
    Connection connection;
    int batch_size;

    public KLine1minHbaseTable() {
        this(DEFAULT_TABLE_NAME);
    }

    public KLine1minHbaseTable(String tableName) {
        this.m_tableName = tableName;
        batch_size = 10;
    }

    public void setTableName(String tableName) {
        this.m_tableName = tableName;
    }

    public void connect() {
        System.getProperties().put("user.name", "hbase");
        System.out.println(System.getenv());
        Properties prop = System.getProperties();
        System.out.println(prop);
        InputStream fis = null;
        try {
            Configuration config = HBaseConfiguration.create();
            try{
                fis = new FileInputStream("conf/hbase-site.xml");
            }catch(FileNotFoundException fileNotFoundException){
                fis = KLine1minHbaseTable.class.getClassLoader().getResourceAsStream("conf/hbase-site.xml");
            }
            config.addResource(fis);
            connection = ConnectionFactory.createConnection(config);
        } catch (IOException ex) {
            ex.printStackTrace();
            logger.error("io ex={}",ex);
        } finally {
            IOUtils.closeQuietly(fis);
        }
    }

    public boolean create() throws IOException {
        return create(false);
    }

    public boolean create(boolean dropIfNotExists) throws IOException {

        TableName hTable = TableName.valueOf(m_tableName);
        htd = new HTableDescriptor(hTable);
        Admin admin = (Admin) connection.getAdmin();
        if (admin.isTableAvailable(hTable)) {
            if (dropIfNotExists) {
                System.out.println("table already exists,trye to drop table first");
                try {
                    deleteTable(m_tableName);
                    System.out.println("table already exists,Drop table successs.");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                createTable(admin, htd);
                return admin.isTableEnabled(hTable);
            } else {
                System.out.println("table already exists,Drop table first!");
                return false;
            }
        } else {
            createTable(admin, htd);
        }
        return true;
    }

    private void createTable(Admin admin, HTableDescriptor htd) throws IOException {
        htd.addFamily(new HColumnDescriptor(family));
        // htd.addFamily(new HColumnDescriptor(CONTNET_KEY));
        // htd.addFamily(new HColumnDescriptor(START_DATE_KEY));
        admin.createTable(htd);
        System.out.println("table create OK!");
    }

    /**
     * Delete a table
     */
    public void deleteTable(String tableName) throws IOException {

        TableName hTable = TableName.valueOf(tableName);
        htd = new HTableDescriptor(hTable);
        Admin admin = (Admin) connection.getAdmin();
        admin.disableTable(hTable);
        admin.deleteTable(hTable);
    }

    /**
     * Put (or insert) a row
     */
    public void insertRow(String rowKey, String family, String qualifier, String value) throws IOException {
        if (connection == null) {
            connect();
        }
        if (htd == null) {
            TableName hTable = TableName.valueOf(m_tableName);
            htd = new HTableDescriptor(hTable);
        }
        TableName tableName = htd.getTableName();
        Table table = connection.getTable(tableName);
        byte[] row1 = Bytes.toBytes(rowKey);
        Put p1 = new Put(row1);
        p1.addColumn(Bytes.toBytes(family), Bytes.toBytes(qualifier), Bytes.toBytes(value));
        table.put(p1);
    }

    /**
     * Delete a row
     */
    public void delRecord(byte[] rowKey) throws IOException {
        if (connection == null) {
            connect();
        }
        if (htd == null) {
            TableName hTable = TableName.valueOf(m_tableName);
            htd = new HTableDescriptor(hTable);
        }
        TableName tableName = htd.getTableName();
        Table table = connection.getTable(tableName);
        Delete del = new Delete(rowKey);
        table.delete(del);
    }

    /**
     * Get a row
     */
    public Result getOneRecord(byte[] rowKey) throws IOException {
        if (connection == null) {
            connect();
        }
        if (htd == null) {
            TableName hTable = TableName.valueOf(m_tableName);
            htd = new HTableDescriptor(hTable);
        }
        TableName tableName = htd.getTableName();
        Table table = connection.getTable(tableName);
        Get get = new Get(rowKey);
        return table.get(get);
    }

    /**
     * Scan (or list) a table
     */
    public void getAllRecord(HbaseGetConsumer consumer) throws IOException {
        if (connection == null) {
            connect();
        }
        if (htd == null) {
            TableName hTable = TableName.valueOf(m_tableName);
            htd = new HTableDescriptor(hTable);
        }
        TableName tableName = htd.getTableName();
        Table table = connection.getTable(tableName);
        Scan scan = new Scan();
        scan.setBatch(batch_size);
        scan.setMaxVersions(1);
        scan.setSmall(false);
        scan.setCaching(4096);
        scan.addColumn(family,CLOSE_KEY);
        ResultScanner resultScanner = table.getScanner(scan);
        
        Result r = null;
        while ((r = resultScanner.next()) != null) {
            try {
                consumer.consume(r);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void setBatchSize(int batchSize) {
        if (batchSize <= 0) {
            batchSize = 1024;
        }
        this.batch_size = batchSize;
    }

    public void close() throws IOException {
        if (connection != null) {
            try {
                connection.close();
            } finally {
                connection = null;
            }
        }
    }

    public static void main(String args[]) throws MasterNotRunningException, ZooKeeperConnectionException, IOException {
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
                is = KLine1minHbaseTable.class.getClassLoader().getResourceAsStream("conf/hbase-site.xml");
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
            if (connection != null) {
                connection.close();
            }
            IOUtils.closeQuietly(is);
        }
        
        Map<String, List<KLine>> m = KLineGetUtil.getAll();
        long start = System.currentTimeMillis();
        Collection<KLine> klines = m.get("600001");
        long end = System.currentTimeMillis();
        System.out.println("get all cost=" +(end - start));
        klines.forEach(System.out::println);
    }

    public Table table() throws IOException {
        if (connection == null) {
            connect();
        }
        if (htd == null) {
            TableName hTable = TableName.valueOf(m_tableName);
            htd = new HTableDescriptor(hTable);
        }
        TableName tableName = htd.getTableName();
        Table table = connection.getTable(tableName);
        return table;
    }


}
