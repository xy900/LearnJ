package com.cms.dao.impl;

import javax.annotation.Resource;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;
import com.cms.dao.TestDao;
import com.cms.entity.TestEntity;

@Repository
public class TestDaoImlp extends SqlSessionDaoSupport implements TestDao{
	
    @Resource
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory){
        super.setSqlSessionFactory(sqlSessionFactory);
    }
    
	@Override
	public TestEntity get(String state, Integer id) {
		return getSqlSession().selectOne(state, id);
	}

}
