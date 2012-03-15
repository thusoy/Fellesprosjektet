package calendar;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import server.AwesomeXml;
import client.ClientController;


public class DBObject<T> {
	private String dbTableName;
	private String db = "jdbc:mysql://mysql.stud.ntnu.no/tarjeikl_fp33";
	private Connection con;
	private Long id;
	public static final int PORT = 1337; 
	
	public DBObject(){
		String className = this.getClass().getName();
		this.dbTableName = className;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public DBObject(String dbTableName){
		this();
		this.dbTableName = dbTableName;
	}
	
	/**
	 * Genererer et 
	 * @return
	 * @throws SQLException
	 */
	public List<T> all() throws SQLException{
//		con = DriverManager.getConnection(db, "tarjeikl_fpuser", "bruker");
//		Statement stmt = con.createStatement();
//		String query = String.format("SELECT * FROM %s", dbTableName);
//		ResultSet rs = stmt.executeQuery(query);
//		while(rs.next()){
//			System.out.println(rs.getString(2));
//		}
//		con.close();
		return null;
	}
	
	/**
	 * Returnerer id-en til objektet.
	 * Et databaseobjekts id vil ikke være satt før det er lagret for første gang 
	 * med save()-metoden. 
	 * @return
	 */
	public long getId(){
		if (id == null){
			throw new IllegalStateException("Must call save() to recieve an id!");
		}
		return id;
	}
	
	public void save() {
		long dbId = sendObjectToServerAndGetId();
		this.id = dbId;
	}
	
	/**
	 * Create xml of the object, and send it to the server.
	 * @param obj
	 */
	protected long sendObjectToServerAndGetId(){
		long id = ClientController.saveObjectAndGetId(this);
//		client.
		return id;
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
