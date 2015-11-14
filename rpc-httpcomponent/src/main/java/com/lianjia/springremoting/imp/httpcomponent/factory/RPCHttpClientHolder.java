package com.lianjia.springremoting.imp.httpcomponent.factory;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.springframework.beans.factory.DisposableBean;

/**
 * @author huisman
 * @since 1.0.0
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved.
 */
public class RPCHttpClientHolder implements DisposableBean {
	public static final String RPC_INTERNAL_HTTP_CLIENT = "rpcInternalHttpClient";
	private CloseableHttpClient httpclient;
	private PoolingHttpClientConnectionManager clientManager;

	/**
	 * @param httpclient
	 * @param clientManager
	 */
	public RPCHttpClientHolder(CloseableHttpClient httpclient,
			PoolingHttpClientConnectionManager clientManager) {
		super();
		this.httpclient = httpclient;
		this.clientManager = clientManager;
	}



	@Override
	public void destroy() throws Exception {
		if (this.httpclient != null) {
			this.httpclient.close();
		}
		if (this.clientManager != null) {
			this.clientManager.shutdown();
		}
	}

}


