# **Java并发编程面试题**

#### 1.volatile是什么?

- **volatile是Java虚拟机提供的轻量级的同步机制**
  - 保证可见性
  - 不保证原子性
  - 禁止指令重排

#### 2.JMM(Java内存模型)

##### 2.1基本概念

**JMM**(Java内存模型 **Java Memory Model**,简称JMM)本身是一种抽象的概念,**并不真实存在**,它描述的是一种规则,通过这种规范定义了程序中各个变量(包括实例字段、静态字段和构成数组对象的元素)的访问方式。

**JMM**关于同步的规定：

- 线程解锁前，必须把共享变量刷新回主内存；
- 线程加锁前，必修读取主内存的最新值到自己的工作内存；
- 加锁解锁是同一把锁

由于**JVM运行程序的实体是线程**，而每个线程创建时JVM都会为其**创建一个工作内存（有些地方称为栈空间）**，**工作内存是每个线程私有的数据区域**，而Java内存模型规定所有的**共享变量都存在主内存中**，主内存是共享内存区域，所有线程都可以访问，但**线程对变量的操作（读取赋值等）必须在工作内存中进行**，***首先是要将变量从主内存拷贝到自己的工作内存，然后对变量进行操作，操作完成后再将变量写回主内存***，不能直接操作主内存中的变量，各个线程中的工作内存中存着主内存中的变量副本拷贝，因此不同线程间无法访问对方的工作内存，线程之间的通信（传值）必须通过主内存来完成，其简要访问过程如下图所示：

![](/img/微信图片_20190830172602.png)

##### 2.2.三大特性

- 可见性

代码实现：

```java
public class TestJMM {
    public static void main(String[] args) {		
		MyData myData = new MyData();
        new Thread(() -> {
            System.out.println(Thread.currentThread().getName() + "come in");
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            myData.addTo60();
            System.out.println(Thread.currentThread().getName() + "operate finished");
        }, "线程一").start();

        while (myData.number != 60) {
            // looping
        }
        System.out.println(Thread.currentThread().getName() + "number value is:" + myData.number);
          }
}


class MyData {
    volatile int number = 0;

    public void addTo60() {
        this.number = 60;
    }
}
```

如果不加 volatile 关键字，则主线程会进入死循环，加 volatile 则主线程能够退出，说明加了 volatile 关键字变量，当有一个线程修改了值，会马上被另一个线程感知到，当前值作废，从新从主内存中获取值。对其他线程可见，这就叫可见性。



- 原子性

原子性:不可分割,完整性。也即某个线程正在做某个具体业务的时候，中间不可以被加塞，或者被分割，需要整体完整。要么同时成功，要么同时失败。

代码实现:

```java
public class TestJMM {
    public static void main(String[] args) {
        MyData myData = new MyData();
        for (int i = 0; i < 20; i++) {
            new Thread(() -> {
                for (int j = 0; j < 1000; j++) {
                    myData.addPlusPlus();
                    myData.incrementAndGet();
                }
            }, String.valueOf(i)).start();
        }
        while (Thread.activeCount() > 2) {
            Thread.yield();
        }
      	//此输出几乎不会达到20000
        System.out.println(Thread.currentThread().getName() + "\t value :" + myData.number);
      
      	//此时会输出20000
        System.out.println(Thread.currentThread().getName() + "\t value :" + myData.atomicInteger);

    }
}

class MyData {
    volatile int number = 0;

    //保证原子性的解决办法之一 加上synchronized
    public void addPlusPlus() {
        number++;
    }

    //保证原子性的解决办法之二
    AtomicInteger atomicInteger = new AtomicInteger(0);

    public void incrementAndGet(){
        atomicInteger.incrementAndGet();
    }
}
```

- 有序性

  - 计算机在执行程序时,为了提高性能,编译器和处理器常常会对指令做重排,一般分以下三种 

    ![](/img/微信图片_20190903132236.png)

    - 单线程环境里面确保程序最终执行结果和代码顺序执行的结果一致。

  - 处理器在进行重排序时必须要考虑指令之间的**数据依赖性**

  - 多线程环境中线程交替执行，由于编译器优化重排的存在，两个线程中使用的变量能否保持一致性时无法确定的，结果无法预测

  ```java
  public class ReSortSeqDemo {
      int a = 0;
      boolean flag = false;
      
      public void method01() {
          a = 1;           // flag = true;
                           // ----线程切换----
          flag = true;     // a = 1;
      }

      public void method02() {
          if (flag) {
              a = a + 3;
              System.out.println("a = " + a);
          }
      }
  }
  如果两个线程同时执行，method01 和 method02 如果线程 1 执行 method01 重排序了，然后切换的线程 2 执行 method02 就会出现不一样的结果。
  ```

##### 2.3禁止指令排序

volatile 实现禁止指令排序的优化,从而避免了多线程环境下程序出现乱序的现象

先了解一个概念,内存屏障(Memory Barrier)又称内存棚栏,是一个CPU指令,它的作用有两个:

- 保证特定操作的执行顺序
- 保证某些变量的内存可见性(利用该特性实现 volatile 的内存可见性)

由于编译器个处理器都能执行指令重排序优化，如果在指令间插入一条 Memory Barrier 则会告诉编译器和 CPU，不管什么指令都不能个这条 Memory Barrier 指令重排序，也就是说通过插入内存屏障禁止在内存屏障前后执行重排序优化。内存屏障另一个作用是强制刷出各种 CPU 缓存数据，因此任何 CPU 上的线程都能读取到这些数据的最新版本。

下面是保守策略下，volatile写插入内存屏障后生成的指令序列示意图：

![](/img/微信图片_20190904185801.png)

下面是在保守策略下，volatile读插入内存屏障后生成的指令序列示意图：

![](/img/21ebc7e8190c4966948c4ef4424088be_th.png)

##### 2.4.线程安全性保证

- 工作内存与主内存同步延迟现象导致可见性问题
  - 可以使用 synchronized 或 volatile 关键字解决,它们可以使一个线程修改后的变量立即对其他线程可见
- 对于指令重排导致可见性问题和有序性问题
  - 可以利用volatile关键字解决,因为volatile的另一个作用就是禁止指令重排

#### 3.你在哪些地方用到过volatile

##### 3.1单例

```java
public class SingletonDemo {
    private static SingletonDemo install = null;
    private SingletonDemo(){
        System.out.println("我是私有构造器: \t SingletonDemo");
    }

    public static SingletonDemo getinstall(){
        if (null == install){
            install = new SingletonDemo();
        }
        return install;
    }

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            threadPool.execute(()-> SingletonDemo.getinstall());
        }
        threadPool.shutdown();
    }
}

//发现构造器里的内容会多次输出
```

##### 3.2双重锁单例:DCL(Double Check Lock)

- 代码:

```java
public class SingletonDemo {
    private static SingletonDemo install = null;

    private SingletonDemo() {
        System.out.println("我是私有构造器: \t SingletonDemo");
    }

    public static SingletonDemo getinstall() {
        if (null == install) {
            synchronized (SingletonDemo.class) {
                if (null == install) {
                    install = new SingletonDemo();
                }
            }
        }
        return install;
    }

    public static void main(String[] args) {
        ExecutorService threadPool = Executors.newFixedThreadPool(10);
        for (int i = 0; i < 10; i++) {
            threadPool.execute(() -> SingletonDemo.getinstall());
        }
        threadPool.shutdown();
    }
}
```

- 如果没有加volatile就不一定是线程安全的,原因是指令重排序的存在,加入volatile可以禁止指令重排序

  - 原因是在于某一个线程执行到第一次检测,读取到的instance不为null时,**instance的引用对象可能还没有完成初始化**

  - instance = new SingletonDemo()可以分为以下三步完成

    ```java
    memory = allocate();  // 1.分配对象空间
    instance(memory);     // 2.初始化对象
    instance = memory;    // 3.设置instance指向刚分配的内存地址，此时instance != null
    ```

  - 步骤2 和 步骤3 不存在依赖关系,而且无论重排前还是重排后程序的执行结果在单线程中并没有改变,因此这种优化是允许的。

  - 发生重排:

    ```java
    memory = allocate();  // 1.分配对象空间
    instance = memory;    // 3.设置instance指向刚分配的内存地址，此时instance != null，但对象还没有初始化完成
    instance(memory);     // 2.初始化对象
    ```


  - 所以不加volatile返回的实例不为空,但可能是未初始化的实例

#### 4.CAS你知道吗

```java
    public static void main(String[] args) {
        AtomicInteger atomicInteger = new AtomicInteger(5);
        // 获取真实值，并替换为相应的值
        boolean compareAndSet = atomicInteger.compareAndSet(5, 2019);//期望值和真实值一样就修改
        System.out.println(compareAndSet+"\t current data:"+atomicInteger.get());
		//true	 current data:2019
      
        boolean compareAndSet1 = atomicInteger.compareAndSet(5, 1024);//期望值和真实值一样就修改
        System.out.println(compareAndSet1+"\t current data:"+atomicInteger.get());
     	//false	 current data:2019
    }
```

##### 4.1CAS底层原理?谈谈对Unsafe的理解?

##### 4.1.1getAndIncrement();

```java
AtomicInteger.incrementAndGet();
```

```java
    /**
     * Atomically increments by one the current value.
     *
     * @return the updated value
     */
    public final int incrementAndGet() {
        return unsafe.getAndAddInt(this, valueOffset, 1) + 1;
    }
```

引出一个问题：UnSafe 类是什么？

##### **4.1.2Unsafe类**

```java
public class AtomicInteger extends Number implements java.io.Serializable {
    private static final long serialVersionUID = 6214790243416807050L;

    // setup to use Unsafe.compareAndSwapInt for updates
    private static final Unsafe unsafe = Unsafe.getUnsafe();
    private static final long valueOffset;

    static {
        try {
            valueOffset = unsafe.objectFieldOffset
                (AtomicInteger.class.getDeclaredField("value"));
        } catch (Exception ex) { throw new Error(ex); }
    }

    private volatile int value;
  	// ...
  }
```

- Unsafe是CAS的核心类,由于Java无法访问底层系统,而需要通过本地(native)方法来访问,Unsafe类相当于一个后门,基于该类可以直接操作特定内存数据。Unsafe类存在于 sun.misc 包中,其内部方法操作可以像 C 指针一样直接操作内存,因为Java中CAS执行操作依赖于Unsafe类。
- 变量 valueOffset，表示该变量值在内存中的偏移量，因为 Unsafe 就是根据内存偏移量来获取数据的。
- 变量value 用 volatile 修饰，保证了多线程之间的内存可见性 

##### 4.1.3CAS是什么？

- CAS的全称　Compare And Swap,它是一条CPU并发
- 它的功能是判断内存某一个位置的值是否为预期,如果是则更改这个值,这个过程就是原子的
- CAS并发原体现在Java语言中就是 sun.misc.Unsafe类中的各个方法。调用Unsafe类中的CAS方法，JVM会帮我们实现出CAS的汇编指令。这是一种完全依赖硬件的功能，通过它实现了原子操作。由于CAS是一种系统源语，源语属于操作系统用语范畴，是由若干条指令组成，用于完成某一个功能的过程，并且源语的执行必须是连续的，在执行过程中不允许被中断，也就是说CAS是一条原子指令，不会造成所谓的数据不一致的问题。
- 分析一下getAndAddInt这个方法

```java
// unsafe.getAndAddInt
public final int getAndAddInt(Object obj, long valueOffset, long expected, int val) {
    int temp;
    do {
        temp = this.getIntVolatile(obj, valueOffset);  // 获取快照值
    } while (!this.compareAndSwap(obj, valueOffset, temp, temp + val));  // 如果此时 temp 没有被修改，就能退出循环，否则重新获取
    return temp;
}
```

##### 4.2CAS的缺点

- 循环时间长,开销很大
  - 如果CAS失败,会一直尝试,如果CAS长时间一直不成功,可能会给CPU带来很大的开销(比如线程数很多,每次比较都是失败,就会一直循环),所以希望是线程数比较小的场景
- 只能保证一个共享变量的原子操作
  - 对于多个共享变量的操作时,循环CAS就无法保证操作的原子性
- 引出ABA问题



#### 5.原子类的AtomicInteger的ABA问题谈一谈？原子更新引用知道吗？

- 原子引用

```java
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
```

- ABA问题是怎么产生的

  ```java
  public class ABADemo {
      private static AtomicReference<Integer> atomicReference = new AtomicReference<>(100);
      public static void main(String[] args) {
          new Thread(()->{
              atomicReference.compareAndSet(100,101);
              atomicReference.compareAndSet(101,100);
          },"线程一").start();

          new Thread(()->{
              //保证上面的线程先执行
              try {
                  TimeUnit.SECONDS.sleep(2);
              } catch (InterruptedException e) {
                  e.printStackTrace();
              }
              boolean change = atomicReference.compareAndSet(100, 2019);

              System.out.println(change+"\t"+atomicReference.get()); // 2019
          },"线程二").start();
      }
  }
  ```

  当有一个值从 A 改为 B 又改为 A，这就是 ABA 问题。

- 时间戳原子引用

```java
public class ABADemo2 {
    private static AtomicStampedReference<Integer> atomicStampedReference = new AtomicStampedReference<>(100,1);

    public static void main(String[] args) {
        new Thread(()->{
            int stamp = atomicStampedReference.getStamp();
            System.out.println(Thread.currentThread().getName()+"\t 第一次版本号为："+stamp);
            //保证两个线程的初始版本为一致
            try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace(); }

            //制造ABA问题
            atomicStampedReference.compareAndSet(100,101
                    ,atomicStampedReference.getStamp(),atomicStampedReference.getStamp()+1);
            System.out.println(Thread.currentThread().getName()+"\t 第二次版本号为："+atomicStampedReference.getStamp());

            atomicStampedReference.compareAndSet(101,100
                    ,atomicStampedReference.getStamp(),atomicStampedReference.getStamp()+1);
            System.out.println(Thread.currentThread().getName()+"\t 第三次版本号为："+atomicStampedReference.getStamp());

        },"线程一").start();

        new Thread(()->{
            int stamp = atomicStampedReference.getStamp();
            System.out.println(Thread.currentThread().getName() + "\t 第一次版本号为：" + stamp);
            //等待线程一执行完
            try {
                TimeUnit.SECONDS.sleep(3);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            boolean result = atomicStampedReference.compareAndSet(100, 2019
                    , stamp, stamp + 1);
            System.out.println("是否已更改："+result);
            System.out.println("最终版本号为："+atomicStampedReference.getStamp()
                    +"\t当前的值为："+atomicStampedReference.getReference());
        },"线程二").start();
    }
}
```

我们先保证两个线程的初始版本为一致，后面修改是由于版本不一样就会修改失败。

#### 6.我们知道 ArrayList 是线程不安全类，请编写一个不安全的案例，并给出解决方案？

- 故障现象

```java
public class ArrayListDemo {
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        for(int i = 0;i<30;i++){
            new Thread(()->{
                list.add(UUID.randomUUID().toString().substring(0,8));
                System.out.println(Thread.currentThread().getName()+"\t"+list);
            },String.valueOf(i)).start();
        }
        //java.util.ConcurrentModificationException
    }
}

```

发现报：`java.util.ConcurrentModificationException`

- 导致原因

  - 并发修改导致的异常

- 解决方案

  - `new Vector();`
  - `Collections.synchronizedList(new ArrayList<>());`
  - `new CopyOnWriteArrayList<>();`

- 优化建议

  - 在读多写少的时候推荐使用 CopyOnWriteArrayList 这个类

  ![](/img/CopyOnWriteArrayList.png)
#### 7.Java中锁你知道哪些?请手写一个自旋锁?

##### 7.1公平和非公平锁

- 是什么：
  - **公平锁**:是指多个线程按照申请的顺序来获取值
  - **非公平锁**:是指多个线程获取值的顺序并不是按照申请锁的顺序,有可能后申请的线程比先申请的线程优先获取锁，在高并发的情况下，可能会造成优先级翻转或者饥饿现象。
- 两者区别：
  - **公平锁**:在并发环境中，每一个线程在获取锁时会先查看此锁维护的等待队列，如果为空，或者当前线程时等待队列的第一个 就占有锁，否则就会加入到等待队列中，以后会按照 FIFO 的规则获取锁。
  - **非公平锁**：一上来就尝试占有锁，如果失败再进行排队。

##### 7.2可重入锁和不可重入锁

- 是什么：

  - **可重入锁**:指的是同一个线程外层函数获得锁之后，内层仍然能获取到该锁，在同一个线程在外层方法获取锁的时候，在进入内层方法会自动获取该锁
  - **不可重入锁：**所谓不可重入锁，即若当前线程执行了某个方法已经获取了该锁，那么在方法中尝试再次获取该锁时，就会获取不到并阻塞

- 代码实现：

  - 可重入锁：
  - 不可重入锁：

- synchronized和ReentrantLock都是可重入锁

  - synchronized

    ```java
    public class SynchronziedDemo {
        public synchronized void print(){
            System.out.println("invoice print()...");
            add();
        }

        public synchronized void add() {
            System.out.println("invoice add()...");
        }

        public static void main(String[] args) {
            SynchronziedDemo sync = new SynchronziedDemo();
            sync.print();
            //invoice print()...
            //invoice add()...
        }
    }
    ```

    上面可以说明 synchronized 是可重入锁。

  - ReentrantLock

    ```java
    public class ReentrantLockDemo {
        private Lock lock = new ReentrantLock();//默认参数是false,即非公平锁

        public void print(){
            try {
                lock.lock();
                System.out.println("invoice print()...");
                add();
            }finally {
                lock.unlock();
            }
        }

        public void add() {
            try {
                lock.lock();
                lock.lock();
                System.out.println("invoice add()...");
            }finally {
                lock.unlock();
                lock.unlock();
            }
        }

        public static void main(String[] args) {
            ReentrantLockDemo reentrantLockDemo = new ReentrantLockDemo();
            reentrantLockDemo.print();
            //invoice print()...
            //invoice add()...
        }
    }
    ```

    上面例子可以说明 ReentrantLock 是可重入锁，而且在 #doAdd 方法中加两次锁和解两次锁也可以

##### 7.3.自旋锁

- 是指尝试获取锁的线程不会立即阻塞，而是**采用循环的方式去尝试获取锁**，这样的好处是减少线程上下文切换的消耗，缺点就是循环会消耗CPU。

- 手动实现自旋锁

  ```java
  public class SpinLockDemo {
      private AtomicReference<Thread> atomicReference = new AtomicReference<>();

      public void myLock() {
          Thread thread = Thread.currentThread();
          System.out.println(thread.getName()+"\t come in...");
          while (!atomicReference.compareAndSet(null,thread)){
              //loop
          }
      }

      public void myUnLock(){
          Thread thread = Thread.currentThread();
          atomicReference.compareAndSet(thread,null);
          System.out.println(thread.getName()+"\t invoked myUnLock()");
      }

      public static void main(String[] args) {
          SpinLockDemo spinLockDemo = new SpinLockDemo();

          new Thread(()->{
              spinLockDemo.myLock();
              //AA进来后休眠五秒再释放  这样BB就一直自旋
              try { TimeUnit.SECONDS.sleep(5); } catch (InterruptedException e) { e.printStackTrace();}
              spinLockDemo.myUnLock();
          },"AA").start();

          try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace();}

          new Thread(()->{
              spinLockDemo.myLock();
              //为了演示不那么快  休眠一秒
              try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException e) { e.printStackTrace();}
              spinLockDemo.myUnLock();
          },"BB").start();

      }
  }
  ```

  输出：

  ```java
  AA	 come in...
  BB	 come in...
  //停五秒 等待AA线程 compareAndSet
  AA	 invoked myUnLock()
  //停一秒
  BB	 invoked myUnLock()
  ```

  获取锁的时候，如果原子引用为空就获取锁，不为空表示有人获取了锁，就循环等待。

##### 7.4.独占锁（写锁）/共享锁（读锁）

多个线程同时读一个资源类没什么问题，所以为了满足并发量，读取共享资源可以同时进行。但是，如果有一个线程想去写共享资源，就不应该再有其它线程可以对该资源类进行读或写

- 是什么
  - 独占锁：指该锁一次只能被一个线程持有
  - 共享锁：指该锁可以被多个线程持有
- 对于 ReentrantLock 和synchronized都是独占锁；对于ReentrantReadWriteLock 其读锁是共享锁，写锁是独占锁。读锁的共享可保证并发读是非常高效的
- 总结
  - 读-读能共存
  - 读-写不能共存
  - 写-写不能共存
  - 写操作：原子+独占。整个过程必须是一个完整的统一体，中间不允许被分割被打断
- 读写锁例子

```java
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



```
测试:
```java
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
```
输出结果:
```java
0	 正在写：0

0	 写入完成

1	 正在写：1

1	 写入完成

2	 正在写：2

2	 写入完成

3	 正在写：3

3	 写入完成

4	 正在写：4

4	 写入完成

5	 正在写：5

5	 写入完成

6	 正在写：6

6	 写入完成

7	 正在写：7

7	 写入完成

8	 正在写：8

8	 写入完成

9	 正在写：9

9	 写入完成

0	 正在读取：

1	 正在读取：

2	 正在读取：

3	 正在读取：

6	 正在读取：

5	 正在读取：

8	 正在读取：

4	 正在读取：

9	 正在读取：

7	 正在读取：

7	 读取完成7

2	 读取完成2

1	 读取完成1

3	 读取完成3

6	 读取完成6

5	 读取完成5

4	 读取完成4

9	 读取完成9

8	 读取完成8

0	 读取完成0
```
能保证**读写**、**写读**和**写写**的过程是互斥的时候是独享的，**读读**的时候是共享的。

#### 8.CountDownLatch/CyclicBarrier/Semaphore用过吗?

##### 8.1CountDownLatch

让一些线程阻塞直到另一个线程完成一系列操作后才被唤醒。CountDownLatch主要有两个方法，当一个线程调用 await() 方法时，调用线程会被阻塞，其他线程调用 **countDown() 方法会将计数减一**（调用 countDown() 方法的线程不会被阻塞），当计数其值变为 **零** 时，调用 await() 方法被阻塞的线程会被唤醒，继续执行。

```java
/**
 * 场景:下班之后,老总必须等所有员工都走完才能走并锁门
 */
public class CountDownLatchDemo {
    public static void main(String[] args) {
        for (int i = 0; i < 50; i++) {
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+"\t 下班了,离开公司");
            }, String.valueOf(i)).start();
        }

        System.out.println(Thread.currentThread().getName()+"\t 老总最后关门,离开公司");
    }
}
```

此时输出:

```java
1	 下班了,离开公司
2	 下班了,离开公司
main	 老总最后关门,离开公司
0	 下班了,离开公司
3	 下班了,离开公司
7	 下班了,离开公司
8	 下班了,离开公司
4	 下班了,离开公司
6	 下班了,离开公司
9	 下班了,离开公司
5	 下班了,离开公司
```

发现老总没等其他人走完就走了,把其他人锁在公司

将程序改为:

```java
/**
 * 场景:下班之后,老总必须等所有员工都走完才能走并锁门
 */
public class CountDownLatchDemo {
    private static final int COUNT = 10;
    private static CountDownLatch countDownLatch = new CountDownLatch(COUNT);

    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            new Thread(()->{
                countDownLatch.countDown();
                System.out.println(Thread.currentThread().getName()+"\t 下班了,离开公司");
            }, String.valueOf(i)).start();
        }
        try {
            countDownLatch.await();
            System.out.println(Thread.currentThread().getName()+"\t 老总最后关门,离开公司");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

```

输出:

```java
0	 下班了,离开公司
2	 下班了,离开公司
1	 下班了,离开公司
4	 下班了,离开公司
5	 下班了,离开公司
6	 下班了,离开公司
3	 下班了,离开公司
8	 下班了,离开公司
7	 下班了,离开公司
9	 下班了,离开公司
main	 老总最后关门,离开公司
```

##### 8.2CyclicBarrier

```java
/**
 * 场景:收集七颗龙珠才能召唤神龙
 */
public class CyclicBarrierDemo {
    public static void main(String[] args) {
        CyclicBarrier cyclicBarrier = new CyclicBarrier(7,()-> System.out.println("***龙珠收齐-召唤神龙***"));

        for (int i = 0; i < 7; i++) {
            final int intTemp = i;
            new Thread(()->{
                System.out.println(Thread.currentThread().getName()+"\t 收集到第"+intTemp+"颗龙珠");
                try {
                    cyclicBarrier.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (BrokenBarrierException e) {
                    e.printStackTrace();
                }
            }, String.valueOf(i)).start();
        }
    }
}
```

输出:

```java
0	 收集到第0颗龙珠
3	 收集到第3颗龙珠
1	 收集到第1颗龙珠
2	 收集到第2颗龙珠
4	 收集到第4颗龙珠
5	 收集到第5颗龙珠
6	 收集到第6颗龙珠
***龙珠收齐-召唤神龙***
```

##### 8.3Semaphore

```java
/**
 * 场景:假设有3个车位,8个车去抢
 */
public class SemaphoreDemo {

    public static void main(String[] args) {
        Semaphore semaphore = new Semaphore(3, true);

        for (int i = 0; i < 8; i++) {
            new Thread(() -> {
                try {
                    semaphore.acquire();//获取一个许可
                    System.out.println(Thread.currentThread().getName() + "\t 抢到车位");
                    int time = new Random().nextInt(10);
                    try { TimeUnit.SECONDS.sleep(time); } catch (InterruptedException e) { e.printStackTrace(); }
                    System.out.println(Thread.currentThread().getName() + "\t 停车" + time + "秒,离开车位");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }finally {
                    semaphore.release();//释放一个许可
                }
            }, String.valueOf(i)).start();
        }
    }
}
```

输出

```java
0	 抢到车位
2	 抢到车位
1	 抢到车位
1	 停车0秒,离开车位
3	 抢到车位
2	 停车3秒,离开车位
4	 抢到车位
3	 停车5秒,离开车位
5	 抢到车位
4	 停车2秒,离开车位
6	 抢到车位
6	 停车1秒,离开车位
7	 抢到车位
0	 停车8秒,离开车位
5	 停车5秒,离开车位
7	 停车4秒,离开车位
```

#### 9.阻塞队列你知道吗?

##### 9.1阻塞队列有哪些?

- ArrayBlockingQueue:是一个基于数组结构的有界阻塞队列,次队列按FIFO(先进先出)对元素进行排序
- LinkedBlockingQueue:是一个基于链表结构的有界(但大小默认值为Integer.MAX_VALUE)阻塞队列,此队列按FIFO(先进先出)对元素进行排序,吞吐量通常要高于ArrayBlockingQueue
- SynchronousQueue:是一个不存储元素的阻塞队列,每个插入操作必须要等到另一个线程调用移除操作,否则插入操作一直处于阻塞状态,吞吐量通常要高于LinkedBlockingQueue

##### 9.2什么是阻塞队列

- 阻塞队列,顾名思义,首先它是一个队列,而一个阻塞队列在数据结构中所起的作用大致如图所示:

  ![](/img/1234sdafsdf.png)

  - 当阻塞队列是空时,从队列获取元素的操作将会被阻塞;
  - 当阻塞队列是满时,往队列里添加元素的操作将会被阻

- 好处：在多线程领域：所谓阻塞，在，某些情况下会**挂起**线程（即阻塞），一旦条件满足，被刮起的线程又会自动被唤醒。为什么需要BlockingQueue，好处是我们不需要关心什么时候需要阻塞线程，什么时候需要唤醒线程，因为这一切BlockingQueue都一手包办了。
| 方法类型 |   抛出异常    |   特殊值    |   阻塞   |         超时         |
| :--: | :-------: | :------: | :----: | :----------------: |
|  插入  |  add(e)   | offer(e) | put(e) | offer(e,time,unit) |
|  移除  | remove()  |  poll()  | take() |  poll(time,unit)   |
|  检查  | element() |  peek()  |  不可用   |        不可用         |

| 抛异常      | 当阻塞队列满时，再往队列里add插入元素会抛IllegalStateException:QyeyeFull                      当阻塞队列空时，再往队列里remove移除元素会抛NoSuchElementException |
| -------- | :--------------------------------------- |
| **特殊值**  | **插入方法，成功true失败false                                                                                                                  移除方法，成功返回出队列的元素，队列里面没有就返回null** |
| **一直阻塞** | **当阻塞队列满时，生产者线程继续往队列里put元素，队列会一直阻塞生产线程直到put数据or响应中断退出。                                                                                                                                                当阻塞队列空时，消费者线程试图从队列里take元素，队列会一直阻塞消费者线程直到队列可用。** |
| **超时**   | **如果操作不能马上进行，操作会被阻塞指定的时间，如果指定时间没执行，则返回一个特殊值，一般是 true 或者 false** |

##### 9.3SynchronousQueue

SynchronousQueue，实际上它不是一个真正的队列，因为它不会为队列中元素维护存储空间。与其它队列不同的是，它维护一组线程，这些线程在等待着把元素加入或者移除队列

```java
public class BlockingQueueDemo {
    public static void main(String[] args) throws InterruptedException{
        BlockingQueue<String> blockingQueue = new SynchronousQueue<>();

        new Thread(()->{
            try {
                System.out.println(Thread.currentThread().getName()+"\t put 1");
                blockingQueue.put("1");

                System.out.println(Thread.currentThread().getName()+"\t put 2");
                blockingQueue.put("2");

                System.out.println(Thread.currentThread().getName()+"\t put 3");
                blockingQueue.put("3");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        },"AAA").start();


        new Thread(()->{
            try {
                try { TimeUnit.SECONDS.sleep(6); } catch (InterruptedException e) { e.printStackTrace();}
                System.out.println(Thread.currentThread().getName()+"\t"+blockingQueue.take());

                try { TimeUnit.SECONDS.sleep(6); } catch (InterruptedException e) { e.printStackTrace();}
                System.out.println(Thread.currentThread().getName()+"\t"+blockingQueue.take());

                try { TimeUnit.SECONDS.sleep(6); } catch (InterruptedException e) { e.printStackTrace();}
                System.out.println(Thread.currentThread().getName()+"\t"+blockingQueue.take());

            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        },"BBB").start();
    }
}
```

##### 9.4使用场景

- 生产者消费者模式
- 线程池
- 消息中间件

##### 9.5代码演示

**线程通信之生产者消费者传统版**

```java
/**
 * 需求:一个初始值为0的变量,两个线程对其交替操作,一个加1,一个减1,来5轮
 */
public class ProdConsumer_TraditionDemo {
    /**
     * 1.线程   操作  资源类
     * 2.判断   干活  通知
     * 3.防止虚假唤醒机制
     * @param args
     */
    public static void main(String[] args) {
        ShareData shareData = new ShareData();
        new Thread(()->{
            for (int i = 0; i < 5; i++) {
                shareData.increment();
            }
        },"AA").start();

        new Thread(()->{
            for (int i = 0; i < 5; i++) {
                shareData.decrement();
            }
        },"BB").start();
    }
}

class ShareData{
    private volatile int number;
    private Lock lock = new ReentrantLock();
    private Condition condition = lock.newCondition();

    public void increment(){
        lock.lock();
        try {
            while (number !=0){
                //不能生产 等待
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            number++;
            System.out.println(Thread.currentThread().getName()+"\t"+number);
            condition.signalAll();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }


    public void decrement(){
        lock.lock();
        try {
            while (number ==0){
                //不能消费 等待
                try {
                    condition.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            number--;
            System.out.println(Thread.currentThread().getName()+"\t"+number);
            condition.signalAll();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}
```

输出:

```java
AA	1
BB	0
AA	1
BB	0
AA	1
BB	0
AA	1
BB	0
AA	1
BB	0
```

**线程通信之生产者消费者队列版**

```java
/**
 * 需求:
 * 生产蛋糕,生产一个取一个,5秒钟之后大老板叫停,停止生产,停止消费
 */
public class ProdConsumer_BlockQueueDemo {
    public static void main(String[] args) {
        ProdConsumer prodConsumer = new ProdConsumer(new ArrayBlockingQueue<String>(10));
        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "生产线程启动..");
                prodConsumer.myProd();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Prod").start();

        new Thread(() -> {
            try {
                System.out.println(Thread.currentThread().getName() + "消费线程启动..\n");
                prodConsumer.myConsumer();
                System.out.println();
                System.out.println();
                System.out.println();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, "Consumer").start();

        //5秒钟之后停止生产消费
        try { TimeUnit.SECONDS.sleep(50); } catch (InterruptedException e) { e.printStackTrace();}
        prodConsumer.stop();
    }
}

class ProdConsumer {
    private volatile boolean FLAG = true;

    private BlockingQueue<String> blockingQueue = null;

    private AtomicInteger atomicInteger = new AtomicInteger();

    public ProdConsumer(BlockingQueue<String> blockingQueue) {
        this.blockingQueue = blockingQueue;
        System.out.println("使用" + blockingQueue.getClass().getName() + "消费队列\n");

    }

    public void myProd() throws Exception {
        String data = null;
        boolean retResult;
        while (FLAG) {
            data = String.valueOf(atomicInteger.getAndIncrement());
            retResult = blockingQueue.offer(data, 2L, TimeUnit.SECONDS);
            if (retResult) {
                System.out.println(Thread.currentThread().getName() + "\t 生产了" + data + "成功");
            } else {
                System.out.println(Thread.currentThread().getName() + "\t 生产了" + data + "失败");
            }
            TimeUnit.SECONDS.sleep(1);
        }
        System.out.println(Thread.currentThread().getName() + "老板叫停,不允许生产,表示FLAG= false,生产结束");
    }

    public void myConsumer() throws Exception {
        String data = null;
        while (FLAG) {
            data = blockingQueue.poll(2L, TimeUnit.SECONDS);
            if (data == null || data.equalsIgnoreCase("")) {
                System.out.println(Thread.currentThread().getName() + "\t 超过2秒没有取到蛋糕,消费退出");
            } else {
                System.out.println(Thread.currentThread().getName() + "\t 消费队列蛋糕" + data + "成功!");
            }
        }
    }

    public void stop(){
        this.FLAG = false;
    }
}
```

输出：

```java
使用java.util.concurrent.ArrayBlockingQueue消费队列

Prod生产线程启动..
Consumer消费线程启动..

Prod	 生产了0成功
Consumer	 消费队列蛋糕0成功!
Prod	 生产了1成功
Consumer	 消费队列蛋糕1成功!
Prod	 生产了2成功
Consumer	 消费队列蛋糕2成功!
Prod	 生产了3成功
Consumer	 消费队列蛋糕3成功!
Prod	 生产了4成功
Consumer	 消费队列蛋糕4成功!
Prod老板叫停,不允许生产,表示FLAG= false,生产结束
Consumer	 超过2秒没有取到蛋糕,消费退出
```



#### 10 synchronized和Lock有什么区别?

- 原始结构
  - synchronized 是关键字属于JVM层面,反应在字节码上是 monitorenter 和 monitorexit,其底层是通过 monitor 对象来完成。其实wait/notify等方法也是依赖 monitor 对象，所以只有在同步块或方法中才能调用 wait/notify 等方法。
  - Lock 时具体的类（java.util.concurrent.locks.Lock）时Api层面的锁
- 使用方法
  - synchronized 不需要用户手动去释放，当 synchronizd 代码执行完后，系统会自动让线程释放对锁的占用
  - ReentrantLock 需要用户手动的释放锁，若没有主动释放锁，可能会导致出现死锁的情况，lock() 和 unlock()方法需要搭配 try/finally 来使用
- 等待是否可中断
  - synchronized 不可中断，除非抛出异常或者正常执行完成，
  - ReentrantLock 可中断，设置超时方法 tryLock(long timeout,TimeUnit unit),lockInterruptibly() 放代码块中，调用interrupt() 方法可中断
- 加锁是否公平
  - synchronized 非公平锁
  - ReentrantLock 默认非公平锁，构造方法中可以传入布尔值，true为公平所，false为非公平锁
- 锁可以绑定多个 Condition
  - synchronized 没有 Condition
  - ReentrantLock 用来实现分组唤醒需要唤醒的线程们，**可以精确唤醒**，而不是像 synchronized 要么随机唤醒一个线程，要么唤醒全部线程。

##### 10.1 代码演示 ReentrantLock 精确唤醒

```java
/**
 * 需求:多线程之间按顺序调用,实现 A->B->C 三个线程启动,如下:
 *      AA打印5次,BB打印10次,CC打印15次
 *      紧接着
 *      AA打印5次,BB打印10次,CC打印15次
 *      ....
 *      来10轮
 */
public class SyncAndReentrantLockDemo {
    public static void main(String[] args) {
        PreciseWakeUp preciseWakeUp = new PreciseWakeUp();
        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                preciseWakeUp.print5();
            }
        },"AA").start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                preciseWakeUp.print10();
            }
        },"BB").start();

        new Thread(()->{
            for (int i = 0; i < 10; i++) {
                preciseWakeUp.print15();
            }
        },"CC").start();
    }
}

class PreciseWakeUp{
    private int temp = 1; //AA:1 ; BB:2 ; CC:3
    private Lock lock = new ReentrantLock();
    private Condition c1 = lock.newCondition();
    private Condition c2 = lock.newCondition();
    private Condition c3 = lock.newCondition();


    public void print5(){
       lock.lock();
       try {
           //1.判断
           while (temp !=1){
               try {
                   c1.await();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
           //2.干活
           for (int i = 0; i < 5; i++) {
               System.out.println(Thread.currentT//hread().getName()+"\t"+ i);
           }
           //3.通知
           temp = 2;
           c2.signal();
       }catch (Exception e){
           e.printStackTrace();
       }finally {
           lock.unlock();
       }
    }

    public void print10(){
        lock.lock();
        try {
            //1.判断
            while (temp !=2){
                try {
                    c2.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //2.干活
            for (int i = 0; i < 10; i++) {
                System.out.println(Thread.currentThread().getName()+"\t"+ i);
            }
            //3.通知
            temp = 3;
            c2.signal();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }

    public void print15(){
        lock.lock();
        try {
            //1.判断
            while (temp !=3){
                try {
                    c1.await();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //2.干活
            for (int i = 0; i < 15; i++) {
                System.out.println(Thread.currentThread().getName()+"\t"+ i);
            }
            //3.通知
            temp = 1;
            c1.signal();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            lock.unlock();
        }
    }
}
```

#### 11.线程池用过吗?谈谈对 ThreadPoolExector 的理解

##### 11.1 为什么使用线程池?线程池的优势?

线程池用于多线程处理中,它可以根据系统的情况,可以有效地控制线程执行的数量，优化运行效果。线程池做的工作主要是控制运行的线程的数量，处理过程中将任务放入队列，然后在线程创建后启动这些任务，如果线程数量超过了最大数量，那么超出数量的线程排队等候，等其它线程执行完毕，再从队列中取出来任务来执行。

主要特点为：

- 线程复用
- 控制最大并发数
- 管理线程

主要优点：

- 降低资源消耗，通过重复利用已创建的线程来降低线程创建和销毁造成的消耗
- 提高响应速度，当任务到达时，任务可以不需要等到线程创建就立即执行
- 提高线程可管理性，线程是稀缺资源，如果无限的创建，不仅仅会消耗系统资源，还会降低系统稳定性，使用线程可以统一进行分配、调优和监控

##### 11.2 创建线程的几种方式

- 继承 Thread

- 实现 Runnable 接口

- 实现 Callable 接口

  ```java
  public class CallableDemo {
      public static void main(String[] args) throws ExecutionException, InterruptedException {
          // 在 FutureTask 中传入 Callable 的实现类
          FutureTask<Integer> futureTask = new FutureTask<Integer>(new Callable<Integer>() {
              @Override
              public Integer call() throws Exception {
                  return 666;
              }
          });

          //把 futureTask 放入线程中
          new Thread(futureTask).start();
          //获取结果
          Integer result = futureTask.get();
          System.out.println(result);
      }
  }
  ```

##### 11.3 线程池如何使用？

**架构说明**

![](/img/u=947447203,3545120712&fm=26&gp=0.jpg)

**编码实现**

- Executors.newSingleThreadExecutor():只有一个线程的线程池,因此所有提交的任务是顺序执行（一个任务接一个任务的场景）
- Executors.newCachedThreadPool();线程池中有很多线程需要同时执行，老的可用线程将会被新的任务触发重新执行，如果线程超过60秒内没执行，那么将被终止并从池中删除（执行很多短期异步的小程序或者负载较轻的服务器）
- Executors.newFixedThreadPool()：拥有固定线程数的线程池，如果没有任务执行，那么线程会一直等待（执行长期的任务，性能好很多）
- Executors.newScheduledThreadPool()：用来调度即将执行的任务的线程池
- Executors.newWorkStealingPool()：newWorkStealingPool 适合使用在很耗时的操作，但是 newWorkStealingPool 不是ThreadPoolExecutor的扩展，它是新的线程池类 ForkJoinPool 的扩展，但是都是在统一的一个Executors类中实现，由于能够合理的使用CPU进行对任务操作（并行操作），所以很适合使用在很耗时的任务中

##### ThreadPoolExecutor

ThreadPoolExecutor作为 java.util.concurrent包对外提供基础实现，以内部线程池的形式对外提供 管理任务执行、线程调度、线程池管理等服务

##### 11.4线程池的几个重要参数介绍？

![](/img/微信图片_20190923214537.png)

| 参数              | 作用                                       |
| --------------- | ---------------------------------------- |
| corePoolSize    | 线程池中的常驻核心线程数（类似于银行今日值班窗口）                |
| maximumPoolSize | 线程池能够容纳同时执行的最大线程数（类似于银行最多几个窗口）           |
| keepAliveTime   | 多余的空闲线程的存活时间。当前线程池数量超过 corePoolSize时，当空闲时间达到KeepAliveTime值时，多余空闲线程会被销毁，直到只剩下 corePoolSize个线程为止（类似于银行临时来加班的窗口，没人了再等一段时间还没人就下班） |
| unit            | keepAliveTiiem 的单位                       |
| workQueue       | 任务队列，被提交但尚未被执行的任务，也就是 阻塞任务队列（类似于银行的候客区）  |
| threadFactory   | 表示生成线程池中工作线程的线程工厂，用于创建线程  一般用默认即可        |
| defaultHandle   | 拒绝策略， 当提交任务数超过 maxmumPoolSize+workQueue 之和时，任务会交给RejectedExecutionHandler 来处理 |

##### 11.5线程池的工作原理?

![](/img/线程池工作原理图.png)

![](/img/微信图片_20190924180636.png)

![](/img/92ad4409-2ab4-388b-9fb1-9fc4e0d832cd.jpg)

#### 12.线程池用过吗？生产上你如何设置合理参数？

##### 12.1 线程池的拒绝策略你谈谈？

- 是什么
  - 等待队列已满，再也塞不下新的任务，同时线程池中的线程数达到了最大线程数，无法继续为新任务服务
- 拒绝策略
  - AbortPolicy：处理程序遭到拒绝将抛出运行时 RejectedExecutionException
  - CallerRunsPolicy：线程调用运行该任务的 execute 本身。此策略提供简单的反馈控制机制，能够减缓新任务的提交速度。
  - DiscardPolicy：不能执行的任务将被删除
  - DiscardOldestPolicy：如果执行程序尚未关闭，则位于工作队列头部的任务将被删除，然后重试执行程序（如果再次失败，则重复此过程）

##### 12.2  你在工作中单一的、固定数的和可变的三种创建线程池的方法，用到哪个最多？（超级大坑）

![](/img/微信图片_20190926210632.png)

Java中的BlockQueue主要有两种，分别是ArrayBlockingQueue和LinkedBlockingQueue。

ArrayBlockingQueue是一个用数组实现的有界阻塞队列，必须设置容量值；

LinkedBlockingQueue是一个用链表实现的有界阻塞队列，容量可以选择进行设置，不设置的话，将是一个无边界的阻塞队列，最大长度为Integer.MAX_VALUE

这里的问题就出在：不设置的话，将是一个无边界的阻塞队列，也就是说，如果我们不设置LinkedBlockingQueue的容量的话，其默认容量将会是Integer.MAX_VALUE。

而 newFixedThreadPool 中创建 LinkedBlockingQueue 时，并未指定容量，此时LinkedBlockingQueue就是一个无边界的队列，是可以不断的向队列中添加任务的，这种情况下就有可能因为任务过多而导致内存溢出的问题。

上面提到的问题主要体现在newFixedThreadPool和newSingleThreadExecutor两个工厂方法上，并不是说newCachedThreadPool和newScheduledThreadPool这两个方法就安全了，这两种方式创建的最大线程数可能是Integer.MAX_VALUE，而创建这么多线程，必然就有可能导致OOM。

##### 12.3 你在工作中是如何使用线程池的，是否自定义过线程池使用

自定义线程池

```java
	public static void main(String[] args) {
        ExecutorService threadPool = new ThreadPoolExecutor(
                2,
                5,
                1L,
                TimeUnit.SECONDS,
                new LinkedBlockingDeque<>(3),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());

        try {
            for (int i = 0; i <9 ; i++) {
                threadPool.execute(()->{
                    System.out.println(Thread.currentThread().getName()+"处理任务");
                });
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            threadPool.shutdown();
        }
    }
```

##### 12.4 合理配置线程池你是如何考虑的？

- CPU密集型：
  - CPU密集的意思是需要进行大量计算，而没有阻塞，CPU一直全速进行
  - CPU密集型任务尽可能减少线程数量，一般为CPU核数+1个线程的线程池
- IO密集型
  - 由于IO密集型任务线程并不是一直在执行任务，可以分配多一点线程数，比如CPU*2
  - 也可以使用公式：CPU核数 / （1-阻塞系数）；其中阻塞系数在0.8-0.9之间

#### 13.死锁编码以及定位分析

- 产生死锁的原因

  - 死锁是指两个或两个以上的进程在执行过程中，因争夺资源而造成的一种相互等待的现象，如果无外力干涉，那它们都将无法推进下去，如果系统的资源充足，进程的资源请求都能够得到满足，死锁出现的可能性就会降低。

- 代码

  ```java
  public class DeadLockDemo {
      public static void main(String[] args) {
          String lockA = "LockA";
          String lockB = "LockB";

          DeadLockDemo deadLockDemo = new DeadLockDemo();
          ExecutorService pool = Executors.newFixedThreadPool(2);
          pool.submit(()->deadLockDemo.deadLockInstance(lockA,lockB));
          pool.submit(()->deadLockDemo.deadLockInstance(lockB,lockA));
      }

      public void deadLockInstance(String lockA,String lockB){
          synchronized (lockA){
              System.out.println(Thread.currentThread().getName()+"\t 获取到"+lockA+"\t 尝试获取"+lockB);
              synchronized (lockB){
                  System.out.println(Thread.currentThread().getName()+"\t 获取到"+lockB+"\t 尝试获取"+lockA);
              }
          }
      }
  }
  ```

- j解决
  - jps -l 命令查定位进程号

  ```yaml
  28519 org.jetbrains.jps.cmdline.Launcher
  32376 com.intellij.idea.Main
  28521 com.cuzz.thread.DeadLockDemo
  27836 org.jetbrains.kotlin.daemon.KotlinCompileDaemon
  28591 sun.tools.jps.Jps

  ```

  - jstack 28521 找到死锁查看

  ```yaml
  2019-05-07 00:04:15
  Full thread dump Java HotSpot(TM) 64-Bit Server VM (25.191-b12 mixed mode):

  "Attach Listener" #13 daemon prio=9 os_prio=0 tid=0x00007f7acc001000 nid=0x702a waiting on condition [0x0000000000000000]
     java.lang.Thread.State: RUNNABLE
  // ...
  Found one Java-level deadlock:
  =============================
  "pool-1-thread-2":
    waiting to lock monitor 0x00007f7ad4006478 (object 0x00000000d71f60b0, a java.lang.String),
    which is held by "pool-1-thread-1"
  "pool-1-thread-1":
    waiting to lock monitor 0x00007f7ad4003be8 (object 0x00000000d71f60e8, a java.lang.String),
    which is held by "pool-1-thread-2"

  Java stack information for the threads listed above:
  ===================================================
  "pool-1-thread-2":
          at com.cuzz.thread.DeadLockDemo.method(DeadLockDemo.java:34)
          - waiting to lock <0x00000000d71f60b0> (a java.lang.String)
          - locked <0x00000000d71f60e8> (a java.lang.String)
          at com.cuzz.thread.DeadLockDemo.lambda$main$1(DeadLockDemo.java:21)
          at com.cuzz.thread.DeadLockDemo$$Lambda$2/2074407503.run(Unknown Source)
          at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
          at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
          at java.lang.Thread.run(Thread.java:748)
  "pool-1-thread-1":
          at com.cuzz.thread.DeadLockDemo.method(DeadLockDemo.java:34)
          - waiting to lock <0x00000000d71f60e8> (a java.lang.String)
          - locked <0x00000000d71f60b0> (a java.lang.String)
          at com.cuzz.thread.DeadLockDemo.lambda$main$0(DeadLockDemo.java:20)
          at com.cuzz.thread.DeadLockDemo$$Lambda$1/558638686.run(Unknown Source)
          at java.util.concurrent.ThreadPoolExecutor.runWorker(ThreadPoolExecutor.java:1149)
          at java.util.concurrent.ThreadPoolExecutor$Worker.run(ThreadPoolExecutor.java:624)
          at java.lang.Thread.run(Thread.java:748)

  Found 1 deadlock.

  ```

  最后发现一个死锁。