package com.dyzwj;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CustomThreadLocal2 {
//    static ThreadLocal<String> threadLocal = new ThreadLocal<>();
//    static ThreadLocal<String> threadLocal = new InheritableThreadLocal<>();
//    static ExecutorService executorService = Executors.newFixedThreadPool(2);
//    static ExecutorService executorService = Executors.newFixedThreadPool(100);
    static TransmittableThreadLocal<String> threadLocal = new TransmittableThreadLocal<>();
    static ExecutorService executorService =  TtlExecutors.getTtlExecutorService(Executors.newFixedThreadPool(2));

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            int j = i;
            executorService.execute(new Runnable() {
                @Override
                public void run() {
                    CustomThreadLocal2.threadLocal.set("猿天地"+j);
                    new Service1().call();
                }
            });
        }
    }
}
class Service1 {
    public void call() {
        //需要排队，等执行到这个任务的时候 两个线程绑定的值已经确定了
        CustomThreadLocal2.executorService.execute(new Runnable() {
            @Override
            public void run() {
                //只会打印两个数字 （因为线程池只有两个线程）
                new Dao1().call();
            }
        });

//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                new Dao1().call();
//            }
//        }).start();
    }
}
class Dao1 {
    public void call() {
        System.out.println("Dao:" + CustomThreadLocal2.threadLocal.get());
    }
}
