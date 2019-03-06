package com.cms.service;

import java.util.List;
import com.cms.entity.User;

public interface UserService {
	
	List<User> findByUserName(String userName);
	
	List<User> findByUserNameByCache(String userName);
}
