package com.tkfc.boot.starter.jsch.session;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.ProxySOCKS5;
import com.jcraft.jsch.Session;
import com.tkfc.boot.starter.jsch.dto.JschConfig;
import com.tkfc.core.toolkit.StringUtil;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * jsch session builder
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-02-27 19:07:59
 */
public class JschSessionBuilder {

    //连接跳过证书确认
    private final static String STRICT_HOST_KEY_CHECKING_KEY = "StrictHostKeyChecking";

    /**
     * 创建连接实例
     *
     * @param config 连接配置
     * @throws JSchException 连接异常
     */
    public static Session createSession(JschConfig config) throws JSchException {
        Session session;
        JSch jsch = new JSch();

        if (StringUtil.isNotBlank(config.getRsaPath())) {
            jsch.addIdentity(config.getRsaPath());
        }

        session = jsch.getSession(config.getUser(), config.getHost(), config.getPort());

        if (StringUtil.isNotBlank(config.getPassword())) {
            session.setPassword(config.getPassword());
        }

        if (StringUtil.isNotBlank(config.getStrictHostKeyChecking())) {
            session.setConfig(STRICT_HOST_KEY_CHECKING_KEY, config.getStrictHostKeyChecking());
        } else {
            session.setConfig(STRICT_HOST_KEY_CHECKING_KEY, "no");
        }

        if (config.getTimeout() != 0) {
            session.setTimeout(config.getTimeout());
        }

        if (StringUtil.isNotBlank(config.getSocks5ProxyHost()) && config.getSocks5ProxyPort() > 0) {
            session.setProxy(new ProxySOCKS5(config.getSocks5ProxyHost(), config.getSocks5ProxyPort()));
        }
        session.connect();
        return session;
    }

    /**
     * 关闭session
     *
     * @param session 会话
     */
    public static void closeSession(@Nullable Session session) {
        if (Objects.nonNull(session) && session.isConnected()) {
            session.disconnect();
        }
    }
}
