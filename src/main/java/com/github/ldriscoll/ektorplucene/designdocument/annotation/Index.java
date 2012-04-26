package com.github.ldriscoll.ektorplucene.designdocument.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {

    String name();

    String index();

    String analyzer() default "";

    Defaults defaults() default @Defaults;

}
