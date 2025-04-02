package com.nexoscript.nexonet.api.utils;

import java.util.Objects;

@FunctionalInterface
public interface BiConsumer<T, K> {
    void accept(T t, K k);

    default BiConsumer<T, K> andThen(BiConsumer<? super T, K> after) {
        Objects.requireNonNull(after);
        return (T t, K k) -> { accept(t, k); after.accept(t, k); };
    }
}
