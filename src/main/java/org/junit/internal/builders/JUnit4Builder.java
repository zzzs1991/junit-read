package org.junit.internal.builders;

import org.junit.runner.Runner;
import org.junit.runners.JUnit4;
import org.junit.runners.model.RunnerBuilder;

public class JUnit4Builder extends RunnerBuilder {
    @Override
    public Runner runnerForClass(Class<?> testClass) throws Throwable {
        // 默认返回Junit4 BlockJUnit4ClassRunner的子类
        return new JUnit4(testClass);
    }
}
