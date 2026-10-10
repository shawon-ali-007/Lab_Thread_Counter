
import java.util.concurrent.atomic.AtomicLong;

public class Shawon_Thread extends Thread {

    static AtomicLong safeCounter = new AtomicLong(0);
    static long unsafeCounter = 0;

    long localCounter = 0;
    int increments;
    boolean threadSafe;

    Shawon_Thread(int increments, boolean threadSafe) {
        this.increments = increments;
        this.threadSafe = threadSafe;
    }

    public void run() {
        for (int i = 0; i < increments; i++) {
            if (threadSafe) {
                safeCounter.incrementAndGet();
            } else {
                unsafeCounter++;
            }

            localCounter++;
        }
    }

    public static void main(String[] args)
            throws InterruptedException {

        int threads = Integer.parseInt(args[0]);
        int increments = Integer.parseInt(args[1]);
        boolean threadSafe = Boolean.parseBoolean(args[2]);

        safeCounter.set(0);
        unsafeCounter = 0;

        Shawon_Thread[] workers = new Shawon_Thread[threads];

        for (int i = 0; i < threads; i++) {
            workers[i] = new Shawon_Thread(
                    increments, threadSafe);
            workers[i].start();
        }

        long nonStaticTotal = 0;

        for (int i = 0; i < threads; i++) {
            workers[i].join();
            nonStaticTotal += workers[i].localCounter;
        }

        long staticCount;

        if (threadSafe) {
            staticCount = safeCounter.get();
        } else {
            staticCount = unsafeCounter;
        }

        long expected = (long) threads * increments;
        long difference = Math.abs(staticCount - nonStaticTotal);

        double percentage = nonStaticTotal == 0
                ? (staticCount == 0 ? 0.0 : Double.NaN)
                : (difference * 100.0 / nonStaticTotal);

        System.out.println("Mode: "
                + (threadSafe ? "Thread-safe" : "Unsafe"));
        System.out.println("Threads: " + threads);
        System.out.println("Increments per thread: " + increments);
        System.out.println("Expected count: " + expected);
        System.out.println("Static count: " + staticCount);
        System.out.println("Non-static total: " + nonStaticTotal);
        System.out.println("Absolute difference: " + difference);

        if (Double.isNaN(percentage)) {
            System.out.println("Percentage difference: Undefined");
        } else {
            System.out.printf(
                    "Percentage difference: %.2f%%%n", percentage);
        }
    }
}
