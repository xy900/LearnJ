package com.cms.dao;

import com.cms.entity.TestEntity;

public interface TestDao {
	TestEntity get(String state, Integer id);
}
