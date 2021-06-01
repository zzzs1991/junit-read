package org.junit.runner;

import junit.runner.Version;
import org.junit.internal.JUnitSystem;
import org.junit.internal.RealSystem;
import org.junit.internal.TextListener;
import org.junit.internal.runners.JUnit38ClassRunner;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;

/**
 * <code>JUnitCore</code> is a facade for running tests. It supports running JUnit 4 tests,
 * JUnit 3.8.x tests, and mixtures. To run tests from the command line, run
 * <code>java org.junit.runner.JUnitCore TestClass1 TestClass2 ...</code>.
 * For one-shot test runs, use the static method {@link #runClasses(Class[])}.
 * If you want to add special listeners,
 * create an instance of {@link org.junit.runner.JUnitCore} first and use it to run the tests.
 *
 * @see org.junit.runner.Result
 * @see org.junit.runner.notification.RunListener
 * @see org.junit.runner.Request
 * @since 4.0
 */
/*
JUnitCore 是用于运行测试的外观。
它支持运行 JUnit 4 测试、JUnit 3.8.x 测试和混合测试。
要从命令行运行测试，请运行 java org.junit.runner.JUnitCore TestClass1 TestClass2 ....
对于一次性测试运行，请使用静态方法 runClasses(Class[])。
如果要添加特殊监听器，请先创建 JUnitCore 实例并使用它来运行测试。
 */
public class JUnitCore {
    private final RunNotifier notifier = new RunNotifier();

    /**
     * Run the tests contained in the classes named in the <code>args</code>.
     * If all tests run successfully, exit with a status of 0. Otherwise exit with a status of 1.
     * Write feedback while tests are running and write
     * stack traces for all failed tests after the tests all complete.
     *
     * @param args names of classes in which to find tests to run
     */
    /*
    运行 args 中命名的类中包含的测试。
    如果所有测试都成功运行，则以状态 0 退出。否则以状态 1 退出。
    在测试运行时编写反馈
    在测试全部完成后为所有失败的测试编写堆栈跟踪。
     */
    public static void main(String... args) {
        // 命令行入口
        Result result = new JUnitCore().runMain(new RealSystem(), args);
        System.exit(result.wasSuccessful() ? 0 : 1);
    }

    /**
     * Run the tests contained in <code>classes</code>. Write feedback while the tests
     * are running and write stack traces for all failed tests after all tests complete. This is
     * similar to {@link #main(String[])}, but intended to be used programmatically.
     *
     * @param classes Classes in which to find tests
     * @return a {@link Result} describing the details of the test run and the failed tests.
     */
    /*
    运行包含在类中的测试。 在测试运行时编写反馈并在所有测试完成后为所有失败的测试编写堆栈跟踪。
    这类似于 main(String[])，但旨在以编程方式使用。
     */
    public static Result runClasses(Class<?>... classes) {
        return runClasses(defaultComputer(), classes);
    }

    /**
     * Run the tests contained in <code>classes</code>. Write feedback while the tests
     * are running and write stack traces for all failed tests after all tests complete. This is
     * similar to {@link #main(String[])}, but intended to be used programmatically.
     *
     * @param computer Helps construct Runners from classes
     * @param classes  Classes in which to find tests
     * @return a {@link Result} describing the details of the test run and the failed tests.
     */
    /*
    运行包含在类中的测试。 在测试运行时编写反馈并在所有测试完成后为所有失败的测试编写堆栈跟踪。
    这类似于 main(String[])，但旨在以编程方式使用。
     */
    public static Result runClasses(Computer computer, Class<?>... classes) {
        return new JUnitCore().run(computer, classes);
    }

    /**
     * @param system
     * @param args from main()
     */
    Result runMain(JUnitSystem system, String... args) {
        system.out().println("JUnit version " + Version.id());
        // 解析命令行参数
        JUnitCommandLineParseResult jUnitCommandLineParseResult = JUnitCommandLineParseResult.parse(args);
        // 创建命令行界面监听器
        RunListener listener = new TextListener(system);
        addListener(listener);
        // 创建请求 运行并返回结果
        return run(jUnitCommandLineParseResult.createRequest(defaultComputer()));
    }

    /**
     * @return the version number of this release
     */
    public String getVersion() {
        return Version.id();
    }

    /**
     * Run all the tests in <code>classes</code>.
     *
     * @param classes the classes containing tests
     * @return a {@link Result} describing the details of the test run and the failed tests.
     */
    /*
    在类中运行所有测试。

    classes - 包含测试的类
    Result - 描述测试运行和失败测试的详细信息的结果
     */
    public Result run(Class<?>... classes) {
        return run(defaultComputer(), classes);
    }

    /**
     * Run all the tests in <code>classes</code>.
     *
     * @param computer Helps construct Runners from classes
     * @param classes the classes containing tests
     * @return a {@link Result} describing the details of the test run and the failed tests.
     */
    /*
    在类中运行所有测试。

    computer - 帮助从class中构建runners
     */
    public Result run(Computer computer, Class<?>... classes) {
        return run(Request.classes(computer, classes));
    }

    /**
     * Run all the tests contained in <code>request</code>.
     *
     * @param request the request describing tests
     * @return a {@link Result} describing the details of the test run and the failed tests.
     */
    /*
    运行请求中包含的所有测试。

    request – 描述测试的请求
     */
    public Result run(Request request) {
        // 从Request中获取Runner并调用run方法
        return run(request.getRunner());
    }

    /**
     * Run all the tests contained in JUnit 3.8.x <code>test</code>. Here for backward compatibility.
     *
     * @param test the old-style test
     * @return a {@link Result} describing the details of the test run and the failed tests.
     */
    /*
    运行 JUnit 3.8.x 测试中包含的所有测试。 这里是为了向后兼容。

    junit.framework.Test – 旧式测试
     */
    public Result run(junit.framework.Test test) {
        return run(new JUnit38ClassRunner(test));
    }

    /**
     * Do not use. Testing purposes only.
     */
    /*
    不使用。 仅用于测试目的。 ???
     */
    public Result run(Runner runner) {
        // 构建运行结果
        Result result = new Result();
        // 创建运行结果的监听器
        RunListener listener = result.createListener();
        notifier.addFirstListener(listener);
        try {
            // 通知监听器开始运行测试
            notifier.fireTestRunStarted(runner.getDescription());
            // 开始运行
            runner.run(notifier);
            // 通知监听器测试运行结束
            notifier.fireTestRunFinished(result);
        } finally {
            removeListener(listener);
        }
        return result;
    }

    /**
     * Add a listener to be notified as the tests run.
     *
     * @param listener the listener to add
     * @see org.junit.runner.notification.RunListener
     */
    /*
    添加一个监听器以在测试运行时收到通知。
     */
    public void addListener(RunListener listener) {
        notifier.addListener(listener);
    }

    /**
     * Remove a listener.
     *
     * @param listener the listener to remove
     */
    /*
    移除一个监听器
     */
    public void removeListener(RunListener listener) {
        notifier.removeListener(listener);
    }

    /*
    默认的computer
     */
    static Computer defaultComputer() {
        return new Computer();
    }
}
