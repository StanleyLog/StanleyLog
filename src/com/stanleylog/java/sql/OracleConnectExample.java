package com.stanleylog.java.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 连接Oracle的示例类
 * 
 * @author Zhiguang Sun
 * 
 */
public class OracleConnectExample {

	private static String _url = "jdbc:oracle:thin:@127.0.0.1:1521:ORCL";
	private static String _username = "stl";
	private static String _passwd = "sun";

	private static Connection _conn = null;
	private static Statement _stmt = null;
	private static ResultSet _rs = null;
	private static String _sql = "select * from tab";

	/**
	 * @param args
	 */
	public static void main(String[] args) {

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			_conn = DriverManager.getConnection(_url, _username, _passwd);
			_stmt = _conn.createStatement();
			_rs = _stmt.executeQuery(_sql);

			while (_rs.next()) {
				System.out.println(_rs.getString(1));
			}

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

}
