package calendar;

import static hashtools.Hash.SHA512;
import static hashtools.Hash.createHash;

import java.io.IOException;
import java.io.Serializable;

import server.PersonHandler;

public class Person extends DBCommunicator implements Serializable{

	private static final long serialVersionUID = 7083773845221209444L;
	private String firstname;
	private String lastname;
	private String email;
	private String department;
	private String passwordHash;
	private String salt;
	private long id;
	private static PersonHandler personHandler;

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
		bindToHandler();
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		this.id = System.currentTimeMillis();
		this.department = department;
		setFirstSalt();
		setPassword(password);
		id = personHandler.getUniqueId();
		personHandler.createUser(this);
	}
	
	private Person(){
	}
	
	public static Person recreatePerson(long id, String firstname, String lastname, String email, String department, String passwordHash) throws IOException{
		bindToHandler();
		Person p = new Person();
		p.setFirstname(firstname);
		p.setLastname(lastname);
		p.setEmail(email);
		p.setDepartment(department);
		p.setPasswordHash(passwordHash);
		p.setId(id);
		String salt = personHandler.getSalt(id);
		p.salt = salt;
		return p;
	}

	/**
	 * Krever at en saltet til en bruker er satt f�rst.
	 * @param password
	 */
	public void setPassword(String password) {
		if (salt == null){
			throw new IllegalStateException("Saltet m� v�re satt f�r man kan sette passord for en bruker!");
		}
		String hash = createHash(password, salt);
		this.passwordHash = hash;
	}
	
	public void followPerson(long otherUserId) throws IOException {
		bindToHandler();
		personHandler.followOtherPerson(id, otherUserId);
	}
	
	/**
	 * OBS! Denne m� KUN brukes til � sette et ferdig hashet passord, ellers vil det lagres i klartekst!
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
	
	public void delete() throws IOException{
		personHandler.deleteUser(id);
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
	
	public String fullName(){
		return String.format("%s %s", firstname, lastname);
	}

	public static void bindToHandler() {
		personHandler = (PersonHandler) getHandler(PersonHandler.SERVICE_NAME);
	}

}
