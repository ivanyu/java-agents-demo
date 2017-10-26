package me.ivanyu.javaagentsdemo;

import java.lang.instrument.Instrumentation;

public class MetricsCollectionJavassistAgent {
    public static void premain(final String agentArgs,
                               final Instrumentation inst) throws Exception {
        System.out.printf("Starting %s\n",
                MetricsCollectionJavassistAgent.class.getSimpleName());
        inst.addTransformer(new MetricsCollectionTransformer());
    }
}
