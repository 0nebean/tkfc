package com.tkfc.welus.interceptor.logger;

import com.tkfc.core.function.SerializableConsumer;
import com.tkfc.welus.pojo.AccessLog;
import lombok.RequiredArgsConstructor;

/**
 * 请求日志记录
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-12-17 16:47:37
 */
@RequiredArgsConstructor
public class AccessLogRecorder {

    private final SerializableConsumer<AccessLog> recodeAccessLog;

    /**
     * 检查权限逻辑
     *
     * @param accessLog 访问日志
     */
    public void recorde(AccessLog accessLog) {
        recodeAccessLog.accept(accessLog);
    }
}
