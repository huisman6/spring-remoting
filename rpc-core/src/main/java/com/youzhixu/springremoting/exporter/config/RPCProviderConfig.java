package com.youzhixu.springremoting.exporter.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
public class RPCProviderConfig {
	@Bean(name = "autoConfigRpcExporter")
	public AutoConfigRpcExporter exportService() {
		return new AutoConfigRpcExporter();
	}
}
