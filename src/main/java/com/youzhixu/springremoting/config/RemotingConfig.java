package com.youzhixu.springremoting.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.youzhixu.springremoting.exporter.CustomRegistRpcExporter;
import com.youzhixu.springremoting.listener.ApplicationInitListener;

/**
 * <p>
 * remoting config
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月16日 上午11:39:02
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
@Configuration
public class RemotingConfig {

	@Bean
	public CustomRegistRpcExporter registExporter() {
		return new CustomRegistRpcExporter();
	}

	@Bean
	public ApplicationInitListener listenerBean() {
		return new ApplicationInitListener();
	}
}
