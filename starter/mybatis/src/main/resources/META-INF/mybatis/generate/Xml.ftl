<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<!--author ${author}-->
<!--${description} mapper-->
<!--since ${createTime}-->
<mapper namespace="${daoPackageName}.${modelName}Mapper">

    <#if fieldArr?exists>
    <sql id="basicFiled">
            t.id,
        <#list fieldArr as item>
            t.${item.originalName}<#sep>, </#sep>
        </#list>
    </sql>

    <resultMap id="basicResultMap" type="${modelPackageName}.${modelName}">
            <id column="id" jdbcType="INTEGER" property="id"/>
        <#list fieldArr as item>
            <#if item.isEnum>
            <result column="${item.originalName}" jdbcType="${item.jdbcType}" property="${item.columnName}" javaType="${item.enumPackageName}.${item.enumClassName}" typeHandler="com.tkfc.boot.starter.mybatis.builder.EnumValueTypeHandler"/>
            </#if>
            <#if !item.isEnum && item.columnType != 'JSONObject' && item.columnType != 'JSONArray'>
            <result column="${item.originalName}" jdbcType="${item.jdbcType}" property="${item.columnName}"/>
            </#if>
            <#if !item.isEnum && item.columnType == 'JSONObject'>
            <result column="${item.originalName}" jdbcType="OTHER" property="${item.columnName}" typeHandler="com.tkfc.boot.starter.mybatis.builder.JsonObjectHandler"/>
            </#if>
            <#if !item.isEnum && item.columnType == 'JSONArray'>
            <result column="${item.originalName}" jdbcType="OTHER" property="${item.columnName}" typeHandler="com.tkfc.boot.starter.mybatis.builder.JsonArrayHandler"/>
            </#if>
        </#list>
    </resultMap>
    </#if>


</mapper>
