package com.youzhixu.springremoting.imp.httpcomponent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.youzhixu.springremoting.invoker.config.DefaultInvokerConfig;
import com.youzhixu.springremoting.url.SimpleUrlResolver;
import com.youzhixu.springremoting.url.UrlResolver;

/**
 * <p>
 * UrlResolver&HttpClientFactory
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月16日 上午11:39:02
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
@Configuration
@Import(DefaultInvokerConfig.class)
public class RPCInvokerConfig{
	/**
	  * <p>
	  *  客户端如何解析服务提供者的url
	  * </p> 
	  * @return
	  * @since: 1.0.0
	 */
	@Bean(name="urlResolver")
	public UrlResolver urlResolver(){
		return new SimpleUrlResolver();
	}
}