package com.tkfc.core.toolkit;


import com.tkfc.core.constants.StringPool;
import com.tkfc.core.throwable.BizException;
import com.tkfc.core.throwable.base.ErrorCode;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 反射工具类.
 * 提供访问私有变量,获取泛型类型Class, 提取集合中元素的属性, 转换字符串到对象等Util函数.
 *
 * @author 0neBean
 */
@Slf4j
public class ReflectionUtil {


    /**
     * 调用Getter方法.
     *
     * @param obj          调用对象
     * @param propertyName 属性名
     * @return 属性值
     */
    public static Object invokeGetterMethod(Object obj, String propertyName) {
        String getterMethodName = "get" + StringUtils.capitalize(propertyName);
        return invokeMethod(obj, getterMethodName, new Class[]{}, new Object[]{});
    }

    /**
     * 调用Setter方法.使用value的Class来查找Setter方法.
     *
     * @param obj          调用对象
     * @param propertyName 属性名
     * @param value        为空用value的class代替
     */
    public static void invokeSetterMethod(Object obj, String propertyName, Object value) {
        invokeSetterMethod(obj, propertyName, value, null);
    }


    /**
     * 调用Setter方法.
     *
     * @param obj          调用对象
     * @param propertyName 属性名 用于查找Setter方法,为空时使用value的Class替代.
     * @param value        为空用value的class代替
     * @param propertyType 属性类型
     */
    public static void invokeSetterMethod(Object obj, String propertyName, Object value, Class<?> propertyType) {
        Class<?> type = propertyType != null ? propertyType : value.getClass();
        String setterMethodName = "set" + StringUtils.capitalize(propertyName);
        invokeMethod(obj, setterMethodName, new Class[]{type}, new Object[]{value});
    }

    /**
     * 直接读取对象属性值, 无视private/protected修饰符, 不经过getter函数.
     *
     * @param obj       调用对象
     * @param fieldName 字段名
     * @return 属性值
     */
    public static Object getFieldValue(final Object obj, final String fieldName) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        }

        Object result = null;
        try {
            result = field.get(obj);
        } catch (IllegalAccessException e) {
            log.error("不可能抛出的异常{}", e.getMessage());
        }
        return result;
    }


    /**
     * 直接读取对象的成员变量, 无视private/protected修饰符.
     *
     * @param obj       调用对象
     * @param fieldName 字段名
     * @return 字段
     */
    public static Field getField(final Object obj, final String fieldName) {
        return getAccessibleField(obj, fieldName);
    }

    /**
     * 设置对象字段如果存在值
     *
     * @param obj       调用对象
     * @param fieldName 字段名
     * @param value     为空用value的class代替
     */
    public static void setFieldValueIfExits(final Object obj, final String fieldName, final Object value) {
        Field field = getAccessibleField(obj, fieldName);
        if (Objects.nonNull(field) && Objects.nonNull(value)) {
            try {
                field.set(obj, value);
            } catch (IllegalAccessException e) {
                log.error("不可能抛出的异常:{}", e.getMessage());
            }
        }
    }

    /**
     * 直接设置对象属性值, 无视private/protected修饰符, 不经过setter函数.
     *
     * @param obj       调用对象
     * @param fieldName 字段名
     * @param value     为空用value的class代替
     */
    public static void setFieldValue(final Object obj, final String fieldName, final Object value) {
        Field field = getAccessibleField(obj, fieldName);

        if (field == null) {
            throw new IllegalArgumentException("Could not find field [" + fieldName + "] on target [" + obj + "]");
        }

        try {
            field.set(obj, value);
        } catch (IllegalAccessException e) {
            log.error("不可能抛出的异常:{}", e.getMessage());
        }
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField,   并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     *
     * @param obj       调用对象
     * @param fieldName 字段名
     * @return 字段
     */
    public static Field getAccessibleField(final Object obj, final String fieldName) {
        Assert.notNull(obj, "object不能为空");
        Assert.hasText(fieldName, "fieldName");
        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Field field = superClass.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            } catch (NoSuchFieldException e) {
                // Field不在当前类定义,继续向上转型
            }
        }
        return null;
    }

    /**
     * 循环向上转型, 获取对象的DeclaredField,   并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     *
     * @param Clazz 类型
     * @return 字段
     */
    public static List<Field> getAccessibleFields(final Class<?> Clazz) {
        List<Field> result = new ArrayList<>();
        for (Class<?> superClass = Clazz; superClass != Object.class; superClass = superClass.getSuperclass()) {
            Field[] declaredFields = superClass.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
                result.add(field);
            }
        }
        return result;
    }


    /**
     * 直接调用对象方法, 无视private/protected修饰符.
     * 用于一次性调用的情况.
     *
     * @param obj            调用对象
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @param args           参数
     * @return 方法返回值
     */
    public static Object invokeMethod(final Object obj, final String methodName, final Class<?>[] parameterTypes, final Object[] args) {
        Method method = getAccessibleMethod(obj, methodName, parameterTypes);
        if (method == null) {
            throw new IllegalArgumentException("Could not find method [" + methodName + "] on target [" + obj + "]");
        }

        try {
            return method.invoke(obj, args);
        } catch (Exception e) {
            throw convertReflectionExceptionToUnchecked(e);
        }
    }

    /**
     * 获取随机的枚举实例
     *
     * @param clazz 类
     * @return enum
     */
    public static java.lang.Enum<?> getRandomEnumInstance(Class<? extends java.lang.Enum<?>> clazz) {
        // 获取所有常量
        Object[] objects = clazz.getEnumConstants();
        return (Enum<?>) objects[0];
    }

    /**
     * 直接调用对象方法, 无视private/protected修饰符.
     * 用于一次性调用的情况.
     *
     * @param clazz          调用对象class
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @param args           参数
     * @return 方法返回值
     */
    public static Object invokeMethod(final Class<?> clazz, final String methodName, final Class<?>[] parameterTypes, final Object[] args) {
        Object result;
        try {
            Method method = clazz.getMethod(methodName, parameterTypes);
            if (Enum.class.isAssignableFrom(clazz)) {
                Object enumConstant = clazz.getEnumConstants()[0];
                result = method.invoke(enumConstant, args);
            } else {
                result = method.invoke(null, args);
            }
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            throw new BizException(ErrorCode.REFLECTION_CALL_METHOD_FAIL, e);
        }
        return result;
    }


    /**
     * 循环向上转型, 获取对象的DeclaredMethod,并强制设置为可访问.
     * 如向上转型到Object仍无法找到, 返回null.
     * 用于方法需要被多次调用的情况. 先使用本函数先取得Method,然后调用Method.invoke(Object obj, Object... args)
     *
     * @param obj            调用对象
     * @param methodName     方法名
     * @param parameterTypes 参数类型
     * @return 方法对象
     */
    public static Method getAccessibleMethod(final Object obj, final String methodName, final Class<?>... parameterTypes) {
        Assert.notNull(obj, "object不能为空");

        for (Class<?> superClass = obj.getClass(); superClass != Object.class; superClass = superClass.getSuperclass()) {
            try {
                Method method = superClass.getDeclaredMethod(methodName, parameterTypes);

                method.setAccessible(true);

                return method;

            } catch (NoSuchMethodException e) {
                // Method不在当前类定义,继续向上转型
            }
        }
        return null;
    }


    /**
     * 将反射时的checked exception转换为unchecked exception.
     *
     * @param e 异常
     * @return 运行异常
     */
    private static RuntimeException convertReflectionExceptionToUnchecked(Exception e) {
        if (e instanceof IllegalAccessException || e instanceof IllegalArgumentException
                || e instanceof NoSuchMethodException) {
            return new IllegalArgumentException("Reflection Exception.", e);
        } else if (e instanceof InvocationTargetException) {
            return new RuntimeException("Reflection Exception.", ((InvocationTargetException) e).getTargetException());
        } else if (e instanceof RuntimeException) {
            return (RuntimeException) e;
        }
        return new RuntimeException("Unexpected Checked Exception.", e);
    }

    /**
     * 得到指定类型的指定位置的泛型实参
     *
     * @param clazz 类型
     * @param index 下标
     * @return 实参
     */
    public static Class<?> findParameterizedType(Class<?> clazz, int index) {
        Type parameterizedType = clazz.getGenericSuperclass();
        //CG LUB subclass target object(泛型在父类上)
        if (!(parameterizedType instanceof ParameterizedType)) {
            parameterizedType = clazz.getSuperclass().getGenericSuperclass();
        }
        if (!(parameterizedType instanceof ParameterizedType)) {
            return null;
        }
        Type[] actualTypeArguments = ((ParameterizedType) parameterizedType).getActualTypeArguments();
        if (actualTypeArguments == null || actualTypeArguments.length == 0) {
            return null;
        }
        return (Class<?>) actualTypeArguments[index];
    }

    public static Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("findParameterizedType got an error , ", e);
        }
        return null;
    }

    public static <T> Object newInstance(String className) {
        try {
            return Class.forName(className).getDeclaredConstructor().newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            log.error("newInstance got an error , ", e);
        }
        return null;
    }

    /**
     * 获取对象(JavaBean)的全部set方法
     *
     * @param cls 对象(JavaBean)
     * @return List<Method>
     */
    private static <T> List<Method> getObjectSetMethods(Class<T> cls) {
        List<Method> setMethods = new ArrayList<>();
        Method[] methods = cls.getMethods();
        for (Method method : methods) {
            if (method.getName().startsWith("set")) {
                setMethods.add(method);
            }
        }
        return setMethods;
    }

    /**
     * 获取接口上的泛型T
     *
     * @param o     接口
     * @param index 泛型索引
     */
    public static Class<?> getInterfaceT(Object o, int index) {
        Type[] types = o.getClass().getGenericInterfaces();
        ParameterizedType parameterizedType = (ParameterizedType) types[index];
        Type type = parameterizedType.getActualTypeArguments()[index];
        return checkType(type, index);

    }


    /**
     * 获取类上的泛型T
     *
     * @param o     接口
     * @param index 泛型索引
     */
    public static Class<?> getClassT(Object o, int index) {
        Type type = o.getClass().getGenericSuperclass();
        return checkType(type, index);
    }


    /**
     * 获取类上的泛型T
     *
     * @param type  类型
     * @param index 泛型索引
     */
    public static Class<?> getTypeT(Type type, int index) {
        return checkType(type, index);
    }


    /**
     * 根据方法签名获取类型
     *
     * @param methodSignature 方法签名
     * @return 类型
     */
    public static Class<?> getMethodSignatureClass(String methodSignature) {
        String className = null;
        if (methodSignature.contains(StringPool.RIGHT_CHEV) && methodSignature.contains(StringPool.LEFT_CHEV) && methodSignature.contains(StringPool.SEMICOLON)) {
            Matcher matcher = Pattern.compile("L(\\w+/)*\\w+(\\$\\w+)*;").matcher(methodSignature);
            while (matcher.find()) {
                String matchedString = matcher.group();
                className = matchedString.substring(1, matchedString.length() - 1).replace('/', '.');
            }
        } else if (methodSignature.contains(StringPool.RIGHT_CHEV) && methodSignature.contains(StringPool.LEFT_CHEV) && !methodSignature.contains(StringPool.SEMICOLON)) {
            className = getClassNameByTypeName(methodSignature);
        } else if (methodSignature.startsWith("L")) {
            if (methodSignature.contains(StringPool.SEMICOLON)) {
                methodSignature = methodSignature.replace(StringPool.SEMICOLON, StringPool.EMPTY);
            }
            className = methodSignature.substring(1).replace('/', '.').replaceAll(StringPool.SEMICOLON, StringPool.EMPTY);
        } else {
            className = methodSignature;
        }
        try {
            return Objects.isNull(className) ? null : Class.forName(className);
        } catch (ClassNotFoundException ignore) {
            return null;
        }
    }

    /**
     * 根据type name 获取class
     *
     * @param typeName 类型名称
     * @return 类型
     */
    public static Class<?> getClassByTypeName(String typeName) {
        String className = getClassNameByTypeName(typeName);
        try {
            return Objects.isNull(className) ? null : Class.forName(className);
        } catch (ClassNotFoundException ignore) {
            return null;
        }
    }

    /**
     * 根据type name 获取class name
     *
     * @param typeName 类型名称
     * @return 类型
     */
    public static String getClassNameByTypeName(String typeName) {
        String className = null;
        if (typeName.contains(StringPool.LEFT_CHEV) && typeName.contains(StringPool.RIGHT_CHEV)) {
            // 使用正则表达式匹配泛型中的类名，支持嵌套泛型
            String regex = "<(?:\\s*([a-zA-Z_][a-zA-Z0-9_]*(?:\\.[a-zA-Z_][a-zA-Z0-9_]*)*)\\s*(?:,\\s*)?)+>";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(typeName);
            // 查找匹配项
            if (matcher.find()) {
                // 返回匹配到的内容
                className = matcher.group(1);
            }
        } else {
            className = typeName;
        }
        return className;
    }

    /**
     * 根据类型name 获取class name
     *
     * @param typeName 类型名称
     * @param index    下标
     * @return class name
     */
    public static String getCLassNameByTypeName(String typeName, int index) {
        // 使用正则表达式匹配泛型中的参数
        String regex = "<\\s*([^<>]+)\\s*>";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(typeName);

        // 查找匹配项
        if (matcher.find()) {
            String paramsString = matcher.group(1);
            // 使用逗号分隔参数
            String[] paramsArray = paramsString.split("\\s*,\\s*");

            // 检查索引是否在合法范围内
            if (index >= 0 && index < paramsArray.length) {
                return paramsArray[index];
            }
        }
        return "";
    }

    /**
     * 根据方法签名获取类名
     *
     * @param methodSignature 方法签名
     * @param index           下标
     * @return 类型
     */
    public static String getMethodSignatureClassName(String methodSignature, Integer index) {
        String[] classPaths = new String[index + 1];
        // 定义正则表达式
        String regex = "L([^<;]+);";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(methodSignature);
        int count = 0;
        while (matcher.find() && count < index + 1) {
            classPaths[count] = matcher.group(1).replace('/', '.');
            count++;
        }
        return classPaths[index];
    }


    /**
     * 检查类型
     *
     * @param type  类型
     * @param index 下标
     * @return Class
     */
    private static Class<?> checkType(Type type, int index) {
        if (type instanceof Class<?>) {
            return (Class<?>) type;
        } else if (type instanceof ParameterizedType) {
            ParameterizedType pt = (ParameterizedType) type;
            Type t = pt.getActualTypeArguments()[index];
            return checkType(t, index);
        } else {
            String className = type == null ? "null" : type.getClass().getName();
            throw new IllegalArgumentException("Expected a Class, ParameterizedType" + ", but <" + type + "> is of type " + className);
        }
    }

    /**
     * 判断对象属性是否是基本数据类型,包括时间
     *
     * @param clazz 类名
     * @return bool
     */
    public static boolean isBaseTypeIncludeData(Class<?> clazz) {
        boolean isDate = clazz.equals(Date.class) || clazz.equals(LocalDate.class);
        return isBaseType(clazz) && isDate;

    }

    /**
     * 判断对象属性是否是基本数据类型
     *
     * @param clazz 类名
     * @return bool
     */
    public static boolean isBaseType(Class<?> clazz) {
        return
                clazz.equals(Void.class) ||
                        clazz.equals(void.class) ||
                        clazz.equals(String.class) ||
                        clazz.equals(Integer.class) ||
                        clazz.equals(int.class) ||
                        clazz.equals(Byte.class) ||
                        clazz.equals(byte.class) ||
                        clazz.equals(Long.class) ||
                        clazz.equals(long.class) ||
                        clazz.equals(Double.class) ||
                        clazz.equals(double.class) ||
                        clazz.equals(Float.class) ||
                        clazz.equals(float.class) ||
                        clazz.equals(Character.class) ||
                        clazz.equals(char.class) ||
                        clazz.equals(Short.class) ||
                        clazz.equals(short.class) ||
                        clazz.equals(Boolean.class) ||
                        clazz.equals(boolean.class);

    }


    /**
     * 判断类似否指定名字的注解
     *
     * @param clazz               类型
     * @param annotationClassName 注解名类型
     * @return bool
     */
    public static boolean hasAnnotation(Class<?> clazz, String annotationClassName) {
        Annotation[] annotation = clazz.getAnnotations();
        for (Annotation a : annotation) {
            Class<?> annotationType = a.annotationType();
            String annName = annotationType.getSimpleName();
            if (annName.equalsIgnoreCase(annotationClassName)) {
                return true;
            }
        }
        return false;
    }


    /**
     * 获取spring boot 启动类class
     *
     * @return class
     */
    public static Class<?> getSpringBootMainClass() {
        StackTraceElement[] es = Thread.currentThread().getStackTrace();
        for (StackTraceElement e : es) {
            String className = e.getClassName();
            Class<?> clazz = getClassByClassName(className);
            if (clazz.isAnnotationPresent(SpringBootApplication.class) || clazz.isAnnotationPresent(EnableAutoConfiguration.class)) {
                return clazz;
            }
        }
        return getMainApplicationClass();
    }

    /**
     * 获取main class
     *
     * @return main class
     */
    public static Class<?> getMainApplicationClass() {
        try {
            StackTraceElement[] stackTrace = new RuntimeException().getStackTrace();
            for (StackTraceElement stackTraceElement : stackTrace) {
                if ("main".equals(stackTraceElement.getMethodName())) {
                    return Class.forName(stackTraceElement.getClassName());
                }
            }
        } catch (ClassNotFoundException ignore) {
            // Swallow and continue
        }
        return null;
    }

    /**
     * 通过className 获取class
     *
     * @param className 类名
     * @return class
     */
    public static Class<?> getClassByClassName(String className) {
        Class<?> aClass = null;
        try {
            aClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.error("getClassByClassName got an error , ", e);
        }
        return aClass;
    }

    /**
     * 获取包名下所有类
     *
     * @param packagePath 包名
     * @return java.util.List<java.lang.Class < ?>>
     * @author 0neBean
     * @since 1:14 2022/4/18
     */
    public static List<Class<?>> getClassFromClassPath(String packagePath) throws Exception {
        ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory(resourcePatternResolver);
        // 加载系统所有类资源
        Resource[] resources = resourcePatternResolver.getResources("classpath*:" + packagePath.replaceAll("[.]", "/") + "/**/*.class");
        List<Class<?>> list = new ArrayList<Class<?>>();
        // 把每一个class文件找出来
        for (Resource r : resources) {
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(r);
            Class<?> clazz = ClassUtils.forName(metadataReader.getClassMetadata().getClassName(), null);
            list.add(clazz);
        }
        return list;
    }
}
