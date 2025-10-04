package com.tkfc.core.throwable;

import com.tkfc.core.throwable.base.ErrorCode;
import com.tkfc.core.toolkit.WebUtil;

/**
 * 断言异常
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 14:49
 */
public class AssertFailException extends RunTimException {

    private final static String LAST_ERROR_MESSAGE_KEY = "LAST_ERROR_MESSAGE";

    private static final long serialVersionUID = 1174360235354917591L;

    public AssertFailException() {
        super(ErrorCode.ASSERT_FAIL.getCode());
    }

    public AssertFailException(String message) {
        super(ErrorCode.ASSERT_FAIL.getCode(), message);
        try {
            WebUtil.setSessionAttribute(LAST_ERROR_MESSAGE_KEY, message);
        } catch (Exception ignored) {
        }
    }

    public AssertFailException(Throwable e) {
        super(ErrorCode.ASSERT_FAIL.getCode(), e);
    }

    public AssertFailException(String message, Throwable e) {
        super(ErrorCode.ASSERT_FAIL.getCode(), message, e);
        try {
            WebUtil.setSessionAttribute(LAST_ERROR_MESSAGE_KEY, message);
        } catch (Exception ignored) {
        }
    }
}
