package com.tkfc.boot.starter.mybatis.interceptor;

import com.alibaba.fastjson2.JSONObject;
import com.tkfc.core.toolkit.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.apache.ibatis.type.TypeHandlerRegistry;
import org.springframework.util.CollectionUtils;

import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;


/**
 * 打印每条SQL内容及运行时长
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/11/20 10:40
 */
@Slf4j
@Intercepts(value = {
        @Signature(type = Executor.class, method = "update", args = {MappedStatement.class, Object.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class})})
public class LogSQLExecutionTimeInterceptor implements Interceptor {

    private final static String PRINT_SQL_SWITCH_KEY = "spring.datasource.print.sql";


    public Object intercept(Invocation invocation) throws Throwable {
        final Object[] args = invocation.getArgs();
        //获取原始的ms
        MappedStatement ms = (MappedStatement) args[0];
        Object parameter = null;
        //获取参数，if语句成立，表示sql语句有参数，参数格式是map形式
        if (invocation.getArgs().length > 1) {
            parameter = invocation.getArgs()[1];
        }
        // 获取到节点的id,即sql语句的id
        String sqlId = ms.getId();
        // BoundSql就是封装myBatis最终产生的sql类
        BoundSql boundSql = ms.getBoundSql(parameter);
        // 获取节点的配置
        Configuration configuration = ms.getConfiguration();
        // 获取到最终的sql log
        String sql = showSql(configuration, boundSql);
        StringBuilder logBuffer = getSqlLog(sql, sqlId);
        String print = Optional.ofNullable(PropUtil.getInstance().getConfig(PRINT_SQL_SWITCH_KEY)).orElse("false");
        boolean printBool = ParseUtil.toBoolean(print);
        long start = System.currentTimeMillis();
        Object returnValue;
        try {
            returnValue = invocation.proceed();
        } catch (Exception e) {
            if (printBool) {
                log.info(logBuffer.toString());
            }
            throw e;
        }
        long end = System.currentTimeMillis();
        long time = end - start;
        String sqlResultLog = getSqlResultLog(logBuffer, sql, time, returnValue);
        if (printBool) {
            log.info(sqlResultLog);
        }
        return returnValue;
    }


    // 封装了一下sql语句，使得结果返回完整xml路径下的sql语句节点id + sql语句
    public static StringBuilder getSqlLog(String sql, String sqlId) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("\n\n");
        stringBuilder.append("================================= 【execute sql start】 =================================");
        stringBuilder.append("\n\n");
        stringBuilder.append("executed method : ").append(sqlId).append("\n");
        stringBuilder.append("execution sql   : ").append(sql).append("\n");
        return stringBuilder;
    }

    // 封装了一下sql语句，使得结果返回完整xml路径下的sql语句节点id + sql语句
    public static String getSqlResultLog(StringBuilder logBuffer, String sql, long time, Object returnValue) {
        logBuffer.append("execution time  : ").append(time).append(" ms").append("\n");
        if (sql.toLowerCase().startsWith("select")) {
            logBuffer.append("selected        : [").append(CollectionUtil.getObjCastListSize(returnValue)).append("] row(s)");
        } else {
            logBuffer.append("affected        : [").append(returnValue).append("] row(s)");
        }
        logBuffer.append("\n\n");
        logBuffer.append("================================= 【 execute sql end 】 =================================");
        logBuffer.append("\n\n");
        return logBuffer.toString();
    }

    /* 如果参数是String，则添加单引号， 如果是日期，则转换为时间格式器并加单引号；  对参数是null和不是null的情况作了处理　　*/
    private static String getParameterValue(Object obj) {
        String value;
        if (obj instanceof String) {
            value = "'" + obj + "'";
        } else if (obj instanceof Date) {
            Date date = (Date) obj;
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(date) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }

        }
        return value;
    }

    // 进行？的替换
    public static String showSql(Configuration configuration, BoundSql boundSql) {
        // 获取参数
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql
                .getParameterMappings();
        // sql语句中多个空格都用一个空格代替
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (!CollectionUtils.isEmpty(parameterMappings) && parameterObject != null) {
            // 获取类型处理器注册器，类型处理器的功能是进行java类型和数据库类型的转换　　　　　
            // 如果根据parameterObject.getClass(）可以找到对应的类型，则替换
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
            } else {
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                JSONObject param = new JSONObject();
                // MetaObject主要是封装了originalObject对象，提供了get和set的方法用于获取和设置originalObject的属性值,
                // 主要支持对JavaBean、Collection、Map三种类型对象的操作
                for (int i = 0; i < parameterMappings.size(); i++) {
                    String paramKey = StringUtil.concat("param",ParseUtil.toString(i));
                    String propertyName = parameterMappings.get(i).getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        obj = localDateTimeParam2DateStr(obj);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        // 该分支是动态sql
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        obj = localDateTimeParam2DateStr(obj);
                        param.put(paramKey,Matcher.quoteReplacement(getParameterValue(obj)));
                        sql = sql.replaceFirst("\\?",String.format("\\$\\{%s\\}",paramKey));
                    } else {
                        //打印出缺失，提醒该参数缺失并防止错位
                        param.put(paramKey,"[parameter missing]");
                        sql = sql.replaceFirst("\\?",String.format("\\$\\{%s\\}",paramKey));
                    }
                }
                if (!param.isEmpty()) {
                    sql = StringUtil.replaceExpression(sql,param);
                }
            }
        }
        return sql;
    }

    /**
     * 时间格式转换成 time 字符串
     *
     * @param obj 时间
     * @return 字符串
     */
    private static Object localDateTimeParam2DateStr(Object obj) {
        if (obj.getClass().isAssignableFrom(LocalDateTime.class)) {
            Date date = DateUtil.localDateTimeToDate((LocalDateTime) obj);
            return DateUtil.dateToString(date);
        }
        return obj;
    }

    @Override
    public Object plugin(Object arg0) {
        return Plugin.wrap(arg0, this);
    }

    @Override
    public void setProperties(Properties arg0) {
    }
}
