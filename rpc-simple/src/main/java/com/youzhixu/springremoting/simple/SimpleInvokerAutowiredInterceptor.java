package com.youzhixu.springremoting.simple;

import java.lang.annotation.Annotation;

import org.springframework.core.env.Environment;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

import com.youzhixu.springremoting.constant.ServicePath;
import com.youzhixu.springremoting.exporter.annotation.HessianService;
import com.youzhixu.springremoting.exporter.annotation.HttpService;
import com.youzhixu.springremoting.interceptor.AutowiredAnnotedTypeInterceptor;
import com.youzhixu.springremoting.invoker.annotation.Remoting;

/**
 * <p>
 *
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月18日 下午12:47:45
 * @since 1.0.0
 * @Copyright (c) 2015, Lianjia Group All Rights Reserved.
 */

public class SimpleInvokerAutowiredInterceptor implements AutowiredAnnotedTypeInterceptor {

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public Object resolveAutowiredValue(Class<?> autowiredType, Environment env) {
		HttpService httpService = autowiredType.getAnnotation(HttpService.class);
		if (httpService != null) {
			return createHttpInvokerBean(httpService.value(), autowiredType, env);
		} 
		
		HessianService hessianService = autowiredType.getAnnotation(HessianService.class);
		if (hessianService != null) {
			return createHessianInvokerBean(hessianService.value(), autowiredType, env);
		}
		return null;
	}

	/**
	 * <p>
	 * 创建@HttpService标准的rpc invoker 代理
	 * </p>
	 * 
	 * @param appName
	 * @param serviceInterface
	 * @return
	 * @since: 1.0.0
	 */
	protected Object createHttpInvokerBean(String appName, Class<?> serviceInterface,
			Environment env) {
		HttpInvokerProxyFactoryBean hp = new HttpInvokerProxyFactoryBean();
		hp.setServiceInterface(serviceInterface);
		hp.setServiceUrl(getServiceUrl(appName, serviceInterface, env));
		hp.afterPropertiesSet();
		return hp.getObject();
	}

	/**
	 * <p>
	 * 创建@HessianService标准的rpc invoker 代理
	 * </p>
	 * 
	 * @param appName
	 * @param serviceInterface
	 * @return
	 * @since: 1.0.0
	 */
	protected Object createHessianInvokerBean(String appName, Class<?> serviceInterface,
			Environment env) {
		HessianProxyFactoryBean hp = new HessianProxyFactoryBean();
		hp.setServiceInterface(serviceInterface);
		hp.setServiceUrl(getServiceUrl(appName, serviceInterface, env));
		hp.afterPropertiesSet();
		return hp.getObject();
	}

	private String getServiceUrl(String appName, Class<?> serviceInterface, Environment env) {
		String host = env.getProperty("rpc." + appName + ".url");
		if (host == null || host.trim().isEmpty()) {
			throw new IllegalArgumentException(
					"app:"
							+ appName
							+ ",serviceInterface="
							+ serviceInterface.getName()
							+ ",couldn't found rpc."
							+ appName
							+ ".url from Spring Environment(System property,System Environment,PropertySource)");
		}
		return host + ServicePath.PREFIX + "/" + serviceInterface.getName();
	}


	/**
	 * 负责Remoting.class的值解析
	 */
	@Override
	public boolean accept(Annotation[] memberAnnotations, Class<?> autowiredType) {
		if (memberAnnotations == null || memberAnnotations.length == 0) {
			return false;
		}
		for (Annotation annotation : memberAnnotations) {
			return annotation.annotationType() == Remoting.class;
		}
		return false;
	}

}
