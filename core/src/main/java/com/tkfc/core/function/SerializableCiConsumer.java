package com.tkfc.core.function;

import java.io.Serializable;
import java.util.Objects;
import java.util.function.BiConsumer;

/**
 * 支持序列化的 BiConsumer接口
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 14:49
 */
@FunctionalInterface
public interface SerializableCiConsumer<T, U, I> extends  Serializable {
    /**
     * accept 方法
     * @author 0neBean
     * @since 2022-01-29 21:42:55
     * @param t 参数1
     * @param u 参数2
     * @param i 参数3
     */
    void accept(T t, U u ,I i);

    /**
     * 链式调用
     * @author 0neBean
     * @since 2022-01-29 21:43:17
     * @param after 调用逻辑
     * @return com.tkfc.core.function.SerializableCiConsumer<T,U,I>
     */
    default SerializableCiConsumer<T, U, I> andThen(SerializableCiConsumer<? super T, ? super U,? super I> after) {
        Objects.requireNonNull(after);
        return (t,u,i) -> {
            accept(t,u,i);
            after.accept(t,u,i);
        };
    }
}
