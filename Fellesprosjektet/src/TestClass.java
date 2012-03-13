import java.sql.SQLException;

import no.ntnu.fp.model.Person;
import calendar.Calendar;


public class TestClass {
	public static void main(String[] args) {
		Person john = new Person();
		try {
			Calendar cal = new Calendar(john);
			cal.save();
//			cal.getAll();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}