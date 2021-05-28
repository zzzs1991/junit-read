package org.junit.internal.builders;

import java.util.Arrays;
import java.util.List;

import org.junit.runner.Runner;
import org.junit.runners.model.RunnerBuilder;

/*
所有的默认的可能的runnerBuilder
 */
public class AllDefaultPossibilitiesBuilder extends RunnerBuilder {
    private final boolean canUseSuiteMethod;

    /**
     * @since 4.13
     */
    public AllDefaultPossibilitiesBuilder() {
        canUseSuiteMethod = true;
    }

    /**
     * @deprecated used {@link #AllDefaultPossibilitiesBuilder()}.
     */
    @Deprecated
    public AllDefaultPossibilitiesBuilder(boolean canUseSuiteMethod) {
        this.canUseSuiteMethod = canUseSuiteMethod;
    }

    @Override
    public Runner runnerForClass(Class<?> testClass) throws Throwable {
        // 初始化runnerBuilder列表
        // 包含junit中所有的runnerBuilder
        List<RunnerBuilder> builders = Arrays.asList(
                // 看是否有@Ignore
                ignoredBuilder(),
                // 处理@Runwith
                annotatedBuilder(),
                // 处理suite()
                suiteMethodBuilder(),
                // junit3
                junit3Builder(),
                // junit4
                junit4Builder());
        // 责任链模式
        for (RunnerBuilder each : builders) {
            Runner runner = each.safeRunnerForClass(testClass);
            if (runner != null) {
                return runner;
            }
        }
        return null;
    }

    protected JUnit4Builder junit4Builder() {
        return new JUnit4Builder();
    }

    protected JUnit3Builder junit3Builder() {
        return new JUnit3Builder();
    }

    protected AnnotatedBuilder annotatedBuilder() {
        return new AnnotatedBuilder(this);
    }

    protected IgnoredBuilder ignoredBuilder() {
        return new IgnoredBuilder();
    }

    protected RunnerBuilder suiteMethodBuilder() {
        if (canUseSuiteMethod) {
            return new SuiteMethodBuilder();
        }
        return new NullBuilder();
    }
}