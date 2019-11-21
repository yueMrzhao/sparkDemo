package com.etrans.demo.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 工程名称: sparkDemo
 * 单元名称: DateUtils.java
 * 说   明:
 * 作   者: zy
 * 创建时间: 2019-11-20 11:35 AM
 * 修改历史:
 */
public class DateUtils {
    private static String format_yyyy_MM_dd_HH_mm_ss_SSS = "yyyy-MM-dd HH:mm:ss.SSS";
    private static ThreadLocal<DateUtils.SimpleDateFormatGetter> threadLocal = new ThreadLocal<DateUtils.SimpleDateFormatGetter>() {
        protected DateUtils.SimpleDateFormatGetter initialValue() {
            return new DateUtils.SimpleDateFormatGetter();
        }
    };

    public DateUtils() {
    }

    public static String format(Date date) {
        return ((DateUtils.SimpleDateFormatGetter)threadLocal.get()).getByFormat(format_yyyy_MM_dd_HH_mm_ss_SSS).format(date);
    }

    public static String format(Date date, String format) {
        return ((DateUtils.SimpleDateFormatGetter)threadLocal.get()).getByFormat(format).format(date);
    }

    public static Date parse(String value, String format) throws ParseException {
        return ((DateUtils.SimpleDateFormatGetter)threadLocal.get()).getByFormat(format).parse(value);
    }

    public static Date parse(String value) throws ParseException {
        return ((DateUtils.SimpleDateFormatGetter)threadLocal.get()).getByFormat(format_yyyy_MM_dd_HH_mm_ss_SSS).parse(value);
    }

    public static void main(String[] args) throws ParseException {
        System.out.println(format(new Date(), "yyMMddHHmmss"));
    }

    public static class SimpleDateFormatGetter {
        private Map<String, SimpleDateFormat> map = new HashMap();

        public SimpleDateFormatGetter() {
        }

        public SimpleDateFormat getByFormat(String _format) {
            if (_format == null) {
                return null;
            } else {
                SimpleDateFormat sdf = (SimpleDateFormat)this.map.get(_format);
                if (sdf == null) {
                    sdf = new SimpleDateFormat(_format);
                    this.map.put(_format, sdf);
                }

                return sdf;
            }
        }
    }
}
