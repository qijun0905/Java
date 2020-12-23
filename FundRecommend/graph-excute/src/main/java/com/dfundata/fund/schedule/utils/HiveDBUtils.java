package com.dfundata.fund.schedule.utils;

import com.alibaba.fastjson.JSONArray;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ly
 * @date 2020-02-24 13:40
 * @description
 */
@Component
public class HiveDBUtils {
    private Logger logger = LoggerFactory.getLogger(HiveDBUtils.class);

    @Value("#{'${hive.params}'.split(',')}")
    private List<String> hiveParams;

    @Value("${hive.time_out}")
    private  int TIMEOUT_SECONDS;
    @Value("${hive.driver-class-name}")
    private String dirverName;
    @Value("${hive.jdbcurl}")
    private String url;
    @Value("${hive.username}")
    private String userName;
    @Value("${hive.password}")
    private String password;
    @Value("${hive.user}")
    private String user;
    @Value("${hive.path}")
    private String path;
    @Value("${hadoop.security.authentication}")
    private String Kerberos;
    @Value("${java.security.krb5.conf}")
    private String krb5ConfPath;

    private Connection getConnection() throws IOException {
        logger.info("url: " + url);
        logger.info("user: " + user);
        logger.info("path: " + path);
        logger.info("userName: " + userName);
        logger.info("password: " + password);
        logger.info("Kerberos: " + Kerberos);
        logger.info("krb5ConfPath: " + krb5ConfPath);
        /**
         * 使用hadoop 安全登录
         */
//        org.apache.hadoop.conf.Configuration conf = new org.apache.hadoop.conf.Configuration();
//        conf.set("hadoop.security.authentication", Kerberos);
//        System.setProperty("java.security.krb5.conf", krb5ConfPath);
//        UserGroupInformation.setConfiguration(conf);
//        UserGroupInformation.loginUserFromKeytab(user, path);

        java.sql.Connection connection = null;
        try {
            Class.forName(dirverName);
            DriverManager.setLoginTimeout(TIMEOUT_SECONDS);
            connection = DriverManager.getConnection(url,userName,password);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }


    public void executeSql(String sql) throws SQLException, IOException {
        executeSql(sql,null);
    }

    public void executeSql(String sql, JSONArray ars) throws SQLException, IOException {
        logger.info("execute sql {} ",sql);
        Connection conn = getConnection();
        PreparedStatement pstm = conn.prepareStatement("");
        if(hiveParams != null && hiveParams.size() > 0){
            for (String hiveParam : hiveParams) {
                if("".equals(hiveParam)){
                    continue;
                }
                pstm.execute(hiveParam);
                logger.info("execute sql param {} ",hiveParam);
            }
        }
        pstm = conn.prepareStatement(sql);
        if(ars != null){
            for (int i = 0; i < ars.size(); i++) {
                pstm.setObject(i + 1,ars.get(i));
            }
        }
        pstm.execute();
        closeAll(conn,pstm,null);
    }

    public Long getResult(String sql) throws SQLException, IOException {
        logger.info("execute sql {} ",sql);
        Connection conn = getConnection();
        PreparedStatement pstm = conn.prepareStatement(sql);
        ResultSet rt = pstm.executeQuery();
        while (rt.next()){
            logger.info("get result {}" ,rt.getObject(1));
            return  rt.getLong(1);
        }
        closeAll(conn,pstm,rt);
        return null;
    }


    public void batchInsertSql(String sql,List<String> datas) throws IOException, SQLException {
        logger.info("execute sql {} ",sql);
        Connection conn = getConnection();
        PreparedStatement pstm = null;
        for (String data : datas) {
            pstm = conn.prepareStatement(sql);
            pstm.setObject(1,data);
            pstm.setObject(2,data);
            pstm.setObject(3,"2020-09-09");
            pstm.execute();
        }
    }


    /**
     * 关闭连接
     */
    private void closeAll(Connection conn, Statement stm, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
                logger.info("close  ResultSet");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (stm != null) {
            try {
                stm.close();
                logger.info("close  Statement");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (conn != null) {
            try {
                conn.close();
                logger.info("close  Connection");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
