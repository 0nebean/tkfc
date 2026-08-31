package com.tkfc.welus.interceptor.checker;

import com.tkfc.core.function.SerializableFunction;
import lombok.RequiredArgsConstructor;

/**
 * 权限过滤器
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-08-28 16:45:29
 */
@RequiredArgsConstructor
public class OssAccessUrlConverter {

    private final SerializableFunction<String, String> consumer;

    /**
     * 转换访问链接
     *
     * @param before 权限标签
     * @return bool
     */
    public String convertAccessUrl(String before) {
        return consumer.apply(before);
    }

}
