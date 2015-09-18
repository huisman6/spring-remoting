package com.youzhixu.springremoting.invoker.config;

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
import org.springframework.beans.factory.annotation.InjectionMetadata;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.InstantiationAwareBeanPostProcessorAdapter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.Ordered;
import org.springframework.core.PriorityOrdered;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.util.ClassUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import com.youzhixu.springremoting.interceptor.AutowiredAnnotedTypeInterceptor;
import com.youzhixu.springremoting.invoker.annotation.Remoting;
import com.youzhixu.springremoting.scanner.ClassPathTypeScanner;
import com.youzhixu.springremoting.scanner.CustomizeAssignableTypeFilter;

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
public class CustomizeAutowiredBeanPostProcessor extends InstantiationAwareBeanPostProcessorAdapter
		implements
			PriorityOrdered,
			ApplicationContextAware {
	private final Log logger = LogFactory.getLog(getClass());

	private final Set<Class<? extends Annotation>> autowiredAnnotationTypes =
			new LinkedHashSet<Class<? extends Annotation>>();
	private final Map<String, InjectionMetadata> injectionMetadataCache =
			new ConcurrentHashMap<String, InjectionMetadata>(64);
	private ApplicationContext applicationContext;
	/**
	 * 当我们查找AutowiredAnnotedTypeInterceptor的所有实现类时，扫描的路径
	 */
	private String interceptorBasePackage = "com.youzhixu.springremoting";

	/**
	 * 注入字段求值时。。
	 */
	private final List<AutowiredAnnotedTypeInterceptor> interceptors = new ArrayList<>(6);

	public CustomizeAutowiredBeanPostProcessor() {
		if (logger.isInfoEnabled()) {
			logger.info("正在扫描==================》》classpath for AutowiredAnnotedTypeInterceptor: basePackage="
					+ interceptorBasePackage);
			ClassPathTypeScanner pathTypeScanner = new ClassPathTypeScanner();
			pathTypeScanner.addIncludeFilter(new CustomizeAssignableTypeFilter(
					AutowiredAnnotedTypeInterceptor.class));
			Set<BeanDefinition> definitions =
					pathTypeScanner.findCandidateComponents(interceptorBasePackage);
			if (definitions != null && !definitions.isEmpty()) {
				try {
					// 我们开始实例化数据
					for (BeanDefinition bd : definitions) {
						interceptors.add((AutowiredAnnotedTypeInterceptor) ClassUtils.forName(
								bd.getBeanClassName(),
								AutowiredAnnotedTypeInterceptor.class.getClassLoader())
								.newInstance());

					}
					Collections.sort(interceptors,
							new Comparator<AutowiredAnnotedTypeInterceptor>() {
								@Override
								public int compare(AutowiredAnnotedTypeInterceptor o1,
										AutowiredAnnotedTypeInterceptor o2) {
									return Integer.compare(o2.getOrder(), o1.getOrder());
								}

							});
					if (logger.isInfoEnabled()) {
						for (AutowiredAnnotedTypeInterceptor it : interceptors) {
							logger.info("resolve AutowiredAnnotedTypeInterceptor:" + it.getClass()
									+ ",order=" + it.getOrder());
						}
					}
				} catch (InstantiationException | IllegalAccessException | ClassNotFoundException
						| LinkageError e) {
					throw new IllegalStateException(e);
				}
			} else {
				if (logger.isWarnEnabled()) {
					logger.warn("===========>>> couldn't found AutowiredAnnotedTypeInterceptor subclasses.");
				}
			}

		}
		this.addCustomizedAnnotation(Remoting.class);
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
			throw new IllegalArgumentException("");
		}
		this.autowiredAnnotationTypes.add(clazz);
	}



	@Override
	public int getOrder() {
		return Ordered.LOWEST_PRECEDENCE - 10;
	}

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
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
		private volatile boolean cached = false;
		private volatile Object cachedFieldValue;

		public AutowiredFieldElement(Field field) {
			super(field, null);
		}

		@Override
		protected void inject(Object bean, String beanName, PropertyValues pvs) throws Throwable {
			Field field = (Field) this.member;
			try {
				Object value = null;
				if (this.cached) {
					value = this.cachedFieldValue;
				} else {
					// 主动注入
					if (interceptors != null) {
						for (AutowiredAnnotedTypeInterceptor interceptor : interceptors) {
							// 处理此种类型,我们只查找第一个找到可以处理的
							if (interceptor.accept(field.getDeclaredAnnotations(), field.getType())) {
								value =
										interceptor.resolveAutowiredValue(field.getType(),
												applicationContext.getEnvironment());
								break;
							}
						}
					}
					// 一旦解析过。我们缓存起来
					if (value != null) {
						ReflectionUtils.makeAccessible(field);
						field.set(bean, value);
						cached = true;
						this.cachedFieldValue = value;
					}
				}
			} catch (Throwable ex) {
				throw new BeanCreationException("Could not autowire field: " + field, ex);
			}
		}
	}
}
