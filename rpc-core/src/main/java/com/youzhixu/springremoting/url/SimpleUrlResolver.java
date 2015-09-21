package com.youzhixu.springremoting.url;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

/**
 * <p>
 * 默认从Spring Enviroment查找rpc.appName.url的属性
 * </p>
 * 
 * @author huisman
 * @since 1.0.0
 * @createAt 2015年9月20日 下午3:34:04
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved.
 */
public class SimpleUrlResolver implements UrlResolver, EnvironmentAware {
	private final static Object lock = new Object();
	private Environment env;
	/**
	 * 已经解析过的hosts
	 */
	private static Map<String, String> resolvedHosts = new ConcurrentHashMap<>(8);

	public SimpleUrlResolver() {
		super();
	}

	@Override
	public String resolveUrl(String servcieUrl) {
		if (!resolvedHosts.containsKey(servcieUrl)) {
			String actualUrl = null;
			synchronized (lock) {
				URI uri;
				try {
					uri = new URI(servcieUrl);
				} catch (URISyntaxException e) {
					throw new IllegalArgumentException(e);
				}
				String appName = uri.getHost();
				String path = uri.getPath();
				// 实际请求地址
				actualUrl = this.env.getProperty("rpc." + appName + ".url") + "/" + path;
				if (actualUrl == null || actualUrl.trim().isEmpty()) {
					throw new IllegalArgumentException(
							"app:"
									+ appName
									+ ",virtualUrl="
									+ servcieUrl
									+ ",couldn't found rpc."
									+ appName
									+ ".url from Spring Environment(System property,System Environment,PropertySource)");
				}
			}

			resolvedHosts.put(servcieUrl, actualUrl);
			return actualUrl;
		}
		return resolvedHosts.get(servcieUrl);
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.env = environment;
	}
}
