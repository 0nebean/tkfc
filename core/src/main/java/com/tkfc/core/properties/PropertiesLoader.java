package com.tkfc.core.properties;

import com.tkfc.core.toolkit.EnvUtil;
import com.tkfc.core.toolkit.IoUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Properties;

/**
 * Properties文件载入工具类. 可载入多个properties文件,
 * 相同的属性在最后载入的文件中的值将会覆盖之前的值，但以System的Property优先.
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 14:49
 */
@Slf4j
public class PropertiesLoader {

    private static final ResourceLoader resourceLoader = new DefaultResourceLoader();

    private final Properties properties;

    public PropertiesLoader(String... resourcesPaths) {
        properties = loadProperties(resourcesPaths);
    }

    public Properties getProperties() {
        return properties;
    }

    /**
     * 取出Property，但以System的Property优先,取不到返回空字符串.
     */
    private String getValue(String key) {
        String systemProperty = System.getProperty(key);
        if (systemProperty != null) {
            return systemProperty;
        }
        if (properties.containsKey(key)) {
            return properties.getProperty(key);
        }
        return "";
    }


    /**
     * 取出String类型的Property，但以System的Property优先,如果都为Null则抛出异常.
     * @param key 键
     * @return value
     */
    public String getProperty(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return value;
    }

    /**
     * 取出String类型的Property，但以System的Property优先.如果都为Null则返回Default值.
     * @param key 键
     * @param defaultValue 默认值
     * @return value
     */
    public String getProperty(String key, String defaultValue) {
        String value = getValue(key);
        return value != null ? value : defaultValue;
    }


    /**
     * 取出Integer类型的Property，但以System的Property优先.如果都为Null或内容错误则抛出异常.
     * @param key 键
     * @return value
     */
    public Integer getInteger(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Integer.valueOf(value);
    }

    /**
     * 取出Integer类型的Property，但以System的Property优先.如果都为Null则返回Default值，如果内容错误则抛出异常
     * @param key 键
     * @param defaultValue 默认值
     * @return value
     */
    public Integer getInteger(String key, Integer defaultValue) {
        String value = getValue(key);
        return value != null ? Integer.valueOf(value) : defaultValue;
    }

    /**
     * 取出Double类型的Property，但以System的Property优先.如果都为Null或内容错误则抛出异常.
     * @param key 键
     * @return value
     */
    public Double getDouble(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Double.valueOf(value);
    }

    /**
     * 取出Double类型的Property，但以System的Property优先.如果都为Null则返回Default值，如果内容错误则抛出异常
     * @param key 键
     * @param defaultValue 默认值
     * @return value
     */
    public Double getDouble(String key, Integer defaultValue) {
        String value = getValue(key);
        return value != null ? Double.parseDouble(value) : defaultValue;
    }

    /**
     * 取出Boolean类型的Property，但以System的Property优先.如果都为Null抛出异常,如果内容不是true/false则返回false.
     * @param key 键
     * @return value
     */
    public Boolean getBoolean(String key) {
        String value = getValue(key);
        if (value == null) {
            throw new NoSuchElementException();
        }
        return Boolean.valueOf(value);
    }

    /**
     * 取出Boolean类型的Property，但以System的Property优先.如果都为Null则返回Default值,如果内容不为true/false则返回false.
     * @param key 键
     * @param defaultValue 默认值
     * @return value
     */
    public Boolean getBoolean(String key, boolean defaultValue) {
        String value = getValue(key);
        return value != null ? Boolean.parseBoolean(value) : defaultValue;
    }

    /**
     * 载入多个文件, 文件路径使用Spring Resource格式.
     * @param resourcesPaths 配置文件路径
     * @return 配置
     */
    private Properties loadProperties(String... resourcesPaths) {
        Properties props = new Properties();
        String absoluteClassPath = EnvUtil.getAbsoluteClassPath();
        log.info("do loadProperties ,absoluteClassPath = {}",absoluteClassPath);
        for (String location : resourcesPaths) {
            InputStream is = null;
            log.info("Loading properties file from:" + location);
            try {
                ClassPathResource classPathResource = new ClassPathResource(location);
                if (!location.startsWith("classpath")){
                    location = "classpath*:"+location;
                }
//                Resource resource = resourceLoader.getResource(location);
                is = classPathResource.getInputStream();
                props.load(is);
            } catch (IOException ex) {
                log.info("Could not load properties from path:" + location + ", " + ex.getMessage());
            } finally {
                IoUtil.closeQuietly(is);
            }
        }
        return props;
    }

}