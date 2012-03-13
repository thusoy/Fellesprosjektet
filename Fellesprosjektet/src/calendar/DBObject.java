package calendar;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;


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
	
	public DBObject getAll() throws SQLException{
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
	
	public void save() throws SQLException{
		createTablesIfNecessary();
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
	
	protected void executeQuery(String query){
		
	}
	
}
