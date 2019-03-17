package com.cms.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.commons.lang.StringUtils;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.ZooKeeper.States;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 *  基于ZooKeeper的分布式锁,本地操作zookeeper会偶尔报错,原因待探究
 * @author XieYu
 */
public class ZkLock {
	private static final String LOCKPATH = "/zklock";
	private static final Logger log = LoggerFactory.getLogger(ZkLock.class);
	
	private ZooKeeper zk;
	private String lockPath;//当前节点
	
	public ZkLock(String host) {
		if (StringUtils.isBlank(host)) host = "127.0.0.1:2181";
		try {
			//创建ZooKeeper实例
			CountDownLatch countDownLatch = new CountDownLatch(1);
			zk = new ZooKeeper(host, 5000, 
					new Watcher() {
						@Override
						public void process(WatchedEvent event) {
							if (event.getState() == KeeperState.SyncConnected) { // 已经连接
								countDownLatch.countDown();//计数器减一, 唤醒等待的线程
							}
						}
					}
			);
			/*countDownLatch.await();
			if (zk.exists(LOCKPATH, null) == null) {//创建分布式锁根节点
				String lockpath = zk.create(LOCKPATH, LOCKPATH.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
				log.info("创建分布式锁根节点[{}]", lockpath);
			}*/
		} catch (IOException e) {// | KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	//匿名内部类实现单例模式
	/** 不使用单例模式
	private static class ZooKeeperClient {
		public static final ZkLock instance = new ZkLock();
	}
	
	public static ZkLock getInstance() {
		return ZooKeeperClient.instance;
	}*/
	
	public boolean isConnected() {
		return zk.getState() == States.CONNECTED;
	}
	
	/** 加锁
	 * @param path
	 */
	public void lock(String path) {
		try {
			//创建临时节点
			String lockPath = zk.create(LOCKPATH + "/" + path, (LOCKPATH + "/" + path).getBytes(), 
					ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
			this.lockPath = lockPath;
			log.info("创建临时节点[{}]", lockPath);
			
			CountDownLatch countDownLatchDelete = new CountDownLatch(1);
			
			List<String> children = zk.getChildren(LOCKPATH, null);//获取目前所有的临时节点
			if (children==null || children.size() == 0) return;
			Object[] sortChildren = children.toArray();
			Arrays.sort(sortChildren);//排序
			
			int lockPathIndex = -1;//当前临时节点下标
			String lockPrePath = "";//比当前临时节点小一个的path
			
			if (sortChildren != null) {
				for (int i = 0; i < sortChildren.length; i ++) {
					if (lockPath.equals(LOCKPATH + "/" +(String)sortChildren[i])) {
						lockPathIndex = i;
						break;
					}
				}
			}
			
			//是最小节点则获取到锁
			if (lockPathIndex == 0)  {
				log.info("Locked,节点[{}]获取到锁", lockPath);
				return;
			}
			
			//获取比当前节点小一个的节点并监听
			lockPrePath = (String)sortChildren[lockPathIndex-1];
			log.info("节点[{}]没有获取到锁,监听节点[{}]", lockPath, LOCKPATH + "/" + lockPrePath);
			long beginTime = System.currentTimeMillis();
			//监听(每次都创建一个watcher)
			zk.exists(LOCKPATH + "/" + lockPrePath, new Watcher() {
				@Override
				public void process(WatchedEvent event) {
					log.info("Watcher:{}", event);
					if (event.getType() == EventType.NodeDeleted) {
						countDownLatchDelete.countDown();
					}
				}
			});
			
			countDownLatchDelete.await();//阻塞当前线程
			log.info("等待{}ms后, 节点[{}]获取到锁", System.currentTimeMillis()-beginTime, lockPath);
			return;
			
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 释放锁
	 */
	public void unlock() {
		try {
			int version = zk.exists(lockPath, null).getVersion();
			zk.delete(lockPath, version);
			close();
		} catch (KeeperException | InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 关闭连接
	 */
	public void close() {
		if (zk != null) {
			try {
				zk.close();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	class DeleteWatcher implements Watcher {
		private ZooKeeper zk;
		
		DeleteWatcher(ZooKeeper zk) {
			this.zk = zk;
		}
		@Override
		public void process(WatchedEvent event) {
			log.info("Watcher:{}", event);
			if (event.getType() == EventType.NodeDeleted && event.getPath().equals("")) {
				
			}
		}
	}
	
	
	public static void main(String[] args) {
		ExecutorService pool = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10; i++) {
			pool.execute(new Runnable() {
				
				@Override
				public void run() {
					ZkLock zkLock = new ZkLock(null);
					System.out.println(zkLock.isConnected());
					zkLock.lock("test");
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					zkLock.unlock();
				}
			});
		}
	}
}
