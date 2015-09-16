package com.youzhixu.springremoting.exporter;

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
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import com.youzhixu.springremoting.annotation.RPCService;

/**
 * <p>
 * 动态注册BeanDefinition <br>
 * 我们扫描所有@service的父类接口(标注@RPCService)，动态将不同的类型（httpinvoker,hessian）的rpc服务暴露出去
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月15日 下午4:58:32
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
public class CustomRegistRpcExporter implements ApplicationContextAware {
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ConfigurableApplicationContext configurableApplicationContext =
				(ConfigurableApplicationContext) applicationContext;
		BeanDefinitionRegistry registry =
				(BeanDefinitionRegistry) configurableApplicationContext.getBeanFactory();
		for (String beanName : applicationContext.getBeanDefinitionNames()) {
			Object bean = applicationContext.getBean(beanName);
			Class<?> serviceInterface = findServiceInterface(bean, RPCService.class);
			if (serviceInterface != null) {
				GenericBeanDefinition gd = new GenericBeanDefinition();
				RPCService rpcprotocol = serviceInterface.getAnnotation(RPCService.class);
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("service", bean);
				params.put("serviceInterface", serviceInterface);
				gd.setBeanClass(rpcprotocol.protocol().getProvider());
				gd.setPropertyValues(new MutablePropertyValues(params));
				registry.registerBeanDefinition("/" + serviceInterface.getName(), gd);
			}
		}
	}

	private Class<?> findServiceInterface(Object rpcService,
			Class<? extends Annotation> rpcAnnotation) {
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
