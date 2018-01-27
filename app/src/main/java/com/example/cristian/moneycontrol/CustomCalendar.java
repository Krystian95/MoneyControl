package com.example.cristian.moneycontrol;

import android.util.Log;

import org.dmfs.rfc5545.DateTime;
import org.dmfs.rfc5545.recur.InvalidRecurrenceRuleException;
import org.dmfs.rfc5545.recur.RecurrenceRule;
import org.dmfs.rfc5545.recur.RecurrenceRuleIterator;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Cristian on 04/01/2018.
 */

public class CustomCalendar {

    public Map getDateTime() {

        DateFormat dateFormatDayNumber = new SimpleDateFormat("d");
        DateFormat dateFormatDayLetter = new SimpleDateFormat("EEEE");
        DateFormat dateFormatMonthYear = new SimpleDateFormat("MMMM yyyy");

        Date todayDate = new Date();

        Map<String, String> date = new HashMap<>();
        date.put("day_number", dateFormatDayNumber.format(todayDate));
        date.put("day_letter", dateFormatDayLetter.format(todayDate));

        String text = dateFormatMonthYear.format(todayDate);
        text = String.valueOf(text.charAt(0)).toUpperCase() + text.subSequence(1, text.length());

        date.put("month_year", text);

        return date;
    }

    /*
    Convert local format (dd/mm/yyyy) to (yyyy-mm-dd)
     */
    public String convertToDateFormat(String date) {
        String[] splitted = date.split("/");
        return splitted[2] + "-" + splitted[1] + "-" + splitted[0];
    }

    /*
    Convert local format (yyyy-mm-dd) to (dd/mm/yyyy)
     */
    public String convertToLocalFormat(String date) {
        String[] splitted = date.split("-");
        return splitted[2] + "/" + splitted[1] + "/" + splitted[0];
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ITALY);
        cal.setTimeInMillis(time);
        String date = android.text.format.DateFormat.format("dd-MM-yyyy HH:mm", cal).toString();
        return date;
    }

    /*
    Return date time in milliseconds
     */
    private long dateTimeToMillis(String date, String time) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy hh:mm");
        Date strDate = null;
        try {
            strDate = sdf.parse(date + " " + time);
            return strDate.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return 0;
    }

    /*
    Return TRUE if date_2 happens next to date_1
     */
    public boolean compareDate(Date date_1, Date date_2) {

        if (date_1.after(date_2)) {
            return false;
        } else {
            return true;
        }
    }

    /*
    Return true if the event has an occurrence in the future starting now. False otherwise.
     */
    public boolean eventHasFutureOccurrences(String rRule, String start_date, String start_time) {
        RecurrenceRule rule = null;
        try {
            rule = new RecurrenceRule(rRule);
        } catch (InvalidRecurrenceRuleException e) {
            e.printStackTrace();
        }

        long startMillis = dateTimeToMillis(start_date, start_time);
        DateTime start = new DateTime(startMillis);

        RecurrenceRuleIterator it = rule.iterator(start);

        int maxInstances = 1000; // limit instances for rules that recur forever

        while (it.hasNext() && (!rule.isInfinite() || maxInstances-- > 0)) {
            DateTime nextInstance = it.nextDateTime();
            //Log.e("Next Event Occurrence", String.vaueOf(getDate(nextInstance.getTimestamp())));
            if (compareDate(new Date(), new Date(nextInstance.getTimestamp()))) {
                //Log.e("COMPARE RESULT", new Date().toString() + " < " + new Date(nextInstance.getTimestamp()).toString());
                return true;
            }
        }

        return false;
    }

    public String getActualDateTimeFormatted() {

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date todayDate = new Date();

        return format.format(todayDate);
    }
}
