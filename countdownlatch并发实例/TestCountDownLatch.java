package com.aofex.guess;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @ClassName TestCountDownLatch
 * @Description: TODO
 * @Author tingyu
 * @Date 2020/8/22
 * @Version V1.0
 **/
public class TestCountDownLatch {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        long start = System.currentTimeMillis();
        CountDownLatch begin = new CountDownLatch(1);
        CountDownLatch end = new CountDownLatch(800);
        ThreadFactory threadFactory = new NameTreadFactory();
        RejectedExecutionHandler handler = new MyIgnorePolicy();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(100, 200
                , 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(2000), threadFactory,handler);
        List<Future<Integer>> list = new ArrayList<>(); // 记录跑步情况
        for (int i = 0 ; i< 800; i++) {
            list.add(threadPoolExecutor.submit(new Runner(begin, end)));
        }
        begin.countDown();// 枪声响起
        // 等待任务执行
//        end.await();
        // 统计、
        int count  = 0 ;
        int max = 0;
        for(Future<Integer> f: list) {
            count += f.get();
            if (f.get() > max) {
                max = count;
            }
        }
        System.out.println("最高分= "+max +"总分=" + count + "平均分= " + count/8 + "总消耗时间 = " + (System.currentTimeMillis() - start));

    }

    static class NameTreadFactory implements ThreadFactory {

        private final AtomicInteger mThreadNum = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r, "my-thread-" + mThreadNum.getAndIncrement());
            System.out.println(t.getName() + " has been created");
            return t;
        }
    }

    public static class MyIgnorePolicy implements RejectedExecutionHandler {

        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            doLog(r, e);
        }

        private void doLog(Runnable r, ThreadPoolExecutor e) {
            // 可做日志记录等
            System.err.println( r.toString() + " rejected");
//          System.out.println("completedTaskCount: " + e.getCompletedTaskCount());
        }
    }
}
