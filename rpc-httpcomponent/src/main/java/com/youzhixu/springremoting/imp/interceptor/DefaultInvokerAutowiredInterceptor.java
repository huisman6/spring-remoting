package com.youzhixu.springremoting.imp.interceptor;

import java.lang.annotation.Annotation;

import org.springframework.context.EnvironmentAware;
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
 * @author huisman 
 * @since 1.0.0
 * @createAt 2015年9月20日 下午12:16:08
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved. 
 */
public class DefaultInvokerAutowiredInterceptor implements AutowiredAnnotedTypeInterceptor,EnvironmentAware{
	private Environment env;
	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public Object resolveAutowiredValue(Class<?> autowiredType) {
		HttpService httpService = autowiredType.getAnnotation(HttpService.class);
		if (httpService != null) {
			return createHttpInvokerBean(httpService.value(), autowiredType);
		} 
		
		HessianService hessianService = autowiredType.getAnnotation(HessianService.class);
		if (hessianService != null) {
			return createHessianInvokerBean(hessianService.value(), autowiredType);
		}
		return null;
	}

	
	@Override
	public void setEnvironment(Environment environment) {
		this.env=environment;
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
	protected Object createHttpInvokerBean(String appName, Class<?> serviceInterface) {
		HttpInvokerProxyFactoryBean hp = new HttpInvokerProxyFactoryBean();
		hp.setServiceInterface(serviceInterface);
		hp.setServiceUrl(getServiceUrl(appName, serviceInterface));
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
	protected Object createHessianInvokerBean(String appName, Class<?> serviceInterface) {
		HessianProxyFactoryBean hp = new HessianProxyFactoryBean();
		hp.setServiceInterface(serviceInterface);
		hp.setServiceUrl(getServiceUrl(appName, serviceInterface));
		hp.afterPropertiesSet();
		return hp.getObject();
	}

	private String getServiceUrl(String appName, Class<?> serviceInterface) {
		String host = this.env.getProperty("rpc." + appName + ".url");
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


