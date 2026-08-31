package com.tkfc.welus.interceptor.checker;

import com.tkfc.core.function.SerializableBiFunction;
import lombok.RequiredArgsConstructor;

/**
 * 权限过滤器
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-08-28 16:45:29
 */
@RequiredArgsConstructor
public class PermissionChecker {

    private final SerializableBiFunction<String, Boolean, Boolean> consumer;

    /**
     * 检查权限逻辑
     *
     * @param premTags 权限标签
     * @return bool
     */
    public Boolean checkPrem(String premTags, Boolean needLogin) {
        return consumer.apply(premTags, needLogin);
    }

}
