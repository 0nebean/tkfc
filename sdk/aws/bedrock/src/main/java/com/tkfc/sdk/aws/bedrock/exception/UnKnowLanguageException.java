package com.tkfc.sdk.aws.bedrock.exception;

/**
 * 未知语言异常
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-09-03 14:12:37
 */
public class UnKnowLanguageException extends Exception {

    public UnKnowLanguageException(String message, Throwable e) {
        super(message, e);
    }

    public UnKnowLanguageException(String message) {
        super(message);
    }

    public UnKnowLanguageException(Throwable e) {
        super(e);
    }

    public UnKnowLanguageException() {
    }

}
