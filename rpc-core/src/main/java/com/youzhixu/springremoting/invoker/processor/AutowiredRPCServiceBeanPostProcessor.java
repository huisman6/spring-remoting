package com.youzhixu.springremoting.invoker.processor;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.PropertyValues;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ReflectionUtils;

import com.youzhixu.springremoting.interceptor.AutowiredAnnotedTypeInterceptor;
import com.youzhixu.springremoting.invoker.annotation.Remoting;

/**
 * <p>
 * 默认处理@Remoting标注的自动注入，但可以添加更多注解
 * </p>
 * 
 * @author huisman
 * @since 1.0.0
 * @createAt 2015年9月17日 下午11:12:16
 * @Copyright (c) 2015, Dooioo All Rights Reserved.
 */
public class AutowiredRPCServiceBeanPostProcessor
		extends InstantiationAwareBeanPostProcessorAdapter
		implements
			BeanFactoryAware,
			InitializingBean,
			PriorityOrdered {
	private final Log logger = LogFactory.getLog(getClass());

	private final Set<Class<? extends Annotation>> autowiredAnnotationTypes =
			new LinkedHashSet<Class<? extends Annotation>>();
	// 注入字段的值
	private final Map<Class<?>, Object> injectionMetadataCache =
			new ConcurrentHashMap<Class<?>, Object>(64);
	private ConfigurableListableBeanFactory beanFactory;
	/**
	 * 注入字段求值时待调用的拦截器。。
	 */
	private final List<AutowiredAnnotedTypeInterceptor> interceptors = new ArrayList<>(2);

	public AutowiredRPCServiceBeanPostProcessor() {
		// default
		this.addCustomizedAnnotation(Remoting.class);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// we found all Sub interceptors
		Map<String, AutowiredAnnotedTypeInterceptor> allFoundInterceptors =
				this.beanFactory.getBeansOfType(AutowiredAnnotedTypeInterceptor.class);
		if (allFoundInterceptors != null && !allFoundInterceptors.isEmpty()) {
			for (AutowiredAnnotedTypeInterceptor sub : allFoundInterceptors.values()) {
				this.interceptors.add(sub);
			}
		}
		if (this.interceptors.isEmpty()) {
			throw new IllegalStateException("找不到AutowiredAnnotedTypeInterceptor.");
		}
		Collections.sort(this.interceptors, new Comparator<AutowiredAnnotedTypeInterceptor>() {
			@Override
			public int compare(AutowiredAnnotedTypeInterceptor o1,
					AutowiredAnnotedTypeInterceptor o2) {
				return Integer.compare(o2.getOrder(), o1.getOrder());
			}

		});
		if (logger.isInfoEnabled()) {
			for (AutowiredAnnotedTypeInterceptor it : interceptors) {
				logger.info("resolve AutowiredAnnotedTypeInterceptor:" + it.getClass() + ",order="
						+ it.getOrder());
			}
		}
	}

	@Override
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		this.beanFactory = (ConfigurableListableBeanFactory) beanFactory;
	}

	/**
	 * <p>
	 * 添加自定义注入的标注
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @param clazz
	 */
	public void addCustomizedAnnotation(Class<? extends Annotation> clazz) {
		if (clazz == null) {
			throw new IllegalArgumentException("annotation is null");
		}
		this.autowiredAnnotationTypes.add(clazz);
	}



	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE;
	}

	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName)
			throws BeansException {
		Class<?> clazz = bean.getClass();
		InjectionMetadata metadata = findAutowiringMetadata(clazz.getName(), clazz);
		if (metadata != null) {
			try {
				metadata.inject(bean, null, null);
			} catch (Throwable ex) {
				throw new BeanCreationException(
						"Injection of autowired dependencies failed for class [" + clazz + "]", ex);
			}
		}
		return bean;
	}

	private InjectionMetadata findAutowiringMetadata(String beanName, Class<?> clazz) {
		// 查找需要注入的字段====
		InjectionMetadata metadata = buildAutowiringMetadata(clazz);
		// 如果没找到，则直接返回
		if (metadata == null) {
			return null;
		}
		return metadata;
	}

	/**
	 * <p>
	 * 暂时只支持field注入
	 * </p>
	 * 
	 * @param clazz
	 * @return
	 * @since: 1.0.0
	 */
	private InjectionMetadata buildAutowiringMetadata(Class<?> clazz) {
		LinkedList<InjectionMetadata.InjectedElement> elements =
				new LinkedList<InjectionMetadata.InjectedElement>();
		Class<?> targetClass = clazz;
		do {
			LinkedList<InjectionMetadata.InjectedElement> currElements =
					new LinkedList<InjectionMetadata.InjectedElement>();
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
		} while (targetClass != null && targetClass != Object.class);

		if (elements.isEmpty()) {
			return null;
		}
		return new InjectionMetadata(clazz, elements);
	}

	private AnnotationAttributes findAutowiredAnnotation(AccessibleObject ao) {
		for (Class<? extends Annotation> type : this.autowiredAnnotationTypes) {
			AnnotationAttributes annotation =
					AnnotatedElementUtils.getAnnotationAttributes(ao, type.getName());
			if (annotation != null) {
				return annotation;
			}
		}
		return null;
	}

	/**
	 * Class representing injection information about an annotated field.
	 */
	private class AutowiredFieldElement extends InjectionMetadata.InjectedElement {
		public AutowiredFieldElement(Field field) {
			super(field, null);
		}

		@Override
		protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
			Field field = (Field) this.member;
			Class<?> fieldType = field.getType();
			try {
				Object value = null;
				if (injectionMetadataCache.containsKey(fieldType)) {
					value = injectionMetadataCache.get(fieldType);
				} else {
					// 主动注入
					if (interceptors != null) {
						for (AutowiredAnnotedTypeInterceptor interceptor : interceptors) {
							// 处理此种类型,我们只查找第一个找到可以处理的
							if (interceptor.accept(field.getDeclaredAnnotations(), fieldType)) {
								value = interceptor.resolveAutowiredValue(fieldType);
								break;
							}
						}
					}
					// 一旦解析过。我们缓存起来
					if (value != null) {
						injectionMetadataCache.put(fieldType, value);
					}
				}
				ReflectionUtils.makeAccessible(field);
				field.set(bean, value);
			} catch (Throwable ex) {
				throw new BeanCreationException("Could not autowire field: " + field, ex);
			}
		}
	}
}
