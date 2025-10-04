package com.tkfc.core.toolkit;

import com.tkfc.core.enums.base.BaseEnums;

import java.util.Objects;


/**
 * 枚举工具类
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/4/15 13:52
 */
public class EnumsUtil {

    /**
     * 对比值
     *
     * @param enums 枚举的值
     * @param value 目标值
     * @return java.lang.Boolean
     * @author 0neBean
     * @since 2021-12-05 23:39:44
     */
    public static <T> Boolean equalsValue(BaseEnums<T> enums, T value) {
        return Objects.equals(enums.getValue(), value);
    }

    /**
     * 获取枚举值
     *
     * @param clazz class
     * @return 枚举值数组
     * @author 0neBean
     * @since 2021-12-05 23:42:05
     */
    @SuppressWarnings("unchecked")
    public static <T> BaseEnums<T>[] getValues(Class<T> clazz) {
        return (BaseEnums<T>[]) clazz.getEnumConstants();
    }

    /**
     * 获取枚举的描述信息
     *
     * @param clazz class
     * @param value 值
     * @return java.lang.String
     * @author 0neBean
     * @since 2021-12-05 23:42:05
     */
    public static <T> String getDescriptionByValue(Class<?> clazz, T value) {
        BaseEnums<?> enumConstant = (BaseEnums<?>) clazz.getEnumConstants()[0];
        for (BaseEnums<?> baseEnums : enumConstant.getValues()) {
            if (baseEnums.getValue().equals(value)) {
                return baseEnums.getDescription();
            }
        }
        return "";
    }

    /**
     * 获取枚举的对象
     *
     * @param clazz class
     * @param value 枚举值
     * @return M
     * @author 0neBean
     * @since 2021-12-05 23:42:51
     */
    public static <T, M> M getByValue(Class<M> clazz, T value) {
        BaseEnums<?> enumConstant = (BaseEnums<?>) clazz.getEnumConstants()[0];
        for (BaseEnums<?> baseEnums : enumConstant.getValues()) {
            if (baseEnums.getValue().equals(value)) {
                return JsonUtil.toBean(JsonUtil.toJson(baseEnums), clazz);
            }
        }
        return null;
    }


}
