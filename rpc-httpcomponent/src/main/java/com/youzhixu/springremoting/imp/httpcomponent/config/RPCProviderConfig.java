package com.youzhixu.springremoting.imp.httpcomponent.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.youzhixu.springremoting.exporter.processor.ServiceExporterBeanPostProcessor;
import com.youzhixu.springremoting.imp.httpcomponent.interceptor.HessianServiceExporterInterceptor;
import com.youzhixu.springremoting.imp.httpcomponent.interceptor.HttpServiceExporterInterceptor;
import com.youzhixu.springremoting.interceptor.ServiceExporterRegistryInterceptor;

/**
 * <p>
 * Service Provider & Interceptors
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月16日 上午11:39:02
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
@Configuration
public class RPCProviderConfig {
	@Bean
	public static ServiceExporterBeanPostProcessor serviceExporterBeanFactoryPostProcessor() {
		return new ServiceExporterBeanPostProcessor();
	}

	@Bean
	public ServiceExporterRegistryInterceptor httpServiceExporterInterceptor() {
		return new HttpServiceExporterInterceptor();
	}

	@Bean
	public ServiceExporterRegistryInterceptor hessianServiceExporterInterceptor() {
		return new HessianServiceExporterInterceptor();
	}
}
