package com.cms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.cms.dao.TestDao;
import com.cms.entity.TestEntity;
import com.cms.service.TestService;

@Transactional
@Service
public class TestServiceImpl implements TestService{

	@Autowired
	private TestDao testdao;
	
	@Override
	public TestEntity get(String state, Integer id) {
		return testdao.get(state, id);
	}

	@Override
	public TestEntity get(String state, TestEntity entity) {
		return testdao.get(state, entity);
	}

	@Override
	public int update(String state, Object obj) {
		int count =  testdao.updateById(state, obj);
		String x = null;
		System.out.println(x.equals("test"));//测试事务回滚是否生效
		return count;
	}
	
	/**
	 * @Cacheable value:cache名, key:属性名, condition:过滤缓存结果
	 */
	@Override
	@Cacheable(value = "myTest", key="#key")
	public TestEntity getByCache(Integer key) {
		System.out.println(">>>begin getByCache");
		return testdao.get("test", key);
	}
	
	/**
	 * 清除指定的缓存
	 */
	@Override
	@CacheEvict(value = "myTest")
	public int updateByCache(Object obj) {
		System.out.println(">>>begin updateByCache");
		int count =  testdao.updateById("update", obj);
		return count;
	}

}
