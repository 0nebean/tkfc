package com.tkfc.core.function;

import java.io.Serializable;
import java.util.Objects;

/**
 * 支持序列化的 Closure接口
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 14:49
 */
@FunctionalInterface
public interface SerializableClosure extends Serializable {

    /**
     * Performs this operation on the given argument.
     */
    void accept();

    default SerializableClosure andThen(SerializableClosure after) {
        Objects.requireNonNull(after);
        return () -> {
            accept();
            after.accept();
        };
    }
}
