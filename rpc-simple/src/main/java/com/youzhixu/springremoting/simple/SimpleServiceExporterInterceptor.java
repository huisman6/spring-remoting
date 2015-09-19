package com.youzhixu.springremoting.simple;

import java.lang.annotation.Annotation;

import org.springframework.core.env.Environment;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

import com.youzhixu.springremoting.exporter.annotation.HessianService;
import com.youzhixu.springremoting.exporter.annotation.HttpService;
import com.youzhixu.springremoting.interceptor.ServiceExporterRegistryInterceptor;

/**
 * @author huisman 
 * @since 1.0.0
 * @createAt 2015年9月19日 下午5:34:30
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved. 
 */
public class SimpleServiceExporterInterceptor  implements ServiceExporterRegistryInterceptor{

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public Object createtServcieExporter(Class<?> servcieInterface, Object serviceProvider,
			Environment env) {
		HttpService httpService=servcieInterface.getAnnotation(HttpService.class);
		if (httpService !=null) {
			return createHttpServcieExporter(serviceProvider, servcieInterface, env);
		}
		HessianService hessianService=servcieInterface.getAnnotation(HessianService.class);
		if (hessianService !=null) {
			return createHessianServcieExporter(serviceProvider, servcieInterface, env);
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
	
	private Object createHttpServcieExporter(Object bean,Class<?> servcieInterface,Environment env ) {
		 HttpInvokerServiceExporter exporter=new HttpInvokerServiceExporter();
		 exporter.setService(bean);
		 exporter.setServiceInterface(servcieInterface);
		 exporter.afterPropertiesSet();
		 return exporter;
	}

	private Object createHessianServcieExporter(Object bean,Class<?> servcieInterface,Environment env) {
		HessianServiceExporter exporter=new HessianServiceExporter();
		exporter.setService(bean);
		exporter.setServiceInterface(servcieInterface);
		exporter.afterPropertiesSet();
		return exporter;
	}


}


