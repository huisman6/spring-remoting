package com.youzhixu.springremoting.invoker.config;

import java.lang.annotation.Annotation;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.stereotype.Service;

import com.youzhixu.springremoting.exporter.annotation.HessianService;
import com.youzhixu.springremoting.exporter.annotation.HttpService;

/**
 * <p>
 * 动态注册BeanDefinition <br>
 * 我们扫描所有标注@RPCService或@HessianService的服务接口， 将不同的invoker proxy动态注册
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月15日 下午4:58:32
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
@Order(value = Ordered.LOWEST_PRECEDENCE - 20)
public class AutoConfigRpcInvoker implements ApplicationContextAware {
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ConfigurableApplicationContext configurableApplicationContext =
				(ConfigurableApplicationContext) applicationContext;

		// 扫描所有。实现指定标注的接口。。
		ClassPathScanningCandidateComponentProvider annotationScanner =
				new ClassPathScanningCandidateComponentProvider(false,
						applicationContext.getEnvironment());
		annotationScanner.addIncludeFilter(new AnnotationTypeFilter(HttpService.class));
		annotationScanner.addIncludeFilter(new AnnotationTypeFilter(HessianService.class));
		// 扫描所有classpath
		Set<BeanDefinition> bd = annotationScanner.findCandidateComponents("");
		if (bd != null && !bd.isEmpty()) {
			for (BeanDefinition beanDefinition : bd) {
				System.out.println(beanDefinition);
			}
		}
	}

	private void registerRPCServcie(Object bean, BeanDefinitionRegistry registry) {
		Class<?> serviceInterface = findServiceInterface(bean, HttpService.class);
		if (serviceInterface != null) {
			GenericBeanDefinition gd = new GenericBeanDefinition();
			HttpService rpcprotocol = serviceInterface.getAnnotation(HttpService.class);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("service", bean);
			params.put("serviceInterface", serviceInterface);
			gd.setBeanClass(rpcprotocol.provider());
			gd.setPropertyValues(new MutablePropertyValues(params));
			registry.registerBeanDefinition("/" + serviceInterface.getName(), gd);
		}
	}

	private void registerHessianServcie(Object bean, BeanDefinitionRegistry registry) {
		// maybe other property，customize serialization etc.
		Class<?> serviceInterface = findServiceInterface(bean, HessianService.class);
		if (serviceInterface != null) {
			GenericBeanDefinition gd = new GenericBeanDefinition();
			HessianService rpcprotocol = serviceInterface.getAnnotation(HessianService.class);
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("service", bean);
			params.put("serviceInterface", serviceInterface);
			gd.setBeanClass(rpcprotocol.provider());
			gd.setPropertyValues(new MutablePropertyValues(params));
			registry.registerBeanDefinition("/" + serviceInterface.getName(), gd);
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
