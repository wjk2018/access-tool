package com.cnbi.util;

import org.beetl.core.GroupTemplate;
import org.beetl.core.Template;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName BeetlUtils
 * @Description 模板处理
 * @Author Wangjunkai
 * @Date 2020/5/18 10:48
 **/

@Component
public class BeetlUtils {

    static GroupTemplate groupTemplate;

    public static String get(String tempName, Map<String, Object> shareData){
        Template t = groupTemplate.getTemplate(tempName);
        t.binding("_root", shareData);
        return t.render();
    }

    @Autowired
    public void setGroupTemplate(GroupTemplate groupTemplate){
        BeetlUtils.groupTemplate = groupTemplate;
    }
}