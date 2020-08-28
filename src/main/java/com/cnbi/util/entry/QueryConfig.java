package com.cnbi.util.entry;

import lombok.*;

/**
 * @ClassName QueryConfig
 * @Description
 * @Author Wangjunkai
 * @Date 2020/8/6 16:53
 **/

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class QueryConfig {
    /**
     * 查询对应维度表的scode
     */
    private String code;

    /**
     * cube所属模块
     */
    private String project;

    /**
     * cubeId
     */
    private String cubeId;

    /**
     * 查询的表名
     */
    private String tableName;

    /**
     * 查询字段：fact_a fact_b等  事实表字段名称
     */
    private String factFieldName;

    /**
     * 查询字段：dim_xxx  维度字段名称
     */
    private String dimFieldName;

    /**
     * 保留字段
     */
    private String condition;

    /**
     * 合计表达式
     */
    private String exp;

    /**
     * 查询年份范围(上年-100)
     */
    private String year;

    /**
     * 查询月份范围（上月-1）
     */
    private String month;

    /**
     * 维度名
     */
    private String dimName;

    /**
     * 排序
     */
    private String sortby;

    /**
     * 数据期间
     */
    private String period;

    /**
     * 行转列依据，有值要转，无不转
     */
    private String groupBy;


    private String company;

    /**
     * 返回给前端的key建组装
     */
    private String key;

    /**
     * 是否分栏, N不分，Y分
     */
    private String subfield;

    /**
     * 是否全部使用 condition
     */
    private String allCondition;


    /*单位*/
    private String unit;

    /**
     * 是否隐藏不展示
     */
    private String hide;

    /**
     * 是否需要单位转换
     */
    private String unitConversion;

    /**
     *  Y表示查询结果作为其他查询的依赖
     */
    private String depend;
}