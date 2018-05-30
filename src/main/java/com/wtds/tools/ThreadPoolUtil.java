package com.wtds.tools;

import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolUtil {

	/**
	 * ThreadPoolExecutor 参数说明：<br>
	 * 1.corePoolSize·············核心线程池大小 <br>
	 * 2.maximumPoolSize··········最大线程池大小 <br>
	 * 3.keepAliveTime············线程池中超过corePoolSize数目的空闲线程最大存活时间；可以allowCoreThreadTimeOut(true)使得核心线程有效时间<br>
	 * 4.TimeUnit·················keepAliveTime时间单位<br>
	 * 5.workQueue················阻塞任务队列<br>
	 * 6.threadFactory············新建线程工厂<br>
	 * 7.RejectedExecutionHandler·当提交任务数超过maxmumPoolSize+workQueue之和时，任务会交给RejectedExecutionHandler来处理<br>
	 *
	 * 重点讲解：<br>
	 * 其中比较容易让人误解的是：corePoolSize，maximumPoolSize，workQueue之间关系。<br>
	 * 1.当线程池小于corePoolSize时，新提交任务将创建一个新线程执行任务，即使此时线程池中存在空闲线程。<br>
	 * 2.当线程池达到corePoolSize时，新提交任务将被放入workQueue中，等待线程池中任务调度执行<br>
	 * 3.当workQueue已满，且maximumPoolSize>corePoolSize时，新提交任务会创建新线程执行任务<br>
	 * 4.当提交任务数超过maximumPoolSize时，新提交任务由RejectedExecutionHandler处理<br>
	 * 5.当线程池中超过corePoolSize线程，空闲时间达到keepAliveTime时，关闭空闲线程<br>
	 * 6.当设置allowCoreThreadTimeOut(true)时，线程池中corePoolSize线程空闲时间达到keepAliveTime也将关闭<br>
	 */

	public static void main(String[] args) {

		// creating the ThreadPoolExecutor
		ThreadPoolExecutor executorPool = new ThreadPoolExecutor(4, 2, 0L, TimeUnit.SECONDS,
				new LinkedBlockingQueue<Runnable>(), Executors.defaultThreadFactory());
		for (;;) {
			executorPool.execute(new Runnable() {
				public void run() {
					System.out.println(System.currentTimeMillis());

				}
			});
		}
	}
	
	/**
	 * 获取阻塞线程池
	 * @param min 最小线程数
	 * @param max 最大线程数
	 * @param time 线程闲置销毁时间(秒)
	 * @return
	 */
	public static ThreadPoolExecutor newThreadPoolExecutor(int min,int max,long time) {
		return new ThreadPoolExecutor(min, max, time, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(),
				Executors.defaultThreadFactory());
	}

}
