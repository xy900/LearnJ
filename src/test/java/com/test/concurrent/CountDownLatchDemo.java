package com.test.concurrent;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * <p>CountDownLatch设置一个初始值,调用await都将阻塞知道count为0,调用countDown()来减小count.
 * <p>类似于计数器, 且只能被触发一次
 * <p><b>使用场景</b>
 * <li>一个线程等待n个线程执行完毕之后再执行</li>
 * <li>实现最大并行性:count设为1, 多个线程调用await()进入等待状态, 然后调用countDown()使用多个等待的线程同时被唤醒 </li>
 * @author xieyu
 *
 */
public class CountDownLatchDemo {
	
	public static void main(String[] args) {
		CountDownLatch countDownLatch = new CountDownLatch(5);//初始设定一个计数器
		
		Thread thread = new Thread(new Runnable() {
			public void run() {
				try {
					countDownLatch.await();//await()使当前线程处于等待状态直到count的值减到0, 除非该线程被中断,中断则抛出异常
					System.out.println("等待结束0:" + countDownLatch.getCount());
				} catch (InterruptedException e) {
					System.out.println("error");
					e.printStackTrace();
				}
			}
		});
		thread.start();
		//可以多个线程调用CountDownLatch的await()方法;
		new Thread(new Runnable() {
			public void run() {
				try {
					countDownLatch.await(1, TimeUnit.SECONDS);//await(long, TimeUnit)只等待一段时间,时间到了count不为0也执行
					System.out.println("等待结束1:" + countDownLatch.getCount());
				} catch (InterruptedException e) {
					System.out.println("error");
					e.printStackTrace();
				}
			}
		}).start();
		
		new Thread(new Runnable() {
			public void run() {
				while(true) {
					countDownLatch.countDown();//countDown()减少count的值
					System.out.println("getCount(): " + countDownLatch.getCount());
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					if (countDownLatch.getCount() == 0) {
						System.out.println("===over===");
						return;
					}
				}
			}
		}).start();
	}
}
