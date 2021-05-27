package org.junit.runner;

import org.junit.runner.notification.RunNotifier;

/**
 * A <code>Runner</code> runs tests and notifies a {@link org.junit.runner.notification.RunNotifier}
 * of significant events as it does so. You will need to subclass <code>Runner</code>
 * when using {@link org.junit.runner.RunWith} to invoke a custom runner. When creating
 * a custom runner, in addition to implementing the abstract methods here you must
 * also provide a constructor that takes as an argument the {@link Class} containing
 * the tests.
 *
 * <p>The default runner implementation guarantees that the instances of the test case
 * class will be constructed immediately before running the test and that the runner
 * will retain no reference to the test case instances, generally making them
 * available for garbage collection.
 *
 * @see org.junit.runner.Description
 * @see org.junit.runner.RunWith
 * @since 4.0
 */

/*
Runner运行测试，并在运行时将重要事件通知RunNotifier。
使用@RunWith调用自定义runner时，需要继承Runner。
创建自定义runner时，除了在此处实现抽象方法外，还必须提供一个构造函数，该构造函数将包含测试的Class作为参数。

默认的运行器实现保证在运行测试之前立即构造测试用例类的实例，并且运行器将不保留对测试用例实例的引用，通常使它们可用于垃圾回收。
 */
public abstract class Runner implements Describable {
    /*
     * (non-Javadoc)
     * @see org.junit.runner.Describable#getDescription()
     */
    public abstract Description getDescription();

    /**
     * Run the tests for this runner.
     *
     * @param notifier will be notified of events while tests are being run--tests being
     * started, finishing, and failing
     */
    public abstract void run(RunNotifier notifier);

    /**
     * @return the number of tests to be run by the receiver
     */
    public int testCount() {
        return getDescription().testCount();
    }
}
