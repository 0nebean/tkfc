package com.tkfc.boot.starter.mybatis.builder.metadata;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * DAO对象字段包装
 * @author 0neBean
 */
public class PropertyInfo implements Serializable {

    private static final long serialVersionUID = -2733655100912265744L;

    private Class<?> returnType;
    public Class<?> getReturnType() {
        return returnType;
    }
    public void setReturnType(Class<?> returnType) {
        this.returnType = returnType;
    }

    private Field field;
    public Field getField() {
        return field;
    }
    public void setField(Field field) {
        this.field = field;
    }

    private Method readMethod;
    public Method getReadMethod() {
        return readMethod;
    }
    public void setReadMethod(Method readMethod) {
        this.readMethod = readMethod;
    }

    private Boolean isVersion;
    public Boolean getVersion() {
        return isVersion;
    }
    public void setVersion(Boolean version) {
        isVersion = version;
    }

    private Boolean nullUpdatable;
    public Boolean getNullUpdatable() {
        return nullUpdatable;
    }
    public void setNullUpdatable(Boolean nullUpdatable) {
        this.nullUpdatable = nullUpdatable;
    }

    private Boolean onDuplicateKeyUpdate;
    public Boolean getOnDuplicateKeyUpdate() {
        return onDuplicateKeyUpdate;
    }
    public void setOnDuplicateKeyUpdate(Boolean onDuplicateKeyUpdate) {
        this.onDuplicateKeyUpdate = onDuplicateKeyUpdate;
    }

    private String fieldNameUnderLine;
    public String getFieldNameUnderLine() {
        return fieldNameUnderLine;
    }
    public void setFieldNameUnderLine(String fieldNameUnderLine) {
        this.fieldNameUnderLine = fieldNameUnderLine;
    }

    private Boolean defaultForQuery;
    public Boolean getDefaultForQuery() {
        return defaultForQuery;
    }
    public void setDefaultForQuery(Boolean defaultForQuery) {
        this.defaultForQuery = defaultForQuery;
    }
}
