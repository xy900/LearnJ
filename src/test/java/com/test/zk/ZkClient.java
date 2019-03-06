package com.test.zk;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.KeeperState;

/**
 * ZooKeeper
 * @author XieYu
 */
public class ZkClient {
	
	public static void main(String[] args) {
		try {
			CountDownLatch countDownLatch = new CountDownLatch(1);//计数器,创建ZooKeeper客户端后先阻塞线程,创建完毕进行通知时再唤醒
			long beginTime = System.currentTimeMillis();
			System.out.println("\n>>>Begin create client:" + beginTime);
			
			//创建ZooKeeper客户端
			//创建连接是异步的,启动与服务器的连接,然后马上返回,此时会话处于CONNECTING状态,并通过watcher通知;创建之后,状态变为CONNECTED
			//127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183集群
			ZooKeeper zk = new ZooKeeper("127.0.0.1:2181", 5000, 
					//构造函数可以传入默认的Watcher; 可以为null
					new Watcher() {
						@Override
						public void process(WatchedEvent event) {
								System.out.println("\n>>>Watcher:"+event);
								if (event.getState() == KeeperState.SyncConnected) { // 进行连接
									System.out.println("\n>>>进行连接");
									countDownLatch.countDown();//计数器减一, 唤醒等待的线程
								}
								if (event.getState() == KeeperState.Disconnected) {
									System.out.println("\n>>>断开连接");
								}
						}
					}
			);
			//状态CONNECTING
			System.out.println("\n>>>states:" + zk.getState());
			
			countDownLatch.await();//使线程处于等待状态,创建完毕再唤醒;保证后面的操作在创建完毕后执行
			System.out.println("\n>>>create over:" + (System.currentTimeMillis()-beginTime) + "ms");
			
			//延迟一秒后执行,因为创建连接是异步的;可能没有连接到服务端,sesseionId也可能因为重连接而改变
			//Thread.sleep(1000);
			System.out.println("\n>>>states:" + zk.getState());
			
			//getSessionId获取sessionId
			System.out.println("\n>>>sessionId:0x" + Long.toHexString(zk.getSessionId()));
			//getSessionPasswd获取会话秘密
			System.out.println("\n>>>sessionPasswd:" + new String(zk.getSessionPasswd()));
			
			//创建节点并设置默认的监听器
			if (zk.exists("/zktest", true) == null) {
				System.out.println("\n>>>create: " 
					+ zk.create("/zktest", "zktest".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT));
			}
			//exists节点是否存在, 并返回Stat元数据
			System.out.println("\n>>>exists(/zktest):" + zk.exists("/zktest", false));
			//getData()获取date信息
			System.out.println("\n>>>data(/zktest):" + new String(zk.getData("/zktest", null, null)));
			//不存在则抛异常
			System.out.println("\n>>>data(/):" + new String(zk.getData("/", null, null)));
			
			//create创建一个临时顺序节点: create /zktest/create 测试 world:anyone:cdrwa
			System.out.println("\n>>>create: "
					+ zk.create("/zktest/create", "test".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL));
			
			//获取children
			List<String> children = zk.getChildren("/", true);
			if (children != null) {
				System.out.println("\n>>>getChildren");
				for (String child : children) {
					System.out.println("child:" + child);
				}
			}
		} catch (IOException e) {
			System.out.println("\n>>>IOException!");
			e.printStackTrace();
		} catch (KeeperException e) {
			System.out.println("\n>>>KeeperException!");
			e.printStackTrace();
		} catch (InterruptedException e) {
			System.out.println("\n>>>InterruptedException!");
			e.printStackTrace();
		}
		
		System.out.println("\n>>>Begin Sleeping...");
		try {
			Thread.sleep(60*1000);
		} catch (InterruptedException e) {
			System.out.println("\n>>>InterruptedException!");
			e.printStackTrace();
		}
		System.out.println("\n>>>over");
	}
}
