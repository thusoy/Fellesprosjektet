package calendar;

import java.io.IOException;
import java.net.ConnectException;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import no.ntnu.fp.net.co.ConnectionImpl;


public class DBObject {
	private String dbTableName;
	private String db = "jdbc:mysql://mysql.stud.ntnu.no/tarjeikl_fp33";
	private Connection con;
	private String id = "hei og hopp";
	
	public DBObject(){
		String className = this.getClass().getName();
		this.dbTableName = className;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public DBObject all() throws SQLException{
		con = DriverManager.getConnection(db, "tarjeikl_fpuser", "bruker");
		Statement stmt = con.createStatement();
		String query = String.format("SELECT * FROM %s", dbTableName);
		ResultSet rs = stmt.executeQuery(query);
		while(rs.next()){
			System.out.println(rs.getString(2));
		}
		con.close();
		return this;
	}
	
	public void save() {
		sendObjectToServer(this);
	}
	
	/**
	 * Create xml of the object, and send it to the server.
	 * @param obj
	 */
	private void sendObjectToServer(DBObject obj){
		ConnectionImpl a1 = new ConnectionImpl(1337);
		try {
			a1.send(this.toxml());
		} catch (ConnectException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private String toxml(){
		// Generer xml av objektet
		return "";
	}

	private void createTablesIfNecessary() throws SQLException {
		con = DriverManager.getConnection(db, "tarjeikl_fpuser", "bruker");
		DatabaseMetaData metadata = con.getMetaData();
		ResultSet rs = metadata.getTables(null, null, null, new String[]{"TABLE"});
		while(rs.next()){
			System.out.println("First output: " + rs.getString(1));
		}
		con.close();
	}
}
