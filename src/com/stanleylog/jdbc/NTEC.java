package com.stanleylog.jdbc;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 *
 *
 * @author Zhiguang Sun
 *
 */
public class NTEC {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try
        {						
			Class.forName("ntong.jdbc.ntsql.NTSqlDriver"); 
			/* 2.0 -- test._conn = DriverManager.getConnection("jdbc:ntsql:192.168.1.218:9507"); */
			Connection conn = DriverManager.getConnection("jdbc:ntsql:root/@192.168.56.240:9507");
			
			ResultSet rs = conn.createStatement().executeQuery("select * from alert_user");
			
			while(rs.next()){
				System.out.println(rs.getString("name"));
			}
//			
//			ResultSet rs = test.executeQuery("select * from test1");	
//			ResultSetMetaData rsmd = rs.getMetaData();
//			int nColCount = rsmd.getColumnCount();
//			for (int i=1; i <= nColCount; i++)
//			{
//				System.out.print(rsmd.getColumnName(i)+"     ");
//			}
//			System.out.print("\n");
//			System.out.println("----------------------------------");
//			
//			while (rs.next()) 
//			{   							
//				for (int i=1; i<=nColCount; i++)
//				{
//					Object obj = rs.getObject(i);
//										
//					if(obj != null)					
//						System.out.print(obj.toString()+"     ");
//					else
//						System.out.print("null" + "     ");						
//				} 
//				System.out.println("");				
//			}
//			rs.close();
														
			conn.close();
        }
        catch (ClassNotFoundException e)
        {
            System.out.println(e.toString());
        }
        catch (SQLException e)
        {
            System.out.println(e.toString());
        }	
	}

}
