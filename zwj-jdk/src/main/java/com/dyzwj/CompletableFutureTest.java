package com.dyzwj;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CompletableFutureTest {

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        CompletableFuture cf = CompletableFuture.supplyAsync(() -> {
            try {
                //  Thread.sleep(2000);
            } catch (Exception e) {
                e.printStackTrace();
            }
            System.out.println("supplyAsync " + Thread.currentThread().getName());
            return "hello";
        }, executorService).thenApplyAsync(s -> {
            System.out.println(s + "world");
            return "hhh";
        }, executorService);
        cf.thenRunAsync(() -> {
            System.out.println("ddddd");
        });
        cf.thenRun(() -> {
            System.out.println("ddddsd");
        });
        cf.thenRun(() -> {
            System.out.println(Thread.currentThread());
            System.out.println("dddaewdd");
        });

    }


}
