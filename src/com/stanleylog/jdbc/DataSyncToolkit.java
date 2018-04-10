/**
 * 
 */
package com.stanleylog.jdbc;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

/**
 *  对MSSQL与ORACLE数据中指定表的数据同步工具
 * 
 * @author Zhiguang Sun
 * 
 */
public class DataSyncToolkit {

	Logger logger = Logger.getLogger(DataSyncToolkit.class);
	
	private Connection srcConn;
	private String srcURL = "jdbc:sqlserver://localhost:1433;databaseName=MonitorDataCenter;user=sa;password=sa";
	private String srcClassForName = "com.microsoft.sqlserver.jdbc.SQLServerDriver";


	private Connection destConn;
	private String destURL = "jdbc:oracle:thin:@localhost:1521:orcl";
	private String destClassForName = "oracle.jdbc.driver.OracleDriver";
	private String destUID = "cmbd";
	private String destPWD = "cmbd";
	
	private int daiesPerBatch = 7;
	private int rownumPerCommit = 200;

	public DataSyncToolkit() {
		Properties props = initProperties();
		initConfig(props);
		
		srcConn = getConnection(srcClassForName, srcURL, null, null);
		destConn = getConnection(destClassForName, destURL, destUID, destPWD);

	}
	

	/**
	 * @return
	 */
	private Properties initProperties() {
		Properties props = new Properties();
		InputStream is = null;
		try {
			is = new FileInputStream(new File(System.getProperty("user.dir") + System.getProperty("file.separator") + "config.property"));
			props.load(is);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (is == null)
				try {
					is.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}
		return props;
	}
	
	private void initConfig(Properties props){
		srcURL = props.getProperty("srcURL");
		srcClassForName = props.getProperty("srcClassForName");
		
		destURL = props.getProperty("destURL");
		destClassForName = props.getProperty("destClassForName");
		destUID = props.getProperty("destUID");
		destPWD = props.getProperty("destPWD");
	}
	
	

	private Connection getConnection(String driver, String url, String username, String password) {
		
		try {
			Class.forName(driver);
			if (username == null || password == null) {
				return DriverManager.getConnection(url);
			}
			Connection returnvar = DriverManager.getConnection(url, username, password);
			returnvar.setAutoCommit(false);
			logger.info("..... 初始化连接完成["+ url +"].");
			return returnvar;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			logger.warn(e.getMessage());
		} catch (SQLException e) {
			logger.warn(e.getMessage());
		}
		return null;
	}
	
	

	public ResultSet srcQuery(String sql, List<Object> values) {
		
		try {
			if(values != null){
				PreparedStatement psmts = srcConn.prepareStatement(sql);
				for(int i=1; i<= values.size(); i++){
					psmts.setTimestamp(i, new Timestamp(((Date)values.get(i-1)).getTime()));
				}
				return psmts.executeQuery();
			}
			
			return srcConn.createStatement().executeQuery(sql);

		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	
	public void duplicate(String srcTableName, String destTableName) {
		
		int batchNum = 1;
		Timestamp minDate = null;
		Timestamp maxDate = null; 
		
		ResultSet rs = srcQuery("select min(TXN_DATE), max(TXN_DATE) from " + srcTableName , null);
		
		try {
			rs.next();
			minDate = rs.getTimestamp(1);
			maxDate = rs.getTimestamp(2);
			
		} catch (SQLException e1) {
			logger.warn(e1.getStackTrace());
		}
		
		double diffDaies = (maxDate.getTime() - minDate.getTime()) / (24 * 60 * 60 * 1000);
		logger.info("需要同步"+ diffDaies +"天数据.");
		
		while(diffDaies / daiesPerBatch > 0){
			logger.info("开始同步第"+ batchNum +"批["+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(DateUtils.addDays(minDate, (daiesPerBatch * (batchNum -1)))) + "至" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(DateUtils.addDays(minDate, (daiesPerBatch * batchNum))) +"]的数据...");
			copy(srcTableName, destTableName, DateUtils.addDays(minDate, (daiesPerBatch * (batchNum -1))), DateUtils.addDays(minDate, (daiesPerBatch * batchNum)), rownumPerCommit, batchNum);
			diffDaies -= daiesPerBatch;
			batchNum++;
		}
		logger.info("开始同步第"+ batchNum +"批["+ new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(DateUtils.addDays(minDate, (daiesPerBatch * (batchNum -1)))) + "至" + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new Date()) +"]的数据...");
		copy(srcTableName, destTableName, DateUtils.addDays(minDate, (daiesPerBatch * (batchNum -1))), new Date(), rownumPerCommit, batchNum);
		logger.info("===== " + destTableName + "表复制完成.");
	}
	
	

	/**
	 * @param srcTableName
	 * @param destTableName
	 * @param rownumPerCommit
	 */
	private void copy(String srcTableName, String destTableName,Date minDate, Date maxDate, int rownumPerCommit, int batchNum) {
		ResultSet rs;
		List<Object> values = new ArrayList<Object>();
		values.add(minDate);
		values.add(maxDate);
		rs = srcQuery("select * from " + srcTableName + " where TXN_DATE >= ? and TXN_DATE < ? order by TXN_DATE", values);
		
		int cnt = 0;
		int commit_cnt = 0;
		
		try {
			
			PreparedStatement pstmt = destConn.prepareStatement("insert into " + destTableName + " values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

			while (rs.next()) {
				cnt++;
				ResultSetMetaData  rsmd = rs.getMetaData();					
				for (int i = 1; i <= rsmd.getColumnCount() ; i++) {			
					pstmt.setObject(i, rs.getObject(i));
				}
				
				pstmt.execute();
				logger.info("数据复制进行到: " + rs.getObject(4) + "\t[第"+ batchNum + "批"+ cnt +"条]");
				
				if(commit_cnt++ > rownumPerCommit){
					commit_cnt = 0;
					destConn.commit();	
					logger.info("----- 目标数据库完成批量COMMIT操作.");
				}
			} 
			
		} catch (SQLException e) {
			logger.warn(e.getStackTrace());
		}
	}
	

	

	public static void main(String[] args) {
		DataSyncToolkit tk = new DataSyncToolkit();
		tk.duplicate(args[0], args[1]);
	}


}
