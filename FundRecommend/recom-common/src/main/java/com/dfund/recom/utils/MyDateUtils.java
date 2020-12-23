package com.dfund.recom.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 2020/10/20 9:43 上午
 *
 * @author Seldom
 */
public class MyDateUtils extends DateUtils  {

    /**
     /**
     * 得到日期时间字符串，转换格式（yyyy-MM-dd HH:mm:ss）
     */
    public static String formatDateTime(Date date) {
        return DateFormatUtils.format(date, "yyyy-MM-dd HH:mm:ss");
    }

    /**
     /**
     * 得到日期时间字符串，转换格式（yyyyMMdd）
     */
    public static Integer formatDate(Date date) {
        return Integer.valueOf(DateFormatUtils.format(date, "yyyyMMdd"));
    }

    public static Date integerToDate(Integer integreDate){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMdd");
        Date parse = null;
        try {
            parse = formatter.parse(integreDate + "");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return parse;
    }

    /**
     * 获取过去的天数
     * @param date
     * @return
     */
    public static long pastDays(Date date) {
        long t = new Date().getTime()-date.getTime();
        return t/(24*60*60*1000);
    }

    public static Date getBeforeDay(Date date, Long num){
        long time = date.getTime();
        long diff = 1000L * 60L * 60L * 24L * num;
        long needTime  = time - diff;
        return new Date(needTime);
    }

    public static Integer getBeforeDayInt(Date date, Long num){
        return Integer.valueOf(getDate(getBeforeDay(date,num),"yyyyMMdd"));
    }

    /**
     * 得到当前日期字符串 格式（yyyy-MM-dd）
     * pattern可以为："yyyy-MM-dd" "HH:mm:ss" "E" 等等
     */
    public static String getDate(Date date,String pattern) {
        return DateFormatUtils.format(date, pattern);
    }

    /**
     * 获取 今天  是周几
     * @param date
     * @return 0 周日，1 周一 。。。。 6 周六
     */
    public static int getDayOfWeek(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int w = cal.get(Calendar.DAY_OF_WEEK) - 1;
        return w;
    }

    /**
     * 日期型字符串转化为日期 格式
     * { "yyyy-MM-dd", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd HH:mm",
     *   "yyyy/MM/dd", "yyyy/MM/dd HH:mm:ss", "yyyy/MM/dd HH:mm",
     *   "yyyy.MM.dd", "yyyy.MM.dd HH:mm:ss", "yyyy.MM.dd HH:mm" }
     */
    public static Date StrToDate(String dateStr,String patterns) {
        if (StringUtils.isBlank(dateStr)){
            return null;
        }
        try {
            return parseDate(dateStr, patterns);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static int getHourOfDate(Date date){
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        int hour=cal.get(Calendar.HOUR_OF_DAY);//小时
        return  hour;
    }
    /**
     * 获取date的前一年时间
     * @param date
     * @return
     */
    public static Date getBeforeYearDate(Date date){
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.YEAR, -1); //年份减1
        Date time = ca.getTime();
        return time;
    }

    public static String lastFirday(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        while (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.MONDAY) {
            calendar.add(Calendar.DAY_OF_WEEK, -1);
        }
        int dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1;
        int offset = 7 - dayOfWeek;
        calendar.add(Calendar.DATE, offset - 9);

        return getFirstDayOfWeek(calendar.getTime());//这是从上周日开始数的到本周五为6

    }
    /**
     * 得到某一天的该星期的第一日 00:00:00
     *
     * @param date
     * @param
     *
     * @return
     */
    public static String getFirstDayOfWeek(Date date) {
        int firstDayOfWeek = 6 ;
        Calendar cal = Calendar.getInstance();
        if (date != null) {
            cal.setTime(date);
        }
        cal.setFirstDayOfWeek(firstDayOfWeek);//设置一星期的第一天是哪一天
        cal.set(Calendar.DAY_OF_WEEK, firstDayOfWeek);//指示一个星期中的某天
        cal.set(Calendar.HOUR_OF_DAY, 0);//指示一天中的小时。HOUR_OF_DAY 用于 24 小时制时钟。例如，在 10:04:15.250 PM 这一时刻，HOUR_OF_DAY 为 22。
        cal.set(Calendar.MINUTE, 0);//指示一小时中的分钟。例如，在 10:04:15.250 PM 这一时刻，MINUTE 为 4。
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return getDate(cal.getTime(),"yyyy-MM-dd");
    }

    /**
     * 获取date的前n个月的时间
     * @param date
     * @return
     */
    public static Date getBeforeMonthDate(Date date,int num){
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        ca.add(Calendar.MONTH, -num); //年份减1
        Date time = ca.getTime();
        return time;
    }

}
