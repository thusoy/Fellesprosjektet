package client.helpers;

import java.sql.SQLException;

public 	class StoopidSQLException extends RuntimeException{
	private static final long serialVersionUID = 1445085698672077805L;

	public StoopidSQLException(SQLException e) {
		super("Feil i SQL, stoopid!", e.getCause());
		e.printStackTrace();
	}
	
}
