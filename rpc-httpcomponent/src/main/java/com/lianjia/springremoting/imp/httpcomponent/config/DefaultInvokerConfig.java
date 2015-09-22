package com.lianjia.springremoting.imp.httpcomponent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.lianjia.springremoting.imp.httpcomponent.factory.RPCHttpClientFactoryBean;
import com.lianjia.springremoting.imp.httpcomponent.interceptor.HessianServiceInvokerInterceptor;
import com.lianjia.springremoting.imp.httpcomponent.interceptor.HttpServiceInvokerInterceptor;
import com.lianjia.springremoting.interceptor.AutowiredAnnotedTypeInterceptor;
import com.lianjia.springremoting.invoker.processor.AutowiredRPCServiceBeanPostProcessor;

/**
 * <p>
 * HttpClientFactory& interceptors &bean post processor
 * </p>
 * 
 * @author huisman
 * @since 1.0.0
 * @createAt 2015年9月21日 下午9:05:13
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved.
 */
@Configuration
public class DefaultInvokerConfig {
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

	/**
	 * <p>
	 * 处理@HessianService
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @return
	 */
	@Bean
	public AutowiredAnnotedTypeInterceptor hessianServiceInvokerInterceptor() {
		return new HessianServiceInvokerInterceptor();
	}
}
