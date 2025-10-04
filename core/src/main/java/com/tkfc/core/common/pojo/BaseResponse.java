package com.tkfc.core.common.pojo;

import com.tkfc.core.common.annotations.web.param.Body;
import com.tkfc.core.common.annotations.web.param.BodyProperty;
import com.tkfc.core.throwable.base.ErrorCode;
import lombok.Getter;
import lombok.Setter;

/**
 * 通用返回对象
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/12/22 10:11
 */
@Body(tag = "通用返回对象")
public class BaseResponse<T> {

    @Getter
    @Setter
    @BodyProperty(tag = "当前时间戳")
    private long timestamp = System.currentTimeMillis();

    @Getter
    @Setter
    @BodyProperty(tag = "请求路径")
    private String path;

    @Getter
    @Setter
    @BodyProperty(tag = "请求状态")
    private int status;

    @Getter
    @Setter
    @BodyProperty(tag = "请求异常信息")
    private String error;

    @Getter
    @Setter
    @BodyProperty(tag = "请求错误信息")
    private Object message;

    @Getter
    @Setter
    @BodyProperty(tag = "请求返回数据")
    private T data;

    @Getter
    @Setter
    @BodyProperty(tag = "排序")
    private Sort sort;

    @Getter
    @Setter
    @BodyProperty(tag = "分页")
    private Pagination pagination;

    private BaseResponse() {
    }

    private BaseResponse(int errorCode) {
        this.status = errorCode;
    }

    private BaseResponse(int errorCode, Object message) {
        this.status = errorCode;
        this.message = message;
    }

    private BaseResponse(int status, Object message, T data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

    private BaseResponse(int status, Object message, T data, Pagination pagination) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.pagination = pagination;
    }

    private BaseResponse(int status, Object message, T data, Pagination pagination, Sort sort) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.sort = sort;
        this.pagination = pagination;
    }

    private BaseResponse(int status, Object message, T data, Sort sort) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.sort = sort;
    }

    public static <T> BaseResponse<T> ok() {
        return new BaseResponse<>(ErrorCode.OK.getCode());
    }

    public static <T> BaseResponse<T> ok(T data) {
        return new BaseResponse<>(200, "OK", data);
    }


    public static <T> BaseResponse<T> ok(T data, Pagination pagination) {
        return new BaseResponse<T>(200, "OK", data, pagination);
    }

    public static <T> BaseResponse<T> ok(T data, Sort sort) {
        return new BaseResponse<T>(200, "OK", data, sort);
    }

    public static <T> BaseResponse<T> ok(T data, Pagination pagination, Sort sort) {
        return new BaseResponse<T>(200, "OK", data, pagination, sort);
    }

    public static <T> BaseResponse<T> error(int errorCode) {
        return new BaseResponse<>(errorCode);
    }

    public static <T> BaseResponse<T> error(int errorCode, Object message) {
        return new BaseResponse<>(errorCode, message);
    }

    public static <T> BaseResponse<T> error(int errorCode, Object message, String error) {
        BaseResponse<T> response = new BaseResponse<>(errorCode, message);
        response.setError(error);
        return response;
    }


    public BaseResponse<T> withPath(String path) {
        this.path = path;
        return this;
    }

    public BaseResponse<T> withError(String error) {
        this.error = error;
        return this;
    }

    public BaseResponse<T> withPathAndError(String path, String error) {
        this.path = path;
        this.error = error;
        return this;
    }
}
