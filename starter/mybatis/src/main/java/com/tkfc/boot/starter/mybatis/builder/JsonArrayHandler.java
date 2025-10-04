package com.tkfc.boot.starter.mybatis.builder;

import com.alibaba.fastjson2.JSONArray;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * mybatis json 字段类型映射处理
 *
 * @author 0neBean
 * @version 1.0
 * @since 2023-12-01 17:07:16
 */
@MappedTypes(JSONArray.class)
@MappedJdbcTypes(JdbcType.OTHER)
public class JsonArrayHandler extends BaseTypeHandler<JSONArray> {

    /**
     * 设置非空参数
     *
     * @param ps        prepared statement
     * @param i         参数下标
     * @param parameter 参数
     * @param jdbcType  jdbc type
     * @throws SQLException sql异常
     */
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, JSONArray parameter, JdbcType jdbcType) throws SQLException {
        ps.setString(i, String.valueOf(parameter.toJSONString()));
    }

    /**
     * 根据列名，获取可以为空的结果
     *
     * @param rs         执行结果
     * @param columnName 列名
     * @return JSONArray
     * @throws SQLException sql异常
     */
    @Override
    public JSONArray getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String sqlJson = rs.getString(columnName);
        if (null != sqlJson) {
            return JSONArray.parseArray(sqlJson);
        }
        return null;
    }

    /**
     * 根据列索引，获取可以为内控的接口
     *
     * @param rs          执行结果
     * @param columnIndex 列下标
     * @return JSONArray
     * @throws SQLException sql异常
     */
    @Override
    public JSONArray getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String sqlJson = rs.getString(columnIndex);
        if (null != sqlJson) {
            return JSONArray.parseArray(sqlJson);
        }
        return null;
    }

    /**
     * 获取空值的结果集
     *
     * @param cs          回调
     * @param columnIndex 列下标
     * @return JSONArray
     * @throws SQLException sql异常
     */
    @Override
    public JSONArray getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String sqlJson = cs.getNString(columnIndex);
        if (null != sqlJson) {
            return JSONArray.parseArray(sqlJson);
        }
        return null;
    }

}
