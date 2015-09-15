package com.youzhixu.springremoting.model;

import java.io.Serializable;

/**
 * <p>
 *
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月15日 下午3:10:07
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private String userName;
	private Integer id;
	private String desc;

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return "User [userName=" + userName + ", id=" + id + ", desc=" + desc + "]";
	}

}
