package org.junit.runner;

import java.util.Comparator;

import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.internal.requests.ClassRequest;
import org.junit.internal.requests.FilterRequest;
import org.junit.internal.requests.OrderingRequest;
import org.junit.internal.requests.SortingRequest;
import org.junit.internal.runners.ErrorReportingRunner;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.Ordering;
import org.junit.runners.model.InitializationError;

/**
 * A <code>Request</code> is an abstract description of tests to be run. Older versions of
 * JUnit did not need such a concept--tests to be run were described either by classes containing
 * tests or a tree of {@link  org.junit.Test}s. However, we want to support filtering and sorting,
 * so we need a more abstract specification than the tests themselves and a richer
 * specification than just the classes.
 *
 * <p>The flow when JUnit runs tests is that a <code>Request</code> specifies some tests to be run -&gt;
 * a {@link org.junit.runner.Runner} is created for each class implied by the <code>Request</code> -&gt;
 * the {@link org.junit.runner.Runner} returns a detailed {@link org.junit.runner.Description}
 * which is a tree structure of the tests to be run.
 *
 * @since 4.0
 */
/*
Request是要运行的测试的抽象描述。
较旧的JUnit版本不需要这样的概念-要运行的测试是通过包含测试的类或@Test树来描述的。
但是，我们希望支持过滤和排序，因此我们需要比测试本身更抽象以及比类更丰富的概念。

JUnit运行测试时的流程是，request指定一些要运行的测试
                        -> 为request所隐含的每个类创建一个runner
                        -> runner返回详细的description，该description是要运行的测试的树形结构。
 */
public abstract class Request {
    /**
     * Create a <code>Request</code> that, when processed, will run a single test.
     * This is done by filtering out all other tests. This method is used to support rerunning
     * single tests.
     *
     * @param clazz the class of the test
     * @param methodName the name of the test
     * @return a <code>Request</code> that will cause a single test be run
     */
    /*
        创建一个Request，该Request在处理后将运行一个测试。
        这是通过过滤掉所有其他测试来完成的。
        此方法用于支持重新运行单个测试。
     */
    public static Request method(Class<?> clazz, String methodName) {
        Description method = Description.createTestDescription(clazz, methodName);
        return Request.aClass(clazz).filterWith(method);
    }

    /**
     * Create a <code>Request</code> that, when processed, will run all the tests
     * in a class. The odd name is necessary because <code>class</code> is a reserved word.
     *
     * @param clazz the class containing the tests
     * @return a <code>Request</code> that will cause all tests in the class to be run
     */
    /*
        创建一个Request，该Request在处理后将运行一个类中的所有测试。
        奇怪的名称是必需的，因为class是保留字。
     */
    public static Request aClass(Class<?> clazz) {
        return new ClassRequest(clazz);
    }

    /**
     * Create a <code>Request</code> that, when processed, will run all the tests
     * in a class. If the class has a suite() method, it will be ignored.
     *
     * @param clazz the class containing the tests
     * @return a <code>Request</code> that will cause all tests in the class to be run
     */
    /*
        创建一个Request，该Request在处理后将运行一个类中的所有测试。
        类中的suite()会被忽略
     */
    public static Request classWithoutSuiteMethod(Class<?> clazz) {
        return new ClassRequest(clazz, false);
    }

    /**
     * Create a <code>Request</code> that, when processed, will run all the tests
     * in a set of classes.
     *
     * @param computer Helps construct Runners from classes
     * @param classes the classes containing the tests
     * @return a <code>Request</code> that will cause all tests in the classes to be run
     */
    /*
        创建一个Request，该Request在处理后将运行一组类中的所有测试。
        Computer协助构建Runners
     */
    public static Request classes(Computer computer, Class<?>... classes) {
        try {
            // 创建runnerBuilder
            AllDefaultPossibilitiesBuilder builder = new AllDefaultPossibilitiesBuilder();
            // 计算suite suite 是 Runner的实现
            Runner suite = computer.getSuite(builder, classes);
            // 将runner包装成request并返回
            return runner(suite);
        } catch (InitializationError e) {
            return runner(new ErrorReportingRunner(e, classes));
        }
    }

    /**
     * Create a <code>Request</code> that, when processed, will run all the tests
     * in a set of classes with the default <code>Computer</code>.
     *
     * @param classes the classes containing the tests
     * @return a <code>Request</code> that will cause all tests in the classes to be run
     */
    /*
        创建一个Request，该Request在处理后将运行一组类中的所有测试。
        选择默认Computer
     */
    public static Request classes(Class<?>... classes) {
        return classes(JUnitCore.defaultComputer(), classes);
    }


    /**
     * Creates a {@link Request} that, when processed, will report an error for the given
     * test class with the given cause.
     */
    /*
        创建一个Request，该Request在处理后将报告给定原因的给定测试类的错误。
     */
    public static Request errorReport(Class<?> klass, Throwable cause) {
        return runner(new ErrorReportingRunner(klass, cause));
    }

    /**
     * @param runner the runner to return
     * @return a <code>Request</code> that will run the given runner when invoked
     */
    public static Request runner(final Runner runner) {
        return new Request() {
            @Override
            public Runner getRunner() {
                return runner;
            }
        };
    }

    /**
     * Returns a {@link Runner} for this Request
     *
     * @return corresponding {@link Runner} for this Request
     */
    public abstract Runner getRunner();

    /**
     * Returns a Request that only contains those tests that should run when
     * <code>filter</code> is applied
     *
     * @param filter The {@link Filter} to apply to this Request
     * @return the filtered Request
     */
    /*
        用过滤器过滤
     */
    public Request filterWith(Filter filter) {
        return new FilterRequest(this, filter);
    }

    /**
     * Returns a Request that only runs tests whose {@link Description}
     * matches the given description.
     *
     * <p>Returns an empty {@code Request} if {@code desiredDescription} is not a single test and filters all but the single
     * test if {@code desiredDescription} is a single test.</p>
     *
     * @param desiredDescription {@code Description} of those tests that should be run
     * @return the filtered Request
     */
    /*
        根据给定的Description来匹配过滤.
     */
    public Request filterWith(Description desiredDescription) {
        return filterWith(Filter.matchMethodDescription(desiredDescription));
    }

    /**
     * Returns a Request whose Tests can be run in a certain order, defined by
     * <code>comparator</code>
     * <p>
     * For example, here is code to run a test suite in alphabetical order:
     * <pre>
     * private static Comparator&lt;Description&gt; forward() {
     *   return new Comparator&lt;Description&gt;() {
     *     public int compare(Description o1, Description o2) {
     *       return o1.getDisplayName().compareTo(o2.getDisplayName());
     *     }
     *   };
     * }
     *
     * public static main() {
     *   new JUnitCore().run(Request.aClass(AllTests.class).sortWith(forward()));
     * }
     * </pre>
     *
     * @param comparator definition of the order of the tests in this Request
     * @return a Request with ordered Tests
     */
    /*
        对测试进行排序
     */
    public Request sortWith(Comparator<Description> comparator) {
        return new SortingRequest(this, comparator);
    }

    /**
     * Returns a Request whose Tests can be run in a certain order, defined by
     * <code>ordering</code>
     * <p>
     * For example, here is code to run a test suite in reverse order:
     * <pre>
     * private static Ordering reverse() {
     *   return new Ordering() {
     *     public List&lt;Description&gt; orderItems(Collection&lt;Description&gt; descriptions) {
     *       List&lt;Description&gt; ordered = new ArrayList&lt;&gt;(descriptions);
     *       Collections.reverse(ordered);
     *       return ordered;
     *     }
     *   }
     * }
     *     
     * public static main() {
     *   new JUnitCore().run(Request.aClass(AllTests.class).orderWith(reverse()));
     * }
     * </pre>
     *
     * @return a Request with ordered Tests
     * @since 4.13
     */
    /*
        按给定顺序
     */
    public Request orderWith(Ordering ordering) {
        return new OrderingRequest(this, ordering);
    }
}
