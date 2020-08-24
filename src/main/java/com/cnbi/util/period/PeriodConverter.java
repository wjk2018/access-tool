package com.cnbi.util.period;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**   
 * @ClassName:  PeriodConverter   
 * @Description: 期间转换工具
 * @author: cnbizhh 
 * @date:   2019年11月13日 上午9:52:03   
 *     
 * @Copyright: 2019 www.cnbisoft.com Inc. All rights reserved. 
 * 注意：本内容仅限于安徽经邦软件技术有限公司内部传阅，禁止外泄以及用于其他的商业目 
 */
public class PeriodConverter {
	
	private Calendar calendar;

    /** 类型 (int) */
    private int type = PeriodUtil.MONTH_TYPE;

    public PeriodConverter() {
    	calendar = Calendar.getInstance(TimeZone.getDefault());
    }

    /**
     * 构造方法
     * 
     * @param year
     *            String 指定年份 不设置留空
     * @param month
     *            String 指定月份或季度或半年度 不设置留空
     * @param day
     *            String 指定天数 不设置留空
     */
    public PeriodConverter(String year, String month, String day) {
    	this();
		if (year.matches("-?\\d+")) {
		    setYear(Integer.parseInt(year));
		}
		if (day.matches("-?\\d+")) {
		    setDay(Integer.parseInt(day));
		}else if(day.equals("")){
		    setDay(1);
		}
		setMonth(month);
    }

    /**
     * 设置年份属性
     * 
     * @param year
     *            int
     */
    public void setYear(int year) {
    	calendar.set(Calendar.YEAR, year);
    }

    
    public void setMonth(String month) {
		int monthn = 0;
		if (month.equals("00")) {
		    type = PeriodUtil.YEAR_TYPE;
		    disposeToMonth(monthn, 1, 12);
		} else if (month.contains(PeriodUtil.HALFYEAR_SIGN)) {
		    type = PeriodUtil.HALFYEAR_TYPE;
		    month = month.substring(1, month.length());
		    monthn = Integer.parseInt(month) - 1;
		    disposeToMonth(monthn, 2, 6);// 半年= 6月
		} else if (month.contains(PeriodUtil.QUARTER_SIGN)) {
		    type = PeriodUtil.QUARTER_TYPE;
		    month = month.substring(1, month.length());
		    monthn = Integer.parseInt(month) - 1;
		    disposeToMonth(monthn, 4, 3);// 一季度= 3个月
		} else if (!month.equals("")) {
		    type = PeriodUtil.MONTH_TYPE;
		    calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
		}
    }

    /**
     * 设置天数属性
     * 
     * @param day
     *            int
     */
    public void setDay(int day) {
		if (type == PeriodUtil.MONTH_TYPE) {
		    calendar.set(Calendar.DAY_OF_MONTH, day);
		}
    }

    /**
     * 设置周属性
     * 
     * @param week
     *            int
     */
    public void setWeek(int week) {
		if (type == PeriodUtil.MONTH_TYPE) {
		    calendar.set(Calendar.WEEK_OF_YEAR, week);
		}
    }

    /**
     * 设置时间属性
     * 
     * @param hour
     *            int 时属性
     * @param minute
     *            int 分属性
     * @param second
     *            int 列属性
     */
    public void setTime(int hour, int minute, int second) {
		if (type == PeriodUtil.MONTH_TYPE) {
		    calendar.set(Calendar.HOUR_OF_DAY, hour);
		    calendar.set(Calendar.MINUTE, minute);
		    calendar.set(Calendar.SECOND, second);
		}
    }

    /**
     * 处理年
     *
     * @param year
     */
    public void disposeYear(String year) {
		if (year.matches("-?\\d+")) {
		    int yearint = Integer.parseInt(year);
		    if (yearint > 0) {
		    	setYear(yearint);
		    } else {
		    	addYear(yearint);
		    }
		}
    }

	/**
	 * 年份的差值
	 *
	 * @param addyear
	 *            int
	 */
	public void addYear(int addyear) {
		calendar.add(Calendar.YEAR, addyear);
	}

    /**
     * 月份的差值
     * 
     * @param addmonth
     *            int
     */
    public void addMonth(int addmonth) {
		if (type == PeriodUtil.MONTH_TYPE) {
		    calendar.add(Calendar.MONTH, addmonth);
		} else if (type == PeriodUtil.QUARTER_TYPE) {
		    int monthn = PeriodUtil.getPeriodOnMonthInt(PeriodUtil.QUARTER_TYPE, calendar.get(Calendar.MONTH)) + addmonth;
		    disposeToMonth(monthn, 4, 3);
		} else if (type == PeriodUtil.HALFYEAR_TYPE) {
		    int monthn = PeriodUtil.getPeriodOnMonthInt(PeriodUtil.HALFYEAR_TYPE, calendar.get(Calendar.MONTH)) + addmonth;
		    disposeToMonth(monthn, 2, 6);
		} else if (type == PeriodUtil.YEAR_TYPE) {
		    int monthn = PeriodUtil.getPeriodOnMonthInt(PeriodUtil.YEAR_TYPE, calendar.get(Calendar.MONTH)) + addmonth;
		    disposeToMonth(monthn, 1, 12);
		}
    }

    /**
     * 处理月
     * 
     * @param month
     */
    public void disposeMonth(String month) {
    	if (month.matches("^-\\d*||0")) {
	    int m = Integer.parseInt(month);
		    if (m <= 0) {
		    	addMonth(Integer.parseInt(month));
		    } else {
		    	setMonth(month);
		    }
		} else if (PeriodUtil.isPeriodOnMonth(month)) {
		    setMonth(month);
		}
    }

    /**
     * 天数的差值
     * 
     * @param addday
     *            int
     */
    public void addDay(int addday) {
		if (type == PeriodUtil.MONTH_TYPE) {
		    calendar.add(Calendar.DAY_OF_MONTH, addday);
		}
    }

    /**
     * 周的差值
     * 
     * @param addweek
     *            int
     */
    public void addWeek(int addweek) {
		if (type == PeriodUtil.MONTH_TYPE) {
		    calendar.add(Calendar.WEEK_OF_YEAR, addweek);
		}
    }

    /**
     * 日期的差值
     * 
     * @param addyear
     *            int 年份差值
     * @param addmonth
     *            int 月份差值
     * @param addday
     *            int 天数差值
     */
    public void add(int addyear, int addmonth, int addday) {
		addYear(addyear);
		addMonth(addmonth);
		if (type == PeriodUtil.MONTH_TYPE) {
		    addDay(addday);
		}
    }

    /**
     * 时间的差值
     * 
     * @param addhour
     *            int 时差值
     * @param addminute
     *            int 分差值
     * @param addsecond
     *            int 秒差值
     */
    public void addTime(int addhour, int addminute, int addsecond) {
		if (type == PeriodUtil.MONTH_TYPE) {
		    calendar.add(Calendar.HOUR_OF_DAY, addhour);
		    calendar.add(Calendar.MINUTE, addminute);
		    calendar.add(Calendar.SECOND, addsecond);
		}
    }

    /**
     * 处理转化为最后月
     * 
     * @param numb
     *            int 指定值
     * @param length
     *            int 一年有几个 有4个季度
     * @param one
     *            int 包含几个月 如一个季度有3个月
     * 
     */
    private void disposeToMonth(int numb, int length, int one) {
		int y_amount = 0;
		if (numb >= 0) {
		    y_amount = numb / length;
		    numb = numb % length;
		} else {
		    y_amount = numb / length - 1;
		    numb = numb % length + length;
		}
		addYear(y_amount);
		calendar.set(Calendar.MONTH, numb * one + one - 1);// 设置月份
    }

    /**
     * 获取年份字符串
     * 
     * @return
     */
    public String getYearString() {
    	return getString("yyyy");
    }

    /**
     * 获取月的字符串
     * 
     * @param formatString
     * @return YEAR_TYPE ： "00"\n HALFYEAR_TYPE："H1"\n QUARTER_TYPE:"Q1"\n
     *         MONTH_TYPE:"01"\n
     */
    public String getMonthString(String formatString) {
		String monthStr = "";
		if (type == PeriodUtil.YEAR_TYPE) {
		    monthStr = "00";
		} else if (type == PeriodUtil.HALFYEAR_TYPE) {
		    monthStr = PeriodUtil.HALFYEAR_SIGN + (PeriodUtil.getPeriodOnMonthInt(PeriodUtil.HALFYEAR_TYPE, calendar.get(Calendar.MONTH)) + 1);
		} else if (type == PeriodUtil.QUARTER_TYPE) {
		    monthStr = PeriodUtil.QUARTER_SIGN + (PeriodUtil.getPeriodOnMonthInt(PeriodUtil.QUARTER_TYPE, calendar.get(Calendar.MONTH)) + 1);
		} else if (type == PeriodUtil.MONTH_TYPE) {
		    monthStr = getString(formatString);
		}
		return monthStr;
    }

    /***
     * 获取月的字符串名称
     * 
     * @return
     */
    public String getMonthNameString() {
    	return PeriodUtil.getNameofPeriodOnMonth(getMonthString("MM"));
    }

    /**
     * 返回转化后的期间 字符串类型
     * 
     * @param formatString
     *            String 指定返回格式
     * @return String
     */
    public String getPeriod(String formatString) {
		String monthS = "";
		String month = "";
		if (formatString.contains("MN") || formatString.contains("Mn")) {
		    monthS = getMonthNameString();
		    if (monthS.contains("月")) {
		    	month = monthS.split("月")[0];
		    }
		    if (formatString.contains("MMN")) {
		    	formatString = formatString.replaceAll("MMN", "\\$\\{\\$\\}");
				if (!"".equals(month)) {
				    monthS = month + "月份";
				}
		    } else if (formatString.contains("MMn")) {
		    	formatString = formatString.replaceAll("MMn", "\\$\\{\\$\\}");
				if (!"".equals(month)) {
				    month = String.format("%02d", Integer.parseInt(month));
				    monthS = month + "月";
				}
		    } else if (formatString.contains("MN")) {
				formatString = formatString.replaceAll("MN", "\\$\\{\\$\\}");
				if (!"".equals(month)) {
				    monthS = month + "月份";
				}
		    } else if (formatString.contains("Mn")) {
		    	formatString = formatString.replaceAll("Mn", "\\$\\{\\$\\}");
		    }
		} else if (formatString.contains("MM")) {
		    formatString = formatString.replaceAll("MM", "\\$\\{\\$\\}");
		    monthS = getMonthString("MM");
		} else if (formatString.contains("M")) {
		    formatString = formatString.replaceAll("M", "\\$\\{\\$\\}");
		    monthS = getMonthString("M");
		}
		String period = getString(formatString);
		period = period.replaceAll("\\$\\{\\$\\}", monthS);
		return period;
    }

    /**
     * 返回转换后的期间 日期类型
     * 
     * @return Date
     */
    public Date getDate() {
    	return calendar.getTime();
    }
    
    public Calendar getCalendar(){
    	return calendar;
    }

    /**
     * 返回转化后的期间 字符串类型
     * 
     * @param formatString
     *            String 指定返回格式
     * @return String
     */
    private String getString(String formatString) {
    	DateFormat df = new SimpleDateFormat(formatString);
    	return df.format(getDate());
    }
}
