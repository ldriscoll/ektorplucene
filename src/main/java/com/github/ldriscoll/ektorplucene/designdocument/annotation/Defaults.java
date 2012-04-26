package com.github.ldriscoll.ektorplucene.designdocument.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Defaults {
	
	String field() default "";
	
	String type() default "";
	
	String store() default "";
	
	String index() default "";
	
}
