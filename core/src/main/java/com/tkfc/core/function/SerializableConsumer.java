package com.tkfc.core.function;

import java.io.Serializable;
import java.util.function.Consumer;

/**
 * 支持序列化的 Consumer接口
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 14:49
 */
@FunctionalInterface
public interface SerializableConsumer<T> extends Consumer<T>, Serializable {
}
