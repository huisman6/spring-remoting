package com.lianjia.springremoting.example.spi.service;

import java.util.List;

import com.lianjia.springremoting.example.spi.model.User;
import com.lianjia.springremoting.exporter.annotation.HessianService;


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
@HessianService("user")
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
