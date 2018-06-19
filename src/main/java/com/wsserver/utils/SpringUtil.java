package com.wsserver.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;

/**
 * Created by lrs on 2018/6/17.
 */
public class SpringUtil {

    // Spring应用上下文环境
    private static ApplicationContext applicationContext;


    public static void set(ApplicationContext act) {
        applicationContext = act;
    }


    public static ApplicationContext get() {
        return applicationContext;
    }


    public static Object getBean(String name) throws BeansException {
        return get().getBean(name);
    }
}