package com.test.concurrent;

import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/** 阻塞队列LinkedBlockingQueue模拟生产者消费者模型;
 *  生产者消费者任务由线程池去执行;
 *  监听线程的状态;
 * @author xieyu
 *
 */
public class LinkedBlockingQueueDemo {
	//线程池
	public static ThreadPoolExecutor pool = new ThreadPoolExecutor(5, 100, 60L, 
			TimeUnit.SECONDS, new SynchronousQueue<Runnable>());

	/** 阻塞队列
	 *  take取数据时, 如果队列为空, 当前线程则处于等待状态;
	 *  put添加数据时, 如果队列满了, 当前线程则处于等待状态;
	 */
	public static LinkedBlockingQueue<String> linkedBlockingQueue = new LinkedBlockingQueue<>(10);
	//生产者数量
	private final static int PRODUCER_NUM = 15;
	//消费者数量
	private final static int CUSTOMER_NUM = 14;//消费者数量多于生产者时, 剩下的消费者由于获取不到阻塞队列中的元素, 会处于等待状态
	
	private static AtomicBoolean flag = new AtomicBoolean(true);
	
	public static void main(String[] args) throws InterruptedException {
		System.out.println("=========Begin=========");
		scanThread();
		InitThreadListener();

		//将消费者加入线程池
		for (int i = 0; i < CUSTOMER_NUM; i++) {
			pool.execute(new Customer("customer " + i));
		}
		//将生产者者加入线程池
		for (int i = 0; i < PRODUCER_NUM; i++) {
			pool.execute(new Producer("producer " + i));
		}
		//关闭线程池
		pool.shutdown();
		while(!pool.isTerminated()) {
			//System.out.println("线程池中任务没有执行完,继续执行...");
		}
		
		flag.set(false);
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		System.out.println("=============任务执行完毕=============");
		scanThread();//查看执行完毕后的线程状态
	}

	/**
	 * 监听线程状态
	 */
	public static void InitThreadListener() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				while (flag.get()) {
					scanThread();
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println("=====监听线程状态结束,最后查看一次线程状态=====");
				scanThread();//查看执行完毕后的线程状态
			}
		}, "ThreadScan").start();
	}

	private static void scanThread() {
		System.out.println("CorePoolSize: " + pool.getCorePoolSize() + ", PoolSize:" + pool.getPoolSize() + 
				", TaskCount:" + pool.getTaskCount() + ", LinkedBlockingQueue size:" + linkedBlockingQueue.size());
		//没有调用start的线程
		scanThreadState("NEW", new Thread.State[]{Thread.State.NEW});
		
		//可运行状态: 在JVM中正在执行的线程,可能该线程正在等待其他资源
		scanThreadState("RUNNABLE", new Thread.State[]{Thread.State.RUNNABLE});
		
		//阻塞状态: 被synchronized等锁阻塞
		//另一个线程执行完, 或者调用了wait()方法释放了锁, 才会重新去获取锁
		scanThreadState("BLOCKED", new Thread.State[]{Thread.State.BLOCKED});
		
		//等待状态: 调用wait(),join()等方法,将等待notify(),notifyAll()的执行
		scanThreadState("WAITING", new Thread.State[]{Thread.State.WAITING});
		
		//有时间的等待状态: wait(long), Object.sleep()
		scanThreadState("TIMED_WAITING", new Thread.State[]{Thread.State.TIMED_WAITING});
		
		//线程执行完了或者因异常退出了run()方法，该线程结束生命周期
		scanThreadState("TERMINATED", new Thread.State[]{Thread.State.TERMINATED});
		
		//死锁的线程
		scanThreadDead();
	}
	
	private static void scanThreadState(String name, Thread.State[] states) {
		int stateCount = 0;
		StringBuilder sb = new StringBuilder(" :");
		Map<Thread, StackTraceElement[]> allThread = Thread.getAllStackTraces();//全部线程的堆栈
		for (Thread thread : allThread.keySet()) {//遍历全部线程
			for (Thread.State state : states) {
				stateCount = (thread.getState() == state) ? stateCount + 1 : stateCount;
				if (thread.getState() == state) {
					sb.append(thread + ", ");
				}
			}
		}
		System.out.println("线程" + name + " = " + stateCount + sb.substring(0, sb.length() - 2));
	}

	private static void scanThreadDead() {
		//处理死锁的线程
		long[] deadThreads = ManagementFactory.getThreadMXBean().findDeadlockedThreads();
		if (deadThreads == null) {
			System.out.println("线程没有发现死锁");
			return;
		} 
		for (Thread thread : Thread.getAllStackTraces().keySet()) {
			for (long id : deadThreads) {
				if (thread.getId() == id)
					System.out.println("死锁:" + thread);
			}
		}
	}
}

/**
 * 生产者
 * @author xieyu
 *
 */
class Producer implements Runnable{

	private String name;
	
	public Producer(String name) {
		this.name = name;
	}
	
	@Override
	public void run() {
		try {
			//从阻塞队列中添加元素
			LinkedBlockingQueueDemo.linkedBlockingQueue.put(this.name);
			System.out.println("After Producer: " + this.name);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}

/**
 * 消费者
 * @author xieyu
 *
 */
class Customer implements Runnable{

	private String name;
	
	public Customer(String name) {
		this.name = name;
	}
	
	@Override
	public void run() {
		try {
			//从阻塞队列中获取元素
			String producerName = LinkedBlockingQueueDemo.linkedBlockingQueue.take();
			System.out.println("After Customer: " + this.name + " use " + producerName);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
