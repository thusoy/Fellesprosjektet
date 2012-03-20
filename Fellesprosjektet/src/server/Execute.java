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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Execute {
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
	
	public static PreparedStatement getPreparedStatement(String query) throws SQLException, IOException{
		setUpConnection();
		return conn.prepareStatement(query);
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
				System.out.println("kobler opp mot db...");
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
	 * @return
	 * @throws ClassNotFoundException
	 * @throws IOException
	 * @throws SQLException
	 */
	public static String executeGetString(String query) throws IOException{
		Statement stmt = getStatement();
		ResultSet rs;
		try {
			rs = stmt.executeQuery(query);
			if (rs.next())
				return rs.getString(1);
			else
				throw new RuntimeException("Empty resultset!");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i SQL");
		}
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
	public static Map<Long, Boolean> executeGetHashMap(String query) throws IOException, SQLException{
		Statement stmt = getStatement();
		ResultSet rs = stmt.executeQuery(query);
		Map<Long, Boolean> output = new HashMap<Long, Boolean>();
		while(rs.next()){
			output.put(rs.getLong(1), rs.getBoolean(2));	
		}
		return output;
	}
}
