package com.tkfc.sdk.aws.bedrock.exception;

/**
 * 预期回答之外的异常
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-09-03 14:18:43
 */
public class UnexpectedAiAnswerException extends Exception {

    public UnexpectedAiAnswerException(String message, Throwable e) {
        super(message, e);
    }

    public UnexpectedAiAnswerException(String message) {
        super(message);
    }

    public UnexpectedAiAnswerException(Throwable e) {
        super(e);
    }

    public UnexpectedAiAnswerException() {
    }
}
