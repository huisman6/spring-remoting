package com.youzhixu.springremoting.interceptor.provider;

import java.lang.annotation.Annotation;

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
public class DefaultExporterRegistryInterceptor implements ServiceExporterRegistryInterceptor{

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public Object resolveServcieExporter(Class<?> servcieInterface, Object serviceProvider) {
		HttpService httpService=servcieInterface.getAnnotation(HttpService.class);
		if (httpService !=null) {
			try {
				return createHttpServcieExporter(serviceProvider, servcieInterface,httpService.serializer().newInstance());
			} catch (InstantiationException | IllegalAccessException e) {
				throw new IllegalStateException(e);
			}
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
	
	private Object createHttpServcieExporter(Object bean,Class<?> servcieInterface,Serializer serializer) {
		 CustomizeHttpInvokerServiceExporter exporter=new CustomizeHttpInvokerServiceExporter(serializer);
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

