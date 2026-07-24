import java.util.concurrent.atomic.AtomicLong;

public class ForThreadId {
    static class ThreadId {
        static final AtomicLong nextId = new AtomicLong(0);

        // 定义ThreadLocal变量
        static final ThreadLocal<Long> tl = ThreadLocal.withInitial(nextId::getAndIncrement);
        // 此方法会为每个线程分配一个唯一的id
        static long get() {
            return tl.get();
        }
    }
}
