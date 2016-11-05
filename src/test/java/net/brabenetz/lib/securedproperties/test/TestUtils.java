/*-
 * #%L
 * Secured Properties
 * ===============================================================
 * Copyright (C) 2016 Brabenetz Harald, Austria
 * ===============================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package net.brabenetz.lib.securedproperties.test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;

/**
 * Some utilities to simplify testing.
 */
public final class TestUtils {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TestUtils.class);

    private TestUtils() {
        super();
    }

    /**
     * @see #expectException(MethodToCall, Class) expectException(MethodToCall, Exception.class).
     * @param call
     *        The Method to test
     * @return The expected Exception
     */
    public static Exception expectException(final MethodToCall call) {
        return expectException(call, Exception.class);
    }

    /**
     * Test the Exception-Case of your Logic.
     * <p>
     * Example Usage:
     * </p>
     *
     * <pre>
     * MyException expectedException = TestUtils.expectException(() -&gt; myService.myMethodToTest(), MyException.class);
     *
     * assertThat(expectedException.getMessage(), containsString("..."));
     * </pre>
     *
     * @param <T>
     *        The type of the expected Exception.
     * @param call
     *        The Method to test
     * @param expectedExceptionType
     *        .
     * @return The expected Exception
     */
    @SuppressWarnings("unchecked")
    public static <T extends Exception> T expectException(final MethodToCall call, final Class<T> expectedExceptionType) {
        try {
            call.run();
            throw new AssertionError("The Method should throw an Exception");
        } catch (final Exception e) {
            if (!expectedExceptionType.isInstance(e)) {
                LOG.info("Wrong Exception: " + e.getMessage(), e);
                throw new AssertionError("Wrong Exception instance: " + e.getClass() + ". Message: " + e.getMessage());
            }
            return (T) e;
        }
    }

    /**
     * Functional Interface for {@link TestUtils#expectException(MethodToCall, Class)}.
     */
    @FunctionalInterface
    public interface MethodToCall {
        void run() throws Exception;
    }

    /**
     * <p>
     * check if the Default constructor is hidden, and call it one time for Testcoverage Report :D .
     * <p>
     * Usage: <code>Assert.assertTrue(ClassUtils.isDefaultConstructorHidden(XyzUtils.class));</code>
     * 
     * @param clazz
     *        The Class to Test.
     * @return true if the constructor is hidden.
     */
    public static boolean isDefaultConstructorHidden(final Class<?> clazz) {
        final Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length > 1) {
            return false;
        }

        final Constructor<?> constructor = constructors[0];
        if (Modifier.isPrivate(constructor.getModifiers())) {
            // call the constructor once for TestCoverage Report.
            constructor.setAccessible(true);
            try {
                constructor.newInstance((Object[]) null);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
            return true;
        } else {
            return false;
        }
    }
}
