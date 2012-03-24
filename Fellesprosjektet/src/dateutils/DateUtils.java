package dateutils;

import java.sql.Date;
import java.util.Calendar;

public class DateUtils {

	public static Date getStartOfWeek(int weekNum) {
		Calendar cal = Calendar.getInstance();
		int year = cal.get(Calendar.YEAR);
		cal.clear();
		cal.set(Calendar.WEEK_OF_YEAR, weekNum);
		cal.set(Calendar.YEAR, year);
		Date start = new Date(cal.getTimeInMillis());
		return start;
	}

	public static Date getEndOfWeek(int weekNum) {
		Date startOfWeek = getStartOfWeek(weekNum);
		int aWeekInMs = 7*24*60*60*1000-1;
		Date end = new Date(startOfWeek.getTime() + aWeekInMs);
		return end;
	}
	
	public static Date stripMsFromTime(Date date){
		long original = date.getTime();
		long newTime = (original/1000)*1000;
		return new Date(newTime);
	}
	
	public static int getCurrentWeekNum() {
		Calendar cal = Calendar.getInstance();
		return cal.get(Calendar.WEEK_OF_YEAR);
	}
}
