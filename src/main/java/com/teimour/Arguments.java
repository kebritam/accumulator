// SPDX-License-Identifier: MIT

package com.teimour;

import java.util.LinkedList;
import java.util.List;

/**
 * A collection of arguments that methods in repository classes accept.<br>
 * Hence methods should have same arguments in a same order, arguments in builder should
 * be added in method arguments order with {@code addArgument()} method.
 */
public class Arguments {

    private final List<Object> arguments;

    /**
     * Constructs {@code Arguments} object with its builder.
     * @param builder {@link ArgumentsBuilder} builder class.
     */
    public Arguments(ArgumentsBuilder builder) {
        this.arguments = builder.arguments;
    }

    /**
     * Builder for building {@link Arguments} object.
     */
    public static class ArgumentsBuilder {
        private final List<Object> arguments = new LinkedList<>();

        /**
         * Adds object value that you want to pass to methods, in arguments collection.
         * @param argument object value to be added in argument collection.
         * @return this builder object.
         */
        public ArgumentsBuilder addArgument(Object argument) {
            arguments.add(argument);
            return this;
        }

        /**
         * Ends adding arguments and builds the {@link Arguments} object.
         * @return {@link Arguments} object.
         */
        public Arguments build() {
            return new Arguments(this);
        }
    }

    /**
     * Getter for argument objects.
     * @return array of values in method argument.
     */
    public Object[] getTypeArray() {
         return arguments.toArray();
    }
}
