package server;

import java.sql.Date;

public class RoundTime {
	public static Date roundTime(Date date){
		long original = date.getTime();
		long newTime = (original/1000)*1000;
		return new Date(newTime);
	}
}
