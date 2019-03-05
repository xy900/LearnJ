package com.test.springTest;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.junit.Test;

public class SpringTest_A extends SpringTestBase{
	
	/**
	 * 线程池的拒接策略
	 */
	@Test
	public void ThreadPoolReject() {
		System.out.println("\n>>>ThreadPoolReject Begin");
		CountDownLatch countDownLatch = new CountDownLatch(1);
		
		int corePoolSize = 5;//核心线程数, 一直存活;
		int maximumPoolSize = 10;//线程池维护的最大线程数量; 队列满了之后,会启动非核心线程, 最大不超过maximumPoolSize;
		int queueSize = 5;//阻塞队列大小,用来存储准备处理的任务;核心线程都在处理任务时, 任务将存储到队列中;
		int taskSize = 16;//前5个任务将会被核心线程处理,接下来5个线程将存放在队列中,再接下来5个线程将启动非核心线程处理,最后一个线程将会被拒绝(取决于具体策略);
		
		//使用有界队列,不然线线程池只会存有核心线程,多于的任务都会存到队列中
		ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, 0, 
				TimeUnit.SECONDS, new ArrayBlockingQueue<>(queueSize), new ThreadPoolExecutor.CallerRunsPolicy());
		//四种拒绝策略
		//AbortPolicy(抛异常), 
		//DiscardPolicy(什么都不做),
		//DiscardOldestPolicy(移除队列的队首元素,然后再加入到线程池), 
		//CallerRunsPolicy(直接由提交任务的线程去执行任务的run方法,如在此方法中是执行本方法(ThreadPoolReject)的线程去执行);
		
		show("Begin", executor);
		
		for (int i = 0; i < taskSize; i++) {
			int k = i;
			executor.execute(new Runnable() {
				private int msg = k;
				@Override
				public void run() {
					show("Current Thread(" + msg + ")", executor);
					try {
						Thread.sleep(60*1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					show("over(" + msg + ")", executor);
				}
			});
		}
		//executor.shutdown();
		
		try {
			countDownLatch.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	public static void show(String msg, ThreadPoolExecutor executor) {
		System.out.println("\n>>>" + msg + ":" + Thread.currentThread()
			+ "-" + executor.getQueue().size()  //队列的大小
			+ "-" + executor.getPoolSize()        //当前线程池大小
			+ "-" + executor.getCompletedTaskCount() //已经完成的任务数量
			+ "-" + executor.getLargestPoolSize());  //线程池中曾经最大的线程数量
	}
}
