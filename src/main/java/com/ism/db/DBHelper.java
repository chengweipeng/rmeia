package com.ism.db;

import com.ism.util.Config;

import javax.sql.PooledConnection;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * simple connection management {@link MiniConnectionPoolManager}
 *
 * @author wx
 */
public class DBHelper {
    com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource ds;
    Connection conn;
    Driver driver;
    static DBHelper db = null;
    String conn_string;

    int maxConnections = 20;
    int timeout = 60;
    private Config cfg;
    // ThreadLocal<Connection> threadLocal;

    public static void closeConnection(Connection conn) {
        try {
            if (conn != null)
                conn.close();
        } catch (SQLException sqle) {
            sqle.printStackTrace();
        }
    }

    public PooledConnection getPooledConnection() throws RuntimeException {
        try {
            return ds.getPooledConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection() {
        try {
            if (ds == null) {
                configure(Config.getInstance());
            }
            return ds.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Connection getConnection(String propKey) {
        String conn_string = Config.getInstance().getString(propKey);
        return newConnection(conn_string);
    }

    public static DBHelper getInstance() {
        if (db == null) {
            db = new DBHelper();
            try {
                Class.forName("com.mysql.jdbc.Driver");
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
        return db;
    }

    /**
     * launch a independent database connecton without pool manage.
     *
     * @param conn_string the whole connecton string.
     * @return a connection directly associate with the parameter conn_string.
     */
    public static Connection newConnection(String conn_string) {
        Connection conn = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn = DriverManager.getConnection(conn_string);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (SQLException sqle) {
            sqle.printStackTrace();
            return null;
        } finally {
        }
        return conn;
    }

    private DBHelper() {

    }

    public void configure(Config config) {

        this.cfg = config;
        ds = new com.mysql.jdbc.jdbc2.optional.MysqlConnectionPoolDataSource();
        //ds.setUser(userID);
        String conn_url = cfg.getString("master.db.url", "jdbc:mysql://192.169.10.102:3306/ia?characterEncoding=utf-8&autoReconnect=true&user=root&password=1qaz@WSX?");
        ds.setUrl(conn_url);
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Connection getSQLServerConnection(String propKey) {
        String conn_string = Config.getInstance().getString(propKey);
        if (conn_string == null || conn_string.equals("")) {
            return null;
        }
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
            Connection con = DriverManager.getConnection(conn_string, "JYDB", "JYDB");
            return con;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
