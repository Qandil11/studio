/**
 * 
 */
package de.stamm.core;

import java.sql.Date;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;


/**
 * @author Patrick Stamm
 *
 */
public class Formats {
	
	public static final String getPriceFormatted(double price) {
		DecimalFormat df = (DecimalFormat)NumberFormat.getInstance(Locale.GERMAN);
		df.applyPattern( "#,###,##0.00" );
		return df.format( price );
	}

	public static Double getPriceRounded(double price) {
		price = price * 100;
		price = Math.round(price);
		price = price / 100;
		return price;
	}
	
	public static final String getSqlDateFormatted(String date) {
		if (date == null) {
			return "";
		}
		if (date.equals("")) {
			return "";
		}
		String[] tmpDate = date.split("[-]");
		return tmpDate[2]+"."+tmpDate[1]+"."+tmpDate[0];
	}
	
	public static final String getSqlDateTimeFormatted(String date) {
		if (date == null) {
			return "";
		}
		if (date == "") {
			return "";
		}
		String[] tmpDateTime = date.split(" ");
		return getSqlDateFormatted(tmpDateTime[0]) + " " + getSqlTimeFormatted(tmpDateTime[1]);
	}
	
	public static final String getSqlTimeFormatted(String time) {
		if (time == null) {
			return "";
		}
		if (time.equals("")) {
			return "";
		}
		String[] tmpTime = time.split("[:]");
		return tmpTime[0]+":"+tmpTime[1];
	}

	public static final Calendar getDateTimeFromSqlDatetime(String datetime) {
		if (datetime.equals("")) {
			return null;
		}
		
		String[] mainDate = datetime.split(" ");
		String[] tmpDate = mainDate[0].split("[-]");
		String[] tmpTime = mainDate[1].split("[:]");
		Calendar calendar = Calendar.getInstance();
		calendar.set(Integer.valueOf(tmpDate[0]), (Integer.valueOf(tmpDate[1]) - 1), Integer.valueOf(tmpDate[2]), 
				Integer.valueOf(tmpTime[0]), Integer.valueOf(tmpTime[1]), Integer.valueOf(tmpTime[2]));
		return calendar;
	}
	
	public static final String getSqlDatetimeFromDate(Calendar calendar) {
		if (calendar == null) return "";

		Date myDate = new Date(calendar.getTimeInMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(myDate);
	}
	
	public static final String getSqlDatetimeFormatted(String datetime) {
		Calendar datetime_formatted = getDateTimeFromSqlDatetime(datetime);

		Date myDate = new Date(datetime_formatted.getTimeInMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        return sdf.format(myDate);
	}
	
	public static String generateRandomString(int length, String mode) throws Exception {

		StringBuffer buffer = new StringBuffer();
		String characters = "";

		if (mode.equals("alpha")) characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
		else if (mode.equals("alphanumeric")) characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		else if (mode.equals("numeric")) characters = "1234567890";
		else characters = "1234567890";
		
		int charactersLength = characters.length();

		for (int i = 0; i < length; i++) {
			double index = Math.random() * charactersLength;
			buffer.append(characters.charAt((int) index));
		}
		return buffer.toString();
	}

	/*
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss"); //if 24 hour format
		//long currentTime = System.currentTimeMillis();
		
		//System.out.println("DATUM " + format.format(myDate));
		//long currentTime = myDate.getTime();
		 
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Calendar cal = Calendar.getInstance();
*/	
	
	public static boolean isCurrentTimeInIntervall(String daily_start_time,
			String daily_end_time) {
		try {
			Calendar calendar = Calendar.getInstance();
			Date myDate = new Date(calendar.getTimeInMillis());
			String current_Time = new SimpleDateFormat("HH:mm:ss").format(myDate);


			int currentTime = Integer.valueOf(current_Time.replace(":", ""));
			int startTime = Integer.valueOf(daily_start_time.replace(":", ""));
			int endTime = Integer.valueOf(daily_end_time.replace(":", ""));
			
			//System.out.println("TIME: " + currentTime + " - " + startTime + " - " + endTime);
			
			if (currentTime > startTime && currentTime < endTime) {
				return true;
			}
			
		} catch (Exception e) {
			return false;
		}
		return false;
	}

	public static boolean isCurrentDateInIntervall(String start_date,
			String end_date) {
		try {
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			java.util.Date startDate = format.parse(start_date + " 00:00:00");
			java.util.Date endDate = format.parse(end_date + " 23:59:59");

			Calendar calendar = Calendar.getInstance();
			java.util.Date currentDate = new Date(calendar.getTimeInMillis());

			//System.out.println("Date: " + format.format(currentDate) + " - " + format.format(startDate) + " - " + format.format(endDate));
			
			if (currentDate.compareTo(startDate) > 0 && currentDate.compareTo(endDate) < 0) {
				return true;
			}
			
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
