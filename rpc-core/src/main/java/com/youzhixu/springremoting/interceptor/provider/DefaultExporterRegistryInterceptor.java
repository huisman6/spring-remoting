package com.youzhixu.springremoting.interceptor.provider;

import java.lang.annotation.Annotation;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.remoting.caucho.HessianServiceExporter;

import com.youzhixu.springremoting.exporter.annotation.HessianService;
import com.youzhixu.springremoting.exporter.annotation.HttpService;
import com.youzhixu.springremoting.exporter.executor.CustomizeHttpInvokerServiceExporter;
import com.youzhixu.springremoting.interceptor.ServiceExporterRegistryInterceptor;
import com.youzhixu.springremoting.serialize.Serializer;

/**
 * @author huisman 
 * @since 1.0.0
 * @createAt 2015年9月20日 下午12:19:12
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved. 
 */
public class DefaultExporterRegistryInterceptor implements ServiceExporterRegistryInterceptor,BeanFactoryAware{
	private ConfigurableListableBeanFactory beanFactory;
	private Serializer serializer;
	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory=(ConfigurableListableBeanFactory)beanFactory;
		Map<String,Serializer> alls=  this.beanFactory.getBeansOfType(Serializer.class);
		if (alls == null || alls.isEmpty()) {
			throw new IllegalArgumentException("@HttpService找不到序列化实现"+Serializer.class);
		}
		this.serializer=alls.values().iterator().next();
	}

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public Object resolveServcieExporter(Class<?> servcieInterface, Object serviceProvider) {
		HttpService httpService=servcieInterface.getAnnotation(HttpService.class);
		if (httpService !=null) {
			return createHttpServcieExporter(serviceProvider, servcieInterface);
		}
		HessianService hessianService=servcieInterface.getAnnotation(HessianService.class);
		if (hessianService !=null) {
			return createHessianServcieExporter(serviceProvider, servcieInterface);
		}
		return null;
	}

	
	
	@Override
	public boolean accept(Annotation[] memberAnnotations, Class<?> servcieInterface) {
		if (memberAnnotations == null || memberAnnotations.length == 0) {
			return false;
		}
		//只处理httpinvoker/hessian
		for (Annotation annotation : memberAnnotations) {
			return annotation.annotationType() == HttpService.class ||annotation.annotationType() == HessianService.class;
		}
		return false;
	}
	
	private Object createHttpServcieExporter(Object bean,Class<?> servcieInterface) {
		 CustomizeHttpInvokerServiceExporter exporter=new CustomizeHttpInvokerServiceExporter(this.serializer);
		 exporter.setService(bean);
		 exporter.setServiceInterface(servcieInterface);
		 exporter.afterPropertiesSet();
		 return exporter;
	}

	private Object createHessianServcieExporter(Object bean,Class<?> servcieInterface) {
		HessianServiceExporter exporter=new HessianServiceExporter();
		exporter.setService(bean);
		exporter.setServiceInterface(servcieInterface);
		exporter.afterPropertiesSet();
		return exporter;
	}
}

