package com.test.concurrent;

import java.lang.reflect.Field;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import sun.misc.Unsafe;

/**
 *  使用Unsafe的CAS操作时间线程安全
 * @author XieYu
 *
 */
@SuppressWarnings("restriction")
public class CASDemo {
	private int num;
	public void add() {
		this.num = this.num + 1;
	}
	
	public static void main(String[] args) {
		CASDemo casDemo = new CASDemo();
		CASLock lock = new CASLock();
		ExecutorService executorService = Executors.newFixedThreadPool(10);
		
		System.out.println(">>>Begin");
		for(int i = 1; i <= 1000; i++) {
			executorService.execute(new Runnable() {
				
				@Override
				public void run() {
					lock.lock();
					casDemo.add();
					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					lock.unLock();
				}
			});
		}
		executorService.shutdown();
		
		while(!executorService.isTerminated()) {
			//System.out.println("....");
		}
		System.out.println(casDemo.num);
	
	}
}


@SuppressWarnings("restriction")
class CASLock {
	public volatile int state;//当前状态

	public CASLock() {
	}

	private static long stateOffset;
	private static Unsafe unsafe;

	static {
		try {
			Field f = Unsafe.class.getDeclaredField("theUnsafe");
			f.setAccessible(true);
			try {
				unsafe = (Unsafe) f.get(null);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			stateOffset = unsafe.objectFieldOffset(CASLock.class.getDeclaredField("state"));
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
	}

	/** 加锁操作
	 * @return
	 */
	public boolean lock() {
		while (!unsafe.compareAndSwapInt(this, stateOffset, 0, 1)) {
			System.out.println(Thread.currentThread() + " try lock");
		}
		return true;
	}

	/** 释放锁
	 * @return
	 */
	public boolean unLock() {
		while (!unsafe.compareAndSwapInt(this, stateOffset, 1, 0)) {
			System.out.println(Thread.currentThread() + " try unLock================>");
		}
		return true;
	}
}