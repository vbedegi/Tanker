package com.vbedegi.tanker;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static int getElapsedDays(Date date) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        return now.getDate() - date.getDate();
    }

    public static Date now() {
        return Calendar.getInstance().getTime();
    }

    public static String toString(Date date) {
        DateFormat formatter = createFormatter();
        return formatter.format(date);
    }

    public static Date fromSting(String string) throws ParseException {
        DateFormat formatter = createFormatter();
        return formatter.parse(string);
    }

    private static SimpleDateFormat createFormatter() {
        return new SimpleDateFormat("yyyy.MM.dd");
    }
}
