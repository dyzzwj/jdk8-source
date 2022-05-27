package future;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.*;

public class Test {


    public static void main(String[] args) throws Exception {
//        test1();
        test2();

    }




    public static void test3(){


    }

    /**
     *  CompletableFuture也可以通过回调获取结果 无需像get()方法阻塞主线程
     * @throws Exception
     */
    public static void test2() throws Exception{
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("Future start at:" + dateTimeFormatter.format(LocalDateTime.now()));
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(() -> {
            Thread.sleep(3000);
            return "Future finish;";
        });
        System.out.println(future.get() + " at:" + dateTimeFormatter.format(LocalDateTime.now()));

        System.out.println("CompletableFuture start at:" + dateTimeFormatter.format(LocalDateTime.now()));

        CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "CompletableFuture finish;";
        }).whenComplete((r,t) -> {
            System.out.println( r + "at :" + dateTimeFormatter.format(LocalDateTime.now()));
        });


        System.out.println("主线程会被阻塞，时间：{}" + dateTimeFormatter.format(LocalDateTime.now()));
    }

    /**
     * Future在执行结束后没法回调，调用get()方法会被阻塞；
     * CompletableFuture调用get()方法获取结果也会被阻塞
     */
    public static void test1() throws Exception{
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        System.out.println("Future start at:" + dateTimeFormatter.format(LocalDateTime.now()));
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Future<String> future = executorService.submit(() -> {
            Thread.sleep(3000);
            return "Future finish;";
        });
        System.out.println(future.get() + " at:" + dateTimeFormatter.format(LocalDateTime.now()));

        System.out.println("CompletableFuture start at:" + dateTimeFormatter.format(LocalDateTime.now()));

        CompletableFuture<String> completableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return "CompletableFuture finish;";
        });

        System.out.println(completableFuture.get() + "at :" + dateTimeFormatter.format(LocalDateTime.now()));

        System.out.println("主线程会被阻塞，时间：{}" + dateTimeFormatter.format(LocalDateTime.now()));
    }
}
