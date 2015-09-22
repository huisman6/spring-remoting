package com.lianjia.springremoting.invoker.processor;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.env.Environment;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.util.StringUtils;

import com.lianjia.springremoting.constant.ServicePath;
import com.lianjia.springremoting.exporter.annotation.HessianService;
import com.lianjia.springremoting.exporter.annotation.HttpService;

/**
 * <p>
 * 动态注册BeanDefinition <br>
 * 我们扫描所有标注@RPCService或@HessianService的服务接口， 将不同的invoker proxy动态注册 <br>
 * 如果需要自定义复杂实现，比如基于服务注册发现来负载均衡，请继承此类。
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月15日 下午4:58:32
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
@Deprecated
public class DefaultRpcInvokerProcessor implements BeanFactoryAware, PriorityOrdered {
	private BeanDefinitionRegistry registry;

	/**
	 * ClassPathScanningCandidateComponentProvider默认不检测接口。。
	 */
	static class CustomizeClassPathScanningProvider
			extends ClassPathScanningCandidateComponentProvider {
		public CustomizeClassPathScanningProvider(boolean useDefaultFilters) {
			super(useDefaultFilters);
		}

		public CustomizeClassPathScanningProvider(boolean useDefaultFilters, Environment environment) {
			super(useDefaultFilters, environment);
		}

		/**
		 * 我们只检测接口
		 */
		@Override
		protected boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
			return beanDefinition.getMetadata().isInterface();
		}
	}

	protected Class<?> getHttpServiceInvoker() {
		return HttpInvokerProxyFactoryBean.class;
	}


	protected Class<?> getHessianServiceInvoker() {
		return HessianProxyFactoryBean.class;
	}

	protected String getServiceUrl(String appName, Class<?> serviceInterface) {
		String host = "http://localhost:8080"; //
		// System.getProperty("rpc." + appName + ".url");
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


	public void registerBean(Class<?> beanClass) {
		HttpService httpService = beanClass.getAnnotation(HttpService.class);
		HessianService hessianService = beanClass.getAnnotation(HessianService.class);
		GenericBeanDefinition gd = new GenericBeanDefinition();
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("serviceInterface", beanClass);
		if (httpService != null) {
			params.put("serviceUrl", getServiceUrl(httpService.value(), beanClass));
			gd.setBeanClass(getHttpServiceInvoker());
		} else if (hessianService != null) {
			params.put("serviceUrl", getServiceUrl(hessianService.toString(), beanClass));
			gd.setBeanClass(getHessianServiceInvoker());
		}
		gd.setPropertyValues(new MutablePropertyValues(params));
		registry.registerBeanDefinition(StringUtils.uncapitalize(beanClass.getSimpleName()), gd);
	}


	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.registry = (BeanDefinitionRegistry) beanFactory;
		CustomizeClassPathScanningProvider scanningProvider =
				new CustomizeClassPathScanningProvider(false);
		scanningProvider.addIncludeFilter(new AnnotationTypeFilter(HttpService.class));
		scanningProvider.addIncludeFilter(new AnnotationTypeFilter(HessianService.class));
		Set<BeanDefinition> bd = scanningProvider.findCandidateComponents("");
		try {
			if (bd != null && !bd.isEmpty()) {
				for (BeanDefinition def : bd) {
					Class<?> serviceInterface = Class.forName(def.getBeanClassName());
					registerBean(serviceInterface);
				}
			}
		} catch (ClassNotFoundException e) {
			throw new IllegalArgumentException(e);
		}

	}


	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE - 100;
	}


}
