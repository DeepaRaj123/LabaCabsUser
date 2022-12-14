package com.laba.user.common;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateDifference {

    public static boolean isDateBefore15Days(String date) {

        // SimpleDateFormat converts the
        // string format to date object
//        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy");
        Date d1 = new Date();

        // Try Block
        try {

            // parse method is used to parse
            // the text from a string to
            // produce the date
            Date d2 = sdf.parse(date);
//            Date d2 = sdf.parse(end_date);

            // Calucalte time difference
            // in milliseconds
            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            // Calucalte time difference in
            // seconds, minutes, hours, years,
            // and days
            /*long difference_In_Seconds
                    = (difference_In_Time
                    / 1000)
                    % 60;*/

            /*long difference_In_Minutes
                    = (difference_In_Time
                    / (1000 * 60))
                    % 60;*/

            /*long difference_In_Hours
                    = (difference_In_Time
                    / (1000 * 60 * 60))
                    % 24;*/

            long difference_In_Years
                    = (difference_In_Time
                    / (1000l * 60 * 60 * 24 * 365));

            long difference_In_Days
                    = (difference_In_Time
                    / (1000 * 60 * 60 * 24))
                    % 365;

            if(difference_In_Years > 0){
                return false;
            }

            if(difference_In_Days < 15){
                return true;
            }

            /*// Print the date difference in
            // years, in days, in hours, in
            // minutes, and in seconds

            System.out.print(
                    "Difference "
                            + "between two dates is: ");

            System.out.println(
                    difference_In_Years
                            + " years, "
                            + difference_In_Days
                            + " days, "
                            + difference_In_Hours
                            + " hours, "
                            + difference_In_Minutes
                            + " minutes, "
                            + difference_In_Seconds
                            + " seconds");*/
        }

        // Catch the Exception
        catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isDateToday(String date) {

//        SimpleDateFormat smf = new SimpleDateFormat("yyyy-MM-dd");
        SimpleDateFormat smf = new SimpleDateFormat("dd MMM yyyy");
        String nowTime = smf.format(new Date());

        if(date.equalsIgnoreCase(nowTime)){
            return true;
        }
        else return false;

        /*SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date d1 = new Date();

        try {

            Date d2 = sdf.parse(date);

            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            long difference_In_Years
                    = (difference_In_Time
                    / (1000l * 60 * 60 * 24 * 365));

            long difference_In_Days
                    = (difference_In_Time
                    / (1000 * 60 * 60 * 24))
                    % 365;

            if(difference_In_Years == 0 && difference_In_Days == 0){
                return true;
            }
        }

        catch (ParseException e) {
            e.printStackTrace();
        }
        return false;*/
    }

    public static boolean isTimeAbove1HoursNew(String time) {

//        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm aa");
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
        Date d1 = new Date();
        String format = sdf.format(d1);

        // Try Block
        try {
            Date nowDate = sdf.parse(format);

            Date d2 = sdf.parse(time);

            long difference_In_Time
                    = d2.getTime() - nowDate.getTime();

            long difference_In_Hours
                    = (difference_In_Time
                    / (1000 * 60 * 60))
                    % 24;

            if(difference_In_Hours >= 1){
                return true;
            }
        }

        catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isTimeAbove1Hours(String time) {

        try{
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm aa");
            Date userTime = sdf.parse(time);

            Date nowTime = new Date();

            long differenceInTime = userTime.getTime() - nowTime.getTime();
            long difference_In_Hours
                    = (differenceInTime
                    / (1000 * 60 * 60))
                    % 24;

            if(difference_In_Hours >= 1){
                return true;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
//
//        Date d1 = new Date();
//        String format = sdf.format(d1);
//
//        // Try Block
//        try {
//            Date nowDate = sdf.parse(format);
//
//            Date d2 = sdf.parse(time);
//
//            long difference_In_Time
//                    = d2.getTime() - nowDate.getTime();
//
//            long difference_In_Hours
//                    = (difference_In_Time
//                    / (1000 * 60 * 60))
//                    % 24;
//
//            if(difference_In_Hours >= 1){
//                return true;
//            }
//        }
//
//        catch (ParseException e) {
//            e.printStackTrace();
//        }
//        return false;
    }

    public String convertDateFormats(String date, String dateFormat,String convertFormat){

        SimpleDateFormat currentSmf = new SimpleDateFormat(dateFormat);
        SimpleDateFormat convertSmf = new SimpleDateFormat(convertFormat);


        try{
            Date currentDate = currentSmf.parse(date);
            String convertFormattedDate = convertSmf.format(currentDate);

            return convertFormattedDate;
        }catch (Exception e){
            return "";
        }
    }

}
