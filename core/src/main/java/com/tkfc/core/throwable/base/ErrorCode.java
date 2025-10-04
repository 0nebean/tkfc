package com.tkfc.core.throwable.base;

import java.io.Serializable;

/**
 * 错误码
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 14:49
 */
public class ErrorCode implements Serializable {


    private static final long serialVersionUID = -8628402641467151706L;

    public static final ErrorCode OTHER = new ErrorCode(-1, "其他");
    public static final ErrorCode OK = new ErrorCode(200, "OK");
    public static final ErrorCode DATEFORMAT_UNSUPPORT = new ErrorCode(5, "时间/日期格式不支持");
    public static final ErrorCode KEY_FAIL = new ErrorCode(10, "获取密钥失败");
    public static final ErrorCode ENCRYPT_FAIL = new ErrorCode(11, "加密算法执行失败");
    public static final ErrorCode REFLECTION_CALL_METHOD_FAIL = new ErrorCode(12, "反射调用方法失败");
    public static final ErrorCode DEENCRYPT_FAIL = new ErrorCode(15, "解密算法执行失败");
    public static final ErrorCode SIGNATURE_FAIL = new ErrorCode(20, "签名验证失败");
    public static final ErrorCode BEAN_CREATE_FAIL = new ErrorCode(40, "创建bean实例错误");
    public static final ErrorCode IO_FAIL = new ErrorCode(70, "IO异常");
    public static final ErrorCode NETWORK_EXCEPTION = new ErrorCode(100, "网络连接异常");
    public static final ErrorCode JSCH_ERROR = new ErrorCode(101, "JSCH操作失败");
    public static final ErrorCode SERIALIZE_EXCEPTION = new ErrorCode(120, "JSON序列化错误");
    public static final ErrorCode CLONE_EXCEPTION = new ErrorCode(125, "对象克隆错误");
    public static final ErrorCode LOGGIN_DENINED = new ErrorCode(400, "登录失败");
    public static final ErrorCode ACCESS_DENINED = new ErrorCode(401, "未授权的访问");
    public static final ErrorCode ASSERT_FAIL = new ErrorCode(410, "数据验证失败");
    public static final ErrorCode INVALID_PARAM = new ErrorCode(412, "参数不合法");
    public static final ErrorCode NO_BIND_USER = new ErrorCode(413, "无关联用户");
    public static final ErrorCode SMS_VALICODE_ERROR = new ErrorCode(420, "短信验证码错误");
    public static final ErrorCode INTERNAL_ERROR = new ErrorCode(500, "服务端内部错误");
    public static final ErrorCode TOKEN_EXPIRED = new ErrorCode(601, "Token已过期");
    public static final ErrorCode TOKEN_INVALID = new ErrorCode(602, "无效的Token");
    public static final ErrorCode SECRET_EXPIRED = new ErrorCode(651, "密钥已过期");
    public static final ErrorCode SECRET_INVALID = new ErrorCode(653, "无效的密钥");
    public static final ErrorCode SESSION_INVALID = new ErrorCode(700, "会话已经失效");
    public static final ErrorCode SESSION_EXPIRED = new ErrorCode(702, "会话已经过期");
    private final int code;
    private final String desc;

    public ErrorCode(int value, String desc) {
        this.code = value;
        this.desc = desc;
    }

    public static ErrorCode with(int value, String desc) {
        return new ErrorCode(value, desc);
    }

    public int getCode() {
        return this.code;
    }

    public String getDesc() {
        return this.desc;
    }

    public String toString() {
        return "ERROR CODE[" + this.code + "-" + this.desc + ']';
    }
}
