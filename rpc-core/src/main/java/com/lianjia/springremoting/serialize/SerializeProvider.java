package com.lianjia.springremoting.serialize;

/**
 * <p>
 *
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月21日 上午10:58:34
 * @since 1.0.0
 * @Copyright (c) 2015, Lianjia Group All Rights Reserved.
 */

public enum SerializeProvider {
	JAVA(new JavaSerializer()), NONE(new NoneOpSerializer());
	private Serializer provider;

	private SerializeProvider(Serializer serializer) {
		this.provider = serializer;
	}

	public Serializer provider() {
		return provider;
	}


}
