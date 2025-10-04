package com.tkfc.boot.starter.jsch.executor.abstracts;

import com.alibaba.fastjson2.JSONObject;
import com.jcraft.jsch.*;
import com.tkfc.boot.starter.jsch.dto.JschPipeline;
import com.tkfc.boot.starter.jsch.enums.PipeExecStatusEnum;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.enums.SystemTypeEnum;
import com.tkfc.core.function.SerializableBiConsumer;
import com.tkfc.core.throwable.base.Assert;
import com.tkfc.core.toolkit.*;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * 管道命令执行器
 *
 * @author 0neBean
 * @since 2022-08-03 20:22:31
 */
@Slf4j
public abstract class PipelineExecutor extends BasicCmdExecutor {

    private final static String TEMP_FILE_PATH = Objects.equals(SystemTypeEnum.WINDOWS, EnvUtil.getOsType()) ? "D:\\\\opt\\\\pipe-temp" : "/opt/pipe-temp";

    /**
     * 批量执行 pipeline 命令
     *
     * @param session   会话对象
     * @param pipelines pipeline对象列表
     * @param param     命令执行参数
     * @param output    命令执行响应输出对象
     */
    public void exec(Session session, List<JschPipeline> pipelines, JSONObject param, SerializableBiConsumer<JschPipeline, String> output) {
        for (JschPipeline pipeline : pipelines) {
            exec(session, pipeline, param, output);
        }
    }

    /**
     * 执行单个 pipeline 命令
     *
     * @param session  会话对象
     * @param pipeline pipeline对象
     * @param param    命令执行参数
     * @param output   命令执行响应输出对象
     */
    public void exec(Session session, JschPipeline pipeline, JSONObject param, SerializableBiConsumer<JschPipeline, String> output) {
        //日志buffer
        StringBuilder logBuffer = new StringBuilder();
        pipeline.setPipelineExecStatus(PipeExecStatusEnum.DOING.getValue());
        ChannelExec execChannel = null;
        writeTitleExecLog(String.format(">> pipeline [%s] execute at [%s] <<", pipeline.getPipelineName(), DateUtil.getCurrentLogDateFormat()), logBuffer, pipeline, output);
        try {
            String cmd = FreeMarkerTemplateUtil.generateString(param, pipeline.getPipelineCmd());
            execChannel = initChannelExec(session);
            writeExecLog(cmd, logBuffer, pipeline, output);
            pipeline.setPipelineExecStatus(exec(execChannel, logBuffer, pipeline, output, cmd));
            writeTitleExecLog("exec cmd finish!", logBuffer, pipeline, output);
        } catch (IOException | JSchException | TemplateException | InterruptedException e) {
            pipeline.setPipelineExecStatus(PipeExecStatusEnum.FINISH_WITH_ERROR.getValue());
            writeExecLog(String.format("exec cmd failure , because %s", e.getMessage()), logBuffer, pipeline, output);
            log.error("exec method got an error = ", e);
        } finally {
            closeChannel(execChannel);
        }
        pipeline.setExecLog(logBuffer.toString());
    }


    /**
     * 远程拷贝文件，可以拷贝文件夹
     *
     * @param session  会话对象
     * @param pipeline pipeline对象
     * @param param    命令执行参数
     * @param output   命令执行响应输出对象
     */
    public void scp(Session session, JschPipeline pipeline, JSONObject param, SerializableBiConsumer<JschPipeline, String> output) {
        //日志buffer
        StringBuilder logBuffer = new StringBuilder();
        pipeline.setPipelineExecStatus(PipeExecStatusEnum.DOING.getValue());
        ChannelSftp sftpChannel = null;

        try {
            sftpChannel = initChannelSftp(session);
            writeTitleExecLog(String.format(">> pipeline [%s] execute at [%s] <<", pipeline.getPipelineName(), DateUtil.getCurrentLogDateFormat()), logBuffer, pipeline, output);
            String scpSourcePath = StringUtil.replaceExpression(pipeline.getScpSourcePath(), param);
            String scpTargetPath = StringUtil.replaceExpression(pipeline.getScpTargetPath(), param);
            pipeline.setPipelineExecStatus(put(sftpChannel, logBuffer, pipeline, output, new File(scpSourcePath), scpTargetPath));
            writeTitleExecLog("scp file done!", logBuffer, pipeline, output);
        } catch (IOException | JSchException | InterruptedException | SftpException e) {
            pipeline.setPipelineExecStatus(PipeExecStatusEnum.FINISH_WITH_ERROR.getValue());
            writeExecLog(String.format("scp file failure , because %s", e.getMessage()), logBuffer, pipeline, output);
            log.error("scp method got an error = ", e);
        } finally {
            closeChannel(sftpChannel);
        }
        pipeline.setExecLog(logBuffer.toString());
    }

    /**
     * 远程拷贝文件，可以拷贝文件夹
     *
     * @param session  会话对象
     * @param pipeline pipeline对象
     * @param output   命令执行响应输出对象
     */
    public void copyResource(Session session, JschPipeline pipeline, SerializableBiConsumer<JschPipeline, String> output) {
        //日志buffer
        StringBuilder logBuffer = new StringBuilder();
        pipeline.setPipelineExecStatus(PipeExecStatusEnum.DOING.getValue());
        ChannelSftp sftpChannel = null;
        File tempPath = new File(StringUtil.concat(TEMP_FILE_PATH, EnvUtil.CURRENT_FILE_PATH_SEPARATOR));
        File dest;
        try {
            sftpChannel = initChannelSftp(session);
            writeTitleExecLog(String.format(">> pipeline [%s] execute at [%s] <<", pipeline.getPipelineName(), DateUtil.getCurrentLogDateFormat()), logBuffer, pipeline, output);
            //创建临时目录
            IoUtil.createQuietly(tempPath);
            //从classPath下读取文件 通过流写入临时目录
            Resource classResource = new ClassPathResource(pipeline.getSourceClassPath());
            String fileName = classResource.getURI().toString().substring(classResource.getURI().toString().lastIndexOf("/") + 1);
            dest = new File(StringUtil.concat(TEMP_FILE_PATH, EnvUtil.CURRENT_FILE_PATH_SEPARATOR, fileName));
            IoUtil.copyFile(classResource.getInputStream(), dest);
            //上传临时目录里的文件
            pipeline.setPipelineExecStatus(put(sftpChannel, logBuffer, pipeline, output, dest, pipeline.getScpTargetPath()));
            writeTitleExecLog("copy resource done!", logBuffer, pipeline, output);
        } catch (IOException | JSchException | InterruptedException | SftpException e) {
            pipeline.setPipelineExecStatus(PipeExecStatusEnum.FINISH_WITH_ERROR.getValue());
            writeExecLog(String.format("copy resource failure , because %s", e.getMessage()), logBuffer, pipeline, output);
            log.error("copyResource method got an error = ", e);
        } finally {
            closeChannel(sftpChannel);
        }
        pipeline.setExecLog(logBuffer.toString());
    }

    /**
     * 根据模板生成文件
     *
     * @param session  会话对象
     * @param pipeline pipeline对象
     * @param param    命令执行参数
     * @param output   命令执行响应输出对象
     */
    public void gene(Session session, JschPipeline pipeline, JSONObject param, SerializableBiConsumer<JschPipeline, String> output) {
        //日志buffer
        StringBuilder logBuffer = new StringBuilder();
        writeTitleExecLog(String.format(">> pipeline [%s] execute at [%s] <<", pipeline.getPipelineName(), DateUtil.getCurrentLogDateFormat()), logBuffer, pipeline, output);
        ChannelSftp sftpChannel = null;
        try {
            sftpChannel = initChannelSftp(session);
            String templateName = StringUtil.concat(pipeline.getTemplateKey(), ".ftl");
            FreeMarkerTemplateUtil.putStringTemplate(templateName, pipeline.getTemplateContent());
            File tempPath = new File(TEMP_FILE_PATH);
            if (!tempPath.exists()) {
                Assert.isTrue(tempPath.mkdir());
            }
            String geneFileName = StringUtil.replaceExpression(pipeline.getGeneFileName(), param);
            String scpTargetPath = StringUtil.replaceExpression(pipeline.getScpTargetPath(), param);
            String geneFilePath = subPrefixSlash(geneFileName);
            File geneFile = new File(StringUtil.concat(TEMP_FILE_PATH, StringPool.SLASH, geneFilePath));
            FreeMarkerTemplateUtil.generateFile(param, templateName, geneFile, FreeMarkerTemplateUtil.LoaderType.STRING);
            pipeline.setPipelineExecStatus(PipeExecStatusEnum.FINISH.getValue());
            writeTitleExecLog("gene file done!", logBuffer, pipeline, output);
            pipeline.setPipelineExecStatus(put(sftpChannel, logBuffer, pipeline, output, geneFile, scpTargetPath));
            writeTitleExecLog("scp gene file done!", logBuffer, pipeline, output);
            geneFile.deleteOnExit();
            tempPath.deleteOnExit();
        } catch (Exception e) {
            pipeline.setPipelineExecStatus(PipeExecStatusEnum.FINISH_WITH_ERROR.getValue());
            writeExecLog(String.format("gene file failure , because %s", e.getMessage()), logBuffer, pipeline, output);
            log.error("gene method got an error = ", e);
        } finally {
            closeChannel(sftpChannel);

        }
        pipeline.setExecLog(logBuffer.toString());
    }

    /**
     * 去除路径里的第一个斜杠
     *
     * @param path 路径
     * @return 截取后的路径
     */
    private static String subPrefixSlash(String path) {
        return path.startsWith(StringPool.SLASH) ? path.substring(1) : path;
    }
}
