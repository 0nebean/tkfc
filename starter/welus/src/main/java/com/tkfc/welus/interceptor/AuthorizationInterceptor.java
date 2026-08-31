package com.tkfc.welus.interceptor;


import com.tkfc.core.common.annotations.web.auth.Authenticated;
import com.tkfc.core.common.pojo.BaseResponse;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.throwable.base.ErrorCode;
import com.tkfc.core.toolkit.JsonUtil;
import com.tkfc.core.toolkit.SpringUtil;
import com.tkfc.welus.interceptor.checker.PermissionChecker;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * 权限主机校验拦截
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-08-28 16:13:34
 */
public class AuthorizationInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 如果handler是方法处理器
        if (handler instanceof HandlerMethod handlerMethod) {
            Method method = handlerMethod.getMethod();
            // 检查是否有特定注解（假设注解名为 @RequiredPermission）
            if (method.isAnnotationPresent(Authenticated.class)) {
                PermissionChecker permissionChecker = SpringUtil.getBean(PermissionChecker.class);
                Assert.notNull(permissionChecker, "permission checker not config!");
                Authenticated authenticated = method.getAnnotation(Authenticated.class);
                // 执行权限校验逻辑
                if (!permissionChecker.checkPrem(authenticated.value(), authenticated.needLogin())) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write(JsonUtil.toJson(BaseResponse.error(ErrorCode.ACCESS_DENINED.getCode())));
                    // 拦截请求，不继续执行
                    return false;
                }
            }

        }
        // 继续执行请求
        return true;
    }

}
