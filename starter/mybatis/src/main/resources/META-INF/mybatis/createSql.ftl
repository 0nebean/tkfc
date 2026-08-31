<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="${mapperFullName}">

    <sql id="tableName">
        <choose>
            <when test="_parameter.containsKey('tableSuffix')">
                ${tableName}${r"${tableSuffix}"}
            </when>
            <otherwise>
                ${tableName}
            </otherwise>
        </choose>
    </sql>

    <!--sql条件-->
    <sql id="allColumn">
        <#if properties?exists>
            <trim suffixOverrides=",">
                <#list properties as item>
                    t.`${item.fieldNameUnderLine}`,
                </#list>
            </trim>
        </#if>
    </sql>



    <resultMap id="sysResultMap" type="${modelFullName}">
        <id column="id" jdbcType="INTEGER" property="id"/>
        <#if properties?exists>
            <#list properties as item>
                <#if item.returnType?ends_with("Enum")>
                    <result column="${item.fieldNameUnderLine}" property="${item.field.name}" javaType="${item.returnType}" typeHandler="com.tkfc.boot.starter.mybatis.builder.EnumValueTypeHandler"/>
                </#if>
                <#if !item.returnType?ends_with("Enum") && item.returnType != 'com.alibaba.fastjson2.JSONObject' && item.returnType != 'com.alibaba.fastjson2.JSONArray'>
                    <result column="${item.fieldNameUnderLine}" property="${item.field.name}"/>
                </#if>
                <#if !item.returnType?ends_with("Enum") && item.returnType == 'com.alibaba.fastjson2.JSONArray'>
                    <result column="${item.fieldNameUnderLine}" jdbcType="OTHER" property="${item.field.name}" typeHandler="com.tkfc.boot.starter.mybatis.builder.JsonArrayHandler"/>
                </#if>
                <#if !item.returnType?ends_with("Enum") && item.returnType == 'com.alibaba.fastjson2.JSONObject'>
                    <result column="${item.fieldNameUnderLine}" jdbcType="OTHER" property="${item.field.name}" typeHandler="com.tkfc.boot.starter.mybatis.builder.JsonObjectHandler"/>
                </#if>
            </#list>
        </#if>
    </resultMap>

    <!--sql条件-->
    <sql id="defaultQueryColumn">
        <#if properties?exists>
            <trim suffixOverrides=",">
                <#list properties as item>
                    <#if item.defaultForQuery>
                        t.`${item.fieldNameUnderLine}`,
                    </#if>
                </#list>
            </trim>
        </#if>
    </sql>

    <update id="updateBatch">
        UPDATE
        <include refid="tableName"/> t
        <trim prefix="set" suffixOverrides=",">
            <#if properties?exists>
                <#list properties as item>
                    <#if item.field.name != 'id'>
                        <trim prefix="`${item.fieldNameUnderLine}` = case" suffix="end,">
                            <foreach collection="list" item="item" index="index">
                                <if test="item.${item.field.name} != null">
                                    when id = ${r"#{item.id}"} then
                                    <#if item.returnType == 'com.alibaba.fastjson2.JSONObject'>
                                        <if test="item.${item.field.name} != null">
                                            ${r"#{item."}${item.field.name},jdbcType=OTHER,typeHandler=com.tkfc.boot.starter.mybatis.builder.JsonObjectHandler${r"}"}
                                        </if>
                                    <#elseif item.returnType == 'com.alibaba.fastjson2.JSONArray'>
                                        <if test="item.${item.field.name} != null">
                                            ${r"#{item."}${item.field.name},jdbcType=OTHER,typeHandler=com.tkfc.boot.starter.mybatis.builder.JsonArrayHandler${r"}"}
                                        </if>
                                    <#else>
                                        ${r"#{item."}${item.field.name}${r"}"}
                                    </#if>
                                </if>
                                <if test="item.${item.field.name} == null and ${item.nullUpdatable?c}">
                                    <#if item.returnType == 'com.alibaba.fastjson2.JSONObject' || item.returnType == 'com.alibaba.fastjson2.JSONArray'>
                                        `${item.fieldNameUnderLine}` = NULL
                                    <#else>
                                        `${item.fieldNameUnderLine}` = DEFAULT
                                    </#if>
                                </if>
                            </foreach>
                        </trim>
                    </#if>
                </#list>
            </#if>
        </trim>
        <where>
            <include refid="common.idsForEach"/>
        </where>
    </update>

    <update id="update">
        UPDATE
        <include refid="tableName"/> t
        <trim prefix="set" suffixOverrides=",">
            <#if properties?exists>
                <#list properties as item>
                    <#if item.field.name != 'id'>
                        <#if item.returnType == 'com.alibaba.fastjson2.JSONObject'>
                            <if test="entity.${item.field.name} != null">
                                `${item.fieldNameUnderLine}` =
                                ${r"#{entity."}${item.field.name},jdbcType=OTHER,typeHandler=com.tkfc.boot.starter.mybatis.builder.JsonObjectHandler${r"}"},
                            </if>
                            <if test="entity.${item.field.name} == null and ${item.nullUpdatable?c}">
                                `${item.fieldNameUnderLine}` = NULL,
                            </if>
                        <#elseif item.returnType == 'com.alibaba.fastjson2.JSONArray'>
                            <if test="entity.${item.field.name} != null">
                                `${item.fieldNameUnderLine}` =
                                ${r"#{entity."}${item.field.name},jdbcType=OTHER,typeHandler=com.tkfc.boot.starter.mybatis.builder.JsonArrayHandler${r"}"},
                            </if>
                            <if test="entity.${item.field.name} == null and ${item.nullUpdatable?c}">
                                `${item.fieldNameUnderLine}` = NULL,
                            </if>
                        <#else>
                            <if test="entity.${item.field.name} != null">
                                `${item.fieldNameUnderLine}` = ${r"#{entity."}${item.field.name}${r"}"},
                            </if>
                            <if test="entity.${item.field.name} == null and ${item.nullUpdatable?c}">
                                `${item.fieldNameUnderLine}` = DEFAULT,
                            </if>
                        </#if>
                    </#if>
                </#list>
            </#if>
        </trim>
        <where>
            <include refid="common.beforeWhereSql"/>
            <include refid="common.conditionSql"/>
        </where>
    </update>

    <update id="delete">
        <#if logicalDeleteField?? && logicalDeleteField != ''>
            UPDATE
            <include refid="tableName"/> t
            SET t.${logicalDeleteField} = '1'
            <where>
                <include refid="common.beforeWhereSql"/>
                <include refid="common.conditionSql"/>
            </where>
        </#if>
    </update>

    <delete id="deletePhysically">
        DELETE FROM
        <include refid="tableName"/> t
        <where>
            <include refid="common.beforeWhereSql"/>
            <include refid="common.conditionSql"/>
        </where>
    </delete>

    <insert id="saveBatch" useGeneratedKeys="true" keyProperty="list.id">
        INSERT INTO
        <include refid="tableName"/>
        (
        <trim suffixOverrides=",">
            <#list properties as item>
                <foreach collection="list" item="item" index="index">
                    <if test="index == 0">
                        <#if item.fieldNameUnderLine == 'id' ||  item.fieldNameUnderLine == 'operator_id' ||  item.fieldNameUnderLine == 'operator_name' ||  item.fieldNameUnderLine == 'is_deleted' ||  item.fieldNameUnderLine == 'create_time' ||  item.fieldNameUnderLine == 'update_time'>
                            <#if item.fieldNameUnderLine == 'update_time'>
                                `update_time`,
                            <#else>
                                <if test="item.${item.field.name} != null">
                                    `${item.fieldNameUnderLine}`,
                                </if>
                            </#if>
                        <#else>
                            <#if item.returnType == 'com.alibaba.fastjson2.JSONObject' || item.returnType == 'com.alibaba.fastjson2.JSONArray'>
                                <if test="item.${item.field.name} != null">
                                    `${item.fieldNameUnderLine}`,
                                </if>
                            <#else>
                                `${item.fieldNameUnderLine}`,
                            </#if>
                        </#if>
                    </if>
                </foreach>
            </#list>
        </trim>
        )
        VALUES
        <foreach collection="list" item="item" separator=",">
            (
            <trim suffixOverrides=",">
                <#list properties as item>
                    <#if item.fieldNameUnderLine == 'id' ||  item.fieldNameUnderLine == 'operator_id' ||  item.fieldNameUnderLine == 'operator_name' ||  item.fieldNameUnderLine == 'is_deleted' ||  item.fieldNameUnderLine == 'create_time' ||  item.fieldNameUnderLine == 'update_time'>
                        <#if item.fieldNameUnderLine == 'update_time'>
                            now(),
                        <#else>
                            <if test="item.${item.field.name} != null">
                                ${r"#{item."}${item.field.name}${r"}"},
                            </if>
                        </#if>
                    <#else>
                        <#if item.returnType == 'com.alibaba.fastjson2.JSONObject'>
                            <if test="item.${item.field.name} != null">
                                ${r"#{item."}${item.field.name},jdbcType=OTHER,typeHandler=com.tkfc.boot.starter.mybatis.builder.JsonObjectHandler${r"}"},
                            </if>
                        <#elseif item.returnType == 'com.alibaba.fastjson2.JSONArray'>
                            <if test="item.${item.field.name} != null">
                                ${r"#{item."}${item.field.name},jdbcType=OTHER,typeHandler=com.tkfc.boot.starter.mybatis.builder.JsonArrayHandler${r"}"},
                            </if>
                        <#else>
                            <choose>
                                <when test="item.${item.field.name} != null">
                                    ${r"#{item."}${item.field.name}${r"}"},
                                </when>
                                <otherwise>
                                    DEFAULT,
                                </otherwise>
                            </choose>
                        </#if>
                    </#if>
                </#list>
            </trim>
            )
        </foreach>

    </insert>

    <insert id="saveBatchOnDuplicateKeyUpdate" useGeneratedKeys="true" keyProperty="id">
        INSERT INTO
        <include refid="tableName"/>
        (
        <trim suffixOverrides=",">
            <#list properties as item>
                <foreach collection="list" item="item" index="index">
                    <if test="index == 0">
                        <#if  item.fieldNameUnderLine != 'id' && item.fieldNameUnderLine != 'is_deleted' &&  item.fieldNameUnderLine != 'create_time' &&  item.fieldNameUnderLine != 'update_time'>
                            `${item.fieldNameUnderLine}`,
                        </#if>
                    </if>
                </foreach>
            </#list>
        </trim>
        )
        VALUES
        <foreach collection="list" item="item" separator=",">
            (
            <trim suffixOverrides=",">
                <#list properties as item>
                    <#if  item.fieldNameUnderLine != 'id' && item.fieldNameUnderLine != 'is_deleted' &&  item.fieldNameUnderLine != 'create_time' &&  item.fieldNameUnderLine != 'update_time'>
                        <#if item.returnType == 'com.alibaba.fastjson2.JSONObject'>
                            <choose>
                                <when test="item.${item.field.name} != null">
                                    ${r"#{item."}${item.field.name},jdbcType=OTHER,typeHandler=com.tkfc.boot.starter.mybatis.builder.JsonObjectHandler${r"}"},
                                </when>
                                <otherwise>
                                    DEFAULT,
                                </otherwise>
                            </choose>
                        <#elseif item.returnType == 'com.alibaba.fastjson2.JSONArray'>
                            <choose>
                                <when test="item.${item.field.name} != null">
                                    ${r"#{item."}${item.field.name},jdbcType=OTHER,typeHandler=com.tkfc.boot.starter.mybatis.builder.JsonArrayHandler${r"}"},
                                </when>
                                <otherwise>
                                    DEFAULT,
                                </otherwise>
                            </choose>
                        <#else>
                            <choose>
                                <when test="item.${item.field.name} != null">
                                    ${r"#{item."}${item.field.name}${r"}"},
                                </when>
                                <otherwise>
                                    DEFAULT,
                                </otherwise>
                            </choose>
                        </#if>
                    </#if>
                </#list>
            </trim>
            )
        </foreach>

        ON DUPLICATE KEY UPDATE
        <trim suffixOverrides=",">
            <#if properties?exists>
                <#list properties as item>
                    <foreach collection="list" item="item" index="index">
                        <if test="index == 0">
                            <#if  item.fieldNameUnderLine != 'id' && item.fieldNameUnderLine != 'is_deleted' &&  item.fieldNameUnderLine != 'create_time' &&  item.fieldNameUnderLine != 'update_time'>
                                <if test="item.${item.field.name} != null">
                                    `${item.fieldNameUnderLine}` = VALUES(`${item.fieldNameUnderLine}`),
                                </if>
                            </#if>
                        </if>
                    </foreach>
                </#list>
            </#if>
        </trim>

    </insert>

    <select id="find" resultMap="sysResultMap">
        SELECT
        <choose>
            <when test="sqlWrapper.withoutIgnoreField">
                <include refid="defaultQueryColumn"/>
            </when>
            <otherwise>
                <include refid="allColumn"/>
            </otherwise>
        </choose>
        FROM
        <include refid="tableName"/>
        t
        <include refid="common.dataPermissionJoinSql"/>
        <where>
            <include refid="common.beforeWhereSql"/>
            <include refid="common.conditionSql"/>
            <#if logicalDeleteField?? && logicalDeleteField != ''>
                <choose>
                    <when test="sqlWrapper != null and sqlWrapper.physicallyDeleted != null and sqlWrapper.physicallyDeleted.value != null and sqlWrapper.physicallyDeleted.value == 1">
                        AND (t.${logicalDeleteField} = '0' or t.${logicalDeleteField} = '1')
                    </when>
                    <otherwise>
                        AND t.${logicalDeleteField} = '0'
                    </otherwise>
                </choose>
            </#if>
            <include refid="common.dataPermissionPermissionSql"/>
            <include refid="common.groupBySql"/>
            <#if ((outerSystemModel!false) || (customSystemField!false)) && orderBy?? && orderBy != ''>
            <choose>
                <when test="sqlWrapper != null and sqlWrapper.sort != null and sqlWrapper.sort.orderBy != '' and sqlWrapper.sort.orderBy != 'id'">
                    order by t.${r"${sqlWrapper.sort.orderBy}"}
                    <if test="sqlWrapper.sort.sort != ''">
                        ${r"${sqlWrapper.sort.sort}"}
                    </if>
                </when>
                <otherwise>
                    order by t.${orderBy}
                </otherwise>
            </choose>
            <#else>
            <include refid="common.orderBySql"/>
            <include refid="common.sortSql"/>
            </#if>
        </where>
    </select>

    <select id="count" resultType="long">
        SELECT COUNT(<#if (outerSystemModel!false) || (customSystemField!false)>1<#else>t.id</#if>) FROM
        <include refid="tableName"/>
        t
        <where>
            <include refid="common.beforeWhereSql"/>
            <include refid="common.conditionSql"/>
            <#if logicalDeleteField?? && logicalDeleteField != ''>
                <choose>
                    <when test="sqlWrapper != null and sqlWrapper.physicallyDeleted != null and sqlWrapper.physicallyDeleted.value != null and sqlWrapper.physicallyDeleted.value == 1">
                        AND (t.${logicalDeleteField} = '0' or t.${logicalDeleteField} = '1')
                    </when>
                    <otherwise>
                        AND t.${logicalDeleteField} = '0'
                    </otherwise>
                </choose>
            </#if>
        </where>
    </select>

    <select id="getMaxId" resultType="long">
        SELECT t.id FROM
        ${tableName} t
        ORDER BY t.id DESC LIMIT 1
    </select>

</mapper>
