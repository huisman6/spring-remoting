package com.youzhixu.springremoting.invoker.config;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.env.Environment;
import org.springframework.remoting.caucho.HessianProxyFactoryBean;
import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.youzhixu.springremoting.constant.ServicePath;
import com.youzhixu.springremoting.exporter.annotation.HessianService;
import com.youzhixu.springremoting.exporter.annotation.HttpService;
import com.youzhixu.springremoting.invoker.annotation.Remoting;

/**
 * <p>
 * 	处理@Remoting标注
 * </p> 
 * @author huisman 
 * @since 1.0.0
 * @createAt 2015年9月17日 下午11:12:16
 * @Copyright (c) 2015, Dooioo All Rights Reserved. 
 */
public class AutowiredRemotingBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter implements
 PriorityOrdered, ApplicationContextAware{
	private final Set<Class<? extends Annotation>> autowiredAnnotationTypes =
			new LinkedHashSet<Class<? extends Annotation>>();
	private final Map<String, InjectionMetadata> injectionMetadataCache =
			new ConcurrentHashMap<String, InjectionMetadata>(64);
	private ApplicationContext applicationContext;
	public AutowiredRemotingBeanPostProcessor() {
		autowiredAnnotationTypes.add(Remoting.class);
	}

	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE-10;
	}
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext=applicationContext;
	}
	
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		Class<?> clazz = bean.getClass();
		System.out.println("==========================>"+clazz);
		InjectionMetadata metadata = findAutowiringMetadata(clazz.getName(), clazz);
		if (metadata != null) {
			try {
				metadata.inject(bean, null, null);
			}
			catch (Throwable ex) {
				throw new BeanCreationException("Injection of autowired dependencies failed for class [" + clazz + "]", ex);
			}
		}
		return bean;
	}
	
	/**
	  * <p>
	  *  创建@HttpService标准的rpc invoker 代理
	  * </p> 
	  * @param appName
	  * @param serviceInterface
	  * @return
	  * @since: 1.0.0
	 */
	protected Object createHttpInvokerBean(String appName,Class<?> serviceInterface){
		HttpInvokerProxyFactoryBean hp=new HttpInvokerProxyFactoryBean();
		hp.setServiceInterface(serviceInterface);
		hp.setServiceUrl(getServiceUrl(appName, serviceInterface));
		return hp;
	}
	
	/**
	  * <p>
	  * 创建@HessianService标准的rpc invoker 代理
	  * </p> 
	  * @param appName
	  * @param serviceInterface
	  * @return
	  * @since: 1.0.0
	 */
	protected Object createHessianInvokerBean(String appName,Class<?> serviceInterface){
		HessianProxyFactoryBean hp=new HessianProxyFactoryBean();
		hp.setServiceInterface(serviceInterface);
		hp.setServiceUrl(getServiceUrl(appName, serviceInterface));
		return hp;
	}

	protected String getServiceUrl(String appName, Class<?> serviceInterface) {
		Environment env=applicationContext.getEnvironment();
		String host =env.getProperty("rpc." + appName + ".url"); 
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
	
	private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz) {
		String cacheKey = (StringUtils.hasLength(beanName) ? beanName : clazz.getName());
		InjectionMetadata metadata = this.injectionMetadataCache.get(cacheKey);
		if (InjectionMetadata.needsRefresh(metadata, clazz)) {
			synchronized (this.injectionMetadataCache) {
				metadata = this.injectionMetadataCache.get(cacheKey);
				if (InjectionMetadata.needsRefresh(metadata, clazz)) {
					metadata = buildAutowiringMetadata(clazz);
					this.injectionMetadataCache.put(cacheKey, metadata);
				}
			}
		}
		return metadata;
	}

	/**
	  * <p>
	  * 暂时只支持field注入
	  * </p> 
	  * @param clazz
	  * @return
	  * @since: 1.0.0
	 */
	private InjectionMetadata buildAutowiringMetadata(Class<?> clazz) {
		LinkedList<InjectionMetadata.InjectedElement> elements = new LinkedList<InjectionMetadata.InjectedElement>();
		Class<?> targetClass = clazz;
		do {
			LinkedList<InjectionMetadata.InjectedElement> currElements = new LinkedList<InjectionMetadata.InjectedElement>();
			for (Field field : targetClass.getDeclaredFields()) {
				AnnotationAttributes annotation = findAutowiredAnnotation(field);
				if (annotation != null) {
					if (Modifier.isStatic(field.getModifiers())) {
						continue;
					}
					currElements.add(new AutowiredFieldElement(field));
				}
			}
			elements.addAll(0, currElements);
			targetClass = targetClass.getSuperclass();
		}
		while (targetClass != null && targetClass != Object.class);
		return new InjectionMetadata(clazz, elements);
	}

	private AnnotationAttributes findAutowiredAnnotation(AccessibleObject ao) {
		for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
			AnnotationAttributes annotation = AnnotatedElementUtils.getAnnotationAttributes(ao, type.getName());
			if (annotation != null) {
				return annotation;
			}
		}
		return null;
	}
	
	private Object createInvokerProxyBean(Class<?> serviceInterface) {
		HttpService httpService = serviceInterface.getAnnotation(HttpService.class);
		HessianService hessianService = serviceInterface.getAnnotation(HessianService.class);
		if (httpService != null) {
			return createHttpInvokerBean(httpService.app(), serviceInterface);
		}else if(hessianService !=null){
			return createHessianInvokerBean(hessianService.app(), serviceInterface);
		}
		return null;
	}
	
	/**
	 * Class representing injection information about an annotated field.
	 */
	private class AutowiredFieldElement extends InjectionMetadata.InjectedElement {
		private volatile boolean cached = false;
		private volatile Object cachedFieldValue;

		public AutowiredFieldElement(Field field) {
			super(field, null);
		}

		@Override
		protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
			Field field = (Field) this.member;
			try {
				Object value;
				if (this.cached) {
					value = this.cachedFieldValue;
				}
				else {
					//主动注入
					value=createInvokerProxyBean(bean.getClass());
					if (value != null) {
					ReflectionUtils.makeAccessible(field);
					field.set(bean, value);
					 cached=true;
					 this.cachedFieldValue=value;
					}
				}
			}
			catch (Throwable ex) {
				throw new BeanCreationException("Could not autowire field: " + field, ex);
			}
		}
	}
}

