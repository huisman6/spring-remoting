package com.youzhixu.springremoting.serialize;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>
 * none operation serializer
 * </p>
 * 
 * @author huisman
 * @since 1.0.0
 * @createAt 2015年9月21日 上午12:00:31
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved.
 */
class NoneOpSerializer implements Serializer {

	@Override
	public byte[] serialize(Object obj) throws Exception {
		return null;
	}

	@Override
	public void writeObject(OutputStream out, Object obj) throws IOException {}

	@Override
	public Object readObject(InputStream is) throws IOException {
		return null;
	}

}
