package com.youzhixu.springremoting.interceptor;

import java.lang.annotation.Annotation;

import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;


/**
 * <p>
 * 当自动注入字段值时，会调用此方法获取字段的实际值 <br>
 * 如果有多个类实现了此接口，我们按ordered来排序，只找第一个accept注入类型的拦截器来处理。<br>
 * order 越小越靠前。
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月18日 上午10:29:07
 * @since 1.0.0
 * @Copyright (c) 2015, Lianjia Group All Rights Reserved.
 */
public interface AutowiredAnnotedTypeInterceptor extends Ordered {
	/**
	 * <p>
	 * 当注入字段、方法求值时调用
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @param autowiredType 注入的类型
	 * @param environment spring environment
	 * @return actual resolved value
	 */
	Object resolveAutowiredValue(Class<?> autowiredType, Environment env);

	/**
	 * <p>
	 * 是否可以处理此种类型(annotation,Class)的注入
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @param memberAnnotations 字段、方法（Class Memeber)上的所有标注
	 * @param clazz 要注入的类型
	 * @return
	 */
	boolean accept(Annotation[] memberAnnotations, Class<?> autowiredType);
}
