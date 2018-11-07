package com.test.concurrent;

import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;

/**
 * <p>多个线程处于屏障处等待, 直到全部到达之后才可以继续执行, 并且可以重复使用
 * 
 * @author xieyu
 *
 */
public class CyclicBarrierDemo {
	
	public static void main(String[] args) {
		int size = 4;
		CyclicBarrier cyclicBarrier = new CyclicBarrier(size, //屏障拦截的线程数量
				//CyclicBarrier构造器可以传入一个Runnable任务(可选), 最先到达屏障的线程去执行该任务
				new Runnable() {
					@Override
					public void run() {
						System.out.println("###All end, current " + Thread.currentThread().getId());
					}
				}
			);
		//新建size*2 - 1个线程
		for (int i = 0; i < size*2-1; i++) {
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					System.out.println("Thread " + Thread.currentThread().getId() + " ,waiting number " + 
							cyclicBarrier.getNumberWaiting());
					try {
						Thread.sleep(500);
						//调用await等待, 当拦截的线程数为parties时, 先执行CyclicBarrier定义的任务, 然后唤醒所有等待的线程
						cyclicBarrier.await();
					} catch (InterruptedException e) {
						e.printStackTrace();
					} catch (BrokenBarrierException e) {
						e.printStackTrace();
					}
					System.out.println("end " + Thread.currentThread().getId() + " ,waiting number " + 
								cyclicBarrier.getNumberWaiting());
				}
			}).start();
		}
		
		//最后调用一次await, CyclicBarrier可以再次使用
		try {
			Thread.sleep(5000);
			System.out.println("last thread");
			cyclicBarrier.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (BrokenBarrierException e) {
			e.printStackTrace();
		}
		
	}
}
