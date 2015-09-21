package com.youzhixu.springremoting.exporter.processor;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.stereotype.Service;

import com.youzhixu.springremoting.constant.ServicePath;
import com.youzhixu.springremoting.exporter.annotation.RPCService;
import com.youzhixu.springremoting.interceptor.ServiceExporterRegistryInterceptor;

/**
 * @author huisman
 * @since 1.0.0
 * @createAt 2015年9月17日 下午11:12:16
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
public class ServiceExporterBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
		implements
			PriorityOrdered,
			BeanFactoryAware,
			InitializingBean {
	private final Log logger = LogFactory.getLog(getClass());
	private ConfigurableListableBeanFactory beanFactory;
	private final List<ServiceExporterRegistryInterceptor> interceptors = new ArrayList<>(2);

	public ServiceExporterBeanPostProcessor() {
		super();
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// we found all Sub interceptors
		Map<String, ServiceExporterRegistryInterceptor> allFoundInterceptors =
				this.beanFactory.getBeansOfType(ServiceExporterRegistryInterceptor.class);
		if (allFoundInterceptors != null && !allFoundInterceptors.isEmpty()) {
			for (ServiceExporterRegistryInterceptor sub : allFoundInterceptors.values()) {
				this.interceptors.add(sub);
			}
		}
		if (this.interceptors.isEmpty()) {
			throw new IllegalStateException("找不到AutowiredAnnotedTypeInterceptor.");
		}
		Collections.sort(interceptors, new Comparator<ServiceExporterRegistryInterceptor>() {
			@Override
			public int compare(ServiceExporterRegistryInterceptor o1,
					ServiceExporterRegistryInterceptor o2) {
				return Integer.compare(o2.getOrder(), o1.getOrder());
			}

		});
		if (logger.isInfoEnabled()) {
			for (ServiceExporterRegistryInterceptor it : interceptors) {
				logger.info("resolve ServiceExporterRegistryInterceptor:" + it.getClass()
						+ ",order=" + it.getOrder());
			}
		}
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 10;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		Class<?> rpcInterface = findRPCServiceInterface(bean, RPCService.class);
		if (rpcInterface != null) {
			Object exporter = null;
			// 主动注入
			if (interceptors != null) {
				for (ServiceExporterRegistryInterceptor interceptor : interceptors) {
					// 处理此种类型,我们只查找第一个找到可以处理的
					if (interceptor.accept(rpcInterface.getAnnotations(), rpcInterface)) {
						exporter = interceptor.resolveServcieExporter(rpcInterface, bean);
						break;
					}
				}
			}
			if (exporter == null) {
				throw new IllegalStateException(
						"RPC Service Exporter is null,@RPCService interface is :"
								+ rpcInterface.getName());
			}
			String exporterName = ServicePath.PREFIX + "/" + rpcInterface.getName();
			if (logger.isInfoEnabled()) {
				logger.info("found rpc service interface :" + rpcInterface.getName() + ",exporter="
						+ exporter.getClass().getName() + ",beanName=" + exporterName);
			}
			// do register exporter
			this.beanFactory.registerSingleton(exporterName, exporter);
		}
		return bean;
	}

	/**
	 * <p>
	 * 基于rpc service的实现类来查找接口
	 * </p>
	 * 
	 * @param rpcService
	 * @param rpcAnnotation
	 * @return
	 * @since: 1.0.0
	 */
	private Class<?> findRPCServiceInterface(Object rpcService,
			Class<? extends Annotation> rpcAnnotation) {
		if (rpcService == null) {
			return null;
		}
		if (AnnotationUtils.isAnnotationDeclaredLocally(Service.class, rpcService.getClass())) {
			for (Class<?> interfaceClass : rpcService.getClass().getInterfaces()) {
				if (AnnotationUtils.findAnnotation(interfaceClass, rpcAnnotation) != null) {
					return interfaceClass;
				}
			}
		}
		return null;
	}


	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}
}
