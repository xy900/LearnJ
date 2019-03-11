package com.test;

import java.io.File;
import java.lang.management.ClassLoadingMXBean;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.RuntimeMXBean;
import java.lang.management.ThreadMXBean;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;
import com.sun.management.OperatingSystemMXBean;
import com.test.concurrent.DeadLock;

@SuppressWarnings("restriction")
public class SystemParam {
	
	public static void main(String[] args) {
		//增加死锁线程
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Method method = DeadLock.class.getDeclaredMethod("main", String[].class);
					method.setAccessible(true);
					method.invoke(null, (Object)new String[] {});
				} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
					e.printStackTrace();
				}
			}
		}).start();
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		IPInfo();
		scanOperate();
		scanJvmMemory();
		scanThread();
		scanDisk();
	}
	
	/**
	 * 获取本机IP
	 */
	public static void IPInfo() {
		try {
			System.out.println("\n>>>IP Address");
			InetAddress address = InetAddress.getLocalHost();
			System.out.println("hostName:" + address.getHostName());//主机名
		    System.out.println("主机别名:" + address.getCanonicalHostName());//主机别名
		    System.out.println("IP:" + address.getHostAddress());//获取IP地址
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 内存信息
	 */
	public static void scanOperate() {
		System.out.println("\n>>>Operate");
		OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
		long freeMemory = os.getFreePhysicalMemorySize() / 1024L / 1024L;
		System.out.println("操作系统空闲内存大小" + freeMemory + "MB");
	}
	
	/**
	 * jvm信息
	 */
	public static void scanJvmMemory() {
		System.out.println("\n>>>jvm");
		long freeMemory = Runtime.getRuntime().freeMemory() / 1024L / 1024L;
		long totalMemory = Runtime.getRuntime().totalMemory() / 1024L / 1024L;
		long maxMemory = Runtime.getRuntime().maxMemory() / 1024L / 1024L;
		System.out.println("JVM空闲内存大小" + freeMemory + "MB/" + totalMemory + "MB");
		System.out.println("JVM最大空间" + maxMemory + "MB");
		
		RuntimeMXBean runtime = ManagementFactory.getRuntimeMXBean();
        OperatingSystemMXBean os = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        ThreadMXBean threads = ManagementFactory.getThreadMXBean();//获取Thread信息
        MemoryMXBean mem = ManagementFactory.getMemoryMXBean();//获取内存信息
        ClassLoadingMXBean cl = ManagementFactory.getClassLoadingMXBean();
        System.out.printf("jvmName: %s %s %s%n", runtime.getVmName(), "version", runtime.getVmVersion());
        System.out.printf("jvmJavaVer: %s%n", System.getProperty("java.version"));
        System.out.printf("jvmVendor: %s%n", runtime.getVmVendor());
        System.out.printf("jvmUptime: %s%n", toDuration(runtime.getUptime()));
        System.out.printf("threadsLive: %d%n", threads.getThreadCount());
        System.out.printf("threadsDaemon: %d%n", threads.getDaemonThreadCount());
        System.out.printf("threadsPeak: %d%n", threads.getPeakThreadCount());
        System.out.printf("threadsTotal: %d%n", threads.getTotalStartedThreadCount());
        System.out.printf("heapCurr: %dMB%n", mem.getHeapMemoryUsage().getUsed() / 1024L / 1024L);
        System.out.printf("heapMax: %dMB%n", mem.getHeapMemoryUsage().getMax() / 1024L / 1024L);
        System.out.printf("heapCommitted: %dMB%n", mem.getHeapMemoryUsage().getCommitted() / 1024L / 1024L);
        System.out.printf("osName: %s %s %s%n", os.getName(), "version", os.getVersion());
        System.out.printf("osArch: %s%n", os.getArch());
        System.out.printf("osCores: %s%n", os.getAvailableProcessors());
        System.out.printf("clsCurrLoaded: %s%n", cl.getLoadedClassCount());
        System.out.printf("clsLoaded: %s%n", cl.getTotalLoadedClassCount());
        System.out.printf("clsUnloaded: %s%n", cl.getUnloadedClassCount());
	}
	
	/**
	 * 获取线程信息
	 */
	public static void scanThread() {
		System.out.println("\n>>>Thread");
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
	
	/** 线程信息
	 * @param name
	 * @param states
	 */
	public static void scanThreadState(String name, Thread.State[] states) {
		int stateCount = 0;
		StringBuilder sb = new StringBuilder(" :");
		Map<Thread, StackTraceElement[]> allThread = Thread.getAllStackTraces();//全部线程的堆栈
		for (Thread thread : allThread.keySet()) {//遍历全部线程
			for (Thread.State state : states) {
				stateCount = (thread.getState() == state) ? stateCount + 1 : stateCount;
				if (thread.getState() == state) {
					sb.append(thread +  "(ID:" + thread.getId() + ")" + ", ");
				}
			}
		}
		System.out.println("线程" + name + " = " + stateCount + sb.substring(0, sb.length() - 2));
	}

	/**
	 * 死锁的线程
	 */
	public static void scanThreadDead() {
		long[] deadThreads = ManagementFactory.getThreadMXBean().findDeadlockedThreads();
		if (deadThreads == null) {
			System.out.println("线程没有发现死锁");
			return;
		}
		for (Thread thread : Thread.getAllStackTraces().keySet())
			for (long id : deadThreads)
				if (thread.getId() == id)
					System.out.println("死锁:" + thread);
	}
	
	/**
	 * 磁盘信息
	 */
	public static void scanDisk() {
		System.out.println("\n>>>Disk");
		int diskFreeSize = 0;
		File[] roots = File.listRoots();
		for (File file : roots) {
			System.out.println(file.getName() + "--" + file.getPath());
			diskFreeSize = (int) (diskFreeSize + file.getFreeSpace() / 1024L / 1024L / 1024L);
		}
		System.out.println("磁盘剩余" + diskFreeSize + "GB!");
	}
	
	private static NumberFormat fmtI = new DecimalFormat("###,###", new DecimalFormatSymbols(Locale.ENGLISH));
    private static NumberFormat fmtD = new DecimalFormat("###,##0.000", new DecimalFormatSymbols(Locale.ENGLISH));
	protected static String toDuration(double uptime) {
        uptime /= 1000;
        if (uptime < 60) {
            return fmtD.format(uptime) + " seconds";
        }
        uptime /= 60;
        if (uptime < 60) {
            long minutes = (long) uptime;
            String s = fmtI.format(minutes) + (minutes > 1 ? " minutes" : " minute");
            return s;
        }
        uptime /= 60;
        if (uptime < 24) {
            long hours = (long) uptime;
            long minutes = (long) ((uptime - hours) * 60);
            String s = fmtI.format(hours) + (hours > 1 ? " hours" : " hour");
            if (minutes != 0) {
                s += " " + fmtI.format(minutes) + (minutes > 1 ? " minutes" : " minute");
            }
            return s;
        }
        uptime /= 24;
        long days = (long) uptime;
        long hours = (long) ((uptime - days) * 24);
        String s = fmtI.format(days) + (days > 1 ? " days" : " day");
        if (hours != 0) {
            s += " " + fmtI.format(hours) + (hours > 1 ? " hours" : " hour");
        }
        return s;
    }
}
