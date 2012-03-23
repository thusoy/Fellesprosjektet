package server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import client.helpers.StoopidSQLException;

public class Execute {
	private static long nextId = System.currentTimeMillis();
	private static final String driver = "com.mysql.jdbc.Driver";
	private static final String database = "jdbc:mysql://mysql.stud.ntnu.no/tarjeikl_fp33";
	private static Connection conn = null;
	
	/**
	 * Used internally to initialize the connection to the database and return the statement.
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Statement getStatement() throws IOException {
		setUpConnection();
		try {
			return conn.createStatement();
		} catch (SQLException e){
			throw new RuntimeException("Feil i SQL!");
		}
	}
	
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
		if (conn != null){
			return;
		}
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
	
	public static ResultSet getResultSet(String query) throws IOException {
		setUpConnection();
		Statement st = getStatement();
		try {
			return st.executeQuery(query);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL, stoopid!");
		}
	}
	
	/**
	 * A wrapper for the database calls. Executes the query on the database.
	 * @param query
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void executeUpdate(String query) throws IOException {
		Statement stmt = getStatement();
		try {
			stmt.executeUpdate(query);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL, stoopid!");
		}
	}
	
	/**
	 * A wrapper for the database calls. Returns the first value in the first column of 'query' as
	 * a Date.
	 * @param query
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	@Deprecated
	public static Date executeGetDatetime(String query) throws IOException, SQLException{
		Statement stmt = getStatement();
		ResultSet rs = stmt.executeQuery(query);
		rs.next();
		Timestamp ts = rs.getTimestamp(1);
		return new Date(ts.getTime());
	}
	
	/**
	 * A wrapper for the database calls. Returns the first value in the first column of 'query' as
	 * a Date.
	 * @param query
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	@Deprecated
	public static Date executeGetDate(String query) throws IOException, SQLException{
		Statement stmt = getStatement();
		ResultSet rs = stmt.executeQuery(query);
		rs.next();
		return rs.getDate(1);
	}
	
	/**
	 * A wrapper for the database calls. Returns the first value in the first column of 'query' as
	 * a boolean.
	 * @param query
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	@Deprecated
	public static boolean executeGetBoolean(String query) throws IOException, SQLException{
		Statement stmt = getStatement();
		ResultSet rs = stmt.executeQuery(query);
		rs.next();
		return rs.getBoolean(1);
	}
	
	/**
	 * A wrapper for the database calls. Returns the first value in the first column of 'query' as
	 * an int.
	 * @param query
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	@Deprecated
	public static int executeGetInt(String query) throws IOException, SQLException{
		Statement stmt = getStatement();
		ResultSet rs = stmt.executeQuery(query);
		rs.next();
		return rs.getInt(1);	
	}
	
	/**
	 * A wrapper for the database calls. Returns all the ints returned by the query as 
	 * a List<long>.
	 * @param query
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	@Deprecated
	public static List<Long> executeGetLongList(String query) throws IOException, SQLException{
		Statement stmt = getStatement();
		ResultSet rs = stmt.executeQuery(query);
		List<Long> output = new ArrayList<Long>();
		while(rs.next()){
			output.add(rs.getLong(1));
		}
		return output;
	}
	
	public static void update(String query, long... ids) throws IOException{
		PreparedStatement  ps = Execute.getPreparedStatement(query);
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
	
	/**
	 * A wrapper for the database calls. Returns the first value in the first column of 'query' as
	 * a long.
	 * @param query
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	@Deprecated
	public static long executeGetLong(String query) throws IOException{
		Statement stmt = getStatement();
		ResultSet rs;
		try {
			rs = stmt.executeQuery(query);
			rs.next();
			return rs.getLong(1);
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL!");
		}
	}
	
	/**
	 * A wrapper for the database calls. Returns the first value in the first column of 'query' as
	 * a String.
	 * @param query
	 * @throws IOException
	 */
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
	
	public static String getString(String query, String field) throws IOException{
		PreparedStatement ps = getPreparedStatement(query);
		try {
			ps.setString(1, field);
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
