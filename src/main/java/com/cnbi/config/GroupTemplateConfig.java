package com.cnbi.config;

import org.beetl.core.GroupTemplate;
import org.beetl.core.resource.ClasspathResourceLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @ClassName:  GroupTemplateConfig   
 * @Description: Beetl 
 * @author: cnbizhh 
 * @date:   2019年11月11日 下午1:32:41   
 *     
 * @Copyright: 2019 www.cnbisoft.com Inc. All rights reserved. 
 * 注意：本内容仅限于安徽经邦软件技术有限公司内部传阅，禁止外泄以及用于其他的商业目 
 */
@Configuration
public class GroupTemplateConfig {
	
	@Value("${cnbi.path.tpl}")
	String rootPath;
	
	/**
	 * 
	 * @Title: init   
	 * @Description: 初始化模版  采用的文件加载
	 * @throws IOException      
	 * @return: GroupTemplate      
	 * @throws
	 */
	@Bean
	public GroupTemplate init() throws IOException{
		ClasspathResourceLoader resourceLoader = new ClasspathResourceLoader(rootPath, "utf-8");
		org.beetl.core.Configuration cfg = org.beetl.core.Configuration.defaultConfiguration();
		GroupTemplate gt = new GroupTemplate(resourceLoader, cfg);
		return gt;
	}

}
