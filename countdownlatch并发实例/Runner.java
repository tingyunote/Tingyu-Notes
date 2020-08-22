package com.aofex.guess;

import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName Runner
 * @Description: TODO
 * @Author tingyu
 * @Date 2020/8/22
 * @Version V1.0
 **/
public class Runner implements Callable<Integer> {
    private CountDownLatch begin;
    private CountDownLatch end;

    public Runner(CountDownLatch begin, CountDownLatch end) {
        super();
        this.begin = begin;
        this.end = end;
    }

    @Override
    public Integer call() throws Exception {

        int score = new Random().nextInt(1000);
        begin.await(); // 裁判
        // 跑步
        System.out.println(Thread.currentThread().getName());
        Thread.sleep(score);
//        TimeUnit.MICROSECONDS.sleep(score); // 运动员跑步的时间
        end.countDown();
        return score;
    }
}
