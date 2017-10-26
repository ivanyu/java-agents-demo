package me.ivanyu.javaagentsdemo;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

public class ClassListingTransformer implements ClassFileTransformer {
    @Override
    public byte[] transform(final ClassLoader loader,
                            final String className,
                            final Class<?> classBeingRedefined,
                            final ProtectionDomain protectionDomain,
                            final byte[] classfileBuffer) {
        System.out.println(className);

        // null means "use the bytecode without modifications".
        return null;
    }
}