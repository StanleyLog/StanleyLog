package com.stanleylog.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Created by stanley on 01/11/2017.
 */
public class Oracle {


    public static void main(String[] args) {

    Connection conn;
    String url = "jdbc:oracle:thin:@192.168.56.101:1521/fs10g";
    String classForName = "oracle.jdbc.driver.OracleDriver";
    String username = "cmbd";
    String password = "cmbd";


        try {
            Class.forName(classForName);
            if (username == null || password == null) {
//                return DriverManager.getConnection(url);
            }
            Connection returnvar = DriverManager.getConnection(url, username, password);
            returnvar.setAutoCommit(false);
//            logger.info("..... 初始化连接完成["+ url +"].");
//            return returnvar;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
//            logger.warn(e.getMessage());
        } catch (SQLException e) {
//            logger.warn(e.getMessage());
        }

        //returnvar
    }
}
