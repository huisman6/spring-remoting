package com.youzhixu.springremoting.imp.resolver;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.core.env.Environment;

import com.youzhixu.springremoting.url.UrlResolver;

/**
 * <p>
 * 默认从Spring Enviroment查找rpc.appName.url的属性
 * </p> 
 * @author huisman 
 * @since 1.0.0
 * @createAt 2015年9月20日 下午3:34:04
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved. 
 */
public class SimpleUrlResolver implements UrlResolver{
	private final static Object lock=new Object();
	/**
	 *已经解析过的hosts
	 */
	private static Map<String,String> resolvedHosts=new ConcurrentHashMap<>(8);
	private Environment env;
	public SimpleUrlResolver(Environment env) {
		super();
		this.env = env;
	}
	@Override
	public String resolveHost(String servcieUrl) {
		if (!resolvedHosts.containsKey(servcieUrl)) {
			String actualUrl=null;
			synchronized (lock) {
				URI uri;
				try {
					uri = new URI(servcieUrl);
				} catch (URISyntaxException e) {
					throw new IllegalArgumentException(e);
				}
				String appName=uri.getHost();
				actualUrl = this.env.getProperty("rpc." + appName + ".url");
				if (actualUrl == null || actualUrl.trim().isEmpty()) {
					throw new IllegalArgumentException(
							"app:"
									+ appName+",virtualUrl="+servcieUrl
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
}


