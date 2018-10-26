package com.test.springTest;

import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.ehcache.EhCacheCacheManager;

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
		CacheManager cacheManagerFactory = getBean("cacheManagerFactory");//获取CacheManager 
		CacheManager singletonCacheManager = CacheManager.create();  //获取单例CacheManager, 因为已经存在同名的CacheManager(上面)
		EhCacheCacheManager springCacheManager = getBean("cacheManager");//将CacheManager交给spring管理
		if (cacheManagerFactory == singletonCacheManager) {
			System.out.println(">>>同一实例");
		}
		if (springCacheManager.getCacheManager() == singletonCacheManager) {
			System.out.println(">>>spring中的CacheManager与单例模式的相等");
		}
		Cache cache = singletonCacheManager.getCache("TestEntity");//不是有spring管理的CacheManager
		showCache(cache);
		
		System.out.println("-------spring管理的EhcacheManager-----------");
		System.out.println(test.getByCache(1));
		System.out.println(test.getByCache(1));
		
		System.out.println(test.getByCache(2));
		System.out.println(test.getByCache(2));
		
		System.out.println(">>>update");
		System.out.println(test.updateByCache(1));
		System.out.println(test.getByCache(1));
		
		System.out.println(">>>myTest");
		cache = singletonCacheManager.getCache("myTest");//不是有spring管理的CacheManager
		showCache(cache);
		
		try {
			Thread.sleep(35 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		System.out.println(">>>myTest after 35s");//30s之后
		cache = singletonCacheManager.getCache("myTest");
		showCache(cache);
		
		try {
			Thread.sleep(35 * 1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println(">>>myTest after 70s");//70s之后从内存中移除
		cache = singletonCacheManager.getCache("myTest");
		showCache(cache);
	}
	
	public void showCache(Cache cache) {
		if (cache == null) {
			System.out.println(">>>Not exist cache");
		} else {
			System.out.println(">>>Cache is :" + cache);
			System.out.println(">>>MemorySize is :" + cache.getMemoryStoreSize());
			List<?> list = cache.getKeys();
			if (list != null) {
				System.out.println(">>>Cache size is " + list.size());
				for (Object ele : list) {
					System.out.println(ele + ", " + cache.get(ele));
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
