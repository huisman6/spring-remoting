package com.youzhixu.springremoting.exporter.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.remoting.caucho.HessianServiceExporter;

/**
 * 使用 HessianServiceExporter 传输对象
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface HessianService {
	Class<?> provider() default HessianServiceExporter.class;
}
