package com.youzhixu.springremoting.imp.httpcomponent.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.youzhixu.springremoting.exporter.config.DefaultProviderConfig;

/**
 * <p>
 * Service Provider config & Interceptors & serializer
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月16日 上午11:39:02
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
@Configuration
@Import(DefaultProviderConfig.class)
public class RPCProviderConfig {
}

