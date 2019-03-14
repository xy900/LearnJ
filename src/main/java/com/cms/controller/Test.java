package com.cms.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import com.cms.component.TestPoint;
import com.cms.entity.TestEntity;
import com.cms.service.TestService;
import com.cms.utils.ApplicationContextHelper;
import com.cms.utils.JedisLock;
import com.cms.utils.JedisUtils;
import net.sf.json.JSONObject;
import redis.clients.jedis.Jedis;

@Controller
@RequestMapping("/test")
public class Test {
	
	private static Logger logger = LoggerFactory.getLogger(Test.class);
	
	@Autowired
	private TestPoint point;
	
	@Autowired
	@Qualifier("testPoint") //查找指定name的bean
	private TestPoint testPoint1;//默认通过byType查找, 有多个bean再通过byName查找
	
	@Autowired
	private TestPoint pointSpring;
	
	@Autowired
	private TestService testService;
	
	@RequestMapping("/1.do")
	public String test1(HttpServletRequest request, HttpServletResponse response, Model model) {
		logger.debug("1.do 哈哈");
		logger.info("11.do 蛤蛤");
		model.addAttribute("title", "1.jtp");
		return "1";
	}
	
	@ResponseBody
	@RequestMapping(value="/2.do", produces = "application/json;charset=utf-8")
	public String test2(HttpServletRequest request, HttpServletResponse response, Model model) {
		System.out.println(request.getRequestURI());
		//System.out.println(request.getContextPath());
		model.addAttribute("title", "1.jtp");
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", "李三");
		jsonObject.put("age", 25);
		System.out.println(point.toString());
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject.toString();
	}
	
	@ResponseBody
	@RequestMapping(value="/22.do", produces = "application/json;charset=utf-8")
	public String test2jspx(HttpServletRequest request, HttpServletResponse response, Model model) {
		System.out.println(request.getRequestURI());
		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", "李三");
		jsonObject.put("age", 25);
		System.out.println(testPoint1);
		System.out.println(pointSpring);
		return jsonObject.toString();
	}
	
	/**
	 * 测试回滚
	 */
	@ResponseBody
	@RequestMapping(value="/druid.do", produces = "application/json;charset=utf-8")
	public String druid(HttpServletRequest request, HttpServletResponse response, Model model) {
		System.out.println(request.getRequestURI());
		TestEntity entity = testService.get("test", 1);
		System.out.println(entity);
		TestEntity test = new TestEntity();
		test.setId(1);
		test.setName("test");
		TestEntity entity2 = testService.get("test", test);
		System.out.println(entity2);
		
		testService.update("update", 1);//测试事务回滚是否生效
		return "";
	}
	
	@ResponseBody
	@RequestMapping(value = "resource.do", produces = "application/json;charset=utf-8")
	public String resource(HttpServletRequest request, HttpServletResponse response, Model model) {
		Resource resource = ApplicationContextHelper.applicationContext.getResource("classpath:config/properties/jdbc.properties");
		try {
			InputStream in = resource.getInputStream();
			StringBuilder sb = new StringBuilder();
			int n = 0;
			while ( (n=in.read())!= -1 ) {
				sb.append((char)n);
			}
			System.out.println(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Resource resource1 = ApplicationContextHelper.applicationContext.getResource("/WEB-INF/classes/config/spring.xml");
		System.out.println("\n是否存在文件：" + resource1.exists());
		try {
			InputStream in = resource1.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
			BufferedReader reader = new BufferedReader(inputStreamReader);
			StringBuilder sb = new StringBuilder();
			char[] cbuf = new char[1000];
			/*while ( reader.read(cbuf, 0, 1000) != -1 ) {
				sb.append(cbuf);
			}*/
			while ( reader.read(cbuf) != -1 ) {
				sb.append(cbuf);
			}
			//sb.append(cbuf);
			/*String string = "";
			while ( (string=reader.readLine()) != null ) {
				sb.append(string);
			}*/
			System.out.println(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return "success";
	}
	
	/**
	 * 测试redis
	 */
	@ResponseBody
	@RequestMapping(value="/redis.do", produces = "application/json;charset=utf-8")
	public String redis(HttpServletRequest request, String id, HttpServletResponse response, Model model) {
		JSONObject jsonObject = new JSONObject();
		String value = null;
		//获取redis缓存
		value = JedisUtils.getString(TestEntity.class.getSimpleName() + "_" + id);
		if (StringUtils.isBlank(value)) {//缓存不存在,查库
			logger.info("Get redis failed");
			TestEntity entity = testService.get("test", Integer.valueOf(id));
			jsonObject = JSONObject.fromObject(entity);
			value = jsonObject.toString();
			JedisUtils.setString(TestEntity.class.getSimpleName() + "_" + id, value);
			logger.info("Set redis [{}, {}]", TestEntity.class.getSimpleName() + "_" + id, value);
		} else {
			logger.info("Get redis success");
		}
	
		return value;
	}
	
	/**
	 * 测试ehcache
	 */
	@ResponseBody
	@RequestMapping(value="/ehcache.do", produces = "application/json;charset=utf-8")
	public String ehcacheTest(HttpServletRequest request, @RequestParam(defaultValue="1")String id, HttpServletResponse response, Model model) {
		JSONObject jsonObject = new JSONObject();
		TestEntity entity = testService.get("test", Integer.valueOf(id));
		jsonObject = JSONObject.fromObject(entity);
		return jsonObject.toString();
	}
	
	/** 基于redis的分布式锁（已实现，速度较慢待优化）
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="/redisLock.do", produces = "application/json;charset=utf-8")
	public String redisLock(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("\n=======begin========");
		String prefix = "RedisLock_";
		TestEntity test1 = testService.get("test", 1);
		TestEntity test2 = testService.get("test", 2);
		if (test1 == null || test2 == null) {
			System.out.println("\n>>>为空,不测试");
			return "not test";
		}
		
		long beginTime = System.currentTimeMillis();
		
		int threadCount = 500;//线程个数
		CyclicBarrier cyclicBarrier = new CyclicBarrier(threadCount);//设置屏障,全部线程到达之后再执行
		CountDownLatch enDownLatch = new CountDownLatch(threadCount);//统计完成数的计数器
		
		System.out.println("\n>>>进行测试");
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
					
					String value = UUID.randomUUID().toString().replaceAll("-", "");
					long result = 0;
					do {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						result = JedisUtils.setStringNx(prefix + 1, value);
					} while (result == 0);//获取不到锁则一直尝试
					//成功获取锁
					JedisUtils.pexpire(prefix + 1, 10*1000);//设置超时时间
					TestEntity test1 = testService.get("test", 1);
					HashMap<String, Object> map = new HashMap<>();
					map.put("id", 1);
					map.put("count", test1.getCount() - 1);
					testService.updateCount(map);
					//释放锁
					if (value.equals(JedisUtils.getString(prefix + 1))) {//检查锁是否被当前线程持有
						JedisUtils.del(prefix + 1);
					}
					
					/*try {
						Jedis jedis = JedisUtils.getJedis();
						JedisLock lock = new JedisLock(jedis, prefix + 1, 10000, 30000);
						lock.acquire();
						try {
							TestEntity test1 = testService.get("test", 1);
							HashMap<String, Object> map = new HashMap<>();
							map.put("id", 1);
							map.put("count", test1.getCount() - 1);
							testService.updateCount(map);
						}
						finally {
							lock.release();
							JedisUtils.returnJedis(jedis);
						}
					} catch (Exception e) {
						System.out.println("\n>>>JedisUtils Exception!");
						e.printStackTrace();
					}*/
					enDownLatch.countDown();
				}
			});
		}
		pool.shutdown();
		TestEntity result = null;
		try {
			enDownLatch.await();//所有线程执行完毕之后再执行
			long endTime = System.currentTimeMillis();
			System.out.println("执行结束, 花费时间" + (endTime-beginTime) + "ms");
			System.out.println("\n>>>end:");
			System.out.println("\n>>>初始:" + test1);
			result = testService.get("test", 1);
			System.out.println("\n>>>结束:" + result);
			System.out.println("\n>>>相差:" + (test1.getCount() - result.getCount()) + ", 线程数:" + threadCount);
		} catch (InterruptedException e) {
			System.out.println("\n>>>InterruptedException");
			e.printStackTrace();
		}
		return "相差:" + (test1.getCount() - result.getCount()) + ", 线程数:" + threadCount;
	}
	
	/**
	 *  基于数据库悲观锁实现的分布式锁
	 * @return
	 */
	private static AtomicInteger count1 = new AtomicInteger(0);
	private static AtomicInteger count2 = new AtomicInteger(0);
	private static AtomicInteger error = new AtomicInteger(0);
	@ResponseBody
	@RequestMapping(value="/databaseLock.do", produces = "application/json;charset=utf-8")
	public String databaseLock(HttpServletRequest request, HttpServletResponse response) {
		System.out.println("\n=======begin========");
		TestEntity test1 = testService.get("test", 1);
		TestEntity test2 = testService.get("test", 2);
		if (test1 == null || test2 == null) {
			System.out.println("\n>>>为空,不测试");
			return "not test";
		}
		
		long beginTime = System.currentTimeMillis();
		
		int threadCount = 500;//线程个数
		CyclicBarrier cyclicBarrier = new CyclicBarrier(threadCount);//设置屏障,全部线程到达之后再执行
		CountDownLatch enDownLatch = new CountDownLatch(threadCount);//统计完成数的计数器
		
		System.out.println("\n>>>进行测试");
		DataSource springDataSource = (DataSource) ApplicationContextHelper.applicationContext
				.getBean("springDataSource");
		
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
					
					Connection connect = null;
					while(true) {//获取锁时有可能超时抛异常,一直循环,直到获取锁
						try {
							count1.incrementAndGet();
							connect = springDataSource.getConnection();
							connect.setAutoCommit(false);//设置为非自动提交模式
							//select ... for update 查询时会锁住某一记录, 即加锁; 可能表已经被锁住,出现超时异常; 
							PreparedStatement pst = connect.prepareStatement("select * from test where id = ? for update");
							pst.setInt(1, 2);//设置属性
							pst.executeQuery();
							
							//操作
							TestEntity test1 = testService.get("test", 1);
							HashMap<String, Object> map = new HashMap<>();
							map.put("id", 1);
							map.put("count", test1.getCount() - 1);
							testService.updateCount(map);
							count2.incrementAndGet();
							break;
						} catch (SQLException e) {
							error.incrementAndGet();
							System.out.println("SQLException1!");
							e.printStackTrace();
						} finally {
							if (connect != null) {//释放锁
								try {
									connect.commit();
									connect.close();
								} catch (SQLException e) {
									error.incrementAndGet();
									System.out.println("SQLException2!");
									e.printStackTrace();
								}
							}
						}
					}
					
					enDownLatch.countDown();
				}
			});
		}
		pool.shutdown();
		TestEntity result = null;
		try {
			enDownLatch.await();//所有线程执行完毕之后再执行
			long endTime = System.currentTimeMillis();
			System.out.println("执行结束, 花费时间" + (endTime-beginTime) + "ms");
			System.out.println("\n>>>end:");
			System.out.println("\n>>>初始:" + test1);
			result = testService.get("test", 1);
			System.out.println("\n>>>结束:" + result);
			System.out.println("\n>>>相差:" + (test1.getCount() - result.getCount()) + ", 线程数:" + threadCount);
			System.out.println("try lock:" + count1 + ", success lock:" + count2 + ", error:" + error);
			count1.set(0);
			count2.set(0);
		} catch (InterruptedException e) {
			System.out.println("\n>>>InterruptedException");
			e.printStackTrace();
		}
		return "相差:" + (test1.getCount() - result.getCount()) + ", 线程数:" + threadCount;
	}
}
