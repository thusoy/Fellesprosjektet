package rmi;

import java.io.IOException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;

public interface DBHandler extends Remote{
	public static final String SERVICE_NAME = "DBEngine";
	
	public boolean getBoolean(PreparedStatement ps) throws RemoteException, IOException;
	
	public long getUniqueId() throws RemoteException;
	
	public PreparedStatement getPreparedStatement(String query) throws RemoteException, IOException;
	
	public void update(String query, long... ids) throws RemoteException, IOException;
	
	public void update(PreparedStatement ps, long... ids) throws RemoteException, IOException;
	
	public ResultSet getResultSet(String query, long... ids) throws RemoteException, IOException;
	
	public List<Long> getListOfLongs(PreparedStatement ps) throws RemoteException, IOException;
	
	public String getString(String query) throws RemoteException, IOException;
	
	public String getString(String query, long id) throws RemoteException, IOException;
	
	public int getInt(String query, String field) throws RemoteException, IOException;
		
}
