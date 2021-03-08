package com.cnbi.util.entry;

import cn.hutool.core.convert.Convert;
import com.cnbi.util.constant.ParamConstant;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Objects;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * @ClassName HandleDataQueue
 * @Description
 * @Author Wangjunkai
 * @Date 2020/12/20 12:49
 **/

public class HandleDataQueue extends ConcurrentLinkedQueue<Data> {

    private BigDecimal unit;
    private boolean isTemp;

    public HandleDataQueue(BigDecimal unit, boolean isTemp){
        this.unit = unit;
        this.isTemp = isTemp;
    }

    /**
     * 对数据进行单位转换和保留两位小数
     * @param data
     * @return
     */
    @Override
    public boolean add(Data data) {
        Object val = data.getVal();
        if(val instanceof BigDecimal){
            data.setVal(getVal(data));
        }
        return super.add(data);
    }

    private Object getVal(Data data) {
        if(Objects.equals(data.getUnitConversion(), ParamConstant.UNIT_CONVERSION)){
            if(Objects.isNull(data.getVal())){
                return BigDecimal.ZERO;
            }else{
                BigDecimal convert = Convert.convert(BigDecimal.class, data.getVal());
                if(Objects.equals(data.getUnit(), ParamConstant.UNIT)){
                    return convert.divide(this.unit, 2, RoundingMode.HALF_UP);
                }else{//结论性文字指标为%的，模板中除以了100
                    return convert.multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP);
                }
            }
        }
        return Objects.isNull(data.getVal())?(Objects.isNull(data.getUnit())?"":BigDecimal.ZERO):data.getVal();
    }

    public static void main(String[] args) {
        Double a = 0D;
        Double b = 0.0D;
        double v = a / b;
        System.out.println(v);
    }
}