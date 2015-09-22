package com.lianjia.springremoting.exporter.processor;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.remoting.caucho.HessianServiceExporter;
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.stereotype.Service;

import com.lianjia.springremoting.constant.ServicePath;
import com.lianjia.springremoting.exporter.annotation.HessianService;
import com.lianjia.springremoting.exporter.annotation.HttpService;

/**
 * <p>
 * 动态注册BeanDefinition <br>
 * 我们扫描所有@service的父类接口(标注@RPCService或@HessianService)，动态将不同的类型（httpinvoker,hessian）的rpc服务暴露出去
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月15日 下午4:58:32
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
@Deprecated
@Order(value = Ordered.LOWEST_PRECEDENCE - 30)
public class DefaultRpcExporterProcessor implements ApplicationContextAware {
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ConfigurableApplicationContext configurableApplicationContext =
				(ConfigurableApplicationContext) applicationContext;
		BeanDefinitionRegistry registry =
				(BeanDefinitionRegistry) configurableApplicationContext.getBeanFactory();
		for (String beanName : applicationContext.getBeanDefinitionNames()) {
			Object bean = applicationContext.getBean(beanName);
			if (bean == null) {
				// bean might be null;
				continue;
			}
			registerRPCServcie(bean, registry);
			registerHessianServcie(bean, registry);
		}
	}

	private void registerRPCServcie(Object bean, BeanDefinitionRegistry registry) {
		Class<?> serviceInterface = findServiceInterface(bean, HttpService.class);
		if (serviceInterface != null) {
			GenericBeanDefinition gd = new GenericBeanDefinition();
			// HttpService rpcprotocol = serviceInterface.getAnnotation(HttpService.class);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("service", bean);
			params.put("serviceInterface", serviceInterface);
			gd.setBeanClass(HttpInvokerServiceExporter.class);
			gd.setPropertyValues(new MutablePropertyValues(params));
			registry.registerBeanDefinition(ServicePath.PREFIX + "/" + serviceInterface.getName(),
					gd);
		}
	}

	private void registerHessianServcie(Object bean, BeanDefinitionRegistry registry) {
		// maybe other properties，customize serialization etc.
		Class<?> serviceInterface = findServiceInterface(bean, HessianService.class);
		if (serviceInterface != null) {
			GenericBeanDefinition gd = new GenericBeanDefinition();
			// HessianService rpcprotocol = serviceInterface.getAnnotation(HessianService.class);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("service", bean);
			params.put("serviceInterface", serviceInterface);
			gd.setBeanClass(HessianServiceExporter.class);
			gd.setPropertyValues(new MutablePropertyValues(params));
			registry.registerBeanDefinition(ServicePath.PREFIX + "/" + serviceInterface.getName(),
					gd);
		}
	}

	private Class<?> findServiceInterface(Object rpcService,
			Class<? extends Annotation> rpcAnnotation) {
		if (rpcService == null) {
			return null;
		}
		Class<?> serviceInterface = null;
		if (AnnotationUtils.isAnnotationDeclaredLocally(Service.class, rpcService.getClass())) {
			for (Class<?> interfaceClass : rpcService.getClass().getInterfaces()) {
				if (AnnotationUtils.isAnnotationDeclaredLocally(rpcAnnotation, interfaceClass)) {
					serviceInterface = interfaceClass;
				}
			}
		}
		return serviceInterface;
	}
}
