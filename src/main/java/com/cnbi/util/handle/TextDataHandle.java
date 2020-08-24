package com.cnbi.util.handle;

import cn.hutool.core.lang.Dict;
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
    public void compute(List<Data> datas, HashMap<String, TreeSet<Dict>> result, BigDecimal unit, Map<String, String> paramMap) {
        for (Data data : datas) {
            TreeSet<Dict> dicts = result.get(data.getCubeId());
            if(Objects.isNull(dicts) || dicts.isEmpty()){
                dicts = getTreeSet();
                result.put(data.getCubeId(), dicts);
            }
            Dict dataDict = Dict.create().set("sname", data.getDimName()).set("scode", data.getCode())
                    .set(data.getKey(), getVal(data, unit, false)).set("sort", data.getSort());
            if(Objects.nonNull(data.getUnit())){
                dataDict.set("unit", data.getUnit());
            }
            dicts.add(dataDict);
        }
    }
}