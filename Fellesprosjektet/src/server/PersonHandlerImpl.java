package server;

import static calendar.Person.recreatePerson;

import java.io.IOException;
import java.rmi.RemoteException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import calendar.Appointment;
import calendar.Person;
import client.helpers.StoopidSQLException;

public class PersonHandlerImpl extends Handler implements PersonHandler{
	private static AppointmentHandler appHandler;
	
	public static void init(){
		appHandler = new AppointmentHandlerImpl();
	}

	public void createUser(Person person) throws IOException{
		String query = "INSERT INTO User(userId, email, firstname, lastname, department, " +
					"passwordHash, salt) VALUES(?, ?, ?, ?, ?, ?, ?)";
		try {
			PreparedStatement ps = dbEngine.getPreparedStatement(query);
			ps.setLong(1, person.getId());
			ps.setString(2, person.getEmail());
			ps.setString(3, person.getFirstname());
			ps.setString(4, person.getLastname());
			ps.setString(5, person.getDepartment());
			ps.setString(6, person.getPasswordHash());
			ps.setString(7, person.getSalt());
			ps.execute();
		} catch (SQLException e) {
			throw new StoopidSQLException(e);
		}

	}
	
	public void deleteUser(long personId) throws IOException{
		String query = "DELETE FROM User WHERE userId=?";
		dbEngine.update(query, personId);
	}

	public Person getPerson(long personId) throws IOException {
		String query = "SELECT userId, email, firstname, lastname, department, " + 
					"passwordHash, salt FROM User WHERE userId=?";
		PreparedStatement ps = dbEngine.getPreparedStatement(query);
		try {
			ps.setLong(1, personId);
		} catch (SQLException e) {
			throw new StoopidSQLException(e);
		}
		List<Person> persons = getListFromPreparedStatement(ps);
		if (persons.size() == 1){
			return persons.get(0);
		} else {
			throw new IllegalArgumentException("Fant ikke person med den id-en!");
		}
	}
	
	public String getSalt(long userId) throws RemoteException, IOException{
		return dbEngine.getString("SELECT salt FROM User WHERE userId=?", userId);
	}
	
	public String getSalt(String email) throws RemoteException, IOException{
		return dbEngine.getString("SELECT salt FROM User WHERE email=?", email);
	}
	
	public void followOtherPerson(long userId, long otherUserId) throws RemoteException, IOException{
		String query = "INSERT INTO UserCalendars(userId, followsUserId) VALUES(?, ?)";
		dbEngine.update(query, userId, otherUserId);
	}
	
	public List<Appointment> getFollowAppointments(long userId, int weekNum) throws IOException {
		List<Long> ids = getFollowingIds(userId);
		List<Appointment> apps = new ArrayList<Appointment>();
		for (long id: ids) {
			apps.addAll(appHandler.getAllCreated(id, weekNum));
			apps.addAll(appHandler.getAllInvitedInWeek(id, weekNum));
		}
		return apps;
	}
	
	public List<Appointment> getFollowAppointments(long userId) throws IOException {
		List<Long> ids = getFollowingIds(userId);
		List<Appointment> apps = new ArrayList<Appointment>();
		for (long id: ids) {
			apps.addAll(appHandler.getAllByUser(id));
			apps.addAll(appHandler.getAllInvited(id));
		}
		return apps;
	}
	
	private static List<Long> getFollowingIds(long userId) throws IOException{
		String query = "SELECT followsUserId FROM UserCalendars WHERE userId=?";
		List<Long> ids;
		PreparedStatement ps = dbEngine.getPreparedStatement(query);
		try {
			ps.setLong(1, userId);
			ids = dbEngine.getListOfLongs(ps);
		} catch (SQLException e) {
			throw new StoopidSQLException(e);
		}
		return ids;
	}
	
	private static List<Person> getListFromPreparedStatement(PreparedStatement ps) throws IOException {
		List<Person> list = new ArrayList<Person>();
		try {
			ResultSet rs = ps.executeQuery();
			while(rs.next()){
				long id = rs.getLong("userId");
				String firstname = rs.getString("firstname");
				String lastname = rs.getString("lastname");
				String email = rs.getString("email");
				String department = rs.getString("department");
				String passwordHash = rs.getString("passwordHash");
				Person p = recreatePerson(id, firstname, lastname, email, department, passwordHash);
				list.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("FEil i SQL, stoopid!");
		}
		return list;
	}

	@Override
	public boolean isValidEmail(String email) throws RemoteException, IOException {
		String query = "SELECT * FROM User WHERE email=?";
		try {
			PreparedStatement ps = dbEngine.getPreparedStatement(query);
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			return rs.next();
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("Feil i sQL!");
		}
	}

	@Override
	public long getUserIdFromEmail(String email) throws RemoteException, IOException {
		String query = "SELECT userId FROM User WHERE email=?";
		try {
			PreparedStatement ps = dbEngine.getPreparedStatement(query);
			ps.setString(1, email);
			ResultSet rs = ps.executeQuery();
			if (rs.next()){
				return rs.getLong("userId");
			}
			throw new IllegalArgumentException("Ugyldig e-postadresse!");
		} catch (SQLException e) {
			e.printStackTrace();
			throw new IllegalArgumentException("Ugyldig e-postadresse!"); 
		}
	}

	@Override
	public long getUniqueId() throws IOException, RemoteException {
		return dbEngine.getUniqueId();
	}

}