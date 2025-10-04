package com.tkfc.welus.throwable;

import com.tkfc.core.throwable.RunTimException;
import com.tkfc.core.throwable.base.ErrorCode;

/**
 * 校验异常
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/3/1 14:27
 */
public class ValidException extends RunTimException {

    public ValidException(String errMsg) {
        super(errMsg);
        super.errorCode = 412;
    }

}
