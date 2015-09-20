package com.youzhixu.springremoting.serialization;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * <p>
 *  序列化和反序列化
 * </p> 
 * @author huisman 
 * @since 1.0.0
 * @createAt 2015年9月20日 下午2:40:06
 * @Copyright (c) 2015,Youzhixu.com Rights Reserved. 
 */
public interface Serializer {
	 /**
	  * <p>
	  *  将对象序列化为字节数组
	  * </p> 
	  * @param obj
	  * @return
	  * @since: 1.0.0
	 */
	byte[] serialize(Object obj) throws Exception;
	
	 
	/**
	  * <p>
	  *  将对象写入stream,实现类无需关闭流，调用方负责
	  * </p> 
	  * @param out
	  * @param obj
	  * @since: 1.0.0
	 */
	void writeObject(OutputStream out,Object obj) throws Exception;
    /**
      * <p>
      *  从流中读取对象,实现类无需关闭流，调用方负责
      * </p> 
      * @param is
      * @return
      * @since: 1.0.0
     */
    Object readObject(InputStream is) throws Exception;
}


