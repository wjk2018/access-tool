package com.cnbi.util.handle;

import cn.hutool.core.lang.Dict;
import com.cnbi.util.constant.ResultConstant;
import com.cnbi.util.entry.Data;

import java.math.BigDecimal;
import java.util.*;

/**
 * @ClassName TextDataHandle
 * @Description
 * @Author Wangjunkai
 * @Date 2020/8/7 13:30
 **/

public class TextDataHandle extends DataHandle {


    @Override
    public void compute(List<Data> datas, HashMap<String, Map<String, Object>> result, BigDecimal unit, Map<String, String> paramMap, Map<String, Object> cubeConfig) {
        for (Data data : datas) {
            Map<String, Object> cubeData = result.get(data.getCubeId());
            if(Objects.isNull(cubeData) || cubeData.isEmpty()){
                cubeData = getCollect(cubeConfig, data.getCubeId(), paramMap);
                result.put(data.getCubeId(), cubeData);
            }
            Dict dataDict = Dict.create().set("sname", data.getDimName()).set("scode", data.getCode())
                    .set(data.getKey(), getVal(data, unit, false)).set("sort", data.getSort());
            if(Objects.nonNull(data.getUnit())){
                dataDict.set("unit", data.getUnit());
            }
            ((TreeSet<Dict>)cubeData.get(ResultConstant.DATA)).add(dataDict);
        }
    }
}