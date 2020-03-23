package com.atguigu.testjmm;

import java.util.concurrent.atomic.AtomicReference;

class User{
    private String userName;
    private int age;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public User(String userName, int age) {
        this.userName = userName;
        this.age = age;
    }

    @Override
    public String toString() {
        return "User{" +
                "userName='" + userName + '\'' +
                ", age=" + age +
                '}';
    }
}

public class AtomicReferenceDemo {
    public static void main(String[] args) {
        User user1 = new User("张三",18);
        User user2 = new User("李四",20);

        AtomicReference<User> atomicReference = new AtomicReference<>();
        atomicReference.set(user1);
        System.out.println(atomicReference.compareAndSet(user1,user2)+"\t"+atomicReference.get().toString());
        System.out.println(atomicReference.compareAndSet(user1,user2)+"\t"+atomicReference.get().toString());
    }
}
