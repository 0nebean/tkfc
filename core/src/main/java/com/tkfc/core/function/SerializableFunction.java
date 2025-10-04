package com.tkfc.core.function;

import java.io.Serializable;
import java.util.function.Function;

/**
 * 支持序列化的 Function接口
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 14:49
 */
@FunctionalInterface
public interface SerializableFunction<T, R> extends Function<T, R>, Serializable {
}