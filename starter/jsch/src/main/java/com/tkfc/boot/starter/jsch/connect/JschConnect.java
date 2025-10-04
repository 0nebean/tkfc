package com.tkfc.boot.starter.jsch.connect;

import com.jcraft.jsch.*;
import com.tkfc.boot.starter.jsch.dto.JschPipeline;
import com.tkfc.core.function.SerializableBiConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.Nullable;

import java.util.Objects;

/**
 * 连接实例
 *
 * @author 0neBean
 * @since 2022-08-05 16:24:22
 */
@Slf4j
public abstract class JschConnect {

    //换行
    private final static String NEW_LINE = System.lineSeparator();

    /**
     * 写执行日志
     *
     * @param logStr    执行日志
     * @param logBuffer 日志buffer
     * @param pipeline  命令
     * @param output    日志输入
     */
    protected void writeExecLog(String logStr, StringBuilder logBuffer, JschPipeline pipeline, SerializableBiConsumer<JschPipeline, String> output) {
        if (Objects.nonNull(output)) {
            output.accept(pipeline, logStr);
            output.accept(pipeline, NEW_LINE);
        }

        if (Objects.nonNull(logBuffer)) {
            logBuffer.append(logStr);
            logBuffer.append(NEW_LINE);
        }
    }

    /**
     * 写执行标题日志
     *
     * @param logStr    执行日志
     * @param logBuffer 日志buffer
     * @param pipeline  命令
     * @param output    日志输入
     */
    public void writeTitleExecLog(String logStr, StringBuilder logBuffer, JschPipeline pipeline, SerializableBiConsumer<JschPipeline, String> output) {
        if (Objects.nonNull(output)) {
            output.accept(pipeline, NEW_LINE);
            output.accept(pipeline, logStr);
            output.accept(pipeline, NEW_LINE);
        }
        if (Objects.nonNull(logBuffer)) {
            logBuffer.append(NEW_LINE);
            logBuffer.append(logStr);
            logBuffer.append(NEW_LINE);
        }
    }

    /**
     * 获取ChannelSftp对象
     *
     * @param session 会话
     * @return ChannelSftp
     * @throws JSchException 连接异常
     */
    protected ChannelSftp initChannelSftp(Session session) throws JSchException {
        return (ChannelSftp) session.openChannel("sftp");
    }

    /**
     * 创建ChannelExec对象
     *
     * @param session 会话
     * @return ChannelExec
     * @throws JSchException 连接异常
     */
    protected ChannelExec initChannelExec(Session session) throws JSchException {
        return (ChannelExec) session.openChannel("exec");
    }


    /**
     * 关闭channel
     *
     * @param channel 通道
     */
    protected void closeChannel(@Nullable Channel channel) {
        if (Objects.nonNull(channel) && channel.isConnected()) {
            channel.disconnect();
        }
    }
}
