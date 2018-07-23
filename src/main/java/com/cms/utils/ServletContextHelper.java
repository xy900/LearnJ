package com.cms.utils;

import javax.servlet.ServletContext;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.context.ServletContextAware;

@Component
@Lazy(false)//Spring配置文件中默认使用了懒加载，会使得此类无法实例化
public class ServletContextHelper implements ServletContextAware{
	public static ServletContext servletContext;

	@Override
	public void setServletContext(ServletContext servletContext) {
		ServletContextHelper.servletContext = servletContext;
	}
}
