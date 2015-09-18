package com.youzhixu.springremoting.scanner;

import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;

/**
 * <p>
 * 查找指定class的所有实现类（spring default support class start with "java")
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月18日 下午12:36:45
 * @since 1.0.0
 * @Copyright (c) 2015, Lianjia Group All Rights Reserved.
 */

public class CustomizeAssignableTypeFilter extends AssignableTypeFilter {
	private Class<?> parentClass;

	public CustomizeAssignableTypeFilter(Class<?> targetType) {
		super(targetType);
		this.parentClass = targetType;
	}

	@Override
	protected Boolean matchTargetType(String typeName) {
		if (this.parentClass.getName().equals(typeName)) {
			return true;
		} else if (Object.class.getName().equals(typeName)) {
			return false;
		}
		try {
			Class<?> clazz = ClassUtils.forName(typeName, getClass().getClassLoader());
			return this.parentClass.isAssignableFrom(clazz);
		} catch (Throwable ex) {
			return null;
		}
	}


}
