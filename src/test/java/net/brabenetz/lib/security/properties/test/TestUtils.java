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
package net.brabenetz.lib.security.properties.test;

public class TestUtils {
    private static final org.slf4j.Logger LOG = org.slf4j.LoggerFactory.getLogger(TestUtils.class);

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

    @FunctionalInterface
    public interface MethodToCall {
        public abstract void run() throws Exception;
    }
}
