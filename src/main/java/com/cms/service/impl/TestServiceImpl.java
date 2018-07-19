package com.cms.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cms.dao.TestDao;
import com.cms.entity.TestEntity;
import com.cms.service.TestService;

@Service
public class TestServiceImpl implements TestService{

	@Autowired
	private TestDao testdao;
	
	@Override
	public TestEntity get(String state, Integer id) {
		return testdao.get(state, id);
	}

}
