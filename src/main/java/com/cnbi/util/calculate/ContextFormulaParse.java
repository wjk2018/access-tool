package com.cnbi.util.calculate;

import org.springframework.expression.EvaluationContext;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @ClassName ContextFormulaParse
 * @Description
 * @Author Wangjunkai
 * @Date 2020/5/28 22:22
 **/

public class ContextFormulaParse extends FormulaParse{
    private char firstAilas1 = 'a';
    private int index = 0;
    protected ConcurrentHashMap<String, String> scodeAndalias = new ConcurrentHashMap();
    public ContextFormulaParse() {

    }

    @Override
    public String handleToken(String exp, Object obj) {
        EvaluationContext context = (EvaluationContext) obj;
        exp = exp.trim();
        String alia = (String)scodeAndalias.get(exp);
        if (Objects.nonNull(alia)) {
            return "#".concat(alia);
        } else {
            String alias = "#";
            for(int i = 0; i < this.index / 26; ++i) {
                alias = alias.concat(String.valueOf(this.firstAilas1));
            }
            alias = alias.concat(String.valueOf((char)(this.index % 26 + this.firstAilas1)));
            ++this.index;
            scodeAndalias.put(exp, alias.substring(1));
            context.setVariable(alias.substring(1), 0D);
            return alias;
        }
    }

    public String getAlias(String scode){
        return scodeAndalias.get(scode);
    }
}