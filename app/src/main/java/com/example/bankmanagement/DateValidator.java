package com.example.bankmanagement;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateValidator {

    private static final String DATE_PATTERN = "dd/MM/yyyy";
    private static SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_PATTERN);

    public static boolean isValid(String date) {
        try {
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }

    public static boolean isAbove18(String date) {
        try {
            Date dob = dateFormat.parse(date);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(dob);
            calendar.add(Calendar.YEAR, 18);

            Date today = new Date();
            return today.after(calendar.getTime());
        } catch (ParseException e) {
            return false;
        }
    }
}

