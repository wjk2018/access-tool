package com.cnbi.service;

import cn.hutool.core.convert.Convert;
import com.cnbi.mapper.QueryMapper;
import com.cnbi.util.MetaObjectUtil;
import com.cnbi.util.calculate.ContextFormulaParse;
import com.cnbi.util.calculate.ExpCalculateUtils;
import com.cnbi.util.calculate.FormulaParse;
import com.cnbi.util.constant.ParamConstant;
import com.cnbi.util.entry.Data;
import com.cnbi.util.entry.QueryConfig;
import com.cnbi.util.handle.DataHandle;
import com.cnbi.util.handle.TableDataHandle;
import com.cnbi.util.handle.TempDataHandle;
import com.cnbi.util.handle.TextDataHandle;
import com.cnbi.util.period.PeriodUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

/**
 * @ClassName AccessCore
 * @Description 转换核心类
 * @Author Wangjunkai
 * @Date 2020/8/6 16:42
 **/

@Component
public class AccessCore {

    @Autowired
    ApplicationContext applicationContext;

    Map<String, DataHandle> handleMap = new HashMap();
    @PostConstruct
    public void init(){
        handleMap.put(ParamConstant.TABLE_CUBE, new TableDataHandle());
        handleMap.put(ParamConstant.CHART_CUBE, new TableDataHandle());
        handleMap.put(ParamConstant.TEMP_CUBE, new TempDataHandle());
        handleMap.put(ParamConstant.TEXT_CUBE, new TextDataHandle());
    }

    @Autowired
    QueryMapper queryMapper;

    @Autowired
    ForkJoinPool forkJoinPool;

    public Object query(Map<String, String> param) throws Exception {
        //1','2','3
        Object cube = param.get(ParamConstant.CUBEID);
        List<Data> datas = new ArrayList();
        if(Objects.nonNull(cube)){
            List<QueryConfig> queryConfig = queryMapper.getQueryConfig(cube.toString());
            ExpCalculateUtils expCalculateUtils = new ExpCalculateUtils();
            ContextFormulaParse formulaParse = new ContextFormulaParse();
            datas = forkJoinPool.submit(new ConfigTask(0, queryConfig.size(), queryConfig,
                    param, expCalculateUtils, formulaParse, queryMapper)).get();
        }
        //cubeId_beanName_methodName,cubeId_beanName_methodName
        String specialCubes = param.get(ParamConstant.SPECIAL_CUBE);
        if(Objects.nonNull(specialCubes)){
            String[] specialCubeArray = specialCubes.split(",");
            for (String specialCube : specialCubeArray) {
                String[] CubeConfig = specialCube.split("_");
                applicationContext.getBean(CubeConfig[1]);
                MetaObjectUtil.invokeMathod(applicationContext.getBean(CubeConfig[1]), CubeConfig[2],
                        new Class[]{Map.class, String.class, List.class}, new Object[]{param, CubeConfig[0], datas});
            }
        }
        if(datas.isEmpty()){
            throw new RuntimeException("数据为空");
        }else{
            String cubeType = param.get(ParamConstant.CUBE_TYPE);
            DataHandle dataHandle = handleMap.get(cubeType);
            if(Objects.nonNull(dataHandle)){
                return dataHandle.handle(datas, new BigDecimal(param.get(ParamConstant.UNIT)), forkJoinPool, param);
            }else {
                throw new IllegalArgumentException("type参数非法, 应该为");
            }

        }
    }

}

class ConfigTask extends RecursiveTask<List<Data>> {
    public ConfigTask(int begin, int end, List<QueryConfig> items, Map<String, String> param,
                      ExpCalculateUtils expCalculateUtils, ContextFormulaParse formulaParse, QueryMapper queryMapper) {
        this.begin = begin;
        this.end = end;
        this.items = items;
        this.param = param;
        this.expCalculateUtils = expCalculateUtils;
        this.formulaParse = formulaParse;
        this.queryMapper = queryMapper;
    }
    private static final FormulaParse FORMULA_PARSE = new FormulaParse();
    private static final  Integer  ADJUST_VALUE  =  30;
    private int begin;
    private int end;
    private List<QueryConfig> items;
    private Map<String, String> param;
    private ExpCalculateUtils expCalculateUtils;
    private ContextFormulaParse formulaParse;
    private QueryMapper queryMapper;
    List<Data> datas = new ArrayList<>();
    ConcurrentLinkedQueue<RuntimeException> exp = new ConcurrentLinkedQueue();
    @Override
    protected List<Data> compute() {
        List<QueryConfig> result = new ArrayList<>();
        List<QueryConfig> expResult = new ArrayList<>();
        if(end - begin < ADJUST_VALUE){
            for (QueryConfig r : items) {
                //处理期间，根据数据库的相对期间和当前期间算出数据的维度期间
                r.setPeriod(PeriodUtil.calperiod(param.get(ParamConstant.PERIOD), r.getYear(), r.getMonth()));
                //如果带公式，说明需要计算，移除后单独处理
                if (Objects.nonNull(r.getExp())) {
                    r.setExp(expCalculateUtils.parse(r.getExp(), formulaParse));
                    expResult.add(r);
                } else {
                    result.add(r);
                }
            }
            if(!result.isEmpty()){
                datas = queryMapper.queryData(result, param);
                datas.parallelStream().filter(d -> {
                    try {
                        String alias = formulaParse.getAlias(d.getCubeId().concat("_").concat(d.getCode()).concat("_").concat(d.getSort()));
                        //判断是否需要参与计算
                        if (Objects.nonNull(alias)) {
                            expCalculateUtils.SetValue(alias, Convert.toDouble(d.getVal(), 0D));
                        }
                        if(Objects.equals(d.getHide(), ParamConstant.Y)){
                            return false;
                        }
                    }catch (RuntimeException e){
                        exp.add(e);
                    }
                    return true;
                });
            }
            isError(exp);
            if(!expResult.isEmpty()){
                expResult.stream().forEach(e -> {
                    expHandle(expCalculateUtils, datas, e);
                });
            }
        }else{
            int middle = (begin + end)/2;
            ConfigTask leftTask = new ConfigTask(begin, middle, items, param, expCalculateUtils, formulaParse, queryMapper);
            ConfigTask rightTask = new ConfigTask(middle + 1, end, items, param, expCalculateUtils, formulaParse, queryMapper);
            leftTask.fork();
            rightTask.fork();
            datas.addAll(leftTask.join());
            datas.addAll(rightTask.join());
        }
        return datas;
    }

    public void isError(ConcurrentLinkedQueue<RuntimeException> exceptions){
        if(!exceptions.isEmpty()) {
            throw exceptions.poll();
        }
    }

    //表达式处理
    private void expHandle(ExpCalculateUtils expCalculateUtils, List<Data> datas, QueryConfig reportConf) {
        BigDecimal expVal = expCalculateUtils.getExpVal(reportConf.getExp());
        String key = Convert.toStr(expCalculateUtils.calculate(FORMULA_PARSE.parse(reportConf.getKey(), param)));
        datas.add(Data.builder().code(reportConf.getCode()).dimName(reportConf.getDimName().replaceAll("'", ""))
                .groupBy(reportConf.getGroupBy()).key(key)
                .cubeId(reportConf.getCubeId()).sort(reportConf.getSortby())
                .subfield(reportConf.getSubfield()).unit(reportConf.getUnit())
                .unitConversion(reportConf.getUnitConversion()).val(expVal).build());
    }
}
