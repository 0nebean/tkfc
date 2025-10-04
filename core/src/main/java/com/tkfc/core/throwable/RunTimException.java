package com.tkfc.core.throwable;


import com.tkfc.core.toolkit.StringUtil;

/**
 * 运行时异常
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 14:49
 */
public class RunTimException extends RuntimeException {
    private static final long serialVersionUID = 3761977150343281224L;
    private static final int OTHER = 999999;
    protected Integer errorCode;

    public RunTimException(int errorCode) {
        this.errorCode = OTHER;
        this.errorCode = errorCode;
    }

    public RunTimException(int errorCode, Throwable e) {
        super(e);
        this.errorCode = errorCode;
    }

    public RunTimException(int errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public RunTimException(int errorCode, String message, Throwable e) {
        super(message, e);
        this.errorCode = errorCode;
    }

    public RunTimException(String message, Throwable e) {
        super(message, e);
        this.errorCode = OTHER;
    }

    public RunTimException(String message) {
        super(message);
        this.errorCode = OTHER;
    }

    public RunTimException(Throwable e) {
        super(e);
        this.errorCode = OTHER;
    }

    public RunTimException() {
        this.errorCode = OTHER;
    }

    public int getErrorCode() {
        return this.errorCode;
    }

    @Override
    public String getMessage() {
        return StringUtil.isEmpty(super.getMessage()) ? this.errorCode.toString() : this.errorCode.toString() + ':' + super.getMessage();
    }

    public String getSimpleMessage() {
        return StringUtil.isEmpty(super.getMessage()) ? this.errorCode.toString() : super.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return StringUtil.isEmpty(super.getLocalizedMessage()) ? this.errorCode.toString() : this.errorCode.toString() + ':' + super.getLocalizedMessage();
    }

    @Override
    public void printStackTrace() {
        synchronized (System.err) {
            System.err.println(this.errorCode.toString());
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
