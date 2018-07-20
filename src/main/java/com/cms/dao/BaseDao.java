package com.cms.dao;

import java.util.List;
import javax.annotation.Resource;
import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.support.SqlSessionDaoSupport;
import org.springframework.stereotype.Repository;

@Repository
public class BaseDao extends SqlSessionDaoSupport{
    
	/*mybatis-spring-1.2.0版本以及更高的版本取消了自动注入,需要进行手动注入*/
	@Resource
    public void setSqlSessionFactory(SqlSessionFactory sqlSessionFactory){
        super.setSqlSessionFactory(sqlSessionFactory);
    }
    
    /**
     * 查询对象
     * @param key SQL语句主键
     * @param params 参数
     * @return DTO对象
     */
	@SuppressWarnings("unchecked")
	protected <T> T selectOne(String key, Object params) {
		return (T) getSqlSession().selectOne(key, params);
	}
	
    /**
     * 查询对象集
     * @param key SQL语句主键
     * @param params 参数       和 条件匹配参数
     * @return DTO对象列表
     */
	@SuppressWarnings("unchecked")
	protected <T> List<T> selectList(String key, Object params) {
		return (List<T>) getSqlSession().selectList(key, params);
	}
	
	/** 插入一条记录
	 * @param key SQL语句主键
	 * @param object DTO对象
	 * @return 插入记录数
	 */
	protected int insert(String key, Object object) {
		return getSqlSession().insert(key, object);
	}
	
	/** 删除一条记录
	 * @param key SQL语句主键
	 * @param object DTO对象
	 * @return 删除记录数
	 */
	protected int delete(String key, Object object) {
		return getSqlSession().delete(key, object);
	}
	
    /**
     * 更新一条记录
     * @param key SQL语句主键
     * @param obj DTO对象
     * @return 更新记录数
     */
	protected int update(String key, Object object) {
		return getSqlSession().update(key, object);
	}
	
}
