package com.example.cristian.moneycontrol;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Cristian on 04/01/2018.
 */

public class Calendar {

    public Map getDateTime() {

        DateFormat dateFormatDayNumber = new SimpleDateFormat("d");
        DateFormat dateFormatDayLetter = new SimpleDateFormat("EEEE");
        DateFormat dateFormatMonthYear = new SimpleDateFormat("MMMM yyyy");

        Date todayDate = new Date();

        Map<String, String> date = new HashMap<String, String>();
        date.put("day_number", dateFormatDayNumber.format(todayDate));
        date.put("day_letter", dateFormatDayLetter.format(todayDate));

        String text = dateFormatMonthYear.format(todayDate);
        text = String.valueOf(text.charAt(0)).toUpperCase() + text.subSequence(1, text.length());

        date.put("month_year", text);

        return date;
    }
}
