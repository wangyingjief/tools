package com.wtds.db.annotation;


import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 字段属性注解
 * PkEnum是否为主键
 * name字段名
 * size长度
 * type字段类型
 * defaultValue默认值
 * @author wyj
 *
 */
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DBField {
	/**
	 * 是否为主键
	 * @return
	 */
	public PkEnum parimaryKey() default PkEnum.not;
	/**
	 * 字段名
	 * @return
	 */
	public String name()default "";
	/**
	 * 长度
	 * @return
	 */
	public String size()default "255";
	/**
	 * 字段类型
	 * @return
	 */
	public String type()default "varchar2";
	/**
	 * 默认值
	 * @return
	 */
	public String defaultValue()default "";
}
