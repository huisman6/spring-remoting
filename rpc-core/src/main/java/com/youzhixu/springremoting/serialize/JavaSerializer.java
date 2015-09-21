package com.youzhixu.springremoting.serialize;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;


/**
 * <p>
 * 默认使用java序列化
 * </p>
 * 
 * @author huisman
 * @since 1.0.0
 * @createAt 2015年9月20日 下午3:00:08
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved.
 */
class JavaSerializer implements com.youzhixu.springremoting.serialize.Serializer {

	@Override
	public byte[] serialize(Object obj) throws Exception {
		ByteArrayOutputStream bas = new ByteArrayOutputStream();
		ObjectOutputStream baos = new ObjectOutputStream(bas);
		baos.writeObject(obj);
		return bas.toByteArray();
	}

	@Override
	public void writeObject(OutputStream out, Object obj) throws IOException {
		ObjectOutputStream baos = new ObjectOutputStream(out);
		baos.writeObject(obj);
	}

	@Override
	public Object readObject(InputStream is) throws IOException {
		ObjectInputStream ois = new ObjectInputStream(is);
		try {
			return ois.readObject();
		} catch (Exception e) {
			throw new IOException(e);
		}
	}

}
