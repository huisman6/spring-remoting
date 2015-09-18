package com.youzhixu.springremoting.invoker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 * rpc invoker config
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月16日 上午11:39:02
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
@Configuration
public class RPCInvokerConfig {
	/**
	 * <p>
	 * 必须是静态方法
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @return
	 */
	@Bean(name = "autowiredRemotingBeanPostProcessor")
	public static CustomizeAutowiredBeanPostProcessor customizePostProcessor() {
		return new CustomizeAutowiredBeanPostProcessor();
	}
}
