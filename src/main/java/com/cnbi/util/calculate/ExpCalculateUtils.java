package com.cnbi.util.calculate;

import cn.hutool.core.convert.Convert;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * @ClassName ExpCalculateUtils
 * @Description
 * @Author Wangjunkai
 * @Date 2020/5/28 20:43
 **/

public class ExpCalculateUtils {

    private ExpressionParser parser = new SpelExpressionParser();

    private EvaluationContext context = new StandardEvaluationContext();



    public String parse(String exp, FormulaParse parse) throws RuntimeException {
        return parse.parse(exp, context);
    }

    public void SetValue(String key, Double value){
        context.setVariable(key, value);
    }

    public Object getExpVal(String exp) {
        try {
            Double value = parser.parseExpression(exp).getValue(context, Double.class);
            value = value.isNaN()? 0 : (value.isInfinite() ? 1 : value);
            return BigDecimal.valueOf(value);
        }catch (Exception e){
            return exp;
        }
    }

    public Object calculate(String exp){
        Expression expression = parser.parseExpression(exp);
        try {
            Object value = expression.getValue();
            return Convert.toStr(value);
        }catch (RuntimeException e){
            return expression.getExpressionString();
        }
    }
}