package com.cms.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@Lazy(false)//Spring配置文件中默认使用了懒加载，会使得此类无法实例化
public class ApplicationContextHelper implements ApplicationContextAware{
	public static ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		ApplicationContextHelper.applicationContext = applicationContext;
	}
}
