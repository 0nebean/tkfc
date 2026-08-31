package com.tkfc.boot.starter.mybatis.builder;

import com.tkfc.core.enums.base.BaseEnums;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;

/**
 * 基于 BaseEnums#getValue() 的枚举类型处理器
 *
 * @param <E> 枚举类型
 */
public class EnumValueTypeHandler<E extends Enum<E> & BaseEnums<?>> extends BaseTypeHandler<E> {

    private final Class<E> type;
    private final E[] enums;

    public EnumValueTypeHandler(Class<E> type) {
        if (type == null) {
            throw new IllegalArgumentException("Type argument cannot be null");
        }
        this.type = type;
        this.enums = type.getEnumConstants();
    }

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, E parameter, JdbcType jdbcType) throws SQLException {
        Object enumValue = parameter.getValue();
        if (enumValue == null) {
            ps.setObject(i, null);
            return;
        }
        ps.setString(i, String.valueOf(enumValue));
    }

    @Override
    public E getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return resolveEnum(rs.getString(columnName));
    }

    @Override
    public E getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return resolveEnum(rs.getString(columnIndex));
    }

    @Override
    public E getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return resolveEnum(cs.getString(columnIndex));
    }

    private E resolveEnum(String dbValue) {
        if (dbValue == null) {
            return null;
        }
        for (E item : enums) {
            if (Objects.equals(String.valueOf(item.getValue()), dbValue)) {
                return item;
            }
        }
        throw new IllegalArgumentException("No enum constant " + type.getName() + " by value: " + dbValue);
    }
}
