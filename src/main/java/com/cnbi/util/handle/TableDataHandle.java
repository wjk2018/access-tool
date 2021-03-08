package com.cnbi.util.handle;

import cn.hutool.core.lang.Dict;
import com.cnbi.util.constant.ParamConstant;
import com.cnbi.util.constant.ResultConstant;
import com.cnbi.util.entry.Data;

import java.math.BigDecimal;
import java.util.*;

/**
 * @ClassName TableDataHandle
 * @Description table、chart处理
 * @Author Wangjunkai
 * @Date 2020/8/7 13:36
 **/

public class TableDataHandle extends DataHandle {

    @Override
    public void compute(List<Data> datas, HashMap<String, Map<String, Object>> result, BigDecimal unit, Map<String, String> paramMap, Map<String, Object> cubeConfig) {
        //记录需要分栏的节点，key为ReportData中nodeID，value为ReportData中的subfield
        HashMap<String, String> subfield = new HashMap<>();
        for (Data data : datas) {
            if(Objects.nonNull(data.getSubfield()) && !Objects.equals(data.getSubfield(), ParamConstant.NO_SUBFIELD)){
                subfield.put(data.getCubeId(), data.getSubfield());
            }
            Map<String, Object> cubeData = result.get(data.getCubeId());
            //判断是否需要行转列
            if(Objects.nonNull(data.getGroupBy()) && !Objects.equals(data.getGroupBy(), ParamConstant.NO_GROUP)){
                if(Objects.isNull(cubeData) || cubeData.isEmpty()){
                    cubeData = getCollect(cubeConfig, data.getCubeId(), paramMap);
                    result.put(data.getCubeId(), cubeData);
                    buildDataDict(unit, data, (TreeSet<Dict>) cubeData.get(ResultConstant.DATA));
                }else {
                    TreeSet<Dict> dicts = (TreeSet<Dict>) cubeData.get(ResultConstant.DATA);
                    boolean flag = true;
                    for (Dict dict : dicts) {
                        if (dict.get("groupBy").equals(data.getGroupBy())){
                            dict.set(data.getKey(), data.getVal());
                            flag = false;
                        }
                    }
                    //第一次
                    if(flag){
                        buildDataDict(unit, data, dicts);
                    }
                }
            }else{
                //不需要行转列
                if(Objects.isNull(cubeData) || cubeData.isEmpty()) {
                    cubeData = getCollect(cubeConfig, data.getCubeId(), paramMap);
                    result.put(data.getCubeId(), cubeData);
                }
                Dict dataDict = Dict.create().set("sname", data.getDimName()).set("scode", data.getCode())
                        .set(data.getKey(), data.getVal()).set("sort", data.getSort());
                if(Objects.nonNull(data.getUnit())){
                    dataDict.set("unit", data.getUnit());
                }
                ((TreeSet<Dict>)cubeData.get(ResultConstant.DATA)).add(dataDict);
            }
        }
        if(!subfield.isEmpty()){
            //处理分栏
            handleSubfield(result, subfield);
        }
    }

    private void buildDataDict(BigDecimal unit, Data data, TreeSet<Dict> dicts) {
        Dict dataDict = Dict.create().set("sname", data.getDimName()).set("scode", data.getCode())
                .set(data.getKey(), data.getVal()).set("groupBy", data.getGroupBy())
                .set("sort", data.getSort());
        if(Objects.nonNull(data.getUnit())){
            dataDict.set("unit", data.getUnit());
        }
        dicts.add(dataDict);
    }

    private int prefix = 97;

    private void handleSubfield(HashMap<String, Map<String, Object>> result, HashMap<String, String> subfield) {
        for (Map.Entry<String, String> entry : subfield.entrySet()){
            //获取到需要分栏的节点
            TreeSet<Dict> dicts = (TreeSet<Dict>)result.get(entry.getKey()).get(ResultConstant.DATA);
            //分几栏
            int subfieldTime = Integer.parseInt(entry.getValue());
            int current = 0;
            Dict currentDict = null;
            //遍历节点数据
            Iterator<Dict> dictIterator = dicts.iterator();
            while(dictIterator.hasNext()){
                Dict dict = dictIterator.next();
                if(current == subfieldTime){
                    current = 0;
                }
                if(current == 0){
                    currentDict = dict;
                }else {
                    for (Map.Entry<String, Object> entrySet : dict.entrySet()) {
                        currentDict.set((char)(prefix + current) + entrySet.getKey(), entrySet.getValue());
                    }
                    dictIterator.remove();
                }
                current ++;
            }
        }
    }
}