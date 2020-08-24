package com.cnbi.config;

import org.apache.ibatis.annotations.Mapper;
import org.mybatis.spring.annotation.MapperScan;
import org.mybatis.spring.boot.autoconfigure.MybatisAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName AccessAutoConfiguration
 * @Description 自动配置
 * @Author Wangjunkai
 * @Date 2020/8/20 14:35
 **/
@Configuration
@ConditionalOnClass(MybatisAutoConfiguration.class)
@AutoConfigureAfter(MybatisAutoConfiguration.class)
@MapperScan(basePackages = "com.cnbi.Mapper", annotationClass = Mapper.class)
@ComponentScan("com.cnbi")
public class AccessAutoConfiguration {

}