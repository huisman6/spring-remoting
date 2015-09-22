package com.lianjia.springremoting.imp.netflix.url;

import java.net.URI;

import com.lianjia.springremoting.url.UrlResolver;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.DiscoveryManager;

/**
 * <p>
 *
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月22日 下午1:34:12
 * @since 1.0.0
 * @Copyright (c) 2015, Lianjia Group All Rights Reserved.
 */
@SuppressWarnings("deprecation")
public class EurekaUrlResolver implements UrlResolver {
	@Override
	public String resolveUrl(String serviceUrl) {
		try {
			URI uri = new URI(serviceUrl);
			String appName = uri.getHost();
			InstanceInfo instance =
					DiscoveryManager.getInstance().getLookupService()
							.getNextServerFromEureka(appName, false);
			if (instance != null) {
				StringBuilder sb = new StringBuilder(20);
				// default not secure
				sb.append(uri.getScheme()).append("://").append(instance.getIPAddr())
						.append(":" + instance.getPort()).append(uri.getScheme());
				return sb.toString();
			}
			throw new IllegalArgumentException("service url =" + serviceUrl + ",没有找到可用的服务器。");
		} catch (Exception e) {
			throw new IllegalArgumentException(e);
		}
	}
}
