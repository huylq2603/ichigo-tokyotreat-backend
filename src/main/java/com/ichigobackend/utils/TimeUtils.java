package com.ichigobackend.utils;

import java.time.LocalDateTime;
import java.util.Calendar;

import javax.xml.bind.DatatypeConverter;

/**
 * 
 * @author huylq
 *
 */

public class TimeUtils {

	public static String timeToISOString(LocalDateTime timeStamp) {
		return timeStamp == null ? null : timeStamp.toString() + ":00Z";
	}

	public static LocalDateTime ISOStringToTime(String timeStamp) throws IllegalArgumentException {
		Calendar calendar = DatatypeConverter.parseDateTime(timeStamp);
		
		return LocalDateTime.ofInstant(calendar.toInstant(), calendar.getTimeZone().toZoneId());
	}
	
	public static LocalDateTime toMidnight(LocalDateTime timeStamp) {
		return timeStamp == null ? null : timeStamp.withHour(0).withMinute(0).withSecond(0).withNano(0);
	}

}
