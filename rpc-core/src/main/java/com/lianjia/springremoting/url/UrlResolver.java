package com.lianjia.springremoting.url;


/**
 * <p>
 * 进行http/tcp连接时，需要解析远程主机地址
 * </p>
 * 
 * @author huisman
 * @since 1.0.0
 * @createAt 2015年9月20日 下午3:31:32
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved.
 */
public interface UrlResolver {
	/**
	 * <p>
	 * 根据serviceUrl查找真正的服务请求地址,
	 * </p>
	 * 
	 * @param serviceUrl 虚拟地址，类似：http://user/rpc/com.xxx.xxx.xxx.UserService
	 * @return
	 * @since: 1.0.0
	 */
	String resolveUrl(String serviceUrl);
}
