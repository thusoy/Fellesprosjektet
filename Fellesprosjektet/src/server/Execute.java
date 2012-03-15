package server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Execute {
	private static final String driver = "com.mysql.jdbc.Driver";
	private static final String database = "jdbc:mysql://mysql.stud.ntnu.no/tarjeikl_fp33";
	private static Connection conn;
	
	/**
	 * Used internally to initialize the connection to the database and return the statement.
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 */
	private static Statement getStatement() throws IOException{
		try {
			Class.forName(driver);
		} catch (ClassNotFoundException e2) {
			throw new RuntimeException("Fant ikke SQL-drivere!");
		}
		int tries = 3;
		while(tries > 0){
			try {
				conn = DriverManager.getConnection(database, "tarjeikl_fpuser", "bruker");
				Statement stmt = conn.createStatement();
				return stmt;
			} catch (SQLException e) {
				tries--;
				try {
					Thread.sleep(100);
				} catch (InterruptedException e1) {
					e1.printStackTrace();
				}
			}
		}
		throw new IOException("Couldn't connect to database!");
	}
	
	/**
	 * A wrapper for the database calls. Executes the query on the database.
	 * @param query
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static void executeUpdate(String query) throws IOException, SQLException{
		Statement stmt = getStatement();
		stmt.executeUpdate(query);
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
	public static int executeGetInt(String query) throws IOException, SQLException{
		Statement stmt = getStatement();
		ResultSet rs = stmt.executeQuery(query);
		rs.next();
		return rs.getInt(1);	
	}
	
	/**
	 * A wrapper for the database calls. Returns all the ints returned by the query as 
	 * a List<String>.
	 * @param query
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static List<String> executeGetStringList(String query) throws IOException, SQLException{
		Statement stmt = getStatement();
		ResultSet rs = stmt.executeQuery(query);
		List<String> output = new ArrayList<String>();
		while(rs.next()){
			output.add(rs.getString(1));
		}
		return output;
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
	public static long executeGetLong(String query) throws IOException, SQLException{
		Statement stmt = getStatement();
		ResultSet rs = stmt.executeQuery(query);
		rs.next();
		return rs.getLong(1);
	}
	
	/**
	 * A wrapper for the database calls. Returns the first value in the first column of 'query' as
	 * a String.
	 * @param query
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static String executeGetString(String query) throws IOException, SQLException{
		Statement stmt = getStatement();
		ResultSet rs = stmt.executeQuery(query);
		rs.next();
		return rs.getString(1);	
	}
	
	/**
	 * A wrapper for the database calls. Returns a Map<Integer, Boolean> from the query. The query
	 * must return an int in the first column, and a boolean in the second.
	 * @param query
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static Map<Integer, Boolean> executeGetHashMap(String query) throws IOException, SQLException{
		Statement stmt = getStatement();
		ResultSet rs = stmt.executeQuery(query);
		Map<Integer, Boolean> output = new HashMap<Integer, Boolean>();
		while(rs.next()){
			output.put(rs.getInt(1), rs.getBoolean(2));	
		}
		return output;
	}
}
