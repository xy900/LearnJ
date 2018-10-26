package com.cms.service;

import com.cms.entity.TestEntity;

public interface TestService {

	TestEntity get(String state, Integer id);
	
	TestEntity get(String state, TestEntity entity);
	
	int update(String state, Object obj);
	
	TestEntity getByCache(Integer key);
	
	int updateByCache(Object obj);
}
