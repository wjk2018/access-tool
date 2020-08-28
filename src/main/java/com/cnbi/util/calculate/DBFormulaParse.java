package com.cnbi.util.calculate;

import com.cnbi.mapper.QueryMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName DBFormulaParse
 * @Description 利用sql处理SYS_QUERY_CONFIG中CUBE_CONFIG变量
 *          如 dims:[{id:"sname",text:"科目"}],facts:[{id:"#{to_number(substr(param.period,0,4) || '00') - 1}",text:"#{to_number(substr(param.period,0,4)) - 1 || '年'}"}]
 * @Author Wangjunkai
 * @Date 2020/8/24 18:29
 **/
@Component
public class DBFormulaParse extends FormulaParse {


    private static QueryMapper queryMapper;

    @Override
    public String handleToken(String exp, Object obj) throws RuntimeException {
        Map<String, String> param = (Map<String, String>) obj;
        return queryMapper.queryFromDual(exp, param);
    }

    @Autowired
    public void setQueryMapper(QueryMapper queryMapper){
        this.queryMapper = queryMapper;
    }
}