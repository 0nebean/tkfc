package com.tkfc.core.throwable;

import com.tkfc.core.throwable.base.ErrorCode;
import com.tkfc.core.toolkit.StringUtil;

/**
 * 业务异常
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 14:49
 */
public class BizException extends RunTimException {


    private static final long serialVersionUID = 8499743076873907020L;
    private ErrorCode exceptionType;

    public BizException(ErrorCode exceptionType) {
        this.exceptionType = ErrorCode.OTHER;
        this.exceptionType = exceptionType;
    }

    public BizException(ErrorCode exceptionType, Throwable e) {
        super(e);
        this.exceptionType = ErrorCode.OTHER;
        this.exceptionType = exceptionType;
    }

    public BizException(ErrorCode exceptionType, String message) {
        super(message);
        this.exceptionType = ErrorCode.OTHER;
        this.exceptionType = exceptionType;
    }

    public BizException(int errorCode, String message, Throwable e) {
        super(message, e);
        this.exceptionType = ErrorCode.OTHER;
        this.exceptionType = new ErrorCode(errorCode, message);
    }

    public BizException(int errorCode, String message) {
        super(message);
        this.exceptionType = ErrorCode.OTHER;
        this.exceptionType = new ErrorCode(errorCode, message);
    }

    public BizException(ErrorCode exceptionType, String message, Throwable e) {
        super(message, e);
        this.exceptionType = ErrorCode.OTHER;
        this.exceptionType = exceptionType;
    }

    public BizException(String message, Throwable e) {
        super(message, e);
        this.exceptionType = ErrorCode.OTHER;
    }

    public BizException(String message) {
        super(message);
        this.exceptionType = ErrorCode.OTHER;
    }

    public BizException(Throwable e) {
        super(e);
        this.exceptionType = ErrorCode.OTHER;
    }

    public BizException() {
        this.exceptionType = ErrorCode.OTHER;
    }

    @Override
    public int getErrorCode() {
        return this.exceptionType.getCode();
    }

    @Override
    public String getMessage() {
        return StringUtil.isEmpty(super.getMessage()) ? this.exceptionType.toString() : this.exceptionType.toString() + ':' + super.getMessage();
    }

    @Override
    public String getSimpleMessage() {
        return StringUtil.isEmpty(super.getMessage()) ? this.exceptionType.toString() : super.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return StringUtil.isEmpty(super.getLocalizedMessage()) ? this.exceptionType.toString() : this.exceptionType.toString() + ':' + super.getLocalizedMessage();
    }

    @Override
    public void printStackTrace() {
        synchronized(System.err) {
            System.err.println(this.exceptionType.toString());
        }
        super.printStackTrace();
    }

    @Override
    public String toString() {
        String s = this.getClass().getName();
        String message = this.getMessage();
        return message != null ? s + ": " + message : s;
    }
}
