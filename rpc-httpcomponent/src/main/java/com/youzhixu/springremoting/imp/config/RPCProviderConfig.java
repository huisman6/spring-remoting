package com.youzhixu.springremoting.imp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.youzhixu.springremoting.exporter.config.ServiceExporterBeanPostProcessor;
import com.youzhixu.springremoting.imp.interceptor.DefaultExporterRegistryInterceptor;

/**
 * <p>
 * remoting config
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月16日 上午11:39:02
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
@Configuration
public class RPCProviderConfig {
	/**
	  * <p>
	  *		BeanPostProcessor 必须声明为静态
	  * </p> 
	  * @return
	  * @since: 1.0.0
	 */
	@Bean(name="serviceExporterBeanPostProcessor")
	public static  ServiceExporterBeanPostProcessor createExporterbeanPostProcessor(){
		return new ServiceExporterBeanPostProcessor(DefaultExporterRegistryInterceptor.class.getPackage().getName());
	}
}

