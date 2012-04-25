package com.github.ldriscoll.ektorplucene.designdocument;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {

    String name();

    String index();

    String defaults() default "";

    String analyzer() default "";

}
