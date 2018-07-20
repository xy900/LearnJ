package com.cms.dao.impl;

import org.springframework.stereotype.Repository;

import com.cms.dao.BaseDao;
import com.cms.dao.TestDao;
import com.cms.entity.TestEntity;

@Repository
public class TestDaoImlp extends BaseDao implements TestDao{
    
	@Override
	public TestEntity get(String key, Integer id) {
		return selectOne(key, id);
	}

	@Override
	public TestEntity get(String state, TestEntity entity) {
		return selectOne(state, entity);
	}

	@Override
	public int updateById(String state, Object obj) {
		return update(state, obj);
	}

}
