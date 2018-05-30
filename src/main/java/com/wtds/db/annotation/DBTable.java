package com.wtds.db.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 表注解
 * name表名
 * @author wyj
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBTable {
	/**
	 * 表名
	 * @return
	 */
	public String name() default "";
	
	/**
	 * 是否自动建表
	 * @return
	 */
	public boolean autoCreateTable() default false;
}
