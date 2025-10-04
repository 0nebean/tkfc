package com.tkfc.core.function;

import java.io.Serializable;
import java.util.function.BiFunction;

/**
 * 支持序列化的 BiFunction接口
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 14:49
 */
@FunctionalInterface
public interface SerializableBiFunction<T, U, R> extends BiFunction<T, U, R>, Serializable {
}