# 线程本地存储模式：没有共享就没有伤害

并发问题产生于多个线程同时读写同一共享变量。因此没有共享变量也不会有并发问题，而避免共享变量在Java中有两种方式

1. 线程封闭：即方法里的局部变量
2. 线程本地存储（ThreadLocal）

## ThreadLocal 使用方法

```` Java
static class ThreadId {
    static final AtomicLong nextId = new AtomicLong(0);

    // 定义ThreadLocal变量
    static final ThreadLocal<Long> tl = ThreadLocal.withInitial(nextId::getAndIncrement);
    // 此方法会为每个线程分配一个唯一的id
    static long get() {
        return tl.get();
    }
}
````

上述代码中不同的线程返回的ThreadId是不同的，但是同一个线程反复调用该方法返回的id是相同的。

## ThreadLocal 的工作原理

在探索工作原理之前，首先需要明白`Thread`、`ThreadLocal`和`ThreadLocalMap`之间的关系。`Thread`是线程本身，持有对应的`ThreadLocalMap`实例，用于存放该线程所有的线程局部变量。`ThreadLocalMap`是`ThreadLocal`中的一个静态类，是实际存储数据的容器，他的`key`是当前`ThreadLocal`实例本身。`ThreadLocal`提供操作方法，不持有`ThreadLocalMap`实例，仅获取当前线程的`ThreadLocalMap`，并以自身为`key`获取对应的`Value`。也就是说，对于多个线程的`ThreadLocalMap`来说，他们的`key`即`ThreadLocal`是同一个实例的引用。明白了上述内容之后再来看源码应该就简单一点了。

```Java
class Thread {
  //内部持有ThreadLocalMap
  ThreadLocal.ThreadLocalMap 
    threadLocals;
}
class ThreadLocal<T>{
  public T get() {
    //首先获取线程持有的
    //ThreadLocalMap
    ThreadLocalMap map =
      Thread.currentThread()
        .threadLocals;
    //在ThreadLocalMap中
    //查找变量
    Entry e = 
      map.getEntry(this);
    return e.value;  
  }
  static class ThreadLocalMap{
    //内部是数组而不是Map
    Entry[] table;
    //根据ThreadLocal查找Entry
    Entry getEntry(ThreadLocal key){
      //省略查找逻辑
    }
    //Entry定义
    static class Entry extends
    WeakReference<ThreadLocal>{
      Object value;
    }
  }
}
````