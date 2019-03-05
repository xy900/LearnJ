package com.test.concurrent;

import java.util.concurrent.Exchanger;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 *  Exchanger交换数据
 * @author XieYu
 *
 */
public class ExchangerDemo {
	
	public static void main(String[] args) {
		Exchanger<String> exchanger = new Exchanger<>();
		
		ExecutorService executor = Executors.newFixedThreadPool(5);
		executor.execute(new Runnable() {
			
			@Override
			public void run() {
				try {
					System.out.println("1>>" + Thread.currentThread());
					System.out.println("1.>>" + exchanger.exchange(Thread.currentThread().toString()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		
		executor.execute(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(5000);
					System.out.println("2>>" + Thread.currentThread());
					System.out.println("2.>>" + exchanger.exchange(Thread.currentThread().toString()));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		});
		executor.shutdown();
	}
}
