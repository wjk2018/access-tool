package com.cnbi.util.handle;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.cnbi.util.calculate.DBFormulaParse;
import com.cnbi.util.constant.ParamConstant;
import com.cnbi.util.constant.ResultConstant;
import com.cnbi.util.entry.Data;
import com.cnbi.util.handle.task.DataHandleTask;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;

/**
 * @ClassName DataHandle
 * @Description cube数据处理
 * @Author Wangjunkai
 * @Date 2020/8/7 11:43
 **/

public abstract class DataHandle {


    public HashMap<String, Map<String, Object>> handle(List<Data> datas, BigDecimal unit, ForkJoinPool pool,
                                                       Map<String, String> paramMap, Map<String, Object> cubeConfig,
                                                       ConcurrentLinkedQueue<Exception> exceptions) throws Exception {

        HashMap<String, Map<String, Object>> result = pool.submit(new DataHandleTask(0, datas.size(), datas, unit, this, paramMap, exceptions, cubeConfig)).get();
        if(exceptions.isEmpty()){
            return result;
        }else {
            throw exceptions.poll();
        }
    }

    public abstract void compute(List<Data> datas, HashMap<String, Map<String, Object>> result, BigDecimal unit, Map<String, String> paramMap, Map<String, Object> cubeConfig)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    protected HashMap<String, Object> getCollect(Map<String, Object> cubeConfigs, String cubeId, Map<String, String> paramMap){
        DBFormulaParse dbFormulaParse = new DBFormulaParse();
        HashMap<String, Object> collect = new HashMap<>(2);
        collect.put(ResultConstant.DATA, new TreeSet<Dict>(Comparator.comparingInt(o -> o.getInt("sort"))));
        String parse = dbFormulaParse.parse(cubeConfigs.get(cubeId).toString(), paramMap);
        Map map = JSONUtil.toBean(parse, Map.class);
        collect.put(ResultConstant.CONFIG, map);
        return collect;
    }

}