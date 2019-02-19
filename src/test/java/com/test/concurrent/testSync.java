package com.test.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/** 测试StringBuffer和StringBuilder是否线程安全;
 * 	使用synchronized关键字和ReentrantLock可重入锁实现线程安全的类;
 * 
 * StringBuffer 线程安全  
 * StringBuilder 线程非安全
 * AtomicInteger 原子操作类 线程安全
 * @author XieYu
 * 
 */
public class testSync {
	public static void main(String[] argaa) {
		int threadCount = 200;//线程个数
		CyclicBarrier cyclicBarrier = new CyclicBarrier(threadCount);//设置屏障,全部线程到达之后再执行
		
		MyString my = new MyString();
		StringBuilder sbBuilder = new StringBuilder();
		StringBuffer stringBuffer = new StringBuffer();
		AtomicInteger atomicInteger = new AtomicInteger(0);//原子操作类
		
		//创建线程池
		ExecutorService pool = Executors.newCachedThreadPool();
		//创建多个线程去处理MyString, StringBuilder, StringBuffer三个对象
		for (int i = 0; i < threadCount; i++) {
			pool.execute(new Runnable() {
				@Override
				public void run() {
					try {
						System.out.println("等待...");
						cyclicBarrier.await();//阻塞线程
					} catch (InterruptedException e) {
						System.out.println("InterruptedException!");
						e.printStackTrace();
					} catch (BrokenBarrierException e) {
						System.out.println("BrokenBarrierException!");
						e.printStackTrace();
					}
					
					System.out.println("开始执行...");
					for (int j = 0; j < 1000; j++) {
						my.append(1);
						sbBuilder.append("1");
						stringBuffer.append("1");
						atomicInteger.incrementAndGet();
						/*System.out.println("time:" +System.currentTimeMillis() + ", currentThread:" + Thread.currentThread()
							+ ", MyString:" + my.getNum() + ", StringBuilder:" + sbBuilder.length()
							+ ", StringBuffer:" + stringBuffer.length());*/
					}
					System.out.println("time:" +System.currentTimeMillis() + ", currentThread:" + Thread.currentThread()
						+ "\n         , MyString:" + my.getNum() + ", StringBuilder:" + sbBuilder.length()
						+ ", StringBuffer:" + stringBuffer.length()
						+ ", AtomicInteger:" + atomicInteger);
					
				}
			});
		}
		pool.shutdown();
	}
}


/** 创建测试类
 *  使用不同的方法保证线程安全
 * @author XieYu
 * 
 */
class MyString {
	final ReentrantLock lock = new ReentrantLock(); //使用可重入锁保证线程安全
	
	private volatile Integer num = 0; //volatile关键字   不能保证线程安全(只能保证可见性和禁止指令重排序, 但不能保证原子性)

	public Integer getNum() {
		return num;
	}

	public void setNum(Integer num) {
		this.num = num;
	}
	
	public void append(Integer num) {       //1.synchronized(作用在方法上或者代码块中均可)关键字保证线程安全，每个只有一个线程可以进入此方法
		//this.num = this.num + num;
		
		/*synchronized (MyString.class) {   //2.synchronized作用在类上，统一保存线程安全
			this.num = this.num + num;
		}*/
		
		lock.lock();                         //3.使用可重入锁保证线程安全
		try {
			this.num = this.num + num;
		} finally {
			lock.unlock();
		}
	}
}