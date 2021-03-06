package com.lianjia.springremoting.imp.httpcomponent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.lianjia.springremoting.url.SimpleUrlResolver;
import com.lianjia.springremoting.url.UrlResolver;

/**
 * <p>
 * UrlResolver&HttpClientFactory& interceptors &bean post processor
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月16日 上午11:39:02
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
@Configuration
@Import(DefaultInvokerConfig.class)
public class SimpleRPCInvokerConfig {
	/**
	 * <p>
	 * 客户端如何解析服务提供者的url
	 * </p>
	 * 
	 * @return
	 * @since: 1.0.0
	 */
	@Bean(name = UrlResolver.BEAN_NAME)
	public UrlResolver urlResolver() {
		return new SimpleUrlResolver();
	}
}
