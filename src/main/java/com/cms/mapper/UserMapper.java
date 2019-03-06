package com.cms.mapper;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import com.cms.entity.User;

@Mapper
@Repository
public interface UserMapper {
	
	@Select("select * from t_user where user_name = #{userName}")
	@Results(id="findByUserName", value= {
			@Result(column="id", property="id"),
			@Result(column="user_name", property="userName"),
			@Result(column="pass_wd", property="passwd"),
			@Result(column="pass_count", property="passCount"),
			@Result(column="fail_count", property="failCount"),
		}
	)
	List<User> findByUserName(String userName);
}
