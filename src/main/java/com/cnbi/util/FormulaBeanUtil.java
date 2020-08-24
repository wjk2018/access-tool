package com.cnbi.util;


import com.cnbi.util.period.PeriodUtil;

import java.util.HashMap;

/**
 * @ClassName:  FormulaBeanUtil   
 * @Description: 通用方法   
 * @author: cnbizhh 
 * @date:   2019年11月11日 下午3:30:43   
 *     
 * @Copyright: 2019 www.cnbisoft.com Inc. All rights reserved. 
 * 注意：本内容仅限于安徽经邦软件技术有限公司内部传阅，禁止外泄以及用于其他的商业目 
 */
public class FormulaBeanUtil {

	public static HashMap<String, String> unitMap = new HashMap<String,String>();

	static {
		unitMap.put("1", "元");
		unitMap.put("1000", "千元");
		unitMap.put("10000", "万元");
		unitMap.put("100000000", "亿元");
		unitMap.put("%", "%");
	}

	/**
	 * 
	 * @Title: getVariableDesc   
	 * @Description: 获取变量描述
	 * @param type {unit单位 unit月份、季度及年度}
	 * @param:  var
	 * @return: String      
	 * @throws
	 */
	public static String getVariableDesc(String type,String val){
		String desc = "";
		switch(type){
			case "unit":
				desc = unitMap.get(val);
				break;
			case "month":
				desc = PeriodUtil.monthMap.get(val);
				break;
			default:
				desc = "";
		}
		return desc;
	}
	
	

}
