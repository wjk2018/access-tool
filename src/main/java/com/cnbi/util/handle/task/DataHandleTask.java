package com.cnbi.util.handle.task;

import cn.hutool.core.lang.Dict;
import com.cnbi.util.entry.Data;
import com.cnbi.util.handle.DataHandle;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RecursiveTask;

/**
 * @ClassName DataHandleTask
 * @Description
 * @Author Wangjunkai
 * @Date 2020/8/7 13:47
 **/

public class DataHandleTask extends RecursiveTask<HashMap<String, TreeSet<Dict>>> {

    public DataHandleTask(int begin, int end, List<Data> datas, BigDecimal unit, DataHandle dataHandle, Map<String, String> paramMap, ConcurrentLinkedQueue<Exception> exceptions){
        this.begin = begin;
        this.end = end;
        this.dataHandle = dataHandle;
        this.datas = datas;
        this.unit = unit;
        this.unit = unit;
        this.paramMap = paramMap;
        this.exceptions = exceptions;
    }
    private static final Integer ADJUST_VALUE  =  30;
    protected int begin;
    protected int end;
    protected List<Data> datas;
    protected BigDecimal unit;
    private DataHandle dataHandle;
    private Map<String, String> paramMap;
    private ConcurrentLinkedQueue<Exception> exceptions;
    @Override
    protected HashMap<String, TreeSet<Dict>> compute() {
        HashMap<String, TreeSet<Dict>> result = new HashMap<>();
        if(end - begin < ADJUST_VALUE){
            try {
                dataHandle.compute(datas.subList(begin, end), result, unit, paramMap);
            } catch (Exception e) {
                if(exceptions.isEmpty()) {
                    exceptions.add(e);
                }
            }
        }else {
            int middle = (begin + end)/2;
            DataHandleTask leftTask = new DataHandleTask(begin, middle, datas, unit, dataHandle, paramMap, exceptions);
            DataHandleTask rightTask = new DataHandleTask(middle + 1, end, datas, unit, dataHandle, paramMap, exceptions);
            leftTask.fork();
            rightTask.fork();
            result.putAll(leftTask.join());
            result.putAll(rightTask.join());
        }
        return result;
    }

}