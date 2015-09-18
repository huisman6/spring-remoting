package com.youzhixu.springremoting.example.spi;

import java.util.List;

import com.youzhixu.springremoting.example.provider.model.User;
import com.youzhixu.springremoting.exporter.annotation.HttpService;


/**
 * <p>
 * api-service interface
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月15日 下午3:05:22
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
@HttpService(app = "user")
public interface UserService {
	/**
	 * <p>
	 * 根据用户ID查找用户信息
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @param userId
	 * @return
	 */
	User findById(int userId);

	/**
	 * <p>
	 * 查找所有用户
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @return
	 */
	List<User> findAll();
}
