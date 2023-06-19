package com.mashreq.paymentTracker.utility;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.mashreq.paymentTracker.exception.HandlerException;
import com.mashreq.paymentTracker.exception.ExceptionCodes;

public class DateTimeUtil {

	public static final String SCHEDULE_DATE_FORMAT_DAY = "yyyy-MMM-dd";
	public static final String SCHEDULE_DATE_FORMAT = "yyyy-MMM-dd HH:mm";

	public static String getFormatedDate(String value, String dateFormat) throws HandlerException {
		String formatedDateValue = value;
		// Handle date with quarter format
		if (dateFormat.equalsIgnoreCase("yyyyQ")) {
			formatedDateValue = getFormattedStringFromDateWithQuarter(value);
		} else {
			Date dateFromFormattedString = getDateFromFormattedString(value, SCHEDULE_DATE_FORMAT_DAY);
			formatedDateValue = getFormattedStringFromDate(dateFromFormattedString, dateFormat);
		}
		return formatedDateValue;
	}

	// Handle Date with Quater
	private static String getFormattedStringFromDateWithQuarter(String value) {
		String yearComponent = value.substring(0, 4);
		String quarterComponent = value.substring(4);
		return yearComponent + "-Q" + quarterComponent;
	}

	public static Date getDateFromFormattedString(String dateAsString, String pattern) throws HandlerException {
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat(pattern);
			return dateFormat.parse(dateAsString);
		} catch (ParseException parseException) {
			throw new HandlerException(ExceptionCodes.DATE_PARSE_EXCEPTION, parseException);
		}
	}

	public static String getFormattedStringFromDate(Date date, String format) {
		DateFormat df = new SimpleDateFormat(format);
		return df.format(date);
	}

	public static String getFormattedDate(Date date) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		return sdf.format(date);
	}

}
