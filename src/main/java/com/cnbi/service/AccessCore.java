package com.cnbi.service;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.StrUtil;
import com.alibaba.ttl.TtlRecursiveTask;
import com.cnbi.mapper.QueryMapper;
import com.cnbi.util.MetaObjectUtil;
import com.cnbi.util.Tool;
import com.cnbi.util.calculate.ContextFormulaParse;
import com.cnbi.util.calculate.ExpCalculateUtils;
import com.cnbi.util.calculate.FormulaParse;
import com.cnbi.util.constant.ParamConstant;
import com.cnbi.util.entry.Data;
import com.cnbi.util.entry.HandleDataQueue;
import com.cnbi.util.entry.QueryConfig;
import com.cnbi.util.handle.DataHandle;
import com.cnbi.util.handle.TableDataHandle;
import com.cnbi.util.handle.TempDataHandle;
import com.cnbi.util.handle.TextDataHandle;
import com.cnbi.util.period.PeriodUtil;
import com.google.common.base.Joiner;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.*;

/**
 * @ClassName AccessCore
 * @Description 取数核心类
 * @Author Wangjunkai
 * @Date 2020/8/6 16:42
 **/
@Slf4j
@Component
public class AccessCore {

    @Autowired
    ApplicationContext applicationContext;

    Map<String, DataHandle> handleMap = new HashMap();


    @Autowired
    ForkJoinPool forkJoinPool;


    @PostConstruct
    public void init(){
        handleMap.put(ParamConstant.TABLE_CUBE, new TableDataHandle());
        handleMap.put(ParamConstant.CHART_CUBE, new TableDataHandle());
        handleMap.put(ParamConstant.TEMP_CUBE, new TempDataHandle());
        handleMap.put(ParamConstant.TEXT_CUBE, new TextDataHandle());
    }

    @Autowired
    QueryMapper queryMapper;

    public Object query(Map<String, String> param) throws Exception {
        List<String> cubes = queryMapper.queryCube(param);
        String cube = "'" + Joiner.on("','").join(cubes) + "'";
        Assert.notNull(cube, "project =》{} 的cube 不能为null", param.get(ParamConstant.PROJECT));
        List<QueryConfig> queryConfig = queryMapper.getQueryConfig(cube, param.get(ParamConstant.PROJECT));
        return query(param, queryConfig, this.queryCubeConfig(param));
    }

    public Object query(Map<String, String> param, String cube) throws Exception {
        Assert.notNull(cube, "project =》{} 的cube 不能为null", param.get(ParamConstant.PROJECT));
        List<QueryConfig> queryConfig = queryMapper.getQueryConfig(cube, param.get(ParamConstant.PROJECT));
        return query(param, queryConfig, this.queryCubeConfig(param));
    }

    /**
     * @MethodName: getQueryConfig
     * @Description: 查询配置
     * @Param:  * @param cube
     * @param project
     * @Return: java.util.List<com.cnbi.util.entry.QueryConfig>
     * @Author: wangjunkai
     * @Date: 2020/9/3 11:30
    **/
    public List<QueryConfig> getQueryConfig(String cube, String project){
        return queryMapper.getQueryConfig(cube, project);
    }

    /**
     * @MethodName: queryCubeConfig
     * @Description: 查询cubeConfig
     * @Param:  * @param param
     * @Return: java.util.List<java.util.Map<java.lang.String,java.lang.Object>>
     * @Author: wangjunkai
     * @Date: 2020/9/3 11:49
    **/
    public List<Map<String, Object>> queryCubeConfig(Map<String, String> param){
        return queryMapper.queryCubeConfig(param);
    }

    public Object query(Map<String, String> param, List<QueryConfig> queryConfig, List<Map<String, Object>> cubeConfigs) throws Exception {
        ConcurrentLinkedQueue<Exception> exp = new ConcurrentLinkedQueue();
        HashMap<String, Object> specialResult = new HashMap<>();
        //1','2','3
        ConcurrentLinkedQueue<Data> datas = new HandleDataQueue(new BigDecimal(param.get(ParamConstant.UNIT)),
                ParamConstant.TEMP_CUBE.equals(param.get(ParamConstant.CUBE_TYPE)));
        if(Objects.nonNull(queryConfig) && !queryConfig.isEmpty()){
            ExpCalculateUtils expCalculateUtils = new ExpCalculateUtils();
            ContextFormulaParse formulaParse = new ContextFormulaParse();
            datas = forkJoinPool.submit(new ConfigTask(0, queryConfig.size(), queryConfig,
                    param, expCalculateUtils, formulaParse, queryMapper, exp)).get();
        }
        //cubeId_beanName_methodName_Y,cubeId_beanName_methodName_N
        String specialCubes = param.get(ParamConstant.SPECIAL_CUBE);
        if(StrUtil.isNotBlank(specialCubes)){
            String[] specialCubeArray = specialCubes.split(",");
            for (String specialCube : specialCubeArray) {
                String[] cubeConfig = specialCube.split("_");
                //不走DataHand
                Object bean = applicationContext.getBean(cubeConfig[1]);
                if(cubeConfig.length == 4 && ParamConstant.Y.equals(cubeConfig[3])) {
                        MetaObjectUtil.invokeMathod(bean, cubeConfig[2],
                                new Class[]{Map.class, String.class, Map.class}, new Object[]{param, cubeConfig[0], specialResult});
                }else{
                        MetaObjectUtil.invokeMathod(bean, cubeConfig[2],
                                new Class[]{Map.class, String.class, ConcurrentLinkedQueue.class}, new Object[]{param, cubeConfig[0], datas});
                }
            }
        }
        isError(exp);
        if(datas.isEmpty()){
            return specialResult;
        }else{
            Map<String, Object> cubeConfig = Tool.list2Map(cubeConfigs, "cubeId", "cubeConfig");
            String cubeType = param.get(ParamConstant.CUBE_TYPE);
            DataHandle dataHandle = handleMap.get(cubeType);
            ArrayList<Data> result = new ArrayList<>();
            result.addAll(datas);
            if(Objects.nonNull(dataHandle)){
                HashMap<String, Map<String, Object>> handle = dataHandle.handle(result,
                        new BigDecimal(param.get(ParamConstant.UNIT)), forkJoinPool, param, cubeConfig, exp);
                isError(exp);
                specialResult.putAll(handle);
            }else {
                throw new IllegalArgumentException("type参数非法, 应该为chart/table/text/temp");
            }
            return specialResult;

        }
    }

    public void isError(ConcurrentLinkedQueue<Exception> exceptions) throws Exception {
        if(!exceptions.isEmpty()) {
            throw exceptions.poll();
        }
    }

}
@Slf4j
class ConfigTask extends TtlRecursiveTask<ConcurrentLinkedQueue<Data>> {

    private final static String CAN_COMPUTE_FEILD = "cubeId";

    public ConfigTask(int begin, int end, List<QueryConfig> items, Map<String, String> param,
                      ExpCalculateUtils expCalculateUtils, ContextFormulaParse formulaParse, QueryMapper queryMapper, ConcurrentLinkedQueue<Exception> exp) {
        this.begin = begin;
        this.end = end;
        this.items = items;
        this.param = param;
        this.expCalculateUtils = expCalculateUtils;
        this.formulaParse = formulaParse;
        this.queryMapper = queryMapper;
        this.exp = exp;
        boolean isTemp = ParamConstant.TEMP_CUBE.equals(param.get(ParamConstant.CUBE_TYPE));
        datas = new HandleDataQueue(new BigDecimal(param.get(ParamConstant.UNIT)), isTemp);
    }
    private static final FormulaParse FORMULA_PARSE = new FormulaParse();
    private static final Integer ADJUST_VALUE  =  10;
    private int begin;
    private int end;
    private List<QueryConfig> items;
    private Map<String, String> param;
    private ExpCalculateUtils expCalculateUtils;
    private ContextFormulaParse formulaParse;
    private QueryMapper queryMapper;
    HandleDataQueue datas;
    Map<String, String> depends = new ConcurrentHashMap<>();
    ConcurrentLinkedQueue<Exception> exp;
    @Override
    protected ConcurrentLinkedQueue<Data> compute() {
        //end上一层加过1
        boolean brother = false;
        try {
            brother = Tool.canCompute(items, begin, end - 1, ConfigTask.CAN_COMPUTE_FEILD) ||
                    !Tool.canCompute(items, begin, (begin + end)/2, ConfigTask.CAN_COMPUTE_FEILD);
        } catch (NoSuchFieldException e) {
            Tool.addException(exp, e);
        }
        if(end - begin < ADJUST_VALUE || brother){
            List<QueryConfig> result = new ArrayList<>();
            TreeSet<QueryConfig> expResult = new TreeSet<>(Comparator.comparingInt((QueryConfig o) -> Integer.parseInt(o.getCubeId())).thenComparingInt(o -> Integer.parseInt(o.getSortby())));
            List<QueryConfig> beforeResult = new ArrayList<>();
            boolean needForEach = false;
            for (QueryConfig r : items.subList(begin, end)) {
                //处理期间，根据数据库的相对期间和当前期间算出数据的维度期间
                if(StrUtil.isNotBlank(r.getMonth()) && StrUtil.isNotBlank(r.getYear())){
                    r.setPeriod(PeriodUtil.calperiod(param.get(ParamConstant.PERIOD), r.getYear(), r.getMonth()));
                }
                //如果带公式，说明需要计算，移除后单独处理
                if (Objects.nonNull(r.getExp())) {
                    r.setExp(expCalculateUtils.parse(r.getExp(), formulaParse));
                    expResult.add(r);
                } else if(!ParamConstant.N.equals(r.getDepend())){
                    //depend 为Y作为参数，放入param中，在condition中使用，也可以是QueryConfig中的相关字段（格式为cubeId_sort_字段名），来代替QueryConfig中的值，最常见的是代替code字段
                    if(!ParamConstant.Y.equals(r.getDepend())){
                        //加入遍历标识，如果depend都是Y直接放入param中，如果有不是Y的（是QueryConfig中的相关字段），需要遍历result放入
                        needForEach = true;
                    }
                    beforeResult.add(r);
                } else {
                    result.add(r);
                }
            }
            if(!beforeResult.isEmpty()){
                List<Data> queryDatas = queryMapper.queryData(beforeResult, param);
                queryDatas.parallelStream().filter(d -> {
                    //过滤hide为Y的数据，这些数据只参与计算或者依赖查询
                    return Objects.equals(d.getHide(), ParamConstant.N);
                }).forEach(d -> {
                    try {
                        datas.add(d);
                        String alias = formulaParse.getAlias(d.getCubeId().concat("_").concat(d.getCode()).concat("_").concat(d.getSort()));
                        //判断是否需要参与计算
                        if (Objects.nonNull(alias)) {
                            expCalculateUtils.SetValue(alias, Convert.toDouble(d.getVal(), 0D));
                        }
                        //判断查询结果作为其他查询的依赖
                        if(Objects.nonNull(d.getDepend())){
                            if(ParamConstant.Y.equals(d.getDepend())){
                                depends.put(d.getCubeId() + "_" + d.getSort(), String.valueOf(d.getVal()));
                            }else{
                                String[] dependArary = d.getDepend().split("_");
                                depends.put(dependArary[0] + "_" + dependArary[1], dependArary[2] + "_" + d.getVal());
                            }
                        }
                    }catch (RuntimeException e){
                        exp.add(e);
                        throw e;
                    }
                });
            }
            if(!result.isEmpty()){
                HashMap<String, String> cloneParam = new HashMap<>();
                cloneParam.putAll(param);
                cloneParam.putAll(depends);
                if(needForEach){
                    for (QueryConfig c : result) {
                        String depentVal = depends.get(c.getCubeId() + "_" + c.getSortby());
                        if(StrUtil.isBlank(depentVal)){
                            continue;
                        }
                        int index = depentVal.indexOf("_");
                        try {
                            MetaObjectUtil.setObjectField(c, depentVal.substring(index + 1), depentVal.substring(0, index));
                        } catch (NoSuchFieldException e) {
                            e.printStackTrace();
                            log.error("{}_{}向QueryConfig中塞{}失败", c.getCubeId(), c.getSortby(), depentVal);
                        }
                    }
                }
                List<Data> queryDatas = queryMapper.queryData(result, cloneParam);
                queryDatas.parallelStream().filter(d -> {
                    try {
                        String alias = formulaParse.getAlias(d.getCubeId().concat("_").concat(d.getCode()).concat("_").concat(d.getSort()));
                        //判断是否需要参与计算
                        if (Objects.nonNull(alias)) {
                            expCalculateUtils.SetValue(alias, Convert.toDouble(d.getVal(), 0D));
                        }
                        //过滤hide为Y的数据，这些数据只参与计算或者依赖查询
                        return Objects.equals(d.getHide(), ParamConstant.N);
                    }catch (RuntimeException e){
                        if(exp.isEmpty()){
                            exp.add(e);
                        }
                        throw e;
                    }
                }).forEach(datas :: add);
            }
            if(!expResult.isEmpty()){
                expResult.stream().forEach(e -> {
                    expHandle(expCalculateUtils, datas, e);
                });
            }
        }else{
            int middle = (begin + end)/2;
            //让同一个cube的数据始终在同一个task中
            while(items.get(middle).getCubeId().equals(items.get(middle + 1)) && middle + 1 < end){
                middle ++;
            }
            //subList包含begin,不包含end，所以middle要+1
            middle ++;
            ConfigTask leftTask = new ConfigTask(begin, middle, items, param, expCalculateUtils, formulaParse, queryMapper, exp);
            ConfigTask rightTask = new ConfigTask(middle, end, items, param, expCalculateUtils, formulaParse, queryMapper, exp);
            leftTask.fork();
            rightTask.fork();
            datas.addAll(leftTask.join());
            datas.addAll(rightTask.join());
        }
        return datas;
    }

    //表达式处理
    private void expHandle(ExpCalculateUtils expCalculateUtils, ConcurrentLinkedQueue<Data> datas, QueryConfig reportConf) {
        Object expVal = expCalculateUtils.getExpVal(reportConf.getExp());
        String key = Convert.toStr(expCalculateUtils.calculate(FORMULA_PARSE.parse(reportConf.getKey(), param)));
        datas.add(Data.builder().code(reportConf.getCode()).dimName(reportConf.getDimName().replaceAll("'", ""))
                .groupBy(reportConf.getGroupBy().replace("'", "")).key(key)
                .cubeId(reportConf.getCubeId()).sort(reportConf.getSortby())
                .subfield(reportConf.getSubfield()).unit(reportConf.getUnit())
                .unitConversion(reportConf.getUnitConversion()).val(expVal)
                .hide(reportConf.getHide()).depend(reportConf.getDepend()).build());
        String alias = formulaParse.getAlias(reportConf.getCubeId().concat("_").concat(reportConf.getCode()).concat("_").concat(reportConf.getSortby()));
        //判断是否需要参与计算
        if (Objects.nonNull(alias)) {
            expCalculateUtils.SetValue(alias, Convert.toDouble(expVal, 0D));
        }
    }

}

