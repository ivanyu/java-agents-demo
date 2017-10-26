package me.ivanyu.javaagentsdemo.client;

import me.ivanyu.javaagentsdemo.common.CollectMetrics;
import me.ivanyu.javaagentsdemo.common.MetricsCollector;

public class ClientMain {
    public static void main(String[] args) throws InterruptedException {
        final Adder adder = new Adder();
        for (int i = 0; i < 10; i++) {
            helloWorld();
            adder.add(1, 30);
        }

        try {
            withException();
        } catch (Exception e) {
            // do nothing
        }

        MetricsCollector.getEntries().forEach((key, entry) -> {
            System.out.printf("%s\t%d calls\t%d ns avg\n",
                    key, entry.getCallCounts(), entry.getAvgDuration());
        });
    }

    @CollectMetrics
    private static void helloWorld() throws InterruptedException {
        System.out.println("Hello world");
        Thread.sleep(124L);
    }

    @CollectMetrics
    private static void withException() throws Exception {
        throw new Exception();
    }

    private static class Adder {
        @CollectMetrics // applicable to constructor as well
        Adder() throws InterruptedException {
            Thread.sleep(1234L);
        }

        @CollectMetrics
        public int add(final int a, final int b) {
            return a + b;
        }
    }
}
