package com.lianjia.springremoting.imp.eureka.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.lianjia.springremoting.imp.eureka.url.EurekaUrlResolver;
import com.lianjia.springremoting.imp.httpcomponent.config.DefaultInvokerConfig;
import com.lianjia.springremoting.url.UrlResolver;

/**
 * @author huisman
 * @createAt 2015年9月22日 下午2:44:30
 * @since 1.0.0
 * @Copyright (c) 2015, Lianjia Group All Rights Reserved.
 */
@Configuration
@Import(DefaultInvokerConfig.class)
public class EurekaRPCInvokerConfig {
	@Bean(name = UrlResolver.BEAN_NAME)
	public UrlResolver urlResolver() {
		return new EurekaUrlResolver();
	}
}
