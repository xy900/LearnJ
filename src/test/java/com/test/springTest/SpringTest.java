package com.test.springTest;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.cms.service.TestService;
import com.entity.TestEntityClass;
import com.test.EhcacheTest;
import com.test.spring.factoryBean.TestEntityClassFactoryBean;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

public class SpringTest extends SpringTestBase{
	
	@Autowired
	private TestService test;
	/**
	 * 测试二级缓存
	 */
	@Test
	public void test1() {
		//后台会打印Cache Hit Ratio, 表示缓存命中率
		System.out.println("========test1========");
		/*CacheManager cacheManager = CacheManager.create();
		String[] cacheNames = cacheManager.getCacheNames();
		EhcacheTest.printName(cacheNames);
		
		Cache cache = cacheManager.getCache("TestEntity");
		EhcacheTest.pringCacheElement(cache);*/
		
		//TestService test = getBean("testServiceImpl");
		System.out.println(test == null);
		for (int i = 1; i <= 2 ;i++) {
			System.out.println(">>>>一: 第"+ i + "次");
			System.out.println(test.get("test", i));
		}
		
		/*for (int i = 0; i < 10 ;i++) {
			System.out.println(">>>>二 : 第"+ i + "次");
			System.out.println(test.get("test", i));
		}*/
		/*cacheManager = CacheManager.create();
		cacheNames = cacheManager.getCacheNames();
		EhcacheTest.printName(cacheNames);
		
		cache = cacheManager.getCache("TestEntity");
		EhcacheTest.pringCacheElement(cache);*/
	}
	
	/**
	 * 测试spring与ehcache整合
	 */
	@Test
	public void test2() {
		System.out.println("========test2========");
		CacheManager cacheManager = getBean("cacheManager");//获取ehcache的缓存管理器
		Cache cache = cacheManager.getCache("TestEntity");
		if (cache == null) {
			System.out.println("Not exist cache");
		} else {
			System.out.println("Cache is :" + cache);
			List<?> list = cache.getKeys();
			if (list != null) {
				System.out.println("Cache size is " + list.size());
				for (Object ele : list) {
					System.out.print(ele + ", ");
				}
				System.out.println();
			}
		}
	}
	
	/**
	 * 测试FactoryBean
	 */
	@Test
	public void testFactoryBean() {
		System.out.println("========testFactoryBean========");
		TestEntityClass testEntityClass = getBean("testFactoryBean");
		if (testEntityClass != null) {
			System.out.println(testEntityClass.hashCode() + ":" + testEntityClass);
		} else {
			System.out.println("Not exist testFactoryBean");
		}
		
		//获取FactoryBean实例, 则需要加&
		TestEntityClassFactoryBean testEntityClass1 = getBean("&testFactoryBean");
		if (testEntityClass1 != null) {
			System.out.println(testEntityClass1.hashCode() + ":" + testEntityClass1);
		} else {
			System.out.println("Not exist testFactoryBean");
		}
	}
	
}
