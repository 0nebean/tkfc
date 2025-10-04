package com.tkfc.boot.starter.apollo.toolkit;

import com.ctrip.framework.apollo.ConfigService;
import com.tkfc.core.toolkit.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * @author 0neBean
 * Apollo配置中心工具类
 */
@Slf4j
public class ApolloPropUtils {

    /**
     * 获取配置
     * @param key 键
     * @param namespace 命名空间
     * @return
     */
    public static String getString(String key, String namespace) {
        if (StringUtil.isEmpty(key)) {
            key = "";
        }
        if (StringUtil.isEmpty(namespace)) {
            namespace = "";
        }
        log.debug("get prop from apollo , key = {},namespace = {}", key, namespace);
        return ConfigService.getConfig(namespace).getProperty(key, null);

    }

    public static String getString(String key) {
        if (StringUtil.isEmpty(key)) {
            key = "";
        }
        log.debug("get prop from apollo , key = {}", key);
        return ConfigService.getAppConfig().getProperty(key, null);
    }


}
