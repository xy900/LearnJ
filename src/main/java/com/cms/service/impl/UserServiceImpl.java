package com.cms.service.impl;

import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.cms.entity.User;
import com.cms.mapper.UserMapper;
import com.cms.service.UserService;

@Service
@Transactional
public class UserServiceImpl implements UserService{
	private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
	@Autowired
	private UserMapper userMapper;
	
	@Override
	public List<User> findByUserName(String userName) {
		logger.info("userName[{}]", userName);
		return userMapper.findByUserName(userName);
	}
	
	@Override
	@Cacheable(value="com.cms.entity.User", key="#userName") //可以缓存集合类型
	public List<User> findByUserNameByCache(String userName) {
		logger.info("userName[{}]", userName);
		return userMapper.findByUserName(userName);
	}

	@Override
	public void updateSuccess(String userName) {
		if (StringUtils.isNotBlank(userName)) {
			userMapper.updateSuccess(userName);
		}
	}

	@Override
	public void updateFail(String userName) {
		if (StringUtils.isNotBlank(userName)) {
			userMapper.updateFail(userName);
		}
	}
}
