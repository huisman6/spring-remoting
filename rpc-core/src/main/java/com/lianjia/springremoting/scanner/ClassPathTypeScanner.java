package com.lianjia.springremoting.scanner;

import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.env.Environment;

/**
 * <p>
 * 使用TypeFilter扫描所有类，如果扫描到的数据过多的话，可能内存溢出 <br>
 * 你可以使用<br>
 * 1,AnnotationTypeFilter（查找特定annotation标注的类型）<br>
 * 2,CustomizeAssignableTypeFilter(查找某个类或者接口的所有子类),<br>
 * 3,SpecificTypeFilter（可以指定所有待扫描的Class-白名单）<br>
 * 这三种TypeFilter可以组合使用。
 * 
 * @author huisman
 * @createAt 2015年9月18日 上午9:28:17
 * @since 1.0.0
 * @Copyright (c) 2015, Youzhixu.com All Rights Reserved.
 */
public class ClassPathTypeScanner extends ClassPathScanningCandidateComponentProvider {
	public ClassPathTypeScanner() {
		super(false);
	}

	public ClassPathTypeScanner(Environment environment) {
		super(false, environment);
	}
}
