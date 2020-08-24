package com.cnbi.mapper;

import com.cnbi.util.entry.Data;
import com.cnbi.util.entry.QueryConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import java.util.List;
import java.util.Map;

@Mapper
public interface QueryMapper {

    @Select(" select * from (select SCODE as \"code\", cube_id AS \"cubeId\", STABLE  as \"tableName\", FACT_FIELD  as \"factFieldName\", DIM_FIELD AS \"dimFieldName\",SCONDITION  as \"condition\", EXP  as \"exp\", SYEAR  as \"year\", SMONTH  as \"month\",SORTBY as \"sortby\",DIM_NAME as \"dimName\", nvl(GROUPBY, 'no') as \"groupBy\", skey as \"key\",UNITCONVERSION, nvl(subfield, '0') subfield, ALLCONDITION, sunit unit, hide as \"hide\" from sys_query_config where cube_id in (${cubeId}) and ISDELETE = 'N') order by \"cubeId\", \"sortby\"")
    List<QueryConfig> getQueryConfig(@Param("cubeId") String cubeId);

    @Select("<script>" +
            "<foreach collection=\"list\" item=\"item\" separator=\"union all\"> " +
            "<if test=\"item.allCondition == 'N'.toString()\"> " +
                "select dimName, code, cubeId, sort, groupBy, key, subfield, ${item.factFieldName} val, " +
                "unit, unitConversion, #{item.hide} hide from " +
                "(select ${item.dimName} as dimName,  #{item.code} as code, #{item.cubeId} cubeId, #{item.sortby} sort, #{item.groupBy} " +
                "groupBy, ${item.key} as key, #{item.subfield} subfield, #{item.period} period, " +
                "#{item.unit} unit, " +
                "#{item.unitConversion} unitConversion from dual) " +
                "a left join ${item.tableName} b on " +
                "<trim prefixOverrides=\"and\"> " +
                    "<if test=\"item.dimFieldName != null and item.dimFieldName != ''\"> " +
                        " b.${item.dimFieldName} = a.code " +
                    "</if> " +
                    "<if test=\"item.condition != null and item.condition != ''\"> " +
                        "and ${item.condition} " +
                    "</if> " +
                "</trim> " +
            "</if> " +
            "<if test=\"item.allCondition == 'Y'.toString()\">" +
                "select dimName, code, cubeId, sort, groupBy, key, subfield, ${item.factFieldName} val, unit, unitConversion, #{item.hide} hide from " +
                "(select ${item.dimName} as dimName,  #{item.code} as code, #{item.cubeId} cubeId, #{item.sortby} sort, #{item.groupBy} " +
                "groupBy, ${item.key} as key, #{item.subfield} subfield, #{item.unit} unit, #{item.unitConversion} unitConversion from dual) " +
                "a left join ${item.tableName} b on ${item.condition}" +
            "</if>" +
            "</foreach> " +
            "</script>")
    List<Data> queryData(@Param("list") List<QueryConfig> configs, @Param("param") Map<String, String> param);
}
