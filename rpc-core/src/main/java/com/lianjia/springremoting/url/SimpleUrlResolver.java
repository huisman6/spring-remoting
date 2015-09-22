package com.lianjia.springremoting.url;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

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
	 * 已经解析过的hosts multi-urls，比如：rpc.app.url=http://localhost:8042,http://localhost:8041
	 */
	private final static Map<String, List<String>> resolvedHosts = new ConcurrentHashMap<>(8);
	// 每个rpc.app.url的请求次数
	private static final Map<String, AtomicLong> requestCounter = new ConcurrentHashMap<>(8);

	public SimpleUrlResolver() {
		super();
	}

	@Override
	public String resolveUrl(String servcieUrl) {
		if (!resolvedHosts.containsKey(servcieUrl)) {
			synchronized (lock) {
				String actualUrl = null;
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
				resolvedHosts.put(servcieUrl, Arrays.asList(actualUrl.split(",")));
				requestCounter.put(servcieUrl, new AtomicLong(0));
			}
		}
		return findRoundRobinUrl(servcieUrl);
	}

	/**
	 * <p>
	 * 查找下一个随机的url
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @param serviceUrl
	 * @return
	 */
	private String findRoundRobinUrl(String serviceUrl) {
		List<String> urls = resolvedHosts.get(serviceUrl);
		// only one ignore
		if (urls.size() == 1) {
			return urls.get(0);
		}
		long requestCount = requestCounter.get(serviceUrl).incrementAndGet();
		return urls.get((int) (requestCount % urls.size()));
	}

	@Override
	public void setEnvironment(Environment environment) {
		this.env = environment;
	}
}
