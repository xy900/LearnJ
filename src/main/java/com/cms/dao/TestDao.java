package com.cms.dao;

import java.util.List;
import com.cms.entity.TestEntity;

public interface TestDao {
	TestEntity get(String state, Integer id);
	
	TestEntity get(String state, TestEntity entity);
	
	int updateById(String state, Object obj);
	
	List<TestEntity> getListById(String state, Integer key);
}
