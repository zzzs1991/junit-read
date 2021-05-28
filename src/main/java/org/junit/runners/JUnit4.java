package org.junit.runners;

import org.junit.runners.model.InitializationError;
import org.junit.runners.model.TestClass;

/**
 * Aliases the current default JUnit 4 class runner, for future-proofing. If
 * future versions of JUnit change the default Runner class, they will also
 * change the definition of this class. Developers wanting to explicitly tag a
 * class as a JUnit 4 class should use {@code @RunWith(JUnit4.class)}, not,
 * for example in JUnit 4.5, {@code @RunWith(BlockJUnit4ClassRunner.class)}.
 * This is the only way this class should be used--any extension that
 * depends on the implementation details of this class is likely to break
 * in future versions.
 *
 * @since 4.5
 */
/*
当前默认的JUnit 4类运行器的别名，以供将来使用。
如果将来的JUnit版本更改默认的Runner类，则它们还将更改该类的定义。
希望将一个类明确标记为JUnit 4类的开发人员应使用@RunWith（JUnit4.class），而不是使用@RunWith（BlockJUnit4ClassRunner.class）
例如，在JUnit 4.5中。 这是使用此类的唯一方法-依赖于此类的实现细节的任何扩展都可能在将来的版本中破坏。
 */
public final class JUnit4 extends BlockJUnit4ClassRunner {
    /**
     * Constructs a new instance of the default runner
     */
    public JUnit4(Class<?> klass) throws InitializationError {
        super(new TestClass(klass));
    }
}
