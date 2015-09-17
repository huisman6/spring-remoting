package com.youzhixu.springremoting.exporter.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;

/**
 * 默认使用spring http invoker 实现对象传输
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface HttpService {
	Class<?> provider() default HttpInvokerServiceExporter.class;
}
