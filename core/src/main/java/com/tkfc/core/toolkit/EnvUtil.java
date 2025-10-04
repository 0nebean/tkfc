package com.tkfc.core.toolkit;

import com.tkfc.core.constants.StringPool;
import com.tkfc.core.enums.SystemTypeEnum;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * 环境工具链
 *
 * @author 0neBean
 * @version 1.0
 * @since 2020/10/30 14:49
 */
@Slf4j
public class EnvUtil {

    public final static String CURRENT_OPERATING_SYSTEM_SEPARATOR = System.getProperty("file.separator");
    public final static String CURRENT_FILE_PATH_SEPARATOR = Objects.equals(SystemTypeEnum.WINDOWS, EnvUtil.getOsType()) ? "\\\\" : "/";
    public final static String BIN_PATH = "bin";
    public final static String SERVER_PATH = "server";
    private final static String CLASSPATH_STR = "classpath*:";
    private final static String CLASS_STR = "*.class";
    private final static String SPLIT_STR = "/";

    /**
     * 获取classpath 的绝对路径 需要配合启动脚本启动
     * 如果该方法运行在classpath内 返回 '.'
     *
     * @return path
     */
    public static String getAbsoluteClassPath() {
        String absoluteClassPath = "";
        try {
            /*linux sh startup*/
            absoluteClassPath = new File("").getCanonicalPath();
        } catch (IOException e) {
            log.error("Could not load properties from path");
        }
        /*windows bat startup*/
        if (absoluteClassPath.endsWith(CURRENT_OPERATING_SYSTEM_SEPARATOR + BIN_PATH)) {
            absoluteClassPath = absoluteClassPath.substring(0, absoluteClassPath.lastIndexOf(CURRENT_OPERATING_SYSTEM_SEPARATOR));
        }
        /*idea startup*/
        if (!absoluteClassPath.endsWith(CURRENT_OPERATING_SYSTEM_SEPARATOR + BIN_PATH) && !absoluteClassPath.endsWith(CURRENT_OPERATING_SYSTEM_SEPARATOR + SERVER_PATH)) {
            absoluteClassPath = ".";
        }
        return absoluteClassPath;
    }

    /**
     * 包名转换
     *
     * @param packageName 包名
     * @return java.lang.String
     * @author 0neBean
     * @since 2022/4/26 23:56
     */
    public static String coverPackageNameToClassPath(String packageName) {
        packageName = packageName.replaceAll("\\.", StringPool.SLASH);
        packageName = CLASSPATH_STR + packageName + SPLIT_STR + CLASS_STR;
        return packageName;
    }

    /**
     * 设置环境变量
     *
     * @param key   键
     * @param value 值
     */
    public static void setEnvProps(String key, String value) {
        System.getProperties().setProperty(key, value);
    }

    /**
     * 读取环境变量
     *
     * @param key 键
     * @return value 值
     */
    public static String getEnvProps(String key) {
        return System.getProperties().getProperty(key);
    }

    /**
     * 退出程序
     */
    public static void exit() {
        System.exit(0);
    }

    /**
     * 判断作业系统类型
     * @return SystemTypeEnum
     */
    public static SystemTypeEnum getOsType() {
        String systemName = System.getProperty("os.name").toLowerCase();
        if (systemName.contains("windows")) {
            return SystemTypeEnum.WINDOWS;
        } else if (systemName.contains("mac") || systemName.contains("apple")) {
            return SystemTypeEnum.MAC;
        } else
            return SystemTypeEnum.LINUX;
    }
}
