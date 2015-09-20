package com.youzhixu.springremoting.exporter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.youzhixu.springremoting.exporter.processor.ServiceExporterBeanPostProcessor;
import com.youzhixu.springremoting.interceptor.ServiceExporterRegistryInterceptor;
import com.youzhixu.springremoting.interceptor.provider.DefaultExporterRegistryInterceptor;

/**
 * <p>
 *  默认配置Service processor & Interceptors
 * </p> 
 * @author huisman 
 * @since 1.0.0
 * @createAt 2015年9月20日 下午8:44:16
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved. 
 */
@Configuration
public class DefaultProviderConfig {
	/**
	  * <p>
	  *	BeanPostProcessor 必须声明为静态
	  * </p> 
	  * @return
	  * @since: 1.0.0
	 */
	@Bean(name="serviceExporterBeanPostProcessor")
	public static  ServiceExporterBeanPostProcessor defaultExporterbeanPostProcessor(){
		return new ServiceExporterBeanPostProcessor();
	}
	/**
	  * <p>
	  *  默认的Exporter拦截器
	  * </p> 
	  * @return
	  * @since: 1.0.0
	 */
	@Bean(name="serviceExporterRegistryInterceptor")
	public ServiceExporterRegistryInterceptor defaultExporterRegistryInterceptor(){
		return new DefaultExporterRegistryInterceptor();
	}
}


