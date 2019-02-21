package com.test.concurrent;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

/**
 * 	信号量, 控制当前活动的线程数
 * @author XieYu
 *
 */
public class SemaphoreDemo {
	
	public static void main(String[] args) {
		Semaphore semaphore = new Semaphore(1);
		ExecutorService executorService = Executors.newFixedThreadPool(5);
		for (int i = 0; i < 2; i++) {
			executorService.execute(new Runnable() {
				@Override
				public void run() {
					try {
						semaphore.acquire();//获取许可
						System.out.println("\n" + Thread.currentThread() + "begin");
						Thread.sleep(5000);
						semaphore.release();//释放许可
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
		executorService.shutdown();
		System.out.println("over");
	}

}
