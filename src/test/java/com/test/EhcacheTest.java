package com.test;

import java.util.List;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import net.sf.ehcache.Element;

/**
 * 测试Ehcache缓存
 * @author xieyu
 *
 */
public class EhcacheTest {
	
	public static void main(String[] args) {
		CacheManager cacheManager = CacheManager.create("src/main/resources/config/ehcache.xml");
		String[] cacheNames = cacheManager.getCacheNames();
		System.out.println(">>>>>第一次获取cacheNames");
		printName(cacheNames);
		
		Cache cache = cacheManager.getCache("TestEntity");
		if (cache == null) {
			System.out.println(">>>>>Cache(TestEntity)不存在, 创建Cache");
			cache = new Cache("myTest", 10, true, false, 160, 130, true, 100);
			cacheManager.addCache(cache);//将Cache加入CacheManager后, Cache才是有效状态
		} else {
			System.out.println(">>>>>Cache(TestEntity)存在");
		}
		
		System.out.println(">>>>>第一次获取指定的Cache的Element");
		pringCacheElement(cache);
		
		
		for (int i = 0; i < 20 ; i++) {
			Element element = new Element("name_" + i, "value_" + i);
			cache.put(element);//将Element加入Cache
		}
		cache.flush();//显示调用此行代码才会将数据缓存到磁盘中, element被写入到data文件, Element将被序列化到index文件
		
		cacheNames = cacheManager.getCacheNames();
		printName(cacheNames);
		pringCacheElement(cache);
		 
		cacheManager.shutdown();//要关闭缓存
	}
	
	public static void printName(String[] names) {
		if (names == null || names.length == 0) {
			System.err.println(">>>>>printName(names): names is null or size is 0 !");
			return; 
		}
		for (String name : names) {
			System.out.println(name + ", ");
		}
		System.out.println();
	}
	
	public static void pringCacheElement(Cache cache) {
		List<?> list = cache.getKeys();//获取Element的name列表
		if (list == null || list.size() == 0) {
			System.err.println(">>>>>pringCacheElement(cache): cache is null or size is 0 !");
			return; 
		}
		System.out.println("Cache(" + cache.getName() + ") size is " + list.size());
		for (Object object : list) {
			System.out.print(object + " ");
			Element element = cache.get(object);//通过name获取Element
			System.out.println("Element[" + element.getObjectKey() + ", " + element.getObjectValue() + "]");
		}
		System.out.println();
		cache.removeAll();//清除所有element
	}
}
