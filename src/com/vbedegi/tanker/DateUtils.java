package com.vbedegi.tanker;

import java.util.Calendar;
import java.util.Date;

public class DateUtils {
    public static int getElapsedDays(Date date) {
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        return now.getDate() - date.getDate();
    }
}
