package com.cnbi.util.period;


import com.cnbi.util.constant.ParamConstant;

import java.util.*;

/**   
 * @ClassName:  PeriodUtil   
 * @Description: 报告期间公开类
 * @author: cnbizhh 
 * @date:   2019年11月11日 下午3:46:39   
 *     
 * @Copyright: 2019 www.cnbisoft.com Inc. All rights reserved. 
 * 注意：本内容仅限于安徽经邦软件技术有限公司内部传阅，禁止外泄以及用于其他的商业目 
 */
public class PeriodUtil {
	
	 /** 月份标识M */
    final public static String MONTH_SIGN = "M";// "M";
    /** 季度 标识Q */
    final public static String QUARTER_SIGN = "Q";// "Q";
    /** 半年标识H */
    final public static String HALFYEAR_SIGN = "H";// "H";
    /** 年度 标识Y */
    final public static String YEAR_SIGN = "Y";// "Y";

    /** 年度类型 (int) 1 */
    final public static int YEAR_TYPE = 1;// 1;
    /** 半年类型 (int) 2 */
    final public static int HALFYEAR_TYPE = 2;// 2;
    /** 季度类型 (int) 3 */
    final public static int QUARTER_TYPE = 3;// 3;
    /** 月份类型 (int) 4 */
    final public static int MONTH_TYPE = 4;// 4;
	
	public static HashMap<String, String> monthMap = new HashMap<String,String>();
	
	static {
		monthMap.put("00", "年度");
		monthMap.put("H1", "上半年");
		monthMap.put("H2", "下半年");
		monthMap.put("Q1", "一季度");
		monthMap.put("Q2", "二季度");
		monthMap.put("Q3", "三季度");
		monthMap.put("Q4", "四季度");
		monthMap.put("01", "1月");
		monthMap.put("02", "2月");
		monthMap.put("03", "3月");
		monthMap.put("04", "4月");
		monthMap.put("05", "5月");
		monthMap.put("06", "6月");
		monthMap.put("07", "7月");
		monthMap.put("08", "8月");
		monthMap.put("09", "9月");
		monthMap.put("10", "10月");
		monthMap.put("11", "11月");
		monthMap.put("12", "12月");
	}
	
	/**
     * 根据期间返回指定类型的值 </br>例如 6月 传入5</br> PeriodUtil.MONTH_TYPE:返回月份 =5</br>
     * PeriodUtil.QUARTER_TYPE：根据月份返回季度 =1</br> PeriodUtil.HALFYEAR_TYPE
     * :据月份返回半年 = 0</br> PeriodUtil.YEAR_TYPE：返回 =0</br>
     * 
     * @param type
     *            (int) 类型
     * @param currentMonth
     *            (int) 实际月 0<=currentMonth<12
     * @return PeriodUtil.MONTH_TYPE:返回月份 </br> PeriodUtil.QUARTER_TYPE：根据月份返回季度
     *         </br>PeriodUtil.HALFYEAR_TYPE :据月份返回半年 </br>
     *         PeriodUtil.YEAR_TYPE：返回 0</br> currentMonth < 0 或 currentMonth > 12 返回-1
     * 
     */
    public static int getPeriodOnMonthInt(int type, int currentMonth) {
		if (currentMonth < 0 || currentMonth > 12) {
		    currentMonth = -1;
		} else {
		    switch (type) {
		    case PeriodUtil.QUARTER_TYPE:
			currentMonth /= 3;
			break;
		    case PeriodUtil.HALFYEAR_TYPE:
			currentMonth /= 6;
			break;
		    case PeriodUtil.YEAR_TYPE:
			currentMonth = 0;
			break;
		    case PeriodUtil.MONTH_TYPE:
		    default:
		    }
		}
		return currentMonth;
    }
    
    /**
     * 判断是否为期间(月份，季度，半年，年度)
     * 
     * @param periodOnMonth
     * @return
     */
    public static boolean isPeriodOnMonth(String periodOnMonth) {
		boolean ret = false;
		if (!"".equals(periodOnMonth) && periodOnMonth.matches("[Q|H|0|1][\\d]")) {
		    ret = true;
		}
		return ret;
    }
    
    /**
     * 根据期间获取对应名称
     * 
     * @param periodOnMonth
     *            (String) 指定期间（）
     * @return 返回 期间名称 X月，X季度，X半年，年度
     */
    public static String getNameofPeriodOnMonth(String periodOnMonth) {
    	return monthMap.get(periodOnMonth);
    }
	
	/**
     * 计算期间</br>
     *  例如 Calperiod("2011", "Q2", 0, -3)</br>
     *  返回PeriodConverter
     * 	
     * @param period 年
     * @param year 年差值
     * @param month 月差值
     * @return 返回  PeriodConverter
     */
    public static String calperiod(String period, String year, String month ){
    	if(ParamConstant.Y.equals(month)){
			return  (Integer.parseInt(period.substring(0, 4)) - Integer.parseInt(year)) + "00";
		}
		int yearnum = Integer.parseInt(year);
		int monthnum = Integer.parseInt(month);
		PeriodConverter converter = new PeriodConverter(period.substring(0, 4), period.substring(4), "");
		converter.addYear(yearnum);
		converter.addMonth(monthnum);
		return converter.getPeriod("yyyyMM");
    }

	/**
	 * 计算期间</br>
	 *  例如 Calperiod("2011", "Q2", 0, -3)</br>
	 *  返回PeriodConverter
	 *
	 * @param year 年
	 * @param month 月上的期间
	 * @param yearnum 年差值
	 * @param monthnum 月差值
	 * @return 返回  PeriodConverter
	 */
	public static PeriodConverter calperiod(String year, String month, int yearnum, int monthnum ){
		PeriodConverter converter = new PeriodConverter(year, month, "");
		converter.addYear(yearnum);
		converter.addMonth(monthnum);
		return converter;
	}
    /*获取上一年*/
	public static String getLastYear(String year){
		return String.valueOf(Integer.parseInt(year) - 1);
	}
    /**
     * 计算期间
     * @param year 年
     * @param month 月上的期间
     * @param yearnum 年差值
     * @param monthnum 月差值
     * @return 返回  year
     * */
    public static String getYearString(String year, String month, int yearnum, int monthnum){
    	return calperiod(year,month,yearnum,monthnum).getYearString();
    }

    /**
     * 计算期间
     * @param year 年
     * @param month 月上的期间
     * @param yearnum 年差值
     * @param monthnum 月差值
     * @return 返回  month名称
     * */
    public static String getMonthNameString(String year, String month, int yearnum, int monthnum){
    	return calperiod(year,month,yearnum,monthnum).getMonthNameString();
    }
    
    /**
     * 计算期间
     * @param year 年
     * @param month 月上的期间
     * @param yearnum 年差值
     * @param monthnum 月差值
     * @return 返回  month
     * */
    public static String getMonthString(String year, String month, int yearnum, int monthnum){
    	return calperiod(year,month,yearnum,monthnum).getMonthString("MM");
    }
    
    /**
     * 根据指定期间获取该期间的实际截止月份<br>
     * 		例如Q2(第2季度) 则 返回   “06”（即6月份）<br>
     * 		  H1 上半年同2季度
     * @param periodOnMonth 实际月 0&lt;currentMonth&lt;=12
     * @return
     */
    public static String getEndCurrentMonth(String periodOnMonth) {
		if ("Q1".equals(periodOnMonth)) {
		    periodOnMonth = "03";
		} else if ("Q2".equals(periodOnMonth) || "H1".equals(periodOnMonth)) {
		    periodOnMonth = "06";
		} else if ("Q3".equals(periodOnMonth)) {
		    periodOnMonth = "09";
		} else if ("Q4".equals(periodOnMonth) || "H2".equals(periodOnMonth) || "00".equals(periodOnMonth)) {
		    periodOnMonth = "12";
		}
		return periodOnMonth;
    }
    
    /**
     *过程天</br>
     *	当periodOnMonth 为年度	(equals("00"))时， 返回360</br>
     *	当periodOnMonth 为半年	(matches("Q\\d")) 时，  返回180</br>
     *	当periodOnMonth 为季度  	(matches("Q\\d"))时， 返回90</br>
     *	当periodOnMonth 为月份	(matches("[0|1]\\d"))  时， 返回30</br>
     *	其它返回1
     * @param periodOnMonth
     * @return 返回浮点数
     */
    public static double processDAY(String periodOnMonth){
		double ret = 1;
		if (periodOnMonth.equals("00")) {
		    ret = 360;
		} else if (periodOnMonth.matches("H\\d")) {
		    ret = 180;
		} else if (periodOnMonth.matches("Q\\d") ) {
		    ret = 90;
		} else if (periodOnMonth.matches("[0|1]\\d")) {
		    ret = 30;
		}
		return ret;
    }
	/**
	 * 报告中获取前几年期间
	 * @param period
	 * @return
	 */
	public static List<String> getPeriodList(String period, int isNum, String rptType){
		List<String> perDatas = new ArrayList<>(14);
		List<String> perList0 = new ArrayList<>(5);
		List<String> perList1 = new ArrayList<>(5);
		int cycleNum = isNum * 100;
		for (int i = 0; i <cycleNum ; i+=100) {
			perList0.add(String.valueOf(Integer.valueOf(period) - i));
		}
		if (!rptType.equals("3")){
			perList0.add(period.substring(0, 4) + "00");
			for (int i = 100; i < cycleNum; i += 100) {
				perList0.add(String.valueOf(Integer.valueOf(period)- i - Integer.valueOf(period.substring(4, 6))));
			}
		}
		Collections.sort(perList0);
		Collections.reverse(perList0);
		perDatas.addAll(perList0);
		for (int i = 0; i <cycleNum ; i+=100) {
			perList1.add("b" + (Integer.valueOf(period) - i));
		}
		if (!rptType.equals("3")){
			perList1.add("b" + period.substring(0, 4) + "00");
			for (int i = 100; i < cycleNum; i += 100) {
				perList1.add("b" + (Integer.valueOf(period)- i - Integer.valueOf(period.substring(4, 6))));
			}
		}
		Collections.sort(perList1);
		Collections.reverse(perList1);
		perDatas.addAll(perList1);
		perDatas.add(period + "_A");
		perDatas.add(period + "_B");
		perDatas.add(period + "_C");
		perDatas.add(period + "_D");
		perDatas.add("year");
		perDatas.add("month");
		perDatas.add("datepicker");
		return perDatas;
	}

	/**
	 * 获取季度期间
	 * @param period
	 * @param strMonth
	 * @param strYear
	 * @return
	 */
	public static String byQuarterlyGetPeriod (String period, String strMonth, String strYear){
		int syear = Integer.parseInt(strYear);
		int quarterlyNum = Integer.parseInt(period.substring(period.length()-1));
		int year = Integer.parseInt(period.substring(0,4));
		if(Objects.equals(YEAR_SIGN, strMonth)){
			return year - syear + QUARTER_SIGN + quarterlyNum;
		}
		int smonth = Integer.parseInt(strMonth);
		int lastQuarterlyNum =0,lastYear =0;
		if (quarterlyNum - smonth == 0){
			lastQuarterlyNum = 4;
			lastYear = year - syear - 1;
		} else if (quarterlyNum - smonth < 0){
			lastQuarterlyNum = (smonth - quarterlyNum) + 4 - smonth;
			lastYear = year - 1 - syear - (smonth - quarterlyNum) / 4;
		} else if (quarterlyNum - smonth > 0){
			lastQuarterlyNum = quarterlyNum - smonth;
			lastYear = year;
		}

		return lastYear + QUARTER_SIGN + lastQuarterlyNum;
	}

}
