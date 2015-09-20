package com.youzhixu.springremoting.imp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.youzhixu.springremoting.imp.interceptor.DefaultInvokerAutowiredInterceptor;
import com.youzhixu.springremoting.invoker.processor.AutowiredRPCServiceBeanPostProcessor;

/**
 * <p>
 * rpc invoker config
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月16日 上午11:39:02
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
@Configuration
public class RPCInvokerConfig {
	/**
	 * <p>
	 * BeanPostProcessor 必须声明为静态
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @return
	 */
	@Bean(name = "autowiredRPCServiceBeanPostProcessor")
	public static AutowiredRPCServiceBeanPostProcessor customizePostProcessor() {
		return new AutowiredRPCServiceBeanPostProcessor();
	}
}