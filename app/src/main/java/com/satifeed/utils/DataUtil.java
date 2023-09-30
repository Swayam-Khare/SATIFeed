package com.satifeed.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DataUtil {

    public static String formatDate(String file) {
        String timeInMili = file.split("_")[1];
        Date dateObject = new Date(Long.parseLong(timeInMili));

        SimpleDateFormat dateFormat = new SimpleDateFormat("LLL dd, yyyy");
        return dateFormat.format(dateObject);
    }

    public static String formatTime(String file) {
        String timeInMili = file.split("_")[1];
        Date dateObject = new Date(Long.parseLong(timeInMili));

        SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
        return timeFormat.format(dateObject);
    }

}
