package com.youzhixu.springremoting.imp.httpcomponent.interceptor;

import java.lang.annotation.Annotation;

import com.youzhixu.springremoting.exporter.annotation.HttpService;
import com.youzhixu.springremoting.exporter.executor.CustomizeHttpInvokerServiceExporter;
import com.youzhixu.springremoting.interceptor.adapter.AbstractRPCServiceExporterInterceptor;
import com.youzhixu.springremoting.serialize.Serializer;

/**
 * @author huisman
 * @since 1.0.0
 * @createAt 2015年9月20日 下午12:19:12
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved.
 */
public class HttpServiceExporterInterceptor extends AbstractRPCServiceExporterInterceptor {

	@Override
	public int getOrder() {
		return 0;
	}

	@Override
	public boolean accept(Annotation[] memberAnnotations, Class<?> serviceInterface) {
		return serviceInterface.getAnnotation(HttpService.class) != null;
	}

	@Override
	public Object doResolveExporter(String appName, Object serviceBean, Class<?> serviceInterface,
			Serializer serializer) {
		CustomizeHttpInvokerServiceExporter exporter =
				new CustomizeHttpInvokerServiceExporter(serializer);
		exporter.setService(serviceBean);
		exporter.setServiceInterface(serviceInterface);
		exporter.afterPropertiesSet();
		return exporter;
	}

	@Override
	public String getAppName(Class<?> serviceInterface) {
		HttpService httpService = serviceInterface.getAnnotation(HttpService.class);
		return httpService.value();
	}

	@Override
	public Serializer getSerializer(Class<?> serviceInterface) {
		return serviceInterface.getAnnotation(HttpService.class).serializer().provider();
	}
}
