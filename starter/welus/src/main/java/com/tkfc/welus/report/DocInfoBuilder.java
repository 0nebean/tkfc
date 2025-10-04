package com.tkfc.welus.report;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONWriter;
import com.github.jsonzou.jmockdata.JMockData;
import com.github.jsonzou.jmockdata.MockConfig;
import com.github.jsonzou.jmockdata.TypeReference;
import com.tkfc.core.common.annotations.web.action.Action;
import com.tkfc.core.common.annotations.web.action.RestAction;
import com.tkfc.core.common.annotations.web.method.base.*;
import com.tkfc.core.common.annotations.web.method.json.*;
import com.tkfc.core.common.annotations.web.method.multipart.*;
import com.tkfc.core.common.annotations.web.method.view.*;
import com.tkfc.core.common.annotations.web.param.*;
import com.tkfc.core.common.pojo.*;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.enums.YesOrNoEnum;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.*;
import com.tkfc.welus.definition.enums.CovertParamType;
import com.tkfc.welus.report.dto.build.ActionInfoDto;
import com.tkfc.welus.report.dto.build.MethodInfoDto;
import com.tkfc.welus.report.dto.doc.GatewayDocDto;
import com.tkfc.welus.report.dto.doc.GatewayDocParamDto;
import com.tkfc.welus.report.dto.doc.GatewayDocParamType;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.reflect.*;
import java.util.*;

/**
 * 文档信息构建工具
 *
 * @author 0neBean
 * @since 2022-07-09 23:49:51
 */
@Slf4j
public class DocInfoBuilder {

    private final static String PARAM_TYPE = "0";
    private final static String VOID_TYPE = "Void";
    private final static String GENERICS_CHINESE = "泛型";
    private final static String RETURN_RESULT_TYPE = "1";
    private final static String ACTION_RESOURCE_TYPE = "0";
    private final static String METHOD_RESOURCE_TYPE = "1";
    private final static String METHOD_RESOURCE_NAME = "return";
    private final static String RAW_TYPE_NAME_PACKAGE = "interface ";
    private final static String ALLOW_JSON_FIELD = "welus.report.allow.json";
    private final static String BASE_RESP_NAME = "com.tkfc.core.common.pojo";
    private final static String BASE_RESPONSE_NAME = "com.tkfc.core.common.pojo.BaseResponse<";
    private final static String BASE_PAGE_VO_REQUEST_NAME = "com.tkfc.core.common.pojo.BasePageVoRequest<";

    /**
     * 包装 action 信息
     *
     * @param actionClazz 类
     * @return action 信息
     */
    public static GatewayDocDto recordAction(Class<?> actionClazz) {
        ActionInfoDto actionAnnotationInfo = getActionAnnotationInfo(actionClazz);
        return Objects.isNull(actionAnnotationInfo) ? null : GatewayDocDto.builder()
                .resourceDefKey(SnowflakeIdUtil.generateId().toString())
                .resourceName(actionAnnotationInfo.getName())
                .appKey(getCurrentServerKey())
                .methodType(actionAnnotationInfo.getType())
                .resourceType(ACTION_RESOURCE_TYPE)
                .resourcePath(String.join(StringPool.COMMA, actionAnnotationInfo.getPath()))
                .build();
    }

    /**
     * 构建 method 信息
     *
     * @param actionInfo action 信息
     * @param method     方法
     * @param documents  文档信息
     * @param typeMap    参数类型
     */
    public static void recordMethod(GatewayDocDto actionInfo, Method method, List<GatewayDocDto> documents, Map<String, GatewayDocParamType> typeMap) {
        if (Objects.isNull(actionInfo)) {
            return;
        }
        //记录method
        GatewayDocDto methodInfo = buildMethodInfo(actionInfo, method);
        if (Objects.isNull(methodInfo)) {
            return;
        }
        List<GatewayDocParamDto> params = new ArrayList<>();
        GatewayDocParamDto returnResult = recordMethodReturn(method, methodInfo, typeMap);
        Assert.isTrue(Objects.nonNull(returnResult.getTypeDefKey()), "please check return type of [%s] has [Body] Annotation or just base type", methodInfo.getResourceName());
        covertBaseReqResp(returnResult, typeMap);
        Parameter[] parameters = method.getParameters();
        // 获取参数类型
        Type[] parameterTypes = method.getGenericParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            Type parameterType = parameterTypes[i];
            Parameter parameter = parameters[i];

            GatewayDocParamDto docParam = recordParam(parameter, parameterType, methodInfo, typeMap);
            if (Objects.nonNull(docParam)) {
                boolean notSpringParam = !parameter.getType().getTypeName().contains("springframework");
                if (notSpringParam) {
                    Assert.isTrue(Objects.nonNull(docParam.getTypeDefKey()), "param type of [%s-%s] generics type can not be Object, maybe can specified [?]", methodInfo.getResourceName(), docParam.getParamName());
                    covertBaseReqResp(docParam, typeMap);
                }
                docParam.setSort(i);
                params.add(docParam);
            }
        }
        documents.add(actionInfo);
        Assert.notNull(methodInfo);
        methodInfo.setParams(params);
        methodInfo.setReturnResult(returnResult);
        documents.add(methodInfo);
    }

    /**
     * 包装 parameter 信息
     *
     * @param parameter     参数信息
     * @param parameterType 参数类型
     * @param methodInfo    方法信息
     * @param typeMap       已知参数类型
     * @return parameter 信息
     */
    public static GatewayDocParamDto recordParam(Parameter parameter, Type parameterType, GatewayDocDto methodInfo, Map<String, GatewayDocParamType> typeMap) {
        String methodDefKey = methodInfo.getResourceDefKey();
        GatewayDocParamDto rootNode = buildBodyParamRootNode(methodDefKey);
        rootNode.setFieldType(PARAM_TYPE);
        rootNode.setParamName(parameter.getName());
        //检查泛型参数中的Body注解不能超过1个
        checkGenericsTypeBodyParamCount(rootNode, methodInfo, parameterType);
        //如果是 bodyParam
        if (parameter.isAnnotationPresent(BodyParam.class)) {
            buildBodyDocItem(rootNode, parameterType, typeMap, new HashSet<>());
            return rootNode;
        }
        //如果是 pathParam
        if (parameter.isAnnotationPresent(PathParam.class)) {
            //校验是否为基础类型
            Assert.notTrue(parameterType instanceof ParameterizedType, "[%s-%s] must be base type like [int,Integer]", methodInfo.getResourceName(), parameter.getName());
            Class<?> argType = (Class<?>) parameterType;
            Assert.isTrue(ReflectionUtil.isBaseType(argType), "[%s-%s] must be base type like [int,Integer]", methodInfo.getResourceName(), parameter.getName());
            //获取注解
            PathParam pathParam = parameter.getAnnotation(PathParam.class);
            methodInfo.setAbsolutePath(YesOrNoEnum.NO.getValue());
            GatewayDocParamType typeInfo = buildParamType(parameterType, typeMap);
            return GatewayDocParamDto.builder()
                    .paramName(parameter.getName())
                    .paramDesc(Optional.ofNullable(pathParam).map(PathParam::tag).orElse(null))
                    .paramType(PathParam.class.getSimpleName())
                    .appKey(getCurrentServerKey())
                    .methodDefKey(methodDefKey)
                    .fieldDefKey(SnowflakeIdUtil.generateId().toString())
                    .typeDefKey(typeInfo.getTypeDefKey())
                    .typeFullName(typeInfo.getFullType())
                    .fieldType(PARAM_TYPE)
                    .build();
        }
        //如果是 urlParam
        if (parameter.isAnnotationPresent(UrlParam.class)) {
            //校验是否为基础类型
            Assert.notTrue(parameterType instanceof ParameterizedType, "[%s-%s] must be base type like [int,Integer]", methodInfo.getResourceName(), parameter.getName());
            Class<?> argType = (Class<?>) parameterType;
            Assert.isTrue(ReflectionUtil.isBaseType(argType), "[%s-%s] must be base type like [int,Integer]", methodInfo.getResourceName(), parameter.getName());
            //获取注解
            UrlParam urlParam = parameter.getAnnotation(UrlParam.class);
            GatewayDocParamType typeInfo = buildParamType(parameterType, typeMap);
            return GatewayDocParamDto.builder()
                    .paramName(parameter.getName())
                    .paramDesc(Optional.ofNullable(urlParam).map(UrlParam::tag).orElse(null))
                    .paramType(UrlParam.class.getSimpleName())
                    .appKey(getCurrentServerKey())
                    .methodDefKey(methodDefKey)
                    .fieldDefKey(SnowflakeIdUtil.generateId().toString())
                    .typeDefKey(typeInfo.getTypeDefKey())
                    .typeFullName(typeInfo.getFullType())
                    .fieldType(PARAM_TYPE)
                    .build();
        }
        return null;
    }

    /**
     * 构建对象参数的根节点
     *
     * @param methodDefKey 方法标识
     * @return 对象参数的根节点
     */
    private static GatewayDocParamDto buildBodyParamRootNode(String methodDefKey) {
        return GatewayDocParamDto.builder()
                .appKey(getCurrentServerKey())
                .fieldDefKey(SnowflakeIdUtil.generateId().toString())
                .methodDefKey(methodDefKey)
                .build();
    }

    /**
     * 构建参数的字段对象
     *
     * @param bodyClazz           构建目标对象class
     * @param parentNode          父节点
     * @param typeMap             类型map
     * @param currentClassLinkSet 当前参数的类型链表-防止堆栈溢出
     * @return 父节点的 property list
     */
    private static List<GatewayDocParamDto> buildBodyProperty(Class<?> bodyClazz, GatewayDocParamDto parentNode, Map<String, GatewayDocParamType> typeMap, Set<String> currentClassLinkSet) {
        List<GatewayDocParamDto> propertyList = new ArrayList<>();
        for (Field declaredField : bodyClazz.getDeclaredFields()) {
            if (declaredField.isAnnotationPresent(BodyProperty.class)) {
                BodyProperty bodyProperty = declaredField.getAnnotation(BodyProperty.class);
                Type propertyType = declaredField.getGenericType();
                Assert.notTrue(propertyType.getTypeName().contains("java.util.Map"),"not support [java.util.Map] for params or return 's property item type on " + bodyClazz.getName());
                checkBodyPropTypeGen(bodyClazz, propertyType, declaredField);
                GatewayDocParamType typeInfo = buildParamType(propertyType, typeMap);
                GatewayDocParamDto property = GatewayDocParamDto.builder()
                        .appKey(getCurrentServerKey())
                        .paramDesc(bodyProperty.tag())
                        .paramName(declaredField.getName())
                        .paramType(BodyProperty.class.getSimpleName())
                        .paramExample(bodyProperty.example())
                        .fieldDefKey(SnowflakeIdUtil.generateId().toString())
                        .parentDefKey(parentNode.getFieldDefKey())
                        .methodDefKey(parentNode.getMethodDefKey())
                        .fieldType(parentNode.getFieldType())
                        .typeDefKey(typeInfo.getTypeDefKey())
                        .typeFullName(typeInfo.getFullType())
                        .build();
                //包装property对象下的数据
                if (declaredField.isAnnotationPresent(Body.class)) {
                    buildBodyDocItem(property, declaredField.getGenericType(), typeMap, currentClassLinkSet);
                }
                propertyList.add(property);
            }
        }
        return propertyList;
    }

    /**
     * 检查BodyProperty泛型是否嵌套
     *
     * @param bodyClazz     body的class
     * @param propertyType  BodyProperty 的 Type
     * @param declaredField BodyProperty 的 Field
     */
    private static void checkBodyPropTypeGen(Class<?> bodyClazz, Type propertyType, Field declaredField) {
        if (bodyClazz.getPackageName().contains(BASE_RESP_NAME)) {
            return;
        }
        if (propertyType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) propertyType;
            //填充泛型包名
            for (Type type : parameterizedType.getActualTypeArguments()) {
                String fieldName = String.format("%s-%s-%s-%s", bodyClazz.getName(), declaredField.getName(), type.getTypeName(), type.getTypeName());
                Assert.isTrue(type.getTypeName().contains(StringPool.DOT), "param [%s] can not contain ambiguous generics", fieldName);
            }
        } else {
            String fieldName = String.format("%s-%s-%s", bodyClazz.getName(), declaredField.getName(), propertyType.getTypeName());
            Assert.isTrue(propertyType.getTypeName().contains(StringPool.DOT), "param [%s] can not contain ambiguous generics", fieldName);
            Boolean allowJsonField = ParseUtil.toBoolean(PropUtil.getInstance().getConfig(ALLOW_JSON_FIELD));
            if (!allowJsonField) {
                Assert.notTrue(propertyType.getTypeName().contains("com.alibaba.fastjson"), "param [%s] can not contain ambiguous json field", fieldName);
            }
        }
    }

    /**
     * 构建参数类型
     *
     * @param parameterType 参数类型
     * @param typeMap       类型map
     */
    private static GatewayDocParamType buildParamType(Type parameterType, Map<String, GatewayDocParamType> typeMap) {
        String fullTypeName = parameterType.getTypeName();
        GatewayDocParamType paramType = new GatewayDocParamType();
        paramType.setAppKey(getCurrentServerKey());
        paramType.setTypeDefKey(SnowflakeIdUtil.generateId().toString());
        Set<String> packageNameSet = new HashSet<>();
        //处理泛型的情况
        if (parameterType instanceof ParameterizedType) {
            packageNameSet.add(RAW_TYPE_NAME_PACKAGE);
            ParameterizedType parameterizedType = (ParameterizedType) parameterType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            //填充泛型包名
            fillPackageName(packageNameSet, parameterizedType.getRawType().getTypeName());
            for (Type type : typeArguments) {
                //填充泛型子类型包名
                fillPackageName(packageNameSet, type.getTypeName());
                //创建子类型
                buildParamType(type, typeMap);
            }
        } else {
            //填充填充非泛型类型的包名
            fillPackageName(packageNameSet, fullTypeName);
        }
        //设置类的名称
        paramType.setFullType(fullTypeName);
        paramType.setSimpleType(covertSimpleTypeName(fullTypeName, packageNameSet));
        fillMockData(paramType, parameterType);
        typeMap.put(paramType.getTypeDefKey(), paramType);
        return paramType;
    }

    /**
     * 构建参数类型
     *
     * @param rootNode 根节点
     * @param typeMap  类型map
     */
    private static GatewayDocParamType buildParamTypeWithQuestion(GatewayDocParamDto rootNode, Map<String, GatewayDocParamType> typeMap) {
        String fullTypeName = StringPool.QUESTION_MARK;
        if (Objects.equals(rootNode.getCovertType(), CovertParamType.REQ.getKey())) {
            fullTypeName = buildBaseReqCovertTypeName(fullTypeName);
        }
        if (Objects.equals(rootNode.getCovertType(), CovertParamType.RESP.getKey())) {
            fullTypeName = buildBaseRespCovertTypeName(fullTypeName);
        }
        GatewayDocParamType paramType = new GatewayDocParamType();
        paramType.setAppKey(getCurrentServerKey());
        paramType.setTypeDefKey(SnowflakeIdUtil.generateId().toString());
        paramType.setFullType(fullTypeName);
        paramType.setSimpleType(String.format("%s%s%s", StringPool.LEFT_CHEV, StringPool.QUESTION_MARK, StringPool.RIGHT_CHEV));
        paramType.setMockData(JsonUtil.toJsonWithReferenceDetection(new JSONObject()));
        typeMap.put(paramType.getTypeDefKey(), paramType);
        rootNode.setTypeDefKey(paramType.getTypeDefKey());
        return paramType;
    }

    /**
     * 填充类型的 mock data
     *
     * @param paramType     参数类型 (生成)
     * @param parameterType 参数类型 (类)
     */
    private static void fillMockData(GatewayDocParamType paramType, Type parameterType) {
        MockConfig mockConfig = new MockConfig();
        mockConfig.setEnabledCircle(Boolean.TRUE);
        if (parameterType instanceof ParameterizedType) {
            mockAdvancedTypeData(paramType, parameterType, mockConfig);
        } else {
            Class<?> argType = (Class<?>) parameterType;
            if (ReflectionUtil.isBaseType(argType)) {
                if (paramType.getSimpleType().equals(Void.class.getSimpleName())) {
                    paramType.setMockData(JsonUtil.toJsonWithReferenceDetection(JsonUtil.toJsonWithAutoType(Void.class.getSimpleName())));
                } else {
                    paramType.setMockData(JsonUtil.toJsonWithReferenceDetection(JsonUtil.toJsonWithAutoType(JMockData.mock(buildTypeReference(parameterType), mockConfig).toString())));
                }
            } else {
                mockAdvancedTypeData(paramType, parameterType, mockConfig);
            }
        }
    }

    /**
     * mock 复杂数据类型数据
     *
     * @param paramType     参数类型对象
     * @param parameterType 参数类型
     * @param mockConfig    mock配置
     */
    private static void mockAdvancedTypeData(GatewayDocParamType paramType, Type parameterType, MockConfig mockConfig) {
        try {
            paramType.setMockData(JsonUtil.toJsonWithReferenceDetection(JMockData.mock(buildTypeReference(parameterType), mockConfig)));
        } catch (Exception ignore) {
            paramType.setMockData(JsonUtil.toJsonWithReferenceDetection(new JSONObject()));
        }
    }

    /**
     * 根据类的完整版限定名获取类的简单类名
     *
     * @param fullTypeName   类的完整限定名
     * @param packageNameSet 类的包名
     * @return simpleType
     */
    private static String covertSimpleTypeName(String fullTypeName, Set<String> packageNameSet) {
        String simpleType = fullTypeName;
        for (String packageName : packageNameSet) {
            simpleType = covertArrayTypeName(simpleType);
            simpleType = simpleType.replaceAll(packageName, StringPool.EMPTY);
        }
        simpleType = simpleType.replaceAll(StringPool.DOT_TRANSFER, StringPool.EMPTY);
        return simpleType;
    }

    /**
     * 清除 BaseResponse 返回体
     *
     * @param rootNode   根节点
     * @param targetType 目标类型
     * @return 清除后的目标类型
     */
    private static Type cleanBaseBody(GatewayDocParamDto rootNode, Type targetType) {
        if (targetType.getTypeName().contains(BASE_RESP_NAME) && targetType instanceof ParameterizedType) {
            if (targetType.getTypeName().contains(BASE_PAGE_VO_REQUEST_NAME)) {
                rootNode.setCovertType(CovertParamType.REQ.getKey());
            }
            if (targetType.getTypeName().contains(BASE_RESPONSE_NAME)) {
                rootNode.setCovertType(CovertParamType.RESP.getKey());
            }
            ParameterizedType parameterizedType = (ParameterizedType) targetType;
            return parameterizedType.getActualTypeArguments()[0];
        }
        return targetType;
    }

    /**
     * 清除 Base 类型名称
     *
     * @param typeName 根节点
     * @return 清除后的目标类型
     */
    private static String cleanBaseTypeName(String typeName) {
        String original = typeName;
        if (typeName.contains(BASE_RESP_NAME)) {
            if (typeName.contains(BASE_PAGE_VO_REQUEST_NAME) || typeName.contains(BASE_RESPONSE_NAME)) {
                typeName = typeName.substring(0, typeName.length() - 1);
                typeName = typeName.replace(BASE_PAGE_VO_REQUEST_NAME, StringPool.EMPTY);
                typeName = typeName.replace(BASE_RESPONSE_NAME, StringPool.EMPTY);
                return Objects.equals(typeName, StringPool.QUESTION_MARK) ? original : typeName;
            }
        }
        return typeName;
    }

    /**
     * 包装部数据的类型名称
     *
     * @param typeName 类型名称
     * @return 包装后的类型名称 移除了[]
     */
    private static String covertArrayTypeName(String typeName) {
        String arrayString = StringPool.LEFT_SQ_BRACKET + StringPool.RIGHT_SQ_BRACKET;
        if (isArrayTypeName(typeName)) {
            return typeName.replace(arrayString, StringPool.EMPTY);
        } else {
            return typeName;
        }
    }

    /**
     * 判断是否是数据的类型
     *
     * @param typeName 类型名称
     * @return bool
     */
    private static boolean isArrayTypeName(String typeName) {
        String arrayString = StringPool.LEFT_SQ_BRACKET + StringPool.RIGHT_SQ_BRACKET;
        return typeName.contains(arrayString);
    }

    /**
     * 填充包名
     *
     * @param packageName  包名
     * @param fullTypeName 完整类型名称
     */
    private static void fillPackageName(Set<String> packageName, String fullTypeName) {
        String arrayString = StringPool.LEFT_SQ_BRACKET + StringPool.RIGHT_SQ_BRACKET;
        fullTypeName = covertArrayTypeName(fullTypeName);
        Class<?> clazz = ReflectionUtil.getClass(fullTypeName);
        if (Objects.isNull(clazz)) {
            log.error("welus report doc got un know class, name is {}", fullTypeName);
            return;
        }
        String packageNameStr = clazz.getPackageName();
        if (isArrayTypeName(fullTypeName)) {
            packageNameStr = String.format("%s%s", packageNameStr, arrayString);
        }
        packageName.add(packageNameStr);
    }

    /**
     * 使用反射构造 TypeReference 实例
     *
     * @param type 参数对象类型
     * @return TypeReference 实例
     */
    private static TypeReference<?> buildTypeReference(Type type) {
        // 使用反射构造 TypeReference 实例
        return new TypeReference<>() {
            @Override
            public Type getType() {
                return type;
            }
        };
    }

    /**
     * 生成body参数的属性
     *
     * @param parameterType       参数类型
     * @param rootNode            参数根节点
     * @param typeMap             参数类型map
     * @param currentClassLinkSet 当前参数的类型链表-防止堆栈溢出
     */
    private static void genBodyProperty(Type parameterType, GatewayDocParamDto rootNode, Map<String, GatewayDocParamType> typeMap, Set<String> currentClassLinkSet) {
        Class<?> bodyClazz = (Class<?>) parameterType;
        rootNode.setBodyFieldList(buildBodyProperty(bodyClazz, rootNode, typeMap, currentClassLinkSet));
    }

    /**
     * 设置body参数描述信息
     *
     * @param parameterType 参数类型
     * @param targetType    当前目标类型
     * @param rootNode      参数根节点
     * @param typeMap       参数类型map
     */
    private static void fillBodyInfo(Type parameterType, Type targetType, GatewayDocParamDto rootNode, Map<String, GatewayDocParamType> typeMap) {
        GatewayDocParamType typeInfo = buildParamType(targetType, typeMap);
        rootNode.setTypeDefKey(typeInfo.getTypeDefKey());
        rootNode.setTypeFullName(typeInfo.getFullType());
        Body body = covertTypeToClass(parameterType).getAnnotation(Body.class);
        rootNode.setParamDesc(Optional.ofNullable(body).map(Body::tag).orElse(null));
        rootNode.setParamType(BodyParam.class.getSimpleName());
    }

    /**
     * 设置body参数描述信息
     *
     * @param parameterType 参数类型
     * @param rootNode      参数根节点
     * @param typeMap       参数类型map
     */
    private static void fillBodyInfo(Type parameterType, GatewayDocParamDto rootNode, Map<String, GatewayDocParamType> typeMap) {
        rootNode.setParamType(BodyParam.class.getSimpleName());
        GatewayDocParamType typeInfo;
        if (Objects.isNull(parameterType)) {
            rootNode.setParamDesc(GENERICS_CHINESE);
            typeInfo = buildParamTypeWithQuestion(rootNode, typeMap);
            rootNode.setTypeDefKey(typeInfo.getTypeDefKey());
            rootNode.setTypeFullName(typeInfo.getFullType());
        } else {
            typeInfo = buildParamType(parameterType, typeMap);
            rootNode.setTypeDefKey(typeInfo.getTypeDefKey());
            rootNode.setTypeFullName(typeInfo.getFullType());
            Body body = covertTypeToClass(parameterType).getAnnotation(Body.class);
            rootNode.setParamDesc(Optional.ofNullable(body).map(Body::tag).orElse(null));
        }
    }

    /**
     * 包装type 成 class
     *
     * @param type type
     * @return class
     */
    private static Class<?> covertTypeToClass(Type type) {
        String typeName = type.getTypeName();
        Class<?> clazz = ReflectionUtil.getClass(typeName);
        Assert.notNull(clazz);
        return clazz;
    }

    /**
     * 构建 method 信息
     *
     * @param actionInfo action 信息
     * @param method     方法
     * @return method 信息
     */
    private static GatewayDocDto buildMethodInfo(GatewayDocDto actionInfo, Method method) {
        MethodInfoDto methodAnnotation = getMethodAnnotation(method);
        String actionPath = actionInfo.getResourcePath();
        if (StringUtil.isNotBlank(actionPath)) {
            if (!actionPath.startsWith(StringPool.SLASH)) {
                actionPath = StringPool.SLASH + actionPath;
            }
            if (!actionPath.endsWith(StringPool.SLASH)) {
                actionPath = actionPath + StringPool.SLASH;
            }
        }
        String[] pathArray = Optional.ofNullable(methodAnnotation).map(MethodInfoDto::getPath).orElse(new String[0]);
        String[] finalPathArray = new String[pathArray.length];
        for (int i = 0; i < pathArray.length; i++) {
            String methodPath = pathArray[i];
            boolean methodPathEndWithSlash = methodPath.startsWith(StringPool.SLASH);
            boolean actionPathEndWithSlash = actionPath.endsWith(StringPool.SLASH);
            if (methodPathEndWithSlash && actionPathEndWithSlash) {
                methodPath = methodPath.substring(1);
            }
            finalPathArray[i] = actionPath + methodPath;
        }
        return Objects.isNull(methodAnnotation) ? null : GatewayDocDto.builder()
                .actionDefKey(actionInfo.getResourceDefKey())
                .absolutePath(YesOrNoEnum.YES.getValue())
                .resourceDefKey(SnowflakeIdUtil.generateId().toString())
                .resourceName(String.format("%s%s%s", actionInfo.getResourceName(), StringPool.DASH, methodAnnotation.getName()))
                .resourceType(METHOD_RESOURCE_TYPE)
                .methodType(methodAnnotation.getMethodType())
                .appKey(getCurrentServerKey())
                .authors(String.join(StringPool.COMMA, methodAnnotation.getAuthor()))
                .resourcePath(String.join(StringPool.COMMA, finalPathArray))
                .authType(methodAnnotation.getAuthType())
                .build();
    }

    /**
     * 返回当前应用的 服务key
     *
     * @return serverKey
     */
    private static String getCurrentServerKey() {
        return PropUtil.getInstance().getConfig("spring.application.name");
    }

    /**
     * 返回类的action注解
     *
     * @param actionClazz action的类
     * @return action注解
     */
    private static ActionInfoDto getActionAnnotationInfo(Class<?> actionClazz) {
        Action action = actionClazz.getAnnotation(Action.class);
        if (Objects.nonNull(action)) {
            return buildActionInfo(action.path(), action.value(), action.tag(), Action.class.getSimpleName());
        }

        RestAction restAction = actionClazz.getAnnotation(RestAction.class);
        if (Objects.nonNull(restAction)) {
            return buildActionInfo(restAction.path(), restAction.value(), restAction.tag(), RestAction.class.getSimpleName());
        }
        return null;
    }

    /**
     * 构建Action信息对象
     *
     * @param path  path
     * @param value value
     * @param tag   标签
     * @param type  类型
     * @return Action信息对象
     */
    private static ActionInfoDto buildActionInfo(String[] path, String[] value, String tag, String type) {
        List<String> paths = new ArrayList<>();
        paths.addAll(new ArrayList<>(List.of(path)));
        paths.addAll(new ArrayList<>(List.of(value)));
        return ActionInfoDto.builder().name(tag).type(type).path(CollectionUtil.listToStringArr(paths)).build();
    }

    /**
     * 返回方法上的注解
     *
     * @param method 方法
     * @return method注解
     */
    private static MethodInfoDto getMethodAnnotation(Method method) {
        Delete delete = method.getAnnotation(Delete.class);
        if (Objects.nonNull(delete)) {
            return MethodInfoDto.builder()
                    .path(delete.path())
                    .author(delete.authors())
                    .methodType(delete.annotationType().getSimpleName())
                    .name(delete.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        Get get = method.getAnnotation(Get.class);
        if (Objects.nonNull(get)) {
            return MethodInfoDto.builder()
                    .path(get.path())
                    .author(get.authors())
                    .methodType(get.annotationType().getSimpleName())
                    .name(get.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        GetAndPost getAndPost = method.getAnnotation(GetAndPost.class);
        if (Objects.nonNull(getAndPost)) {
            return MethodInfoDto.builder()
                    .path(getAndPost.path())
                    .author(getAndPost.authors())
                    .methodType(getAndPost.annotationType().getSimpleName())
                    .name(getAndPost.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        Head head = method.getAnnotation(Head.class);
        if (Objects.nonNull(head)) {
            return MethodInfoDto.builder()
                    .path(head.path())
                    .author(head.authors())
                    .methodType(head.annotationType().getSimpleName())
                    .name(head.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        Options options = method.getAnnotation(Options.class);
        if (Objects.nonNull(options)) {
            return MethodInfoDto.builder()
                    .path(options.path())
                    .author(options.authors())
                    .methodType(options.annotationType().getSimpleName())
                    .name(options.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        Patch patch = method.getAnnotation(Patch.class);
        if (Objects.nonNull(patch)) {
            return MethodInfoDto.builder()
                    .path(patch.path())
                    .author(patch.authors())
                    .methodType(patch.annotationType().getSimpleName())
                    .name(patch.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        Post post = method.getAnnotation(Post.class);
        if (Objects.nonNull(post)) {
            return MethodInfoDto.builder()
                    .path(post.path())
                    .author(post.authors())
                    .methodType(post.annotationType().getSimpleName())
                    .name(post.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        Put put = method.getAnnotation(Put.class);
        if (Objects.nonNull(put)) {
            return MethodInfoDto.builder()
                    .path(put.path())
                    .author(put.authors())
                    .methodType(put.annotationType().getSimpleName())
                    .name(put.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        Trace trace = method.getAnnotation(Trace.class);
        if (Objects.nonNull(trace)) {
            return MethodInfoDto.builder()
                    .path(trace.path())
                    .author(trace.authors())
                    .methodType(trace.annotationType().getSimpleName())
                    .name(trace.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        DeleteJson deleteJson = method.getAnnotation(DeleteJson.class);
        if (Objects.nonNull(deleteJson)) {
            return MethodInfoDto.builder()
                    .path(deleteJson.path())
                    .author(deleteJson.authors())
                    .methodType(deleteJson.annotationType().getSimpleName())
                    .name(deleteJson.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        GetJson getJson = method.getAnnotation(GetJson.class);
        if (Objects.nonNull(getJson)) {
            return MethodInfoDto.builder()
                    .path(getJson.path())
                    .author(getJson.authors())
                    .methodType(getJson.annotationType().getSimpleName())
                    .name(getJson.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        HeadJson headJson = method.getAnnotation(HeadJson.class);
        if (Objects.nonNull(headJson)) {
            return MethodInfoDto.builder()
                    .path(headJson.path())
                    .author(headJson.authors())
                    .methodType(headJson.annotationType().getSimpleName())
                    .name(headJson.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        OptionsJson optionsJson = method.getAnnotation(OptionsJson.class);
        if (Objects.nonNull(optionsJson)) {
            return MethodInfoDto.builder()
                    .path(optionsJson.path())
                    .author(optionsJson.authors())
                    .methodType(optionsJson.annotationType().getSimpleName())
                    .name(optionsJson.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        PatchJson patchJson = method.getAnnotation(PatchJson.class);
        if (Objects.nonNull(patchJson)) {
            return MethodInfoDto.builder()
                    .path(patchJson.path())
                    .author(patchJson.authors())
                    .methodType(patchJson.annotationType().getSimpleName())
                    .name(patchJson.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        PostJson postJson = method.getAnnotation(PostJson.class);
        if (Objects.nonNull(postJson)) {
            return MethodInfoDto.builder()
                    .path(postJson.path())
                    .author(postJson.authors())
                    .methodType(postJson.annotationType().getSimpleName())
                    .name(postJson.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        GetAndPostJson getAndPostJson = method.getAnnotation(GetAndPostJson.class);
        if (Objects.nonNull(getAndPostJson)) {
            return MethodInfoDto.builder()
                    .path(getAndPostJson.path())
                    .author(getAndPostJson.authors())
                    .methodType(getAndPostJson.annotationType().getSimpleName())
                    .name(getAndPostJson.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        PutJson putJson = method.getAnnotation(PutJson.class);
        if (Objects.nonNull(putJson)) {
            return MethodInfoDto.builder()
                    .path(putJson.path())
                    .author(putJson.authors())
                    .methodType(putJson.annotationType().getSimpleName())
                    .name(putJson.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        TraceJson traceJson = method.getAnnotation(TraceJson.class);
        if (Objects.nonNull(traceJson)) {
            return MethodInfoDto.builder()
                    .path(traceJson.path())
                    .author(traceJson.authors())
                    .methodType(traceJson.annotationType().getSimpleName())
                    .name(traceJson.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        DeleteMultipart deleteMultipart = method.getAnnotation(DeleteMultipart.class);
        if (Objects.nonNull(deleteMultipart)) {
            return MethodInfoDto.builder()
                    .path(deleteMultipart.path())
                    .author(deleteMultipart.authors())
                    .methodType(deleteMultipart.annotationType().getSimpleName())
                    .name(deleteMultipart.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        GetMultipart getMultipart = method.getAnnotation(GetMultipart.class);
        if (Objects.nonNull(getMultipart)) {
            return MethodInfoDto.builder()
                    .path(getMultipart.path())
                    .author(getMultipart.authors())
                    .methodType(getMultipart.annotationType().getSimpleName())
                    .name(getMultipart.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        HeadMultipart headMultipart = method.getAnnotation(HeadMultipart.class);
        if (Objects.nonNull(headMultipart)) {
            return MethodInfoDto.builder()
                    .path(headMultipart.path())
                    .author(headMultipart.authors())
                    .methodType(headMultipart.annotationType().getSimpleName())
                    .name(headMultipart.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        OptionsMultipart optionsMultipart = method.getAnnotation(OptionsMultipart.class);
        if (Objects.nonNull(optionsMultipart)) {
            return MethodInfoDto.builder()
                    .path(optionsMultipart.path())
                    .author(optionsMultipart.authors())
                    .methodType(optionsMultipart.annotationType().getSimpleName())
                    .name(optionsMultipart.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        PatchMultipart patchMultipart = method.getAnnotation(PatchMultipart.class);
        if (Objects.nonNull(patchMultipart)) {
            return MethodInfoDto.builder()
                    .path(patchMultipart.path())
                    .author(patchMultipart.authors())
                    .methodType(patchMultipart.annotationType().getSimpleName())
                    .name(patchMultipart.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        PostMultipart postMultipart = method.getAnnotation(PostMultipart.class);
        if (Objects.nonNull(postMultipart)) {
            return MethodInfoDto.builder()
                    .path(postMultipart.path())
                    .author(postMultipart.authors())
                    .methodType(postMultipart.annotationType().getSimpleName())
                    .name(postMultipart.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        PutMultipart putMultipart = method.getAnnotation(PutMultipart.class);
        if (Objects.nonNull(putMultipart)) {
            return MethodInfoDto.builder()
                    .path(putMultipart.path())
                    .author(putMultipart.authors())
                    .methodType(putMultipart.annotationType().getSimpleName())
                    .name(putMultipart.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        TraceMultipart traceMultipart = method.getAnnotation(TraceMultipart.class);
        if (Objects.nonNull(traceMultipart)) {
            return MethodInfoDto.builder()
                    .path(traceMultipart.path())
                    .author(traceMultipart.authors())
                    .methodType(traceMultipart.annotationType().getSimpleName())
                    .name(traceMultipart.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        DeleteView deleteView = method.getAnnotation(DeleteView.class);
        if (Objects.nonNull(deleteView)) {
            return MethodInfoDto.builder()
                    .path(deleteView.path())
                    .author(deleteView.authors())
                    .methodType(deleteView.annotationType().getSimpleName())
                    .name(deleteView.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        GetView getView = method.getAnnotation(GetView.class);
        if (Objects.nonNull(getView)) {
            return MethodInfoDto.builder()
                    .path(getView.path())
                    .author(getView.authors())
                    .methodType(getView.annotationType().getSimpleName())
                    .name(getView.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        HeadView headView = method.getAnnotation(HeadView.class);
        if (Objects.nonNull(headView)) {
            return MethodInfoDto.builder()
                    .path(headView.path())
                    .author(headView.authors())
                    .methodType(headView.annotationType().getSimpleName())
                    .name(headView.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        OptionsView optionsView = method.getAnnotation(OptionsView.class);
        if (Objects.nonNull(optionsView)) {
            return MethodInfoDto.builder()
                    .path(optionsView.path())
                    .author(optionsView.authors())
                    .methodType(optionsView.annotationType().getSimpleName())
                    .name(optionsView.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        PatchView patchView = method.getAnnotation(PatchView.class);
        if (Objects.nonNull(patchView)) {
            return MethodInfoDto.builder()
                    .path(patchView.path())
                    .author(patchView.authors())
                    .methodType(patchView.annotationType().getSimpleName())
                    .name(patchView.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        PostView postView = method.getAnnotation(PostView.class);
        if (Objects.nonNull(postView)) {
            return MethodInfoDto.builder()
                    .path(postView.path())
                    .author(postView.authors())
                    .methodType(postView.annotationType().getSimpleName())
                    .name(postView.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        PutView putView = method.getAnnotation(PutView.class);
        if (Objects.nonNull(putView)) {
            return MethodInfoDto.builder()
                    .path(putView.path())
                    .author(putView.authors())
                    .methodType(putView.annotationType().getSimpleName())
                    .name(putView.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        TraceView traceView = method.getAnnotation(TraceView.class);
        if (Objects.nonNull(traceView)) {
            return MethodInfoDto.builder()
                    .path(traceView.path())
                    .author(traceView.authors())
                    .methodType(traceView.annotationType().getSimpleName())
                    .name(traceView.tag())
                    .authType((method.isAnnotationPresent(PreAuthorize.class) ? "admin" : "free"))
                    .build();
        }

        return null;
    }

    /**
     * 构建 body 参数对象
     *
     * @param rootNode            构建目标根节点
     * @param targetType          构建目标 type
     * @param typeMap             类型map
     * @param currentClassLinkSet 当前参数的类型链表-防止堆栈溢出
     */
    private static void buildBodyDocItem(GatewayDocParamDto rootNode, Type targetType, Map<String, GatewayDocParamType> typeMap, Set<String> currentClassLinkSet) {
        String currentTypeName = targetType.getTypeName();
        if (currentClassLinkSet.contains(targetType.getTypeName())) {
            return;
        }
        currentClassLinkSet.add(currentTypeName);
        targetType = cleanBaseBody(rootNode, targetType);
        //泛型处理
        if (targetType instanceof ParameterizedType) {
            //泛型的raw类型是否有Body注解
            if (rawTypeIsBodyParam(rootNode, targetType)) {
                buildBodyDocItemWithRawTypeIsBodyParam(rootNode, targetType, typeMap, currentClassLinkSet);
            } else {
                buildBodyDocItemWithRawTypeIsBase(rootNode, targetType, typeMap, currentClassLinkSet);
            }
        } else {
            //普通对象
            buildBodyDocItemWithBase(rootNode, targetType, typeMap, currentClassLinkSet);
        }


    }

    /**
     * 构建bodyDoc 针对 Gua<Hua> Body嵌套Body的情况
     *
     * @param rootNode            构建目标根节点
     * @param targetType          构建目标 type
     * @param typeMap             类型map
     * @param currentClassLinkSet 当前参数的类型链表-防止堆栈溢出
     */
    private static void buildBodyDocItemWithRawTypeIsBodyParam(GatewayDocParamDto rootNode, Type targetType, Map<String, GatewayDocParamType> typeMap, Set<String> currentClassLinkSet) {
        ParameterizedType parameterizedType = (ParameterizedType) targetType;
        Type rawType = parameterizedType.getRawType();
        fillBodyInfo(rawType, rootNode, typeMap);
        genBodyProperty(rawType, rootNode, typeMap, currentClassLinkSet);
    }

    /**
     * 构建bodyDoc 针对 List<Gua> Map<String,Gua> 基础类型嵌套Body的情况
     *
     * @param rootNode            构建目标根节点
     * @param targetType          构建目标 type
     * @param typeMap             类型map
     * @param currentClassLinkSet 当前参数的类型链表-防止堆栈溢出
     */
    private static void buildBodyDocItemWithRawTypeIsBase(GatewayDocParamDto rootNode, Type targetType, Map<String, GatewayDocParamType> typeMap, Set<String> currentClassLinkSet) {
        ParameterizedType parameterizedType = (ParameterizedType) targetType;
        Type[] typeArguments = parameterizedType.getActualTypeArguments();
        //泛型类 本身有@Body注解 例如: Person<String>
        Class<?> argType;
        for (Type type : typeArguments) {
            if (type instanceof ParameterizedType) {
                buildBodyDocItem(rootNode, targetType, typeMap, currentClassLinkSet);
            } else {
                //泛型类型的实际类型有@Body注解 例如: Map<String,Person> , List<Person> , Student<Person>
                argType = (Class<?>) type;
                if (argType.isAnnotationPresent(Body.class) || ReflectionUtil.isBaseType(argType)) {
                    fillBodyInfo(type, targetType, rootNode, typeMap);
                    genBodyProperty(type, rootNode, typeMap, currentClassLinkSet);
                }
            }
        }
    }

    /**
     * 构建bodyDoc 针对 Gua简单类型
     *
     * @param rootNode            构建目标根节点
     * @param targetType          构建目标 type
     * @param typeMap             类型map
     * @param currentClassLinkSet 当前参数的类型链表-防止堆栈溢出
     */
    private static void buildBodyDocItemWithBase(GatewayDocParamDto rootNode, Type targetType, Map<String, GatewayDocParamType> typeMap, Set<String> currentClassLinkSet) {
        if (targetType.toString().equals(StringPool.QUESTION_MARK)) {
            fillBodyInfo(null, rootNode, typeMap);
            return;
        }
        Class<?> argType = (Class<?>) targetType;
        if (argType.isAnnotationPresent(Body.class) || ReflectionUtil.isBaseType(argType)) {
            fillBodyInfo(argType, rootNode, typeMap);
            genBodyProperty(argType, rootNode, typeMap, currentClassLinkSet);
        }
    }

    /**
     * 泛型的raw类型是否有Body注解
     *
     * @param rootNode   根节点
     * @param targetType 目标类型
     */
    private static boolean rawTypeIsBodyParam(GatewayDocParamDto rootNode, Type targetType) {
        ParameterizedType parameterizedType = (ParameterizedType) targetType;
        Type rawType = parameterizedType.getRawType();
        //泛型类 本身有@Body注解 例如: Person<String>
        Class<?> argType = (Class<?>) rawType;
        return argType.isAnnotationPresent(Body.class) || Objects.equals(rootNode.getCovertType(), CovertParamType.RESP.getKey());
    }

    /**
     * 检查泛型参数中的Body注解不能超过1个
     *
     * @param targetType 目标类型
     * @param methodInfo 方法信息
     */
    private static void checkGenericsTypeBodyParamCount(GatewayDocParamDto rootNode, GatewayDocDto methodInfo, Type targetType) {
        if (targetType.getTypeName().contains(BASE_RESP_NAME)) {
            return;
        }
        String paramName = String.format("%s-%s", methodInfo.getResourceName(), rootNode.getParamName());
        Integer genericsTypeCount = StringUtil.countChars(targetType.getTypeName(), StringPool.LEFT_CHEV);
        Assert.isTrue(genericsTypeCount <= 1, "param [%s] can not contain more than 1 generics type", paramName);
        if (targetType instanceof ParameterizedType) {
            ParameterizedType parameterizedType = (ParameterizedType) targetType;
            Type[] typeArguments = parameterizedType.getActualTypeArguments();
            int bodyParamCount = 0;
            for (Type type : typeArguments) {
                Class<?> argType = (Class<?>) type;
                if (argType.isAnnotationPresent(Body.class)) {
                    ++bodyParamCount;
                }
            }
            Assert.isTrue(bodyParamCount <= 1, "generics param [%s] can not contain more than 1 [Body.class] annotation", paramName);
        }
    }

    /**
     * 记录方法的返回值类型
     *
     * @param method     方法对象
     * @param methodInfo 方法信息
     * @return 参数对象
     */
    private static GatewayDocParamDto recordMethodReturn(Method method, GatewayDocDto methodInfo, Map<String, GatewayDocParamType> typeMap) {
        Type returnType = method.getGenericReturnType();
        returnType = covertVoidType(returnType);
        String methodDefKey = methodInfo.getResourceDefKey();
        GatewayDocParamDto rootNode = buildBodyParamRootNode(methodDefKey);
        rootNode.setFieldType(RETURN_RESULT_TYPE);
        rootNode.setParamName(METHOD_RESOURCE_NAME);
        //如果是 bodyParam
        buildBodyDocItem(rootNode, returnType, typeMap, new HashSet<>());
        return rootNode;
    }

    /**
     * 包装void返回类型
     *
     * @param returnType 返回类型
     * @return 包装后的返回类型
     */
    private static Type covertVoidType(Type returnType) {
        if (returnType.getTypeName().equals("void")) {
            return Void.class;
        } else {
            return returnType;
        }
    }

    /**
     * 包装通用请求的类型名称
     *
     * @param typeName 类型名称
     * @return 包装后的请求类型完全限定名
     */
    private static String buildBaseReqCovertTypeName(String typeName) {
        return String.format("com.tkfc.core.common.pojo.BasePageVoRequest<%s>", typeName);
    }

    /**
     * 包装通用响应的类型名称
     *
     * @param typeName 类型名称
     * @return 包装后的响应类型完全限定名
     */
    private static String buildBaseRespCovertTypeName(String typeName) {
        return String.format("com.tkfc.core.common.pojo.BaseResponse<%s>", typeName);
    }

    /**
     * 包装通用请求的mock数据结构
     *
     * @param mockData 包装前mock数据
     * @return 包装后的mock数据
     */
    @SuppressWarnings("all")
    private static String buildBaseReqCovertMockData(Object mockData) {
        MockConfig mockConfig = new MockConfig();
        mockConfig.setEnabledCircle(Boolean.TRUE);
        BasePageVoRequest<Object> mock = new BasePageVoRequest<>();
        mock.setPhysicallyDeleted(JMockData.mock(PhysicallyDeleted.class, mockConfig));
        mock.setSort(JMockData.mock(Sort.class, mockConfig));
        mock.setPagination(JMockData.mock(Pagination.class, mockConfig));
        mock.setData(mockData);
        return JsonUtil.toJsonWithReferenceDetection(mock);
    }

    /**
     * 包装通用响应的mock数据结构
     *
     * @param mockData 包装前mock数据
     * @return 包装后的mock数据
     */
    @SuppressWarnings("all")
    private static String buildBaseRespCovertMockData(Object mockData) {
        MockConfig mockConfig = new MockConfig();
        mockConfig.setEnabledCircle(Boolean.TRUE);
        BaseResponse<Object> mock = BaseResponse.ok(mockData);
        mock.setTimestamp(JMockData.mock(Long.class, mockConfig));
        mock.setStatus(JMockData.mock(Integer.class, mockConfig));
        mock.setPath(JMockData.mock(String.class, mockConfig));
        mock.setSort(JMockData.mock(Sort.class, mockConfig));
        mock.setPagination(JMockData.mock(Pagination.class, mockConfig));
        mock.setMessage(JMockData.mock(String.class, mockConfig));
        mock.setError(JMockData.mock(String.class, mockConfig));
        mock.setData(mockData);
        return JsonUtil.toJsonWithReferenceDetection(mock);
    }

    /**
     * 包装通用请求结构
     *
     * @param rootNode 根节点
     * @param typeMap  类型map
     */
    private static void covertBaseReqResp(GatewayDocParamDto rootNode, Map<String, GatewayDocParamType> typeMap) {
        boolean notBodyParam = !rootNode.getParamType().equals(BodyParam.class.getSimpleName());
        if (notBodyParam) {
            return;
        }

        boolean isReturn = Objects.equals(rootNode.getParamName(), METHOD_RESOURCE_NAME);
        boolean notReturn = !isReturn;
        GatewayDocParamType paramType = typeMap.get(cleanBaseTypeName(rootNode.getTypeDefKey()));
        if (notReturn) {
            boolean notContainsBaseWarp = !paramType.getFullType().contains(BASE_PAGE_VO_REQUEST_NAME);
            if (notContainsBaseWarp) {
                paramType.setFullType(buildBaseReqCovertTypeName(paramType.getFullType()));
            }
            paramType.setMockData(buildBaseReqCovertMockData(JsonUtil.getAutoTypeWithDefaultValue(paramType.getMockData())));
        }

        boolean withoutRespCovert = !paramType.getFullType().contains(BASE_RESPONSE_NAME);
        boolean notVoid = !paramType.getSimpleType().contains(VOID_TYPE);
        if (isReturn && withoutRespCovert && notVoid) {
            paramType.setFullType(buildBaseRespCovertTypeName(paramType.getFullType()));
            paramType.setMockData(buildBaseRespCovertMockData(JsonUtil.getAutoTypeWithDefaultValue(paramType.getMockData())));
        }
    }

}
