package com.youzhixu.springremoting.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.youzhixu.springremoting.constant.Protocol;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
public @interface RPCService {
	public Protocol protocol() default Protocol.HTTP_INVOKER;
}
