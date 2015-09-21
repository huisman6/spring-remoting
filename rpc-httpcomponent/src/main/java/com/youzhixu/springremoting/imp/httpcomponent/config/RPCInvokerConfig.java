package com.youzhixu.springremoting.imp.httpcomponent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.youzhixu.springremoting.imp.httpcomponent.factory.RPCHttpClientFactoryBean;
import com.youzhixu.springremoting.imp.httpcomponent.interceptor.HessianServiceInvokerInterceptor;
import com.youzhixu.springremoting.imp.httpcomponent.interceptor.HttpServiceInvokerInterceptor;
import com.youzhixu.springremoting.interceptor.AutowiredAnnotedTypeInterceptor;
import com.youzhixu.springremoting.invoker.processor.AutowiredRPCServiceBeanPostProcessor;
import com.youzhixu.springremoting.url.SimpleUrlResolver;
import com.youzhixu.springremoting.url.UrlResolver;

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
public class RPCInvokerConfig {
	/**
	 * <p>
	 * 客户端如何解析服务提供者的url
	 * </p>
	 * 
	 * @return
	 * @since: 1.0.0
	 */
	@Bean(name = "urlResolver")
	public UrlResolver urlResolver() {
		return new SimpleUrlResolver();
	}

	@Bean(name = RPCHttpClientFactoryBean.RPC_HTTP_CLIENT)
	public Object rpcHttpClientFactoryBean() {
		return new RPCHttpClientFactoryBean();
	}

	@Bean
	public static AutowiredRPCServiceBeanPostProcessor autowiredRPCServiceBeanPostProcessor() {
		return new AutowiredRPCServiceBeanPostProcessor();
	}

	/**
	 * <p>
	 * 处理@HttpService
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @return
	 */
	@Bean
	public AutowiredAnnotedTypeInterceptor httpServiceInvokerInterceptor() {
		return new HttpServiceInvokerInterceptor();
	}

	@Bean
	public AutowiredAnnotedTypeInterceptor hessianServiceInvokerInterceptor() {
		return new HessianServiceInvokerInterceptor();
	}


}
