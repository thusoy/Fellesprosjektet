package no.ntnu.fp.model;

import static hashtools.Hash.createHash;
import static hashtools.Hash.SHA512;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.sql.SQLException;

import server.Execute;
import server.PersonHandler;

public class Person {

	private String firstname;
	private String lastname;
	private String email;
	private String department;
	private String passwordHash;
	private String salt;
	private long id;

	/**
	 * Constructs a new Person and saved the object to the database.
	 * @param firstname
	 * @param lastname
	 * @param email
	 * @param department
	 * @param password
	 * @throws IOException
	 */
	public Person(String firstname, String lastname, String email, String department, String password) throws IOException {		
		if (firstname == null || lastname == null || email == null || password == null)
			throw new IllegalArgumentException("A person needs no-null values for firstname, " +
				"lastname, email and password!");
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.id = System.currentTimeMillis();
		this.department = department;
		setFirstSalt();
		setPassword(password);
		PersonHandler.createUser(this);
	}
	
	private Person(){
		
	}
	
	public static Person recreatePerson(long id, String firstname, String lastname, String email, String department, String passwordHash) throws IOException{
		Person p = new Person();
		p.setFirstname(firstname);
		p.setLastname(lastname);
		p.setEmail(email);
		p.setDepartment(department);
		p.setPasswordHash(passwordHash);
		p.setId(id);
		String query = String.format("SELECT salt FROM User WHERE userId=%d", id);
		String salt = Execute.executeGetString(query);
		p.salt = salt;
		return p;
	}

	/**
	 * Krever at en saltet til en bruker er satt først.
	 * @param password
	 */
	public void setPassword(String password) {
		if (salt == null){
			throw new IllegalStateException("Saltet må være satt før man kan sette passord for en bruker!");
		}
		String hash = createHash(password, salt);
		this.passwordHash = hash;
	}
	
	public void followPerson(long otherUserId) throws IOException {
		String query = "INSERT INTO UserCalendars(userId, followsUserId) VALUES(%d, %d)";
		Execute.executeUpdate(String.format(query, id, otherUserId));
	}
	
	/**
	 * OBS! Denne må KUN brukes til å sette et ferdig hashet passord, ellers vil det lagres i klartekst!
	 * @param passwordHash
	 */
	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}
	
	private void setFirstSalt(){
		this.salt =  SHA512(Long.toString(System.currentTimeMillis()));
	}
	
	public String getSalt(){
		return this.salt;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public void setLastname(String lastname) {
		this.lastname = lastname;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setDepartment(String department) {
		this.department = department;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getFirstname() {
		return firstname;
	}
	public String getLastname() {
		return lastname;
	}

	public String getEmail() {
		return email;
	}

	public long getId() {
		return id;
	}

	public String getDepartment() {
		return department;
	}

	public String getPasswordHash() {
		return passwordHash;
	}



	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((department == null) ? 0 : department.hashCode());
		result = prime * result + ((email == null) ? 0 : email.hashCode());
		result = prime * result
				+ ((firstname == null) ? 0 : firstname.hashCode());
		result = prime * result + (int) (id ^ (id >>> 32));
		result = prime * result
				+ ((lastname == null) ? 0 : lastname.hashCode());
		result = prime * result
				+ ((passwordHash == null) ? 0 : passwordHash.hashCode());
		result = prime * result + ((salt == null) ? 0 : salt.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Person other = (Person) obj;
		if (department == null) {
			if (other.department != null)
				return false;
		} else if (!department.equals(other.department))
			return false;
		System.out.println("Never got here!");
		if (email == null) {
			if (other.email != null)
				return false;
		} else if (!email.equals(other.email))
			return false;
		if (firstname == null) {
			if (other.firstname != null)
				return false;
		} else if (!firstname.equals(other.firstname))
			return false;
		if (id != other.id)
			return false;
		if (lastname == null) {
			if (other.lastname != null)
				return false;
		} else if (!lastname.equals(other.lastname))
			return false;
		if (passwordHash == null) {
			if (other.passwordHash != null)
				return false;
		} else if (!passwordHash.equals(other.passwordHash))
			return false;
		if (salt == null) {
			if (other.salt != null)
				return false;
		} else if (!salt.equals(other.salt))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String string = "%s %s (%s)";
		return String.format(string, firstname, lastname, email);
	}

}
