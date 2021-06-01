package org.junit.runners.model;


/**
 * Represents one or more actions to be taken at runtime in the course
 * of running a JUnit test suite.
 *
 * @since 4.5
 */
/*
表示在运行JUnit测试套件的过程中要在运行时执行的一个或多个操作。
 */
public abstract class Statement {
    /**
     * Run the action, throwing a {@code Throwable} if anything goes wrong.
     */
    public abstract void evaluate() throws Throwable;
}