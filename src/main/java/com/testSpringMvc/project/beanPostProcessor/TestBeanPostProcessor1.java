package com.testSpringMvc.project.beanPostProcessor;

import com.spring.annotation.Component;
import com.spring.lifeStyle.BeanPostProcessor;

/**
 * @Author: ly
 * @Package: com.test.project.beanPostProcessor
 * @Project: mySpring
 * @name: TestBeanPostProcessor
 * @Date:2024/1/14 16:51
 */
@Component
public class TestBeanPostProcessor1 implements BeanPostProcessor {
    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) {

        System.out.println("实例化前2"+beanName);
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        System.out.println("实例化后2"+beanName);
        return bean;
    }
}
