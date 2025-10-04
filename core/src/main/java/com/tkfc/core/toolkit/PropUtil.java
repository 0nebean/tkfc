package com.tkfc.core.toolkit;

import com.tkfc.core.constants.StringPool;
import com.tkfc.core.function.SerializableBiFunction;
import com.tkfc.core.properties.PropertiesLoader;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 配置文件读取类
 *
 * @author 0neBean
 */
public class PropUtil {

    public final static String DEFAULT_NAME_SPACE = "application";
    public final static String PUBLIC_CONF_SYSTEM = "system";


    private SerializableBiFunction<String, String, String> remoteConfigHandler;

    /**
     * 设置刷新apollo修改变化的逻辑
     *
     * @param remoteConfigHandler 刷新apollo的监听
     * @author 0neBean
     * @since 2021-12-06 00:13:03
     */
    public void setRemoteConfigHandler(SerializableBiFunction<String, String, String> remoteConfigHandler) {
        this.remoteConfigHandler = remoteConfigHandler;
    }

    private PropUtil() {
        initPropertiesLoader();
    }

    /**
     * 初始化配置文件
     */
    private void initPropertiesLoader() {
        loaderMap = new HashMap<>();
    }

    /**
     * 当前对象实例
     */
    private static final PropUtil PROP_UTIL = new PropUtil();

    /**
     * 获取当前对象实例
     *
     * @return PropUtil
     */
    public static PropUtil getInstance() {
        return PROP_UTIL;
    }

    /**
     * 文件名
     */
    private final static String SUFFIX = ".properties";
    /**
     * 属性文件加载对象map
     */
    private Map<String, PropertiesLoader> loaderMap = null;

    /**
     * 性配置文件获取配置
     *
     * @param key       键
     * @param nameSpace 配置文件名
     * @return 配置
     */
    private String getConfigInLoader(String key, String nameSpace) {
        PropertiesLoader propertiesLoader;
        String value;
        if (null == loaderMap.get(nameSpace)) {
            String env = System.getProperty("spring.profiles.active");
            env = StringUtil.isBlank(env) ? "dev" : env;
            String propPath = String.format("%s-%s%s", nameSpace, env, SUFFIX);
            propertiesLoader = new PropertiesLoader(propPath);
            loaderMap.put(nameSpace, propertiesLoader);
        } else {
            propertiesLoader = loaderMap.get(nameSpace);
        }
        value = propertiesLoader.getProperty(key);
        return StringUtil.isEmpty(value) ? StringPool.EMPTY : value;
    }


    /**
     * 获取配置
     *
     * @param key       key
     * @param nameSpace ns
     * @return value
     */
    public String getConfig(String key, String nameSpace) {
        String value = getConfigInLoader(key, nameSpace);
        if (StringUtil.isEmpty(value) && Objects.nonNull(remoteConfigHandler)) {
            value = remoteConfigHandler.apply(key, nameSpace);
        }
        return value;
    }


    /**
     * 获取配置
     *
     * @param key key
     * @return value
     */
    public String getConfig(String key) {
        return getConfig(key, PropUtil.DEFAULT_NAME_SPACE);
    }


    /**
     * 获取配置
     *
     * @param key key
     * @return defaultValue 默认值
     */
    public String getConfigWithDefaultValue(String key, String defaultValue) {
        String result = getConfig(key, PropUtil.DEFAULT_NAME_SPACE);
        return StringUtil.isEmpty(result) ? defaultValue : result;
    }

    /**
     * 获取配置
     *
     * @param key key
     * @return value
     */
    public String getConfigWithDefaultValue(String key, String nameSpace, String defaultValue) {
        String result = getConfig(key, nameSpace);
        return StringUtil.isEmpty(result) ? defaultValue : result;
    }

}
