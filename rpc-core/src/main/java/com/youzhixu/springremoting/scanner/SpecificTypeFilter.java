package com.youzhixu.springremoting.scanner;

import java.util.HashSet;
import java.util.Set;

import org.springframework.core.type.filter.AbstractTypeHierarchyTraversingFilter;

/**
 * <p>
 * 扫描优先级： 1， onlyInterface <br>
 * 2，onlyConcrete <br>
 * 3，onlyAbstract <br>
 * 4，onlyAnnotation <br>
 * 5，onlyFinal <br>
 * 6，targetClasses or targetAnnotations contains <br>
 * </p>
 * 
 * @author huisman
 * @createAt 2015年9月18日 上午11:08:04
 * @since 1.0.0
 * @Copyright (c) 2015, youzhixu.com All Rights Reserved.
 */

public class SpecificTypeFilter extends AbstractTypeHierarchyTraversingFilter {
	/**
	 * 是否只扫描接口
	 */
	private boolean onlyInterface;
	/**
	 * 待扫描的类
	 */
	private Set<String> targetClasses = new HashSet<>(64);

	/**
	 * <p>
	 * 默认搜索所有实现类以及接口
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @param considerInherited
	 * @param considerInterfaces
	 */
	public SpecificTypeFilter(Class<?>... classes) {
		this(false, classes);
	}

	public SpecificTypeFilter(boolean onlyInterface, Class<?>... classes) {
		super(true, true);
		this.setOnlyInterface(onlyInterface);
		if (classes == null) {
			return;
		}
		for (Class<?> cl : classes) {
			addTargetClass(cl);
		}
	}

	@Override
	protected boolean matchClassName(String className) {
		if (onlyInterface) {
			return false;
		}
		return targetClasses.contains(className);
	}

	@Override
	protected Boolean matchSuperClass(String superClassName) {
		if (onlyInterface) {
			return false;
		}
		return targetClasses.contains(superClassName);
	}

	@Override
	protected Boolean matchInterface(String interfaceName) {
		return targetClasses.contains(interfaceName);
	}

	/**
	 * <p>
	 * 指定待扫描的类型
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @param clazz
	 */
	public void addTargetClass(Class<?> clazz) {
		this.targetClasses.add(clazz.getName());
	}

	/**
	 * <p>
	 * 是否只能扫描接口
	 * </p>
	 * 
	 * @since: 1.0.0
	 * @param onlyInterface
	 */
	public void setOnlyInterface(boolean onlyInterface) {
		this.onlyInterface = onlyInterface;
	}



}
