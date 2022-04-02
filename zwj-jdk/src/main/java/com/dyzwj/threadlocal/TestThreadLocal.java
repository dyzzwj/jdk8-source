package com.dyzwj.threadlocal;

import com.alibaba.ttl.TransmittableThreadLocal;
import com.alibaba.ttl.threadpool.TtlExecutors;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TestThreadLocal {

    // 实现类使用TTL的实现
    private static final ThreadLocal<Person> THREAD_LOCAL = new TransmittableThreadLocal<>();
    // 线程池使用TTL包装一把
    private static final ExecutorService THREAD_POOL = TtlExecutors.getTtlExecutorService(Executors.newSingleThreadExecutor());




//    private static final ThreadLocal<Person> THREAD_LOCAL = new InheritableThreadLocal<>();

    /**
     * 线程池大小是2，所以第二个任务执行时会继续初始化一条新的线程来执行它（线程池内线程数量若还没达到coreSize大小的话，每次新任务都会启用新的线程来执行的（不管是否有空闲线程与否）），
     * 会触发到init过程，所以它获取到的是最新绑定的数据。
     */
//    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool(2);

    /**
     * 线程池大小是1，所以第二个任务执行时复用的是上个线程（你看线程名称都一样），所以就不会再经历init初始化阶段，所以得到的绑定数据还是旧数据
     * @param args
     * @throws InterruptedException
     */
//    private static final ExecutorService THREAD_POOL = Executors.newSingleThreadExecutor();


    public static void main(String[] args) throws InterruptedException {
        THREAD_LOCAL.set(new Person());

        THREAD_POOL.execute(() -> getAndPrintData());
        TimeUnit.SECONDS.sleep(2);
        Person newPerson = new Person();
        newPerson.setAge(100);
        THREAD_LOCAL.set(newPerson); // 给线程重新绑定值


        THREAD_POOL.execute(() -> getAndPrintData());
        TimeUnit.SECONDS.sleep(2);
    }


    private void setData(Person person) {
        System.out.println("set数据，线程名：" + Thread.currentThread().getName());
        THREAD_LOCAL.set(person);
    }

    private static Person getAndPrintData() {
        Person person = THREAD_LOCAL.get();
        System.out.println("get数据，线程名：" + Thread.currentThread().getName() + "，数据为：" + person);
        return person;
    }

    private static class Person {
        private Integer age = 18;

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        @Override
        public String toString() {
            return "Person{" +
                    "age=" + age +
                    '}';
        }
    }



}
