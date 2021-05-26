// SPDX-License-Identifier: MIT

package com.teimour;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Collects method return values by invoking methods in classes that implement {@link Accumulate} interface - Repository classes.
 * If you have multiple repository classes, you should use {@code collectMultipleRepositories()} static factory method.
 * If you have just one Repository class, it's better to use {@code collectSingleRepository()} static factory method,
 * for performance sake.<br>
 * Warning: This approach should just be used on situations where you confident that all requested methods are no-args.
 * If you ignore this warning, {@code IllegalArgumentException} will be thrown.
 * @param <T> the type that should be accumulate.
 * @see Accumulate
 */
public class Accumulators<T> {

    private final List<T> objects;

    private Accumulators(Class<T> wantedClass, Accumulate... repositoryClasses) {
        this.objects = addAccumulate(wantedClass, repositoryClasses);
    }

    /**
     * Creates {@code Accumulators} instance with <b>one</b> repository class.
     * You can use another static factory method.
     * Although because of performance concerns it's preferable to use this one.
     * @param wantedClass class that should be collected from repository class.
     * @param repositoryClass class that implements {@code Accumulate} and contains requested return values;
     * @param <T> generic object type that should be collected from repository class.
     * @return instance of Accumulators
     */
    public static <T> Accumulators<T> collectSingleRepository(Class<T> wantedClass, Accumulate repositoryClass) {
        return new Accumulators<>(wantedClass, repositoryClass);
    }

    /**
     * Creates {@code Accumulators} instance with <b>multiple</b> repository classes.
     * You can use this static method to create mono repository class {@code Accumulators}
     * but it's preferable to use the other one.
     * @param wantedClass class that should be collected from repository classes.
     * @param accumulate first class that implements {@code Accumulate} and contains requested return values.
     * @param accumulates another classes that implement {@code Accumulate} and contain requested return values.
     * @param <T> generic object type that should be collected from repository class.
     * @return instance of Accumulators
     */
    public static <T> Accumulators<T> collectMultiRepositories(Class<T> wantedClass,
                                                               Accumulate accumulate,
                                                               Accumulate... accumulates) {
        Accumulate[] newAccumulates = new Accumulate[accumulates.length + 1];
        System.arraycopy(accumulates, 0, newAccumulates, 1, accumulates.length);
        newAccumulates[0] = accumulate;
        return new Accumulators<>(wantedClass, newAccumulates);
    }

    private List<T> addAccumulate(Class<T> wantedClass, Accumulate... repositoryClasses) {
        return Arrays.stream(repositoryClasses)
                .flatMap(repo ->
                        Arrays
                                .stream(repo.getClass().getMethods())
                                .filter(checkReturnType(wantedClass))
                                .map(returnRequestedClass(repo))
                ).collect(Collectors.toList());
    }

    private Predicate<Method> checkReturnType(Class<?> clazz) {
        return method -> clazz.isAssignableFrom(method.getReturnType());
    }

    @SuppressWarnings("unchecked")
    private Function<? super Method, T> returnRequestedClass(Object obj) {
        return method -> {
            try {
                return (T) method.invoke(obj);
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };
    }

    /**
     * Getter for requested accumulate objects.
     * @return List of the type that should be accumulated.
     */
    public List<T> getObjects() {
        return new ArrayList<>(objects);
    }
}
