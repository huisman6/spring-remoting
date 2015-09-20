package com.youzhixu.springremoting.invoker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.youzhixu.springremoting.interceptor.AutowiredAnnotedTypeInterceptor;
import com.youzhixu.springremoting.interceptor.provider.DefaultInvokerAutowiredInterceptor;
import com.youzhixu.springremoting.invoker.processor.AutowiredRPCServiceBeanPostProcessor;

/**
 * <p>
 *  默认Invoker processor以及Interceptors配置
 * </p> 
 * @author huisman 
 * @since 1.0.0
 * @createAt 2015年9月20日 下午8:47:23
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved. 
 */
@Configuration
public class DefaultInvokerConfig {
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
	
	/**
	  * <p>
	  *  配置默认拦截器
	  * </p> 
	  * @return
	  * @since: 1.0.0
	 */
	@Bean(name = "autowiredAnnotedTypeInterceptor")
	public static AutowiredAnnotedTypeInterceptor defaultAnnotedTypeInterceptor() {
		return new DefaultInvokerAutowiredInterceptor();
	}
}


