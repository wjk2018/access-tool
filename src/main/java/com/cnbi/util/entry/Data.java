package com.cnbi.util.entry;

import lombok.*;

import java.io.Serializable;

/**
 * @ClassName ReportData
 * @Description
 * @Author Wangjunkai
 * @Date 2020/5/14 15:38
 **/
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode
@Builder(toBuilder = true)
public class Data implements Serializable {
    private String code;
    private String cubeId;
    private String dimName;
    private String sort;
    private String groupBy;
    private Object val;
    private String key;
    private String subfield;
    private String unit;
    private String unitConversion;
    private String hide;
}