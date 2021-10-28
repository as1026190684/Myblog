package com.jx.blogap1.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class TimeHandle {

    /**
     * 日期转换时间戳
     * @author YYTE_JX
     * @date 2021/10/17 0017
     * @param string
     * @return java.lang.Long
     */
    public static Long toTimeStamp(String string) {
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date parse = sdf.parse(string);
            return parse.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 时间戳 获取第n天（负数则是前几天，正数则是往后）
     */
    public static Long getday(int a){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH,a);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        long day = calendar.getTimeInMillis();
        return day;
    }

    /**
     * 时间戳 获取当天00：00：00
     */
    public static Long getTodayZero(Long a){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();
        date.setTime(a);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY,0);
        calendar.set(Calendar.MINUTE,0);
        calendar.set(Calendar.SECOND,0);
        calendar.set(Calendar.MILLISECOND,0);
        Long todayZero = calendar.getTimeInMillis();
        return todayZero;
    }


    /**
     * 时间戳 获取年
     */
    public static int getYear(Long time){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();
        String timeString = sdf.format(time);
        try {
            date = sdf.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int year = calendar.get(Calendar.YEAR);
        return year;
    }

    /**
     * 时间戳 获取月
     */
    public static int getMonth(Long time){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();
        String timeString = sdf.format(time);
        try {
            date = sdf.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int month = (calendar.get(Calendar.MONTH) + 1);
        return month;
    }

    /**
     * 时间戳 获取天
     */
    public static int getDay(Long time){
        SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
        Date date=new Date();
        System.out.println(time);
        String timeString = sdf.format(time);
        try {
            date = sdf.parse(timeString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int day = (calendar.get(Calendar.DAY_OF_MONTH));
        return day;
    }


    /**
     * 时间戳 根据时间戳时间范围转换成时间戳日期集合
     */
    public static List<Long> getTimeStampList(Long dStart, Long End) {
        Date date=new Date();
        Date dEnd=new Date();
        dEnd.setTime(End);
        date.setTime(dStart);
        Calendar cStart = Calendar.getInstance();
        cStart.setTime(date);

        List dateList = new ArrayList();
        //别忘了，把起始日期加上
        dateList.add(dStart);
        // 此日期是否在指定日期之后
        while (dEnd.after(cStart.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            cStart.add(Calendar.DAY_OF_MONTH, 1);
            dateList.add(cStart.getTimeInMillis());
        }
        return dateList;
    }


    /**
     * 时间戳 转String
     */
    public static String gettimeToString(Long time){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(time);
        String lon = sdf.format(date);
        return lon;
    }
}
