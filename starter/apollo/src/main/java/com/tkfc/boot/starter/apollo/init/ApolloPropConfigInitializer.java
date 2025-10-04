package com.tkfc.boot.starter.apollo.init;

import com.tkfc.boot.starter.apollo.toolkit.ApolloPropUtils;
import com.tkfc.core.toolkit.PropUtil;
import com.tkfc.core.toolkit.StringUtil;
import lombok.extern.slf4j.Slf4j;

/**
 * apollo 初始化类
 * @author 0neBean
 * @since 2021-12-17 19:31:44
 */
@Slf4j
public class ApolloPropConfigInitializer {

    private final static String APP_ID_KEY = "app.id";
    private final static String EVN_KEY = "env";
    private final static String CONFIG_SERVICE = "apollo.configService";
    private final static String APOLLO_BOOTSTRAP_ENABLED = "apollo.bootstrap.enabled";
    private final static String APOLLO_BOOTSTRAP_NAMESPACES = "apollo.bootstrap.namespaces";
    public final static String PUBLIC_CONF_APOLLO = "apollo";
    private final static String APP_ID = "spring.application.name";
    private final static String APOLLO_META_URL_SUFFIX = ".meta";

    /**
     * apollo 初始化逻辑
     * @author 0neBean
     * @since 2021-12-17 19:32:03
     */
    public static void init() {
        String apolloBootstrapNamespaces = PropUtil.getInstance().getConfig(APOLLO_BOOTSTRAP_NAMESPACES, PUBLIC_CONF_APOLLO);
        String env = System.getProperty(EVN_KEY);
        if (StringUtil.isBlank(env)) {
            env = "dev";
            System.getProperties().setProperty(EVN_KEY, env);
            log.info("{} is setting {}", EVN_KEY, env);
        }
        String apolloBootstrapEnabled = PropUtil.getInstance().getConfig(APOLLO_BOOTSTRAP_ENABLED, PUBLIC_CONF_APOLLO);
        String apolloMeta = PropUtil.getInstance().getConfig(env.toLowerCase() + APOLLO_META_URL_SUFFIX, PUBLIC_CONF_APOLLO);
        String appId = PropUtil.getInstance().getConfig(APP_ID, PropUtil.DEFAULT_NAME_SPACE);
        System.getProperties().setProperty(CONFIG_SERVICE, apolloMeta);
        log.info("{} is setting {}", CONFIG_SERVICE, apolloMeta);
        System.getProperties().setProperty(APOLLO_BOOTSTRAP_ENABLED,apolloBootstrapEnabled);
        log.info("{} is setting {}", APOLLO_BOOTSTRAP_ENABLED, apolloBootstrapEnabled);
        System.getProperties().setProperty(APOLLO_BOOTSTRAP_NAMESPACES,apolloBootstrapNamespaces);
        log.info("{} is setting {}", APOLLO_BOOTSTRAP_NAMESPACES, apolloBootstrapNamespaces);
        System.getProperties().setProperty(APP_ID_KEY, appId);
        log.info("{} is setting {}", APP_ID_KEY, appId);
        PropUtil.getInstance().setRemoteConfigHandler((key, nameSpace)->{
            try {
                return ApolloPropUtils.getString(key, nameSpace);
            } catch (Exception ignored) {
                return null;
            }
        });
    }
}
