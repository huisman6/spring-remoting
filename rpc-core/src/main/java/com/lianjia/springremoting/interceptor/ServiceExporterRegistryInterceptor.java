package com.lianjia.springremoting.interceptor;

import java.lang.annotation.Annotation;

import org.springframework.core.Ordered;

/**
 * <p>
 * 服务提供方暴露服务时可以拦截注册的Exporter类型
 * </p>
 * 
 * @author huisman
 * @since 1.0.0
 * @createAt 2015年9月19日 下午3:22:08
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
public interface ServiceExporterRegistryInterceptor extends Ordered {
	/**
	 * @param servcieInterface rpc服务接口
	 * @param serviceProvider 提供服务的实现方
	 * @return
	 * @since: 1.0.0
	 */
	Object resolveServcieExporter(Class<?> serviceInterface, Object serviceProvider);

	/**
	 * @since: 1.0.0
	 * @param memberAnnotations 字段、方法（Class Memeber)上的所有标注
	 * @param servcieInterface 要注入的类型
	 * @return
	 */
	boolean accept(Annotation[] memberAnnotations, Class<?> servcieInterface);
}
