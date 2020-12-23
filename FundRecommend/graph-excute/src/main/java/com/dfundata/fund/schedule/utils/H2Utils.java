package com.dfundata.fund.schedule.utils;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * 2020/10/15 2:20 下午
 *
 * @author Seldom
 */
public class H2Utils {

    //数据库连接URL，当前连接的是E:/H2目录下的gacl数据库
    private static final String JDBC_URL = "jdbc:h2:tcp://127.0.0.1/~/test";
    //连接数据库时使用的用户名
    private static final String USER = "sa";
    //连接数据库时使用的密码
    private static final String PASSWORD = "";
    //连接H2数据库时使用的驱动类，org.h2.Driver这个类是由H2数据库自己提供的，在H2数据库的jar包中可以找到
    private static final String DRIVER_CLASS="org.h2.Driver";


    public static Connection getConnection() {

        java.sql.Connection connection = null;
        try {
            Class.forName(DRIVER_CLASS);
//            DriverManager.setLoginTimeout(100);
            connection = DriverManager.getConnection(JDBC_URL,USER,PASSWORD);
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
        return connection;
    }

    public static void  executeSql(String sql) throws Exception {
        Connection conn = getConnection();
        PreparedStatement pstm = conn.prepareStatement(sql);
        boolean execute = pstm.execute();
    }

    public static String  executeQuerySql(String sql) {
        try{
            Connection conn = getConnection();
            PreparedStatement pstm = conn.prepareStatement(sql);
            ResultSet resultSet= pstm.executeQuery();
            while (resultSet.next()) {
                String string = resultSet.getString(1);
                System.out.println(resultSet.getString(1));
                return  string;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


}
