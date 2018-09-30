package com.test.concurrent;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 模拟并发,多个线程调用同一个对象的wait方法,使得线程进入等待状态,然后调用notifyAll唤醒所有线程(进去就绪状态).
 * @author xieyu
 *
 */
public class Parallellimit {
	static File file = new File("Parallellimit.txt");
	static FileOutputStream out = null;
	static FileWriter writer = null;
	static {
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			out = new FileOutputStream(file);
			writer = new FileWriter(file);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		
		ExecutorService pool = Executors.newCachedThreadPool();
		Counts count = new Counts(); //共享操作该计数变量,不能使用int或者integer，Java无法对非对象、和包装类进行加锁wait
		count.num = 0;
		for(int i=0;i<10000;i++){ //启动线程
			MyRunnable runnable = new MyRunnable(count);
			pool.execute(runnable);
		}
		pool.shutdown(); //关闭线程池,无法加入新线程任务,但不影响现有线程
	}
}

class MyRunnable implements Runnable {

	private Counts count;
	/**
	 * 通过构造方法传入初值，避免set和get时线程的不安全性
	 */
	public MyRunnable(Counts count) {
		this.count = count;
	}

	public void run() {
		try {
			/**
			 * 加锁,保证线程可见性和安全性
			 */
			synchronized (count) {
				count.num++;
				if (count.num < 10000) {
					System.out.println(count.num);
					try {
						Parallellimit.writer.write(count.num+"\n");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					count.wait();// 一定要调用count对象的wait,默认对this,无法持有线程的锁，抛出异常
				}
				/**
				 * 达到10000时唤醒所有线程
				 */
				if (count.num == 10000) {
					count.notifyAll();
				}
				System.out.println("并发量 count=" + count.num + ":" +System.currentTimeMillis());
				try {
					Parallellimit.writer.write("并发量 count=" + count.num+ ":" +System.currentTimeMillis()+"\n");
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}


class Counts {
	public int num;
}