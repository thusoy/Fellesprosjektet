package no.ntnu.fp.model;

import hashtools.Hash;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

import server.PersonHandler;

/**
 * The <code>Person</code> class stores information about a single person.
 * 
 * @author Thomas &Oslash;sterlie
 *
 * @version $Revision: 1.5 $ - $Date: 2005/02/20 14:52:29 $
 */
public class Person extends calendar.DBObject{

	/**
	 * This member variable holds the person's name.
	 */
	private String firstname;
	private String lastname;
	/**
	 * This member variable holds the person's email address.
	 */
	private String email;

	/**
	 * This member variable holds the person's date of birth.
	 */
	//private Date dateOfBirth;

	/**
	 * This member variable holds a unique identifier for this object.
	 */
	private long id;
	private long personalCalendarId;

	private String department;
	private String passwordHash;

	/**
	 * This member variable provides functionality for notifying of changes to
	 * the <code>Group</code> class.
	 */
	private PropertyChangeSupport propChangeSupp;

	/**
	 * Constant used when calling 
	 * {@link java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)}
	 * on {@linkplain #addPropertyChangeListener(java.beans.PropertyChangeListener) registered
	 * <code>PropertyChangeListener<code> objects} when the person's name is changed.
	 * 
	 * @see #setName(String) the setName(String) method
	 */
	public final static String FIRSTNAME_PROPERTY_NAME = "fname";
	public final static String LASTNAME_PROPERTY_NAME = "lname";
	/**
	 * Constant used when calling 
	 * {@link java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)}
	 * on {@linkplain #addPropertyChangeListener(java.beans.PropertyChangeListener) registered
	 * <code>PropertyChangeListener<code> objects} when the person's email address is changed.
	 * 
	 * @see #setEmail(String) the setEmail(String) method
	 */
	public final static String EMAIL_PROPERTY_NAME = "email";

	/**
	 * Constant used when calling 
	 * {@link java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)}
	 * on {@linkplain #addPropertyChangeListener(java.beans.PropertyChangeListener) registered
	 * <code>PropertyChangeListener<code> objects} when the person's date of birth is changed.
	 * 
	 * @see #setEmail(String) the setDateOfBirth(java.util.Date) method
	 */
	//public final static String DATEOFBIRTH_PROPERTY_NAME = "dateOfBirth";

	public final static String DEPARTMENT_PROPERTY_NAME = "department";
	public final static String PASSWORDHASH_PROPERTY_NAME = "password";
	/**
	 * Default constructor. Must be called to initialize the object's member variables.
	 * The constructor sets the name and email of this person to empty
	 * {@link java.lang.String}, while the date of birth is given today's date. The 
	 * {@linkplain #getId() id field} is set to current time when the object is created.
	 */
	public Person() {
		firstname = "";
		lastname = "";
		email = "";
		passwordHash = "";
		department = "";
//		dateOfBirth = new Date();
		id = System.currentTimeMillis();
		propChangeSupp = new PropertyChangeSupport(this);
	}

	/**
	 * Constructs a new <code>Person</code> object with specified name, email, and date
	 * of birth.
	 * 
	 * @param name The name of the person.
	 * @param email The person's e-mail address
	 * @param dateOfBirth The person's date of birth.
	 * @throws IOException 
	 */
	public Person(String firstname, String lastname, String email, String department, String password, boolean recreation) throws IOException {		
		this();
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
		String passwordHash = setPasswordHash(password);
		this.passwordHash = passwordHash;
//		this.dateOfBirth = dateOfBirth;
		this.department = department;
		id = System.currentTimeMillis();
		this.personalCalendarId = id;
		if(!recreation) {
			PersonHandler.createUser(this);
		}
	}

	public String setPasswordHash(String password) {
		String salt = getSalt();
		String bytes = Hash.SHA512(password + salt);
		return bytes;
	}

	private String getSalt(){
		return Long.toString(System.currentTimeMillis());
	}
	/**
	 * Assigns a new name to the person.<P>
	 * 
	 * Calling this method will invoke the 
	 * <code>propertyChange(java.beans.PropertyChangeEvent)</code> method on 
	 * all {@linkplain
	 * #addPropertyChangeListener(java.beans.PropertyChangeListener) registered
	 * <code>PropertyChangeListener<code> objecs}.  The {@link java.beans.PropertyChangeEvent}
	 * passed with the {@link java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)}
	 * method has the following characteristics:
	 * 
	 * <ul>
	 * <li>the <code>getNewValue()</code> method returns a {@link java.lang.String} 
	 * with the newly assigned name</li>
	 * <li>the <code>getOldValue()</code> method returns a {@link java.lang.String} 
	 * with the person's old name</li>
	 * <li>the <code>getPropertyName()</code> method returns a {@link java.lang.String} 
	 * with the value {@link #NAME_PROPERTY_NAME}.</li>
	 * <li>the <code>getSource()</code> method returns this {@link Person} object
	 * </ul>
	 * 
	 * @param name The person's new name.
	 *
	 * @see java.beans.PropertyChangeListener <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/beans/PropertyChangeListener.html">java.beans.PropertyChangeListener</a>
	 * @see java.beans.PropertyChangeEvent <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/beans/PropertyChangeEvent.html">java.beans.PropertyChangeEvent</a>
	 */
	public void setFirstname(String firstname) {
		String oldName = this.firstname;
		this.firstname = firstname;
		PropertyChangeEvent event = new PropertyChangeEvent(this, FIRSTNAME_PROPERTY_NAME, oldName, firstname);
		propChangeSupp.firePropertyChange(event);
	}
	public void setLastname(String lastname) {
		String oldName = this.lastname;
		this.lastname = lastname;
		PropertyChangeEvent event = new PropertyChangeEvent(this, LASTNAME_PROPERTY_NAME, oldName, lastname);
		propChangeSupp.firePropertyChange(event);
	}

	/**
	 * Assigns a new email address to the person.<P>
	 * 
	 * Calling this method will invoke the 
	 * <code>propertyChange(java.beans.PropertyChangeEvent)</code> method on 
	 * all {@linkplain
	 * #addPropertyChangeListener(java.beans.PropertyChangeListener) registered
	 * <code>PropertyChangeListener<code> objecs}.  The {@link java.beans.PropertyChangeEvent}
	 * passed with the {@link java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)}
	 * method has the following characteristics:
	 * 
	 * <ul>
	 * <li>the <code>getNewValue()</code> method returns a {@link java.lang.String} 
	 * with the newly assigned email address</li>
	 * <li>the <code>getOldValue()</code> method returns a {@link java.lang.String} 
	 * with the person's old email address</li>
	 * <li>the <code>getPropertyName()</code> method returns a {@link java.lang.String} 
	 * with the value {@link #EMAIL_PROPERTY_NAME}.</li>
	 * <li>the <code>getSource()</code> method returns this {@link Person} object
	 * </ul>
	 * 
	 * @param email The person's new email address.
	 *
	 * @see java.beans.PropertyChangeListener <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/beans/PropertyChangeListener.html">java.beans.PropertyChangeListener</a>
	 * @see java.beans.PropertyChangeEvent <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/beans/PropertyChangeEvent.html">java.beans.PropertyChangeEvent</a>
	 */
	public void setEmail(String email) {
		String oldEmail = this.email;
		this.email = email;
		PropertyChangeEvent event = new PropertyChangeEvent(this, EMAIL_PROPERTY_NAME, oldEmail, this.email);
		propChangeSupp.firePropertyChange(event);
	}

	/**
	 * Assigns a new date of birth to the person.<P>
	 * 
	 * Calling this method will invoke the 
	 * <code>propertyChange(java.beans.PropertyChangeEvent)</code> method on 
	 * all {@linkplain
	 * #addPropertyChangeListener(java.beans.PropertyChangeListener) registered
	 * <code>PropertyChangeListener<code> objecs}.  The {@link java.beans.PropertyChangeEvent}
	 * passed with the {@link java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)}
	 * method has the following characteristics:
	 * 
	 * <ul>
	 * <li>the <code>getNewValue()</code> method returns a {@link java.util.Date} 
	 * with the newly assigned date of birth</li>
	 * <li>the <code>getOldValue()</code> method returns a {@link java.util.Date} 
	 * with the person's old date of birth</li>
	 * <li>the <code>getPropertyName()</code> method returns a {@link java.lang.String} 
	 * with the value {@link #DATEOFBIRTH_PROPERTY_NAME}.</li>
	 * <li>the <code>getSource()</code> method returns this {@link Person} object
	 * </ul>
	 * 
	 * @param dateOfBirth The person's new date of birth.
	 * 
	 * @see java.beans.PropertyChangeListener <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/util/Date.html">java.util.Date</a>
	 * @see java.beans.PropertyChangeListener <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/beans/PropertyChangeListener.html">java.beans.PropertyChangeListener</a>
	 * @see java.beans.PropertyChangeEvent <a href="http://java.sun.com/j2se/1.4.2/docs/api/java/beans/PropertyChangeEvent.html">java.beans.PropertyChangeEvent</a>
	 */	
//	public void setDateOfBirth(Date dateOfBirth) {
//		Date oldDateOfBirth = this.dateOfBirth;
//		this.dateOfBirth = dateOfBirth;
//		PropertyChangeEvent event = new PropertyChangeEvent(this, DATEOFBIRTH_PROPERTY_NAME, oldDateOfBirth, this.dateOfBirth);
//		propChangeSupp.firePropertyChange(event);
//	}
	public void setDepartment(String department) {
		String oldDepartment = this.department;
		this.department = department;
		PropertyChangeEvent event = new PropertyChangeEvent(this, DEPARTMENT_PROPERTY_NAME, oldDepartment, this.department);
		propChangeSupp.firePropertyChange(event);
	}
	public void setId(long id) {
		this.id = id;
	}
	public void setPCalendarId(long id) {
		this.personalCalendarId = id;
	}

	/**
	 * Returns the person's name.
	 * 
	 * @return The person's name.
	 */
	public String getFirstname() {
		return firstname;
	}
	public String getLastname() {
		return lastname;
	}

	/**
	 * Returns the person's email address.
	 * 
	 * @return The person's email address.
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Returns the person's date of birth.
	 * 
	 * @return The person's date of birth.
	 */
//	public Date getDateOfBirth() {
//		return dateOfBirth;
//	}

	/**
	 * Returns this object's unique identification.
	 * 
	 * @return The person's unique identification.
	 */
	public long getId() {
		return id;
	}

	public String getDepartment() {
		return department;
	}

	public String getPasswordHash() {
		return passwordHash;
	}
	public long getPCalendarId() {
		return personalCalendarId;
	}
	/**
	 * Add a {@link java.beans.PropertyChangeListener} to the listener list.
	 * 
	 * @param listener The {@link java.beans.PropertyChangeListener} to be added.
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propChangeSupp.addPropertyChangeListener(listener);
	}

	/**
	 * Remove a {@link java.beans.PropertyChangeListener} from the listener list.
	 * 
	 * @param listener The {@link java.beans.PropertyChangeListener} to be removed.
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propChangeSupp.removePropertyChangeListener(listener);
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
		result = prime * result
				+ (int) (personalCalendarId ^ (personalCalendarId >>> 32));
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
		if (personalCalendarId != other.personalCalendarId)
			return false;
		return true;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		String s = "Firstname: " + getFirstname() + "; ";
		s += "Lastname: " + getLastname() + "; ";
		s += "Email: " + getEmail() + "; ";
		s += "Department: " + getDepartment() + "; ";
//		s += "Date of birth: " + getDateOfBirth().toString();
		return s;
	}
}