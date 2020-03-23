package com.atguigu.testjmm;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class MyCatch {
    private volatile Map<String, Object> map = new HashMap<>();
    private ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public void put(String key, Object value) {
        lock.writeLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "\t 正在写：" + key);
            try { TimeUnit.MILLISECONDS.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
            map.put(key, value);
            System.out.println(Thread.currentThread().getName() + "\t 写入完成");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void get(String key) {
        lock.readLock().lock();
        try {
            System.out.println(Thread.currentThread().getName() + "\t 正在读取：");
            try { TimeUnit.MILLISECONDS.sleep(300); } catch (InterruptedException e) { e.printStackTrace(); }
            Object result = map.get(key);
            System.out.println(Thread.currentThread().getName() + "\t 读取完成" + result);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            lock.readLock().lock();
        }
    }
}

public class ReadWriteLockDemo {
    public static void main(String[] args) {
        MyCatch myCatch = new MyCatch();
        for (int i = 0; i < 10; i++) {
            final int tempInt = i;
            new Thread(() -> {
                myCatch.put(tempInt + "", tempInt + "");
            }, String.valueOf(i)).start();
        }

        for (int i = 0; i < 10; i++) {
            final int tempInt = i;
            new Thread(() -> {
                myCatch.get(tempInt + "");
            }, String.valueOf(i)).start();
        }
    }
}
