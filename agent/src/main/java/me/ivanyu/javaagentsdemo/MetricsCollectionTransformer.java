package me.ivanyu.javaagentsdemo;

import javassist.*;
import javassist.bytecode.AnnotationsAttribute;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.annotation.Annotation;
import me.ivanyu.javaagentsdemo.common.CollectMetrics;
import me.ivanyu.javaagentsdemo.common.MetricsCollector;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class MetricsCollectionTransformer implements ClassFileTransformer {

    private final ClassPool classPool = ClassPool.getDefault();

    @Override
    public byte[] transform(final ClassLoader loader,
                            final String className,
                            final Class<?> classBeingRedefined,
                            final ProtectionDomain protectionDomain,
                            final byte[] classfileBuffer)
            throws IllegalClassFormatException {

        // className can be null, ignoring such classes.
        if (className == null) {
            return null;
        }

        // Javassist uses "." as a separator in class/package names.
        final String classNameDots = className.replaceAll("/", ".");
        final CtClass ctClass = classPool.getOrNull(classNameDots);

        // Won't find some classes from java.lang.invoke,
        // but we're not interested in them anyway.
        if (ctClass == null) {
            return null;
        }

        // A frozen CtClass is a CtClass
        // that was already converted to Java class.
        if (ctClass.isFrozen()) {
            // No longer need to keep the CtClass object in memory.
            ctClass.detach();
            return null;
        }

        try {
            boolean anyMethodInstrumented = false;

            // Behaviors == methods and constructors.
            for (final CtBehavior behavior : ctClass.getDeclaredBehaviors()) {
                if (isAnnotatedAsCollectMetrics(behavior)) {
                    System.out.printf("%s - will collect metrics\n",
                            behavior.getLongName());
                    instrument(behavior);
                    anyMethodInstrumented = true;
                }
            }

            if (anyMethodInstrumented) {
                return ctClass.toBytecode();
            }
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            // No longer need to keep the CtClass object in memory.
            ctClass.detach();
        }

        return null;
    }

    /**
     * Checks if the behavior is annotated with @CollectMetrics.
     */
    private boolean isAnnotatedAsCollectMetrics(final CtBehavior behavior) {
        final MethodInfo methodInfo = behavior.getMethodInfo();

        for (final Object attrInfo : methodInfo.getAttributes()) {
            if (attrInfo instanceof AnnotationsAttribute) {
                final Annotation annotation = ((AnnotationsAttribute) attrInfo)
                        .getAnnotation(CollectMetrics.class.getName());
                return annotation != null;
            }
        }

        return false;
    }

    /**
     * Instruments the behavior with metric reporting code.
     */
    private void instrument(final CtBehavior behavior)
            throws CannotCompileException, NotFoundException {

        behavior.addLocalVariable("$_traceTimeStart", CtClass.longType);
        behavior.insertBefore("$_traceTimeStart = System.nanoTime();");

        // Add reporting of the call, e.g.:
        // MetricsCollector.report("<full method name>", 1256);
        // Won't work in case of exception.
        final String reportCode = MetricsCollector.class.getName() +
                ".report(" +
                "\"" + behavior.getLongName() + "\", " +
                "System.nanoTime() - $_traceTimeStart" +
                ");";
        behavior.insertAfter(reportCode);
    }
}
