package com.pro.managers;

import org.joda.time.DateTime;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.PeriodFormatter;
import org.joda.time.format.PeriodFormatterBuilder;

public class PeriodManager {
	
	public static final String DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
	
	public static String getInterval(String last) {
		String ret = null;
		DateTime current_date = new DateTime();
		DateTime last_date = format(last);
		// PERIOD
		Period period = new Period(last_date, current_date);
		PeriodFormatter pformater = new PeriodFormatterBuilder().
				appendYears().appendSuffix(" an ", " ans ").
				appendMonths().appendSuffix(" mois ").
				appendDays().appendSuffix(" jour ", " jours ").
				appendHours().appendSuffix(" heure ", " heures ").
				appendMinutes().appendSuffix(" minute ", " minutes ").
				appendSeparator(" et ").
				appendSeconds().appendSuffix(" seconde ", " secondes ").
				toFormatter();
		ret = pformater.print(period);
		return ret;
	}
	
	public static DateTime format(String date) {
		DateTime ret = null;
		DateTimeFormatter formater = DateTimeFormat.forPattern(DATE_FORMAT);
		ret = formater.parseDateTime(date);
		return ret;
	}
	
	public static String format(DateTime date) {
		String ret = null;
		DateTimeFormatter formater = DateTimeFormat.forPattern(DATE_FORMAT);
		ret = date.toString(formater);
		return ret;
	}
	
	public static void main(String[] args) {
		DateTime dt = new DateTime();
		System.out.println(format(dt));
		//System.out.println(getInterval("05/11/2019 00:26:32"));
	}
}
