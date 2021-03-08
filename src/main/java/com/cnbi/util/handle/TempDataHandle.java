package com.cnbi.util.handle;

import cn.hutool.core.lang.Dict;
import com.cnbi.util.BeetlUtils;
import com.cnbi.util.FormulaBeanUtil;
import com.cnbi.util.MetaObjectUtil;
import com.cnbi.util.constant.ParamConstant;
import com.cnbi.util.entry.Data;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName TempDataHandle
 * @Description
 * @Author Wangjunkai
 * @Date 2020/8/7 14:36
 **/

public class TempDataHandle extends DataHandle {

    private final static String METHOD_NAME = "put";


    @Override
    public void compute(List<Data> datas, HashMap<String, Map<String, Object>> result, BigDecimal unit, Map<String, String> paramMap, Map<String, Object> cubeConfig) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ConcurrentHashMap<String, Object> dataMap = new ConcurrentHashMap<>();
        ConcurrentHashMap<String, Byte> btl = new ConcurrentHashMap<>();
        datas.stream().forEach(r -> {
                dataMap.put(r.getKey(), r.getVal());
                btl.put(r.getCubeId(), (byte) 0);
        });
        String monthName = FormulaBeanUtil.getVariableDesc("month", paramMap.get(ParamConstant.PERIOD).substring(4));
        Dict data = Dict.create().set("year", paramMap.get(ParamConstant.PERIOD).substring(0, 4))
                .set("month", paramMap.get(ParamConstant.PERIOD).substring(4))
                .set("dim", "B")
                .set("unit", unit)
                .set("comName", paramMap.get(ParamConstant.COM_NAME))
                .set("unitName", FormulaBeanUtil.getVariableDesc("unit", unit.toString()))
                .set("monthName", monthName)
                .set("dataMap", dataMap)
                .set("periodName", getPeriodName(paramMap, monthName));
        for (Map.Entry<String, Byte> entry : btl.entrySet()) {
            String s = BeetlUtils.get(paramMap.get(ParamConstant.PROJECT).concat("/").concat(entry.getKey()).concat(".btl"), data);
            MetaObjectUtil.invokeMathod(result, METHOD_NAME, new Class[]{Object.class, Object.class}, new Object[]{entry.getKey(), s});
        }
    }

    private String getPeriodName(Map<String, String> paramMap, String monthName) {
        String periodName = paramMap.get(ParamConstant.PERIOD).substring(0, 4) + "年";
        String month = paramMap.get(ParamConstant.PERIOD).substring(4);
        if (month.equals("00") || month.contains("Q")) {
            periodName += monthName;
        } else if(month.equals("01") || month.equals("1")){
            periodName += monthName;
        }else{
            periodName += "1~" + monthName;
        }
        return periodName;
    }
}