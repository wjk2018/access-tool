package com.cnbi.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.Function;

import static com.cnbi.util.MetaObjectUtil.getObjectValue;

/**
 * @ClassName Tool
 * @Description 常用工具类
 * @Author Wangjunkai
 * @Date 2020/8/24 16:36
 **/

public class Tool {

    /**
     * @MethodName: list2Map
     * @Description: TODO
     * @param list [{key:K1, value:V1},{key:K1, value:V1}]
     * @param key
     * @param value
     * @Return: java.util.Map<java.lang.String,java.lang.Object>  {K1:V1, K2:V2}
     * @Author: wangjunkai
     * @Date: 2020/8/24 16:41
    **/
    public static Map<String, Object> list2Map(List<Map<String, Object>> list, String key, String value){
        HashMap<String, Object> result = new HashMap<>();
        for (Map<String, Object> map : list) {
            result.put(map.get(key).toString(), map.get(value));
        }
        return result;
    }

    /**
     * @MethodName: canCompute
     * @Description: 判断是否可以直接计算
     * @Param:  * @param list
     * @param first
     * @param last
     * @param field
     * @Return: boolean
     * @Author: wangjunkai
     * @Date: 2020/8/28 16:02
    **/
    public static boolean canCompute(List list, int first, int last, String field) throws NoSuchFieldException {
        return Objects.equals(getObjectValue(list.get(first), field), getObjectValue(list.get(last), field));
    }

    public static void addException(ConcurrentLinkedQueue<Exception> exceptions, Exception e){
        if(exceptions.isEmpty()){
            exceptions.add(e);
        }
    }
}