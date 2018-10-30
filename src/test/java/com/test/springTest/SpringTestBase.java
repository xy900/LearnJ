package com.test.springTest;

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractJUnit4SpringContextTests;

/**
 * spring测试类
 * @author xieyu
 *
 */
@ContextConfiguration(locations = {"classpath:config/spring.xml",
								   "classpath:config/redis.xml"
		})
public class SpringTestBase extends AbstractJUnit4SpringContextTests {
	/*private ApplicationContext applicationContext = 
			new ClassPathXmlApplicationContext("config/spring.xml",
											   "config/redis.xml"
					);*/
	public ApplicationContext getApplicationContent() {
		return applicationContext;
	}
	
	@SuppressWarnings("unchecked")
	public <T> T getBean(String name) {
		return (T) applicationContext.getBean(name);
	}
	
	public <T> T getBean(Class<T> clazz) {
		return applicationContext.getBean(clazz);
	}
}
