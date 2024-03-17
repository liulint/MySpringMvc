package com.spring.annotation;

import com.spring.annotation.AliasFor;
import com.spring.annotation.Component;

import java.lang.annotation.*;

/**
 * @Author: ly
 * @Package: com.springmvc.annotation
 * @Project: mySpring
 * @name: Controller
 * @Date:2024/1/27 22:50
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    @AliasFor(annotation = Component.class)
    String value() default "";
}
