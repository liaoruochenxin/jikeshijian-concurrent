public class App {
    public static void main(String[] args) throws Exception {
        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("t1 id:" + ForThreadId.ThreadId.get());
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 5; i++) {
                System.out.println("t2 id:" + ForThreadId.ThreadId.get());
            }
        });

        t1.start();
        t2.start();
    }
}
