package com.lianjia.springremoting.exporter.executor;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.remoting.httpinvoker.HttpInvokerServiceExporter;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

import com.lianjia.springremoting.serialize.Serializer;

/**
 * <p>
 * 
 * </p>
 * 
 * @author huisman
 * @since 1.0.0
 * @createAt 2015年9月20日 下午7:47:29
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved.
 */
public class CustomizeHttpInvokerServiceExporter extends HttpInvokerServiceExporter {
	private Serializer serializer;

	/**
	 * <p>
	 * 指定序列化协议
	 * </p>
	 * 
	 * @param serializer
	 */
	public CustomizeHttpInvokerServiceExporter(Serializer serializer) {
		super();
		this.serializer = serializer;
	}

	@Override
	protected RemoteInvocation readRemoteInvocation(HttpServletRequest request, InputStream is)
			throws IOException, ClassNotFoundException {
		InputStream dis = decorateInputStream(request, is);
		try {
			Object obj = this.serializer.readObject(dis);
			if (!(obj instanceof RemoteInvocation)) {
				throw new RemoteException("Deserialized object needs to be assignable to type ["
						+ RemoteInvocation.class.getName() + "]: " + obj);
			}
			return (RemoteInvocation) obj;
		} finally {
			dis.close();
		}
	}

	@Override
	protected void writeRemoteInvocationResult(HttpServletRequest request,
			HttpServletResponse response, RemoteInvocationResult result, OutputStream os)
			throws IOException {
		OutputStream oos = decorateOutputStream(request, response, os);
		try {
			this.serializer.writeObject(oos, result);
		} finally {
			oos.close();
		}
	}


}
