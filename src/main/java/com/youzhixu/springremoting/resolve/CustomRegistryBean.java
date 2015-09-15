package com.youzhixu.springremoting.resolve;

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
import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.youzhixu.springremoting.annotation.RPCService;

/**
 * <p>
 *
 * </p>
 * 
 * @author liuhui
 * @createAt 2015年9月15日 下午4:58:32
 * @since 1.0.0
 * @Copyright (c) 2015, Lianjia Group All Rights Reserved.
 */
@Component
public class CustomRegistryBean implements ApplicationContextAware {

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		System.out.println("post process=========>>>");
		// 查找所有已经注册的bean
		ConfigurableApplicationContext configurableApplicationContext =
				(ConfigurableApplicationContext) applicationContext;
		BeanDefinitionRegistry registry =
				(BeanDefinitionRegistry) configurableApplicationContext.getBeanFactory();
		for (String beanName : applicationContext.getBeanDefinitionNames()) {
			Object bean = applicationContext.getBean(beanName);
			Class<?> serviceInterface = findServiceInterface(bean, RPCService.class);
			if (serviceInterface != null) {
				GenericBeanDefinition gd = new GenericBeanDefinition();
				// java field
				Map<String, Object> params = new HashMap<String, Object>();
				params.put("service", bean);
				params.put("serviceInterface", serviceInterface);
				gd.setBeanClass(HttpInvokerServiceExporter.class);
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
