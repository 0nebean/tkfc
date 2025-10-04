package com.tkfc.boot.starter.mybatis.builder.metadata;

import com.tkfc.core.common.annotations.orm.*;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.core.toolkit.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 0neBean
 * 元数据管理类
 */
@Slf4j
public class MetaDataMappingManager {

    private static final Map<Class<?>, BeanInfo> beanInfoMappings = new HashMap<>();


    /**
     * 获取类信息
     *
     * @param clazz 类信息
     * @param <T>   泛型
     * @return BeanInfo
     */
    public static <T> BeanInfo getBeanInfo(Class<T> clazz) {
        BeanInfo beanInfo = beanInfoMappings.get(clazz);
        if (Objects.isNull(beanInfo)) {
            beanInfo = new BeanInfo();

            if (clazz.isAnnotationPresent(TableName.class)) {
                beanInfo.setTableName(clazz.getAnnotation(TableName.class).value());
            }

            if (clazz.isAnnotationPresent(OrderBy.class)) {
                beanInfo.setOrderBy(clazz.getAnnotation(OrderBy.class).value());
            }
            beanInfo.setOuterSystemModel(clazz.isAnnotationPresent(CustomSystemField.class));
            String modelFullName = clazz.getName();
            String mapperFullName = modelFullName.replaceAll(".model.", ".mapper.") + "Mapper";
            beanInfo.setModelFullName(modelFullName);
            beanInfo.setMapperFullName(mapperFullName);
            beanInfo.setProperties(initPropertyInfos(beanInfo, clazz));
            List<Boolean> collect = beanInfo.getProperties().stream().map(PropertyInfo::getOnDuplicateKeyUpdate).filter(b -> b).collect(Collectors.toList());
            beanInfo.setHasOnDuplicateKeyUpdate(CollectionUtil.isNotEmpty(collect));
            beanInfoMappings.put(clazz, beanInfo);
        }
        log.debug("load database model {} ,mapping CRUD method for database table {}", beanInfo.getModelFullName(), beanInfo.getTableName());
        return beanInfo;
    }

    private static final Set<String> BASE_MODEL_FIELD = new HashSet<>() {{
        add("id");
        add("operatorId");
        add("operatorName");
        add("isDeleted");
        add("createTime");
        add("updateTime");
    }};

    /**
     * 读取字段详情
     *
     * @param beanInfo 类信息
     * @param clazz    类型信息
     * @param <T>      泛型
     * @return PropertyInfo
     */
    private static <T> List<PropertyInfo> initPropertyInfos(BeanInfo beanInfo, Class<T> clazz) {
        final List<Field> fields = new ArrayList<>();
        List<PropertyInfo> propertyInfos = new ArrayList<>();
        org.springframework.util.ReflectionUtils.doWithFields(clazz, fields::add);
        CustomSystemField customSystemField = clazz.getAnnotation(CustomSystemField.class);
        fields.forEach(field -> {
            PropertyDescriptor prop = BeanUtils.getPropertyDescriptor(clazz, field.getName());
            if (prop == null || field.isAnnotationPresent(IgnoreFiled.class)) {
                return;
            }
            if (Objects.nonNull(customSystemField)) {
                boolean isSystemField = BASE_MODEL_FIELD.contains(field.getName());
                boolean notKeep = CollectionUtil.indexOfStringArray(customSystemField.keepField(), field.getName()) == -1;
                if (isSystemField && notKeep) {
                    return;
                }
            }
            String underlineFieldName = StringUtil.camelCaseToUnderline(field.getName());
            if (field.isAnnotationPresent(LogicalDelete.class)) {
                beanInfo.setLogicalDeleteField(underlineFieldName);
            }
            Method readMethod = prop.getReadMethod();
            PropertyInfo propertyInfo = new PropertyInfo();
            propertyInfo.setReturnType(field.getType());
            propertyInfo.setField(field);
            propertyInfo.setReadMethod(readMethod);
            propertyInfo.setVersion(field.isAnnotationPresent(Version.class));
            propertyInfo.setNullUpdatable(field.isAnnotationPresent(NullUpdatable.class));
            propertyInfo.setDefaultForQuery(!field.isAnnotationPresent(IgnoreForQuery.class));
            propertyInfo.setOnDuplicateKeyUpdate(field.isAnnotationPresent(OnDuplicateKeyUpdate.class));
            propertyInfo.setFieldNameUnderLine(underlineFieldName);
            propertyInfos.add(propertyInfo);
        });
        return Collections.unmodifiableList(propertyInfos);
    }


}
