package com.cnbi.util.handle.task;

import com.cnbi.util.MetaObjectUtil;
import com.cnbi.util.Tool;
import com.cnbi.util.entry.Data;
import com.cnbi.util.handle.DataHandle;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.RecursiveTask;

/**
 * @ClassName DataHandleTask
 * @Description
 * @Author Wangjunkai
 * @Date 2020/8/7 13:47
 **/

public class DataHandleTask extends RecursiveTask<HashMap<String, Map<String, Object>>> {

    private final static String CAN_COMPUTE_FEILD = "cubeId";

    public DataHandleTask(int begin, int end, List<Data> datas, BigDecimal unit, DataHandle dataHandle, Map<String, String> paramMap,
                          ConcurrentLinkedQueue<Exception> exceptions, Map<String, Object> cubeConfig){
        this.begin = begin;
        this.end = end;
        this.dataHandle = dataHandle;
        this.datas = datas;
        this.unit = unit;
        this.unit = unit;
        this.paramMap = paramMap;
        this.exceptions = exceptions;
        this.cubeConfig = cubeConfig;
    }

    /**
     * ADJUST_VALUE需要大于数据最多的cube值
     * TODO:可以自动根据cube数据判断这个值
     */
    private static final Integer ADJUST_VALUE  =  10;
    protected int begin;
    protected int end;
    protected List<Data> datas;
    protected Map<String, Object> cubeConfig;
    protected BigDecimal unit;
    private DataHandle dataHandle;
    private Map<String, String> paramMap;
    private ConcurrentLinkedQueue<Exception> exceptions;
    @Override
    protected HashMap<String, Map<String, Object>> compute() {
        HashMap<String, Map<String, Object>> result = new HashMap<>();
        boolean brother = false;
        try {
            /**
             * 两种情况：1、begin——end中全是一个cube
             *          2、begin——end有两个cube，但(begin + end)/2落在第二个cube上
             */
            brother = Tool.canCompute(datas, begin, end -1 , DataHandleTask.CAN_COMPUTE_FEILD) ||
                    !Tool.canCompute(datas, begin, (begin + end)/2, DataHandleTask.CAN_COMPUTE_FEILD);
        } catch (Exception e) {
            Tool.addException(exceptions, e);
        }
        if(end - begin < ADJUST_VALUE || brother){
            try {
                dataHandle.compute(datas.subList(begin, end), result, unit, paramMap, cubeConfig);
            } catch (Exception e) {
                Tool.addException(exceptions, e);
            }
        }else {
            int middle = (begin + end)/2;
            //让同一个cube的数据始终在同一个task中
            while(datas.get(middle).getCubeId().equals(datas.get(middle + 1)) && middle + 1 < end){
                middle ++;
            }
            middle ++;
            DataHandleTask leftTask = new DataHandleTask(begin, middle, datas, unit, dataHandle, paramMap, exceptions, cubeConfig);
            DataHandleTask rightTask = new DataHandleTask(middle, end, datas, unit, dataHandle, paramMap, exceptions, cubeConfig);
            leftTask.fork();
            rightTask.fork();
            result.putAll(leftTask.join());
            result.putAll(rightTask.join());
        }
        return result;
    }

}