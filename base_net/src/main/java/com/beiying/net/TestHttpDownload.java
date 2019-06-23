package com.beiying.net;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by beiying on 2019/6/8.
 */

public class TestHttpDownload {
    private static final long count = 100000001;


    public static void main(String args[]) throws InterruptedException {
        /* 1、两个线程、两个实体，无法验证 */
        // TrainTicket2 tt1 = new TrainTicket2();
        // TrainTicket2 tt2 = new TrainTicket2();
        // new Thread(tt1, "窗口①").start();
        // new Thread(tt2, "窗口二").start();
		/* 未发生线程安全问题 */

		/* 2、两个线程、同一个实体、调用相同的方法，无法验证 */
//		 TrainTicket2 tt = new TrainTicket2();
//		 new Thread(tt, "窗口①").start();
//		 new Thread(tt, "窗口二").start();
		/* 出现线程安全问题 :窗口二<true>抢到了第【101】张火车票.why? */

		/* 3、两个线程、同一个实体、调用不同的方法，验证synchronized使用this锁 */
//        TrainTicket2 tt = new TrainTicket2();
//        new Thread(tt, "窗口①").start();
//        Thread.sleep(40);
//        tt.flag = false;
//        new Thread(tt, "窗口二").start();

//        testVolatile();
//        testAutomic();
//        compareWaitAndSleep();
//        testVolatile1();
        product();
        consume();
    }

    private static void testOkHttp() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://ss0.bdstatic.com/7Ls0a8Sm1A5BphGlnYG/sys/portrait/item/91a7360a.jpg")
                .addHeader("Range", "bytes=0-2")
                .build();
        try {
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                Headers headers = response.headers();
                for (int i = 0;i < headers.size();i++) {
                    System.out.println(headers.name(i) + ":" + headers.value(i));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void concurrrency() throws InterruptedException {
        long start = System.currentTimeMillis();
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                int a = 0;
                for (long i = 0;i < count;i++) {
                    a += 5;
                }
            }
        });
        thread.start();
        int b = 0;
        for (long i = 0;i < count;i++) {
            b--;
        }
        long time = System.currentTimeMillis() - start;
        thread.join();//count较少时上下文切换可能占用大部分执行时间
        System.out.println("concurrency:" + time + "ms,b=" + b);
    }



    private static void serial() {
        long start = System.currentTimeMillis();
        int a = 0;
        for (long i = 0;i < count;i++) {
            a += 5;
        }
        int b = 0;
        for (long i = 0;i < count;i++) {
            b--;
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("serial:" + time + "ms,b=" + b);
    }

    static class TrainTicket2 implements Runnable {
        private int ticketCount = 100; // 初始化100张火车票(全局变量)
        static Object mutex = new Object(); // 多个线程使用同一把锁mutex
        public boolean flag = true;

        @Override
        public void run() {
            if (flag) {
			    synchronized (this) { // synchronized放这里只有一个线程在执行,why?
                    while (ticketCount > 0) {
                        try {
                            Thread.sleep(40);
                        } catch (InterruptedException e) {
                        }
    //                    synchronized (this) { // synchronized放这里出现第101张票，发生线程不安全问题,ticketCount为1时两个线程都能执行到此?
                        System.out.println(
                                Thread.currentThread().getName() + "<true>抢到了第【" + (100 - ticketCount + 1) + "】张火车票");
                        ticketCount--;
                    }
                }
            } else {
                shopTicket();
            }
        }

        private synchronized void shopTicket() {
            while (ticketCount > 0) {
                try {
                    Thread.sleep(40);
                } catch (InterruptedException e) {
                }
                System.out.println(Thread.currentThread().getName() + ":<false>抢到了第【" + (100 - ticketCount + 1) + "】张火车票");
                ticketCount--;
            }
        }
    }

    volatile int a = 1;
    volatile int b = 2;

    public void change(){
        a = 3;
        b = a;
    }

    public void print(){
        System.out.println("b="+b+";a="+a);
    }
    private static void testVolatile() {
        while (true){
            final TestHttpDownload test = new TestHttpDownload();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    test.change();
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    test.print();
                }
            }).start();

        }
    }

    int i;
    public synchronized void addI(){
        i++;
    }
    private static void testAutomic() throws InterruptedException {
        final TestHttpDownload test1 = new TestHttpDownload();
        for (int n = 0; n < 1000; n++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    test1.addI();
                }
            }).start();
        }

        Thread.sleep(10000);//等待10秒，保证上面程序执行完成

        System.out.println(test1.i);
    }

    private static class Thread1 implements Runnable{
        @Override
        public void run(){
            synchronized (TestHttpDownload.class) {
                System.out.println("enter thread1...");
                System.out.println("thread1 is waiting...");
                try {
                    //调用wait()方法，线程会放弃对象锁，进入等待此对象的等待锁定池
                    TestHttpDownload.class.wait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("thread1 is going on ....");
                System.out.println("thread1 is over!!!");
            }
        }
    }

    private static class Thread2 implements Runnable{
        @Override
        public void run(){
            synchronized (TestHttpDownload.class) {
                System.out.println("enter thread2....");
                System.out.println("thread2 is sleep....");
                //只有针对此对象调用notify()方法后本线程才进入对象锁定池准备获取对象锁进入运行状态。
                TestHttpDownload.class.notify();
                //==================
                //区别
                //如果我们把代码：TestD.class.notify();给注释掉，即TestD.class调用了wait()方法，但是没有调用notify()
                //方法，则线程永远处于挂起状态。
                try {
                    //sleep()方法导致了程序暂停执行指定的时间，让出cpu该其他线程，
                    //但是他的监控状态依然保持者，当指定的时间到了又会自动恢复运行状态。
                    //在调用sleep()方法的过程中，线程不会释放对象锁。
                    Thread.sleep(5000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                System.out.println("thread2 is going on....");
                System.out.println("thread2 is over!!!");
            }
        }
    }

    private static void compareWaitAndSleep() {
        new Thread(new Thread1()).start();
        try {
            Thread.sleep(5000);
        } catch (Exception e) {
            e.printStackTrace();
        }
        new Thread(new Thread2()).start();
    }


    // private static volatile boolean flag=false;
    private static  boolean flag=false;
    private static int n;
    public static void testVolatile1() throws InterruptedException {
        // 启动一个线程不停的i++
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (!flag){
                    n++;
                    System.out.println("thread:" +n);
                }
            }
        }).start();
        // main线程睡眠 保证上面的运行
        Thread.sleep(1000);
        // 修改变量值，使得线程停止退出
        flag=true;
        System.out.println(n);
    }

    private static LinkedList<Integer> products = new LinkedList<>();
    private static int maxCount = 100;
    private static void product() {
        for (int i = 0;i < 5;i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        synchronized (products) {
                            while(products.size() == maxCount) {
                                try {
                                    products.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            int random = new Random().nextInt(1000);
                            products.add(random);
                            System.out.println(Thread.currentThread().getName() + "生产：" + random);
                            products.notifyAll();
                        }
                    }
                }
            }, "productor" + i).start();
        }
    }

    private static void consume() {
        for (int i = 0;i < 10;i++) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while(true) {
                        synchronized (products) {
                            while(products.isEmpty()) {
                                try {
                                    products.wait();
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }

                            System.out.println(Thread.currentThread().getName() + "消费：" + products.remove());
                            products.notifyAll();
                        }
                    }
                }
            }, "consumer" + i).start();
        }
    }

    private void read() {
        try {
            FileInputStream fis = new FileInputStream(new File(""));

            byte[] sizeBytes = new byte[4];
            fis.read(sizeBytes);

            int keyLength = fis.read();

            byte[] b = new byte[keyLength];
            fis.read(b);

            int valueLenght = fis.read();
            b = new byte[valueLenght];
            fis.read(b);



        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
