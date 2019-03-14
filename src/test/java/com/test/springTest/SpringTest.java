package com.test.springTest;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javax.sql.DataSource;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.ehcache.EhCacheCacheManager;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.core.JdbcTemplate;

import com.cms.component.TestPoint;
import com.cms.dao.JdbcDao;
import com.cms.entity.TestEntity;
import com.cms.service.TestService;
import com.cms.utils.JedisLock;
import com.cms.utils.JedisUtils;
import com.entity.TestEntityClass;
import com.test.spring.factoryBean.TestEntityClassFactoryBean;
import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;
import redis.clients.jedis.Jedis;

public class SpringTest extends SpringTestBase{
	
	@Autowired
	private TestService test;
	
	/**
	 * association关联查询,懒加载
	 */
	@Test
	public void test0() {
		System.out.println("###test-association:1");
		List<TestEntity> list  = test.getListById("testList", null);
		if (list != null) {
			System.out.println(">>>size:" + list.size());
			for (TestEntity ele : list) {
				System.out.println("id : " + ele.getId());
				System.out.println(ele);
			}
		}
		
		System.out.println("\n###test0-association:2");
		list  = test.getListById("testList2", null);
		if (list != null) {
			System.out.println(">>>size:" + list.size());
			for (TestEntity ele : list) {
				System.out.println("id : " + ele.getId());
				System.out.println(ele);
			}
		}
	}
	
	/**
	 * collection集合查询,懒加载
	 */
	@Test
	public void test01() {
		System.out.println("###test-collection:0");
		List<TestEntity> list  = test.getListById("testList3", null);
		if (list != null) {
			System.out.println(">>>size:" + list.size());
			for (TestEntity ele : list) {
				System.out.println("id : " + ele.getId() + ", child size : " + (ele==null?0:ele.getChild().size()));
				System.out.println(ele);
			}
		}
	}
	
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
		cache = singletonCacheManager.getCache("myTest");
		showCache(cache);
		System.out.println(">>>getAgain");
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
	 * 测试FactoryBean, 需要放开spring-test.xml相应的注释, 以及@Test注解
	 */
	//@Test
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
	
	/**
	 * ApplicationContext拥有BeanFactory的功能外,还提供资源获取,消息解析,事件处理与传播等功能
	 * @throws IOException 
	 */
	@Test
	public void testApplicationContext() throws IOException {
		System.out.println(">>>testApplicationContext");
		
		System.out.println("###Resource");
		ApplicationContext context = getApplicationContent();
		Resource resource = context.getResource("classpath:config/redis.xml");
		System.out.println(resource.exists());
		System.out.println(resource.getFilename());
		System.out.println(resource.getURL());
		System.out.println(resource.getURI());
		try {
			System.out.println(IOUtils.toString(resource.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println("\n###Message");
		System.out.println(context.getMessage("name", new Object[] {"名字", "张二狗"}, Locale.getDefault()));
		System.out.println(context.getMessage("name", new Object[] {"name", "dog two"}, Locale.US));
		
		System.out.println("\n###TransactionManager");
		// TODO
	}
	
	@Value("${jdbc.driverClassName}")
	private String jdbcDriverClassName;
	
	@Value("${jdbc.url}")
	private String url;
	
	@Value("${jdbc.username}")
	private String username;
	
	@Value("${jdbc.password}")
	private String password;
	
	/**
	 * SpringJBCD
	 */
	@Test
	public void jdbc() {
		System.out.println(">>>Begin jdbc()");
		
		System.out.println("###原生jdbc");
		System.out.println(jdbcDriverClassName);
		System.out.println(url);
		System.out.println(username);
		System.out.println(password);
		String sql = "select count(*) from test";
		try {
			Class.forName(jdbcDriverClassName);
			//每一次都取重新获取一个Connection连接
			Connection connect = DriverManager.getConnection(url, username, password);
			System.out.println(">>>" + connect);
			PreparedStatement pst = connect.prepareStatement(sql);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				System.out.println("jdbc:" + rs.getString(1));
			}
			
			//获取数据库表中的字段类型
			System.out.println(">>>字段类型");
			pst = connect.prepareStatement("select * from test");
			rs = pst.executeQuery();
			ResultSetMetaData resultSetMetaData = rs.getMetaData();
			int columnCount = resultSetMetaData.getColumnCount();
			System.out.println("ColumnCount : " + columnCount);
			for (int j = 1; j <= columnCount; j++) {
				//字段名-字段类型(Types)
				System.out.println(resultSetMetaData.getColumnName(j) + "-" + resultSetMetaData.getColumnType(j));
				System.out.println(resultSetMetaData.getColumnType(j) == Types.VARCHAR);
			}
			while(rs.next()) {
				System.out.println("jdbc:" + rs.getString(1) + "," + rs.getString(2));
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		System.out.println("###dataSource数据库连接池");
		DataSource dataSource = getBean("dataSource");
		try {
			//通过数据库连接池,每一次从数据库连接池里取有效的连接,避免每一次都取新的连接
			Connection connect1 = dataSource.getConnection();
			Connection connect2 = dataSource.getConnection();
			System.out.println(">>>" + connect1 + "|");
			System.out.println(">>>" + connect2 + "|");
		} catch (SQLException e) {
			e.printStackTrace();
		}		
		
		/***   Spring dataSource   */
		System.out.println("###Spring dataSource");//不是连接池,每次都创建新连接
		DataSource springDataSource = getBean("springDataSource");
		try {
			//通过数据库连接池,每一次从数据库连接池里取有效的连接,避免每一次都取新的连接
			Connection connect1 = springDataSource.getConnection();
			Connection connect2 = springDataSource.getConnection();
			System.out.println(">>>" + connect1 + "|");
			System.out.println(">>>" + connect2 + "|");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		/***   Spring JdbcTemplate   */
		System.out.println("###JdbcTemplate");
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		Integer integer = jdbcTemplate.queryForObject(sql, Integer.class);
		System.out.println(">>>count:" + integer);
		
		/***   Spring JdbcDaoSupport   */
		System.out.println("###JdbcDaoSupport");
		JdbcDao jdbcDao = getBean(JdbcDao.class);
		System.out.println(">>>count:" + jdbcDao.queryForObject(sql, Integer.class));
	}
	
	/**
	 * AOP
	 */
	@Test
	public void testAop() {
		System.out.println("===new bean===");//new出来的对象无法实现AOP
		System.out.println("Begin new TestPoint");
		TestPoint test = new TestPoint();
		System.out.println("Begin TestPoint toString()");
		System.out.println(test.toString());
		System.out.println("After TestPoint toString()");
		
		System.out.println("===spring bean===");//由spring管理的bean可以实现AOP
		test = getBean("pointSpring");
		System.out.println(test.toString());
	}
	
	static Long lockCount = 0L;
	static Long lockCount1 = 0L;
	
	/**
	 * 分布式锁
	 * <li>基于数据库的悲观锁实现
	 * <li>基于redis的setnx操作实现
	 */
	@Test
	public void testRedisLock() {
		System.out.println("\n=======begin========");
		String prefix = "RedisLock_";
		TestEntity test1 = test.get("test", 1);
		TestEntity test2 = test.get("test", 2);
		if (test1 == null || test2 == null) {
			System.out.println("\n>>>为空,不测试");
			return;
		}
		
		long beginTime = System.currentTimeMillis();
		
		
		int threadCount = 500;//线程个数
		CyclicBarrier cyclicBarrier = new CyclicBarrier(threadCount);//设置屏障,全部线程到达之后再执行
		CountDownLatch enDownLatch = new CountDownLatch(threadCount);//统计完成数的计数器
		
		System.out.println("\n>>>进行测试");
		DataSource springDataSource = getBean("springDataSource");
		
		//创建线程池
		ExecutorService pool = Executors.newFixedThreadPool(threadCount*2);
		for (int i = 0; i < threadCount; i++) {
			pool.execute(new Runnable() {
				@Override
				public void run() {
					System.out.println("\\n>>>等待...");
					try {
						cyclicBarrier.await();//阻塞线程
					} catch (InterruptedException | BrokenBarrierException e) {
						System.out.println("\n>>>error");
						e.printStackTrace();
					}
					System.out.println("\\n>>>开始执行...");
					
					//1.利用同步块实现线程安全(仅适用于单机系统, 不适用与分布式系统)
					/*synchronized (TestEntity.class) {
						TestEntity test1 = test.get("test", 1);
						HashMap<String, Object> map = new HashMap<>();
						map.put("id", 1);
						map.put("count", test1.getCount() - 1);
						test.updateCount(map);
					}*/
					
					//2.基于redis的分布式锁  未实现
					/*try {
						Jedis jedis = JedisUtils.getJedis();
						JedisLock lock = new JedisLock(jedis, prefix + 1, 10000, 30000);
						lock.acquire();
						try {
							lockCount1++;
							TestEntity test1 = test.get("test", 1);
							HashMap<String, Object> map = new HashMap<>();
							map.put("id", 1);
							map.put("count", test1.getCount() - 1);
							test.updateCount(map);
							JedisUtils.setString(prefix + 11, lockCount1+"");
						}
						finally {
							lock.release();
							JedisUtils.returnJedis(jedis);
						}
					} catch (Exception e) {
						System.out.println("\n>>>JedisUtils Exception!");
						e.printStackTrace();
					}*/
					
					
					//2.1.基于redis的分布式锁,已实现(速度较慢待优化)
					String value = UUID.randomUUID().toString().replaceAll("-", "");
					long result = 0;
					do {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						lockCount++;
						result = JedisUtils.setStringNx(prefix + 1, value);
					} while (result == 0);//获取不到锁则一直尝试
					//成功获取锁
					JedisUtils.pexpire(prefix + 1, 10*1000);//设置超时时间
					lockCount1++;
					TestEntity test1 = test.get("test", 1);
					HashMap<String, Object> map = new HashMap<>();
					map.put("id", 1);
					map.put("count", test1.getCount() - 1);
					test.updateCount(map);
					//释放锁
					if (value.equals(JedisUtils.getString(prefix + 1))) {//检查锁是否被当前线程持有
						JedisUtils.del(prefix + 1);
					}
					
					
					
					//3.使用数据库的排他锁(悲观锁)实现分布式锁
					/*Connection connect = null;
					while(true) {//获取锁时有可能超时抛异常,一直循环,直到获取锁
						try {
							lockCount++;
							connect = springDataSource.getConnection();
							connect.setAutoCommit(false);//设置为非自动提交模式
							//select ... for update 查询时会锁住某一记录, 即加锁; 可能表已经被锁住,出现超时异常; 
							PreparedStatement pst = connect.prepareStatement("select * from test where id = ? for update");
							pst.setInt(1, 2);//设置属性
							pst.executeQuery();
							
							//操作
							TestEntity test1 = test.get("test", 1);
							HashMap<String, Object> map = new HashMap<>();
							map.put("id", 1);
							map.put("count", test1.getCount() - 1);
							test.updateCount(map);
							lockCount1++;
							break;
						} catch (SQLException e) {
							System.out.println("SQLException1!");
							e.printStackTrace();
						} finally {
							if (connect != null) {//释放锁
								try {
									connect.commit();
									connect.close();
								} catch (SQLException e) {
									System.out.println("SQLException2!");
									e.printStackTrace();
								}
							}
						}
					}*/
					
					enDownLatch.countDown();
				}
			});
		}
		pool.shutdown();
		
		try {
			enDownLatch.await();//所有线程执行完毕之后再执行
			long endTime = System.currentTimeMillis();
			System.out.println("执行结束, 花费时间" + (endTime-beginTime) + "ms");
			System.out.println("\n>>>end:");
			System.out.println("\n>>>初始:" + test1);
			TestEntity result = test.get("test", 1);
			System.out.println("\n>>>结束:" + result);
			System.out.println("\n>>>相差:" + (test1.getCount() - result.getCount()) + ", 线程数:" + threadCount);
			System.out.println(lockCount + "_" + lockCount1);
		} catch (InterruptedException e) {
			System.out.println("\n>>>InterruptedException");
			e.printStackTrace();
		}
	}
	
}
