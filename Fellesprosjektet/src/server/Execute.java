package server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import client.helpers.StoopidSQLException;

public class Execute {
	private static long nextId = System.currentTimeMillis();
	private static final String driver = "com.mysql.jdbc.Driver";
	private static final String database = "jdbc:mysql://mysql.stud.ntnu.no/tarjeikl_fp33";
	private static Connection conn = null;
	
	public static long getUniqueId(){
		return nextId++;
	}
	
	public static PreparedStatement getPreparedStatement(String query) throws IOException{
		setUpConnection();
		try {
			return conn.prepareStatement(query);
		} catch (SQLException e) {
			throw new StoopidSQLException(e);
		}
	}
	
	private static void setUpConnection() throws IOException{
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e2) {
			throw new RuntimeException("Fant ikke SQL-drivere!");
		}
		if (conn == null){
			int tries = 3;
			while(tries > 0){
				try {
					conn = DriverManager.getConnection(database, "tarjeikl_fpuser", "bruker");
					break;
				} catch (SQLException e) {
					tries--;
					System.out.println("klarte ikke koble til, prøver igjen...");
					try {
						Thread.sleep(100);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
				}
			}
		}
	}
	
	public static boolean getBoolean(PreparedStatement ps) throws IOException{
		try {
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				return rs.getBoolean(1);
			} else {
				throw new IllegalArgumentException("Empty ResultSet!");
			}
		} catch (SQLException e) {
			throw new StoopidSQLException(e);
		}
	}
	
	public static void update(String query, long... ids) throws IOException{
		PreparedStatement  ps = getPreparedStatement(query);
		update(ps, ids);
	}
	
	public static void update(PreparedStatement ps, long... ids) throws IOException{
		try {
			for(int i = 0; i < ids.length; i++){
				ps.setLong(i + 1, ids[i]);
			}
			ps.executeUpdate();
		} catch (SQLException e) {
			throw new StoopidSQLException(e);
		}
	}
	
	public static ResultSet getResultSet(String query, long... ids) throws IOException{
		try {
			PreparedStatement ps = getPreparedStatement(query);
			for(int i = 0; i < ids.length; i++){
				ps.setLong(i + 1, ids[i]);
			}
			return ps.executeQuery();
		} catch (SQLException e) {
			throw new StoopidSQLException(e);
		}
	}
	
	public static List<Long> getListOfLongs(PreparedStatement ps) throws IOException {
		try {
			ResultSet rs = ps.executeQuery();
			List<Long> output = new ArrayList<Long>();
			while(rs.next()){
				output.add(rs.getLong(1));
			}
			return output;
		} catch (SQLException e) {
			throw new StoopidSQLException(e);
		}
	}
	
	public static String getString(String query) throws IOException{
		PreparedStatement ps = getPreparedStatement(query);
		return getString(ps);
	}
	
	public static String getString(String query, long id) throws IOException{
		PreparedStatement ps = getPreparedStatement(query);
		try {
			ps.setLong(1, id);
			return getString(ps);
		} catch (SQLException e) {
			throw new StoopidSQLException(e);
		}
	} 
	
	private static String getString(PreparedStatement ps){
		try{
			ResultSet rs = ps.executeQuery();
			if (rs.next())
				return rs.getString(1);
			else
				throw new IllegalArgumentException("Empty resultset!");
		} catch (SQLException e) {
			throw new StoopidSQLException(e);
		}
	}
	
	public static int getInt(String query, String field) throws IOException{
		PreparedStatement ps = getPreparedStatement(query);
		try {
			ps.setString(1, field);
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				return rs.getInt(1);
			} else {
				throw new IllegalArgumentException("Empty resultset!");
			}
		} catch (SQLException e) {
			throw new StoopidSQLException(e);
		}
	} 
	
}
