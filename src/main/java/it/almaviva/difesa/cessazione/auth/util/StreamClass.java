package it.almaviva.difesa.cessazione.auth.util;


import org.springframework.util.CollectionUtils;

import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class StreamClass {

    private StreamClass() {}

    public static <T> Set<T> getDeltaSetBetweenTwoCollections(Collection<T> minuend, Predicate<T> predicate) {
        return minuend.stream()
                .filter(predicate)
                .collect(Collectors.toSet());
    }

    public static <T> List<T> getDeltaListBetweenTwoCollections(Collection<T> minuend, Predicate<T> predicate) {
        return minuend.stream()
                .filter(predicate).collect(Collectors.toList());
    }

    public static <T> Predicate<T> createDeltaPredicateBetweenTwoCollections(Collection<T> subtrahend) {
        return CollectionUtils.isEmpty(subtrahend) ? value -> true : value -> !subtrahend.contains(value);
    }
}