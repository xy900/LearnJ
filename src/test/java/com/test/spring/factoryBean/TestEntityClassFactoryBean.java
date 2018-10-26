package com.test.spring.factoryBean;

import java.util.Date;
import org.springframework.beans.factory.FactoryBean;
import com.entity.TestEntityClass;

/**
 * FactoryBean, 用于实例化指定的对象
 * @author xieyu
 *
 */
public class TestEntityClassFactoryBean implements FactoryBean<TestEntityClass>{
	private String id;
	private String name;
	private Integer no;
	private Date currTime;
	
	@Override
	public TestEntityClass getObject() throws Exception {//返回实例
		return new TestEntityClass(id, name, no, currTime);
	}

	@Override
	public Class<TestEntityClass> getObjectType() {
		return TestEntityClass.class;
	}

	@Override
	public boolean isSingleton() {
		return true;
	}
}
