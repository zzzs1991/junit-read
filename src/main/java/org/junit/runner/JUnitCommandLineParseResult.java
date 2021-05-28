package org.junit.runner;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.internal.Classes;
import org.junit.runner.FilterFactory.FilterNotCreatedException;
import org.junit.runner.manipulation.Filter;
import org.junit.runners.model.InitializationError;

/*
junit命令行解析结果
 */
class JUnitCommandLineParseResult {
    // 解析出来的过滤器参数
    private final List<String> filterSpecs = new ArrayList<String>();
    // 解析出来的类
    private final List<Class<?>> classes = new ArrayList<Class<?>>();
    // 解析时的异常
    private final List<Throwable> parserErrors = new ArrayList<Throwable>();

    /**
     * Do not use. Testing purposes only.
     */
    JUnitCommandLineParseResult() {}

    /**
     * Returns filter specs parsed from command line.
     */
    public List<String> getFilterSpecs() {
        return Collections.unmodifiableList(filterSpecs);
    }

    /**
     * Returns test classes parsed from command line.
     */
    public List<Class<?>> getClasses() {
        return Collections.unmodifiableList(classes);
    }

    /**
     * Parses the arguments.
     *
     * @param args Arguments
     */
    /*
        解析参数
     */
    public static JUnitCommandLineParseResult parse(String[] args) {
        JUnitCommandLineParseResult result = new JUnitCommandLineParseResult();

        result.parseArgs(args);

        return result;
    }

    private void parseArgs(String[] args) {
        // 先解析选项 再解析参数
        // 选项就是 --filter
        // 参数就是 指定的测试类
        parseParameters(parseOptions(args));
    }

    String[] parseOptions(String... args) {
        for (int i = 0; i != args.length; ++i) {
            String arg = args[i];

            if (arg.equals("--")) {
                return copyArray(args, i + 1, args.length);
            } else if (arg.startsWith("--")) {
                if (arg.startsWith("--filter=") || arg.equals("--filter")) {
                    String filterSpec;
                    if (arg.equals("--filter")) {
                        ++i;

                        if (i < args.length) {
                            filterSpec = args[i];
                        } else {
                            parserErrors.add(new CommandLineParserError(arg + " value not specified"));
                            break;
                        }
                    } else {
                        filterSpec = arg.substring(arg.indexOf('=') + 1);
                    }

                    filterSpecs.add(filterSpec);
                } else {
                    parserErrors.add(new CommandLineParserError("JUnit knows nothing about the " + arg + " option"));
                }
            } else {
                return copyArray(args, i, args.length);
            }
        }

        return new String[]{};
    }

    private String[] copyArray(String[] args, int from, int to) {
        String[] result = new String[to - from];
        for (int j = from; j != to; ++j) {
            result[j - from] = args[j];
        }
        return result;
    }

    void parseParameters(String[] args) {
        for (String arg : args) {
            try {
                classes.add(Classes.getClass(arg));
            } catch (ClassNotFoundException e) {
                parserErrors.add(new IllegalArgumentException("Could not find class [" + arg + "]", e));
            }
        }
    }

    private Request errorReport(Throwable cause) {
        return Request.errorReport(JUnitCommandLineParseResult.class, cause);
    }

    /**
     * Creates a {@link Request}.
     *
     * @param computer {@link Computer} to be used.
     */
    /*
        通过传入的computer来创建request
     */
    public Request createRequest(Computer computer) {
        // 参数解析失败的集合如果为空进入if块
        // parserErrors是runMain方法中JUnitCommandLineParseResult.parse()获得
        if (parserErrors.isEmpty()) {
            // 创建request
            // 传入computer策略, 和 参数中解析来的 测试类...
            Request request = Request.classes(
                    computer, classes.toArray(new Class<?>[classes.size()]));
            // 添加过滤功能
            return applyFilterSpecs(request);
        } else {
            // 报告错误
            return errorReport(new InitializationError(parserErrors));
        }
    }

    private Request applyFilterSpecs(Request request) {
        try {
            for (String filterSpec : filterSpecs) {
                Filter filter = FilterFactories.createFilterFromFilterSpec(
                        request, filterSpec);
                request = request.filterWith(filter);
            }
            return request;
        } catch (FilterNotCreatedException e) {
            return errorReport(e);
        }
    }

    /**
     * Exception used if there's a problem parsing the command line.
     */
    public static class CommandLineParserError extends Exception {
        private static final long serialVersionUID= 1L;

        public CommandLineParserError(String message) {
            super(message);
        }
    }
}
