package com.tkfc.welus.verification;

import com.tkfc.core.common.annotations.web.action.Action;
import com.tkfc.core.common.annotations.web.action.RestAction;
import com.tkfc.core.common.annotations.web.param.PathParam;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.EnvUtil;
import com.tkfc.core.toolkit.ReflectionUtil;
import com.tkfc.welus.annotation.EnableWelus;
import com.tkfc.core.common.annotations.web.report.IgnoreReportDoc;
import com.tkfc.welus.report.DocInfoBuilder;
import com.tkfc.welus.report.ReportDocInfoService;
import com.tkfc.welus.report.dto.doc.GatewayDocDto;
import com.tkfc.welus.report.dto.doc.GatewayDocParamType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * 只允许 welus 注解
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/1/11 10:45
 */
@Slf4j
public class OnlyWelusAnnotation {

    //文档信息
    private final static List<GatewayDocDto> DOCUMENTS = new ArrayList<>();
    private final static Map<String, GatewayDocParamType> TYPES = new HashMap<>();


    public static void verify() throws IOException, ClassNotFoundException {
        //收集所有包含action的资源对象目录
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(Thread.currentThread().getContextClassLoader());
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resolver);
        Class<?> springBootMainClass = ReflectionUtil.getSpringBootMainClass();
        List<String> actionPaths = Optional.ofNullable(springBootMainClass).map(c -> c.getAnnotation(EnableWelus.class)).map(EnableWelus::actionPackages).map(Arrays::asList).orElse(Collections.emptyList());
        List<Resource> resources = new ArrayList<>();
        for (String actionPath : actionPaths) {
            Resource[] temp = resolver.getResources(EnvUtil.coverPackageNameToClassPath(actionPath));
            Collections.addAll(resources, temp);
        }
        //遍历资源对象化目录
        for (Resource resource : resources) {
            String className = metadataReaderFactory.getMetadataReader(resource).getClassMetadata().getClassName();
            Class<?> clazz = Class.forName(className);
            //校验 action 注解合法性
            verifyActionAnnotation(clazz);
            if (clazz.isAnnotationPresent(Action.class) || clazz.isAnnotationPresent(RestAction.class)) {
                //记录action文档信息
                GatewayDocDto actionInfo = DocInfoBuilder.recordAction(clazz);
                List<Method> methods = Optional.of(clazz).map(Class::getDeclaredMethods).map(Arrays::asList).orElse(Collections.emptyList());
                for (Method method : methods) {
                    //校验方法注解合法性
                    verifyMethodAnnotation(method, clazz);
                    //校验参数注解合法性
                    Annotation[][] parameterAnnotations = method.getParameterAnnotations();
                    verifyParamAnnotation(parameterAnnotations, clazz, method);
                    if (!method.isAnnotationPresent(IgnoreReportDoc.class)) {
                        //记录action下的方法和方法参数文档信息
                        DocInfoBuilder.recordMethod(actionInfo, method, DOCUMENTS, TYPES);
                    }
                }
            }
        }
        log.info("reporting api documentation to gateway management server , doc size = {}", DOCUMENTS.size());
        Boolean reportExecuteResult = ReportDocInfoService.uploadRecordResult(DOCUMENTS, TYPES);
        Assert.isTrue(reportExecuteResult, "report welus doc info failure.");
    }


    /**
     * 校验 action 注解
     *
     * @param clazz 方法所属类 用于打印日志
     */
    private static void verifyActionAnnotation(Class<?> clazz) {
        if (clazz.isAnnotationPresent(Controller.class)) {
            printActionErrAndExit(Controller.class, clazz);
        }
        if (clazz.isAnnotationPresent(RestController.class)) {
            printActionErrAndExit(RestController.class, clazz);
        }
    }

    /**
     * 校验参数注解
     *
     * @param parameterAnnotations 参数的所有注解
     * @param clazz                方法所属类 用于打印日志
     * @param method               引用方法
     */
    private static void verifyParamAnnotation(Annotation[][] parameterAnnotations, Class<?> clazz, Method method) {
        for (Annotation[] parameterAnnotation : parameterAnnotations) {
            for (Annotation annotation : parameterAnnotation) {
                if (annotation instanceof RequestBody) {
                    printMethodErrAndExit(RequestBody.class, clazz, method.getName());
                }
                if (annotation instanceof RequestParam) {
                    printMethodErrAndExit(RequestParam.class, clazz, method.getName());
                }
                if (annotation instanceof PathParam) {
                    pathVariableWithPathParam(parameterAnnotation, PathParam.class, clazz, method.getName());
                }
                if (annotation instanceof PathVariable) {
                    pathVariableWithPathParam(parameterAnnotation, PathVariable.class, clazz, method.getName());
                }
            }
        }
    }

    /**
     * 校验方法注解合法性
     *
     * @param method 被校验的方法
     * @param clazz  方法所属类 用于打印日志
     */
    private static void verifyMethodAnnotation(Method method, Class<?> clazz) {
        if (method.isAnnotationPresent(DeleteMapping.class)) {
            printMethodErrAndExit(DeleteMapping.class, clazz, method.getName());
        }
        if (method.isAnnotationPresent(GetMapping.class)) {
            printMethodErrAndExit(GetMapping.class, clazz, method.getName());
        }
        if (method.isAnnotationPresent(PatchMapping.class)) {
            printMethodErrAndExit(PatchMapping.class, clazz, method.getName());
        }
        if (method.isAnnotationPresent(PostMapping.class)) {
            printMethodErrAndExit(PostMapping.class, clazz, method.getName());
        }
        if (method.isAnnotationPresent(PutMapping.class)) {
            printMethodErrAndExit(PutMapping.class, clazz, method.getName());
        }
        if (method.isAnnotationPresent(RequestMapping.class)) {
            printMethodErrAndExit(RequestMapping.class, clazz, method.getName());
        }
    }

    /**
     * path 两种校验注解需要一起使用
     *
     * @param parameterAnnotation 单个参数所有注解
     * @param annotation          当前检查注解类型
     * @param action              类
     * @param methodName          方法名
     * @author 0neBean
     * @since 22:38 2022/4/20
     */
    private static void pathVariableWithPathParam(Annotation[] parameterAnnotation, Class<?> annotation, Class<?> action, String methodName) {
        boolean pathVariable = Boolean.FALSE;
        boolean pathParam = Boolean.FALSE;
        for (Annotation a : parameterAnnotation) {
            if (a instanceof PathVariable) {
                pathVariable = Boolean.TRUE;
            }
            if (a instanceof PathParam) {
                pathParam = Boolean.TRUE;
            }
        }
        boolean bind = pathVariable && pathParam;
        if (!bind) {
            printPathParamBind(annotation, action, methodName);
        }
    }

    /**
     * 打印 action 注解校验异常
     *
     * @param annotation 注解
     * @param action     所属action
     */
    private static void printActionErrAndExit(Class<?> annotation, Class<?> action) {
        log.error("class {} use unsafe annotation [{}] ,please replace", action.getSimpleName(), annotation.getSimpleName());
        log.error("system will exit ...");
        System.exit(0);
    }

    /**
     * 打印 method 注解校验异常
     *
     * @param annotation 注解
     * @param action     所属action
     * @param methodName 方法名
     */
    private static void printMethodErrAndExit(Class<?> annotation, Class<?> action, String methodName) {
        log.error("method {}.{} use unsafe annotation [{}] ,please replace", action.getSimpleName(), methodName, annotation.getSimpleName());
        log.error("system will exit ...");
        System.exit(0);
        System.out.println();
    }

    /**
     * 打印 path param校验异常
     *
     * @param annotation 注解
     * @param action     所属action
     * @param methodName 方法名
     */
    private static void printPathParamBind(Class<?> annotation, Class<?> action, String methodName) {
        log.error("method {}.{} use annotation [{}] ,@PathVariable should bind @PathParam at same time", action.getSimpleName(), methodName, annotation.getSimpleName());
        log.error("system will exit ...");
        System.exit(0);
        System.out.println();
    }

}
