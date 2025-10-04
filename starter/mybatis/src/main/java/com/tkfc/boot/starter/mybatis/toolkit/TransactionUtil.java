package com.tkfc.boot.starter.mybatis.toolkit;

import com.tkfc.core.function.SerializableClosure;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * 异步执行类
 *
 * @author 0neBean
 * @since 2022-08-17 12:03:45
 */
@Slf4j
public class TransactionUtil {

    /**
     * 事务提交后执行
     * 如果当前没有事务，则直接执行回调函数
     * 如果有事务，则在事务提交后执行回调函数
     *
     * @param callBack 执行的代码
     */
    public static void execAfterCommit(SerializableClosure callBack) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            log.debug("No active transaction, executing callback directly");
            callBack.accept();
            return;
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                try {
                    callBack.accept();
                } catch (Exception e) {
                    log.error("Error executing callback after transaction commit", e);
                    throw e;
                }
            }

            @Override
            public void suspend() {
                // 不需要实现
            }

            @Override
            public void resume() {
                // 不需要实现
            }

            @Override
            public void flush() {
                // 不需要实现
            }

            @Override
            public void beforeCommit(boolean readOnly) {
                // 不需要实现
            }

            @Override
            public void beforeCompletion() {
                // 不需要实现
            }

            @Override
            public void afterCompletion(int status) {
                // 不需要实现
            }
        });
    }

}
