package server;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

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
	private static Statement getStatement() throws ClassNotFoundException, IOException{
		Class.forName(driver);
		int tries = 3;
		while(tries > 0){
			try {
				conn = DriverManager.getConnection(database);
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
	public static void executeUpdate(String query) throws ClassNotFoundException, IOException, SQLException{
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
	public static Date executeGetDate(String query) throws ClassNotFoundException, IOException, SQLException{
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
	public static boolean executeGetBoolean(String query) throws ClassNotFoundException, IOException, SQLException{
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
	public static int executeGetInt(String query) throws ClassNotFoundException, IOException, SQLException{
		Statement stmt = getStatement();
		ResultSet rs = stmt.executeQuery(query);
		rs.next();
		return rs.getInt(1);	
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
	public static String executeGetString(String query) throws ClassNotFoundException, IOException, SQLException{
		Statement stmt = getStatement();
		ResultSet rs = stmt.executeQuery(query);
		rs.next();
		return rs.getString(1);	
	}
}
