package com.cms.dao;

import org.springframework.jdbc.core.support.JdbcDaoSupport;

public class JdbcDao extends JdbcDaoSupport{
	
	public <T> T queryForObject(String sql, Class<T> clazz) {
		return getJdbcTemplate().queryForObject(sql, clazz);
	}
}
