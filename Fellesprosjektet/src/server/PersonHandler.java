package server;

import static no.ntnu.fp.model.Person.recreatePerson;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import no.ntnu.fp.model.Person;

public class PersonHandler {

	public static void createUser(Person person) throws IOException{
		String firstname = person.getFirstname();
		String lastname = person.getLastname();
		String email = person.getEmail();
		String department = person.getDepartment();
		String passwordHash = person.getPasswordHash();
		String salt = person.getSalt();
		long userId = person.getId();

		String query =
				"INSERT INTO User(userId, email, firstname, lastname, department, passwordHash, salt) " +
				"VALUES(%d, '%s', '%s', '%s', '%s', '%s', '%s')";

		Execute.executeUpdate(String.format(query, userId, email, firstname, 
				lastname, department, passwordHash, salt));

	}
	public static void updateUser(Person person) throws IOException {
		String firstname = person.getFirstname();
		String lastname = person.getLastname();
		String email = person.getLastname();
		String department = person.getDepartment();
		String passwordHash = person.getPasswordHash();
		

		String query =
				"UPDATE User SET" +
						" email='%s'" +
						" firstname='%s'" +
						" lastname='%s'" +
						" department='%s'" +
						" passwordHash='%s'";

		Execute.executeUpdate(String.format(query, email, firstname, lastname, department, passwordHash));
	}
	
	public static void deleteUser(long personId) throws IOException{
		String query =
				"DELETE FROM User WHERE userId=%d";
		Execute.executeUpdate(String.format(query, personId));
	}

	public static Person getPerson(long personId) throws IOException {
		String query = "SELECT userId, email, firstname, lastname, department, passwordHash, salt FROM User " +
						"WHERE userId=%d";
		ResultSet rs = Execute.getResultSet(String.format(query, personId));
		List<Person> persons = getListFromResultSet(rs);
		if (persons.size() == 1){
			return persons.get(0);
		} else {
			throw new IllegalArgumentException("Fant ikke person med den id-en!");
		}
	}
	
	private static List<Person> getListFromResultSet(ResultSet rs) throws IOException {
		List<Person> list = new ArrayList<Person>();
		try {
			while(rs.next()){
				long id = rs.getLong("userId");
				String firstname = rs.getString("firstname");
				String lastname = rs.getString("lastname");
				String email = rs.getString("email");
				String department = rs.getString("department");
				String passwordHash = rs.getString("passwordHash");
				Person p = recreatePerson(firstname, lastname, email, department, "");
				p.setPasswordHash(passwordHash);
				p.setId(id);
				list.add(p);
			}
		} catch (SQLException e) {
			e.printStackTrace();
			throw new RuntimeException("FEil i SQL, stoopid!");
		}
		return list;
	}

}