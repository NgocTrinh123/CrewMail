package com.dazone.crewemail.utils;

import android.content.Context;

import com.dazone.crewemail.R;
import com.dazone.crewemail.data.MailData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;


public class TimeUtils {


    public static final Calendar FIRST_DAY_OF_TIME;
    public static final Calendar LAST_DAY_OF_TIME;
    public static final int DAYS_OF_TIME;
    public static final int MONTHS_OF_TIME;

    static {
        FIRST_DAY_OF_TIME = Calendar.getInstance();
        FIRST_DAY_OF_TIME.set(Calendar.getInstance().get(Calendar.YEAR) - 100, Calendar.JANUARY, 1);
        LAST_DAY_OF_TIME = Calendar.getInstance();
        LAST_DAY_OF_TIME.set(Calendar.getInstance().get(Calendar.YEAR) + 100, Calendar.DECEMBER, 31);
        DAYS_OF_TIME = (int) ((LAST_DAY_OF_TIME.getTimeInMillis() - FIRST_DAY_OF_TIME.getTimeInMillis()) / (24 * 60 * 60 * 1000));
        MONTHS_OF_TIME = (LAST_DAY_OF_TIME.get(Calendar.YEAR) - FIRST_DAY_OF_TIME.get(Calendar.YEAR)) * 12 + LAST_DAY_OF_TIME.get(Calendar.MONTH) - FIRST_DAY_OF_TIME.get(Calendar.MONTH);
    }

    /**
     * CONVERT TIME TO STRING - NOTIFICATION SETTING
     */
    public static String getStrFromTime(int hours, int minutes) {
        /** CHECK AM/PM */
        String result = hours < 13 ? "AM " : "PM ";
        /** CHECK HOURS */
        result += hours < 10 ? "0" + hours + ":" : hours + ":";
        /** CHECK MINUTES */
        result += minutes < 10 ? "0" + minutes : minutes;
        return result;
    }

    /**
     * CONVERT STRING TO TIME - NOTIFICATION SETTING
     */
    public static Calendar getTimeFromStr(String strTime) {
        String hours = strTime.substring(3, 5);
        String minutes = strTime.substring(6, 8);
        int intHours = Integer.parseInt(hours);
        int intMinutes = Integer.parseInt(minutes);
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, intHours);
        calendar.set(Calendar.MINUTE, intMinutes);
        return calendar;
    }

    /**
     * COMPARE TIME
     */
    public static boolean compareTime(String strFromTime, String strToTime) {
        Calendar calFromTime = getTimeFromStr(strFromTime);
        Calendar calToTime = getTimeFromStr(strToTime);
        int hourFromTime = calFromTime.get(Calendar.HOUR_OF_DAY);
        int minuteFromTime = calFromTime.get(Calendar.MINUTE);
        int hourToTime = calToTime.get(Calendar.HOUR_OF_DAY);
        int minuteToTime = calToTime.get(Calendar.MINUTE);

        if ((hourToTime == hourFromTime)) {
            if (minuteToTime > minuteFromTime) {
                return true;
            }
        } else {
            if (hourToTime > hourFromTime) {
                return true;
            }
        }
        return false;
    }

    /**
     * CHECK TIME BETWEEN
     */
    public static boolean isBetweenTime(String strFromTime, String strToTime) {
        Calendar calendar = Calendar.getInstance();
        Calendar calFromTime = getTimeFromStr(strFromTime);
        Calendar calToTime = getTimeFromStr(strToTime);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int hourFromTime = calFromTime.get(Calendar.HOUR_OF_DAY);
        int minuteFromTime = calFromTime.get(Calendar.MINUTE);
        int hourToTime = calToTime.get(Calendar.HOUR_OF_DAY);
        int minuteToTime = calToTime.get(Calendar.MINUTE);
        if (hourFromTime == hour || hour == hourToTime) {
            if (hourFromTime == hour && hour == hourToTime) {
                if (minuteFromTime <= minute && minute <= minuteToTime) {
                    return true;
                } else {
                    return false;
                }
            }else if(hourFromTime == hour && hour != hourToTime){
                if (minuteFromTime <= minute){
                    return true;
                }else {
                    return false;
                }
            }else if(hourFromTime != hour && hour == hourToTime){
                if ( minute<= minuteToTime){
                    return true;
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }else {
            if (hourFromTime < hour && hour < hourToTime) {
                return true;
            }else {
                return false;
            }
        }
    }

    public static Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 000);
        return calendar.getTime();
    }

    public static Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static int getPositionForMonth(Calendar month) {
        if (month != null) {
            int diffYear = month.get(Calendar.YEAR) - FIRST_DAY_OF_TIME.get(Calendar.YEAR);
            return diffYear * 12 + month.get(Calendar.MONTH) - FIRST_DAY_OF_TIME.get(Calendar.MONTH);
        }
        return 0;
    }

    public static Calendar getMonthForPosition(int position) throws IllegalArgumentException {
        if (position < 0) {
            throw new IllegalArgumentException("position cannot be negative");
        }
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(FIRST_DAY_OF_TIME.getTimeInMillis());
        cal.add(Calendar.YEAR, position / 12);
        cal.add(Calendar.MONTH, position % 12);
        return cal;
    }

    public static Calendar getDayForPosition(int position) throws IllegalArgumentException {
        if (position < 0) {
            throw new IllegalArgumentException("position cannot be negative");
        }

        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(FIRST_DAY_OF_TIME.getTimeInMillis());
        cal.add(Calendar.DAY_OF_YEAR, position);
        return cal;
    }

    public static int getPositionForDay(Calendar day) {
        if (day != null) {
            return (int) ((day.getTimeInMillis() - FIRST_DAY_OF_TIME.getTimeInMillis())
                    / 86400000  //(24 * 60 * 60 * 1000)
            );
        }
        return 0;
    }

    public static String getFormattedDate(long date) {
        final String defaultPattern = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defaultPattern, Locale.getDefault());
        return simpleDateFormat.format(new Date(date));
    }

    public static String getFormattedMonth(long date) {
        final String defaultPattern = "yyyy-MM";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defaultPattern, Locale.getDefault());
        return simpleDateFormat.format(new Date(date));
    }

    public static String getFormattedMyList(long date) {
        final String defaultPattern = "yyyyMM";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defaultPattern, Locale.getDefault());
        return simpleDateFormat.format(new Date(date));
    }

    public static String getFormattedAllEmp(long date) {
        final String defaultPattern = "yyyMMdd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defaultPattern, Locale.getDefault());
        return simpleDateFormat.format(new Date(date));
    }

    public static String getFormattedTime(long date, String defaultPattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defaultPattern, Locale.getDefault());
        return simpleDateFormat.format(new Date(date));
    }

    public static String getFormattedTimeWithoutTimeZone(long date, String defaultPattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(defaultPattern, Locale.getDefault());
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        return simpleDateFormat.format(new Date(date));
    }

    public static String getTimezoneOffsetInMinutes() {
        TimeZone tz = TimeZone.getDefault();
        int offsetMinutes = tz.getRawOffset() / 60000;
        String sign = "";
        if (offsetMinutes < 0) {
            sign = "-";
            offsetMinutes = -offsetMinutes;
        }
        return sign + "" + offsetMinutes;
    }

    /**
     * @param context    application context
     * @param timeString with format "/Date(1450746095000)/"
     * @return Today hh:mm aa || yesterday hh:mm aa || yyyy-MM-dd hh:mm aa
     */
    public static String displayTimeWithoutOffset(Context context, String timeString, int task) {
        try {
            String tempTimeString = timeString;
            tempTimeString = tempTimeString.replace("/Date(", "");
            tempTimeString = tempTimeString.replace(")/", "");
            long time = Long.valueOf(tempTimeString);
            return displayTimeWithoutOffset(context, time, task);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * format time
     *
     * @param context application context
     * @param time    long in milliseconds
     * @return Today hh:mm aa || yesterday hh:mm aa || yyyy-MM-dd hh:mm aa
     */
    //task - 0: EN
    //task - 1: KO
    public static String displayTimeWithoutOffset(Context context, long time, int task) {
        SimpleDateFormat formatter;
        if (task == 0) {
//            formatter = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
            formatter = new SimpleDateFormat("hh:mm aa");
        } else {
//            formatter = new SimpleDateFormat("aa hh:mm", Locale.getDefault());
            formatter = new SimpleDateFormat("aa hh:mm");
        }
        int type = (int) getTimeForMail(time);
        String dateString;
        switch (type) {
            case -2:
                dateString = context.getString(R.string.today) + " " + formatter.format(new Date(time)).toLowerCase();
                break;
            case -3:
                dateString = context.getString(R.string.yesterday) + " " + formatter.format(new Date(time)).toLowerCase();
                break;
            default:

                if (task == 0) {
                    formatter.applyLocalizedPattern("yyyy-MM-dd hh:mm aa");
                } else {
                    formatter.applyLocalizedPattern("yyyy-MM-dd aa hh:mm");
                }
                dateString = formatter.format(new Date(time)).toLowerCase();
                break;
        }
        return dateString;
    }

    public static int getTimeType(long time) {
        int date = 0;
        long secs = (time - System.currentTimeMillis()) / 1000;
        if (secs > 0) {
            int hours = (int) secs / 3600;
            if (hours <= 24) {
                date = 1;
            } else if (hours <= 48) {
                date = 2;
            }
        }
        return date;
    }

    //1: today
    //2: Yesterday
    //0: default

    public static int getYearNote(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return cal.get(Calendar.YEAR);
    }

    public static int getMonthNote(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return cal.get(Calendar.MONTH);
    }

    public static int getDateNote(long date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date);
        return cal.get(Calendar.DAY_OF_MONTH);
    }

    //-2: today
    //-3: Yesterday
    //-4: this month
    //-5: last Month
    //-1: default
    public static long getTimeForMail(long time) {
        int date = -1;
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(System.currentTimeMillis());
        if (cal.get(Calendar.YEAR) == getYearNote(time)) {
            if (cal.get(Calendar.MONTH) == getMonthNote(time)) {
                int temp = cal.get(Calendar.DAY_OF_MONTH) - getDateNote(time);
                if (cal.get(Calendar.DAY_OF_MONTH) == getDateNote(time)) {
                    date = -2;
                } else if (temp == 1) {
                    date = -3;
                } else {
                    date = -4;
                }
            } else if (cal.get(Calendar.MONTH) - 1 == getMonthNote(time)) {
                date = -5;
            }
        } else if (cal.get(Calendar.YEAR) == getYearNote(time) + 1) {
            if (cal.get(Calendar.MONTH) == 0 && getMonthNote(time) == 11) {
                date = -5;
            }
        }
        return date;
    }

    public static long getTimeFromString(String timeString) {
        long time;
        try {
            String tempTimeString = timeString;
            tempTimeString = tempTimeString.replace("/Date(", "");
            tempTimeString = tempTimeString.replace(")/", "");
            time = Long.valueOf(tempTimeString);
        } catch (Exception e) {
            return 0;
        }
        return time;
    }

    public static List<MailData> CheckDateTime(List<MailData> list) {
        List<MailData> listTemp = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            MailData mailData = list.get(i);
            if (i == 0) {
                if (mailData.getMailNo() > 0) {
                    MailData mailDataTemp = new MailData();
                    mailDataTemp.setMailNo(getTimeForMail(getTimeFromString(mailData.getRegisterDate())));
                    mailDataTemp.setRegisterDate(mailData.getRegisterDate());
                    listTemp.add(mailDataTemp);
                    listTemp.add(mailData);
                } else {
                    listTemp.add(mailData);
                }
            } else {
                if (mailData.getMailNo() > 0) {
                    long noTemp = getTimeForMail(getTimeFromString(list.get(i - 1).getRegisterDate()));
                    long noTemp2 = getTimeForMail(getTimeFromString(mailData.getRegisterDate()));
                    if (noTemp != noTemp2) {
                        MailData mailDataTemp = new MailData();
                        mailDataTemp.setMailNo(noTemp2);
                        mailDataTemp.setRegisterDate(mailData.getRegisterDate());
                        listTemp.add(mailDataTemp);
                        listTemp.add(mailData);
                    } else {
                        int month = getMonthNote(getTimeFromString(list.get(i - 1).getRegisterDate()));
                        int monthTemp = getMonthNote(getTimeFromString(mailData.getRegisterDate()));
                        if (month != monthTemp) {
                            MailData mailDataTemp = new MailData();
                            mailDataTemp.setMailNo(noTemp2);
                            mailDataTemp.setRegisterDate(mailData.getRegisterDate());
                            listTemp.add(mailDataTemp);
                            listTemp.add(mailData);
                        } else {
                            listTemp.add(mailData);
                        }
                    }
                } else {
                    listTemp.add(mailData);
                }
            }
        }
        Util.printLogs("list.size : " + list.size());
        return listTemp;
    }

    //Get Month from date long
    public static String getMonth(long date) throws ParseException {
        String monthName;
        String dateNote = Util.parseMili2Date(date, Statics.DATE_MONTH);
        String yearNote = Util.parseMili2Date(date, Statics.DATE_YEAR);
        monthName = dateNote + " " + yearNote;
        return monthName;
    }

    public static String GetMonth(long type, String time) {
        String month = "";
        try {
            if (type == -2) {
                month = Util.getString(R.string.string_today);
            } else if (type == -3) {
                month = Util.getString(R.string.string_yesterday);
            } else if (type == -4) {
                month = Util.getString(R.string.string_this_month);
            } else if (type == -5) {
                month = Util.getString(R.string.last_month);
            } else {
                month = getMonth(getTimeFromString(time));
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        return month;
    }
}
