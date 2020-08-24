package com.cnbi.util.handle;


import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Dict;
import com.cnbi.util.constant.ParamConstant;
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

    public final ConcurrentLinkedQueue<Exception> exceptions = new ConcurrentLinkedQueue<>();

    public HashMap<String, TreeSet<Dict>> handle(List<Data> datas, BigDecimal unit, ForkJoinPool pool, Map<String, String> paramMap) throws Exception {
        HashMap<String, TreeSet<Dict>> result = pool.submit(new DataHandleTask(0, datas.size(), datas, unit, this, paramMap, exceptions)).get();
        if(exceptions.isEmpty()){
            return result;
        }else {
            throw exceptions.poll();
        }
    }

    public abstract void compute(List<Data> datas, HashMap<String, TreeSet<Dict>> result, BigDecimal unit, Map<String, String> paramMap) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    protected TreeSet<Dict> getTreeSet(){
        return new TreeSet<Dict>(Comparator.comparingInt(o -> o.getInt("sort")));
    }

    protected Object getVal(Data data, BigDecimal unit, boolean isTemp) {
        if(Objects.equals(data.getUnitConversion(), ParamConstant.UNIT_CONVERSION)){
            if(Objects.isNull(data.getVal())){
                return BigDecimal.ZERO;
            }else{
                BigDecimal convert = Convert.convert(BigDecimal.class, data.getVal());
                if(Objects.equals(data.getUnit(), ParamConstant.UNIT)){
                    //结论性文字的单位由模板处理
                    if(!isTemp) {
                        return convert.divide(unit, 2, BigDecimal.ROUND_HALF_UP);
                    }
                }else{//结论性文字指标为%的，模板中除以了100
                    return convert.multiply(BigDecimal.valueOf(100));
                }
            }
        }
        return Objects.isNull(data.getVal())?(Objects.isNull(data.getUnit())?"":BigDecimal.ZERO):data.getVal();
    }

}