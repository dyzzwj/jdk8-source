package com.dyzwj;

public class CustomThreadLocal1 {



//    static ThreadLocal<String> threadLocal = new ThreadLocal<>();
    static ThreadLocal<String> threadLocal = new InheritableThreadLocal<>();

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                CustomThreadLocal1.threadLocal.set("zwj");
                new Service().call();

            }
        }).start();

    }

}
class Service {
    public void call() {
        System.out.println("Service:" + Thread.currentThread().getName());
        System.out.println("Service:" + CustomThreadLocal1.threadLocal.get());
//        new Dao().call();
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Dao().call();
            }
        }).start();
    }
}
class Dao {
    public void call() {
        System.out.println("==========================");
        System.out.println("Dao:" + Thread.currentThread().getName());
        System.out.println("Dao:" + CustomThreadLocal1.threadLocal.get());
    }
}
