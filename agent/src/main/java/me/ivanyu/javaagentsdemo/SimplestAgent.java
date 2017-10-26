package me.ivanyu.javaagentsdemo;

import java.lang.instrument.Instrumentation;

public class SimplestAgent {
    /**
     * If the agent is attached to a JVM on the start,
     * this method is invoked before {@code main} method is called.
     *
     * @param agentArgs Agent command line arguments.
     * @param inst      An object to access the JVM instrumentation mechanism.
     */
    public static void premain(final String agentArgs,
                               final Instrumentation inst) {
        System.out.println(
                "Hey, look: I'm instrumenting a freshly started JVM!");
    }

    /**
     * If the agent is attached to an already running JVM,
     * this method is invoked.
     *
     * @param agentArgs Agent command line arguments.
     * @param inst      An object to access the JVM instrumentation mechanism.
     */
    public static void agentmain(final String agentArgs,
                                 final Instrumentation inst) {
        System.out.println("Hey, look: I'm instrumenting a running JVM!");
    }
}
