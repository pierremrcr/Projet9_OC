package com.dummy.myerp.technical.util;

import java.util.Calendar;
import java.util.Date;

public class ConvertDate {

    private ConvertDate(){
        throw new IllegalStateException("Utility Class");
    }

    public static Calendar convertDateToCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }
}
