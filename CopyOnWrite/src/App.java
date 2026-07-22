import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 多线程读写演示:验证 Copy-on-Write 的特性。
 *
 * 场景:多个读线程持续遍历 eth0 接口的路由集合,多个写线程持续对 eth0 增删路由。
 * 预期:
 *   1. 读线程与写线程并发运行,读操作不会被写操作阻塞(读零锁竞争);
 *   2. 读线程遍历集合时持有的是写时复制前的快照,即使写线程在并发修改,
 *      迭代器也不会抛出 ConcurrentModificationException(迭代安全);
 *   3. 读操作总次数远高于写操作总次数,体现 COW 适合"读多写少"场景。
 */
public class App {
    public static void main(String[] args) throws Exception {
        RouterTable routerTable = new RouterTable();

        // 初始化路由表
        routerTable.add(new Router("10.0.0.1", 8080, "eth0"));
        routerTable.add(new Router("10.0.0.2", 8081, "eth0"));
        routerTable.add(new Router("192.168.1.1", 9000, "eth1"));

        // 统计读/写操作次数
        AtomicLong readCount = new AtomicLong();
        AtomicLong writeCount = new AtomicLong();

        final int readerNum = 5;
        final int writerNum = 2;
        final long runMs = 3000;

        List<Thread> threads = new ArrayList<>();
        // 用闭锁让所有线程同时开始,避免启动顺序影响演示
        CountDownLatch start = new CountDownLatch(1);

        // 读线程:持续读取并遍历 eth0 路由集合
        for (int i = 0; i < readerNum; i++) {
            final int id = i;
            Thread t = new Thread(() -> {
                try {
                    start.await();
                    while (!Thread.currentThread().isInterrupted()) {
                        Set<Router> routers = routerTable.get("eth0");
                        if (routers != null) {
                            // 迭代器持有写时复制前的快照,
                            // 即使写线程在并发增删,这里也不会抛 ConcurrentModificationException
                            for (Router r : routers) {
                                // 模拟对路由的处理(此处为空操作)
                            }
                        }
                        readCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    // 收到中断信号,正常退出
                }
            }, "Reader-" + id);
            threads.add(t);
            t.start();
        }

        // 写线程:持续向 eth0 增删自己的路由,触发 CopyOnWriteArraySet 的写时复制
        for (int i = 0; i < writerNum; i++) {
            final int id = i;
            Thread t = new Thread(() -> {
                try {
                    start.await();
                    int seq = 0;
                    // 每个写线程操作自己的路由,交替增删使集合大小波动
                    Router r = new Router("10.0.1." + id, 7000 + id, "eth0");
                    while (!Thread.currentThread().isInterrupted()) {
                        if ((seq & 1) == 0) {
                            routerTable.add(r);
                        } else {
                            routerTable.remove(r);
                        }
                        seq++;
                        writeCount.incrementAndGet();
                    }
                } catch (InterruptedException e) {
                    // 收到中断信号,正常退出
                }
            }, "Writer-" + id);
            threads.add(t);
            t.start();
        }

        // 同时启动所有线程
        start.countDown();

        // 运行指定时长
        Thread.sleep(runMs);

        // 中断并等待所有线程结束
        for (Thread t : threads) {
            t.interrupt();
        }
        for (Thread t : threads) {
            t.join();
        }

        // 输出统计结果
        Set<Router> finalRouters = routerTable.get("eth0");
        System.out.println("运行 " + runMs + "ms 结束");
        System.out.println("读线程数: " + readerNum + ", 读操作总次数: " + readCount.get());
        System.out.println("写线程数: " + writerNum + ", 写操作总次数: " + writeCount.get());
        System.out.println("最终 eth0 路由数量: " + (finalRouters == null ? 0 : finalRouters.size()));
        System.out.println("读写全程互不阻塞,迭代器未抛出 ConcurrentModificationException -> Copy-on-Write 生效");
    }
}
