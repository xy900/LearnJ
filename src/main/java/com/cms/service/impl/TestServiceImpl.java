package com.cms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
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

}
