package com.tkfc.boot.starter.jsch.executor.abstracts;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import com.tkfc.boot.starter.jsch.connect.JschConnect;
import com.tkfc.boot.starter.jsch.dto.JschPipeline;
import com.tkfc.boot.starter.jsch.enums.PipeExecStatusEnum;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.function.SerializableBiConsumer;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.core.toolkit.StringUtil;

import java.io.*;
import java.util.*;

/**
 * 基础命令行执行器
 *
 * @author 0neBean
 * @since 2022-08-05 19:45:33
 */
public abstract class BasicCmdExecutor extends JschConnect {

    private static final List<Integer> SUCCESS_CODES = new ArrayList<>() {{
        add(0);
        add(100);
    }};


    /**
     * 执行命令
     *
     * @param execChannel 执行管道
     * @param logBuffer   日志 buffer
     * @param pipeline    命令
     * @param output      命令执行响应输出对象
     * @param cmd         命令
     * @return 执行结果
     * @throws IOException          抛出的各种异常
     * @throws JSchException        抛出的各种异常
     * @throws InterruptedException 抛出的各种异常
     */
    protected Integer exec(ChannelExec execChannel, StringBuilder logBuffer, JschPipeline pipeline, SerializableBiConsumer<JschPipeline, String> output, String cmd) throws IOException, JSchException, InterruptedException {
        //装载执行参数
        execChannel.setCommand(cmd);
        execChannel.setErrStream(System.err);
        InputStream execStream = execChannel.getInputStream();
        InputStream errStream = execChannel.getErrStream();
        BufferedReader execStreamReader = new BufferedReader(new InputStreamReader(execStream));
        BufferedReader errStreamReader = new BufferedReader(new InputStreamReader(errStream));
        //执行命令
        execChannel.connect();
        //等待响应结果
        Thread.sleep(200);
        //如果执行命令返回内容不为空 返回执行内容
        do {
            String line;
            while (execStreamReader.ready() && (line = execStreamReader.readLine()) != null) {
                writeExecLog(line, logBuffer, pipeline, output);
            }
            //如果执行错误信息不为空 返回错误信息
            while (errStreamReader.ready() && (line = errStreamReader.readLine()) != null) {
                writeExecLog(line, logBuffer, pipeline, output);
            }
        } while (!execChannel.isClosed());
        return (SUCCESS_CODES.contains(execChannel.getExitStatus())) ? PipeExecStatusEnum.FINISH.getValue() : PipeExecStatusEnum.FINISH_WITH_ERROR.getValue();
    }

    /**
     * 上传文件，支持文件夹上传
     *
     * @param sftpChannel 上传通道
     * @param logBuffer   日志 buffer
     * @param pipeline    命令
     * @param output      命令执行响应输出对象
     * @param source      源文件
     * @param targetPath  目标路径
     * @return 执行结果 bool
     * @throws SftpException        抛出的各种异常
     * @throws IOException          抛出的各种异常
     * @throws JSchException        抛出的各种异常
     * @throws InterruptedException 抛出的各种异常
     */
    protected Integer put(ChannelSftp sftpChannel, StringBuilder logBuffer, JschPipeline pipeline, SerializableBiConsumer<JschPipeline, String> output, File source, String targetPath) throws SftpException, JSchException, IOException, InterruptedException {
        sftpChannel.connect();
        return copyFile(sftpChannel, source, logBuffer, pipeline, output, targetPath);
    }

    /**
     * 拷贝文件递归方法
     *
     * @param sftpChannel 上传通道
     * @param targetFile  源文件文件
     * @param logBuffer   日志 buffer
     * @param pipeline    命令
     * @param output      命令执行响应输出对象
     * @param pwd         拷贝路径
     * @return 执行结果 bool
     * @throws SftpException        抛出的各种异常
     * @throws IOException          抛出的各种异常
     */
    private Integer copyFile(ChannelSftp sftpChannel, File targetFile, StringBuilder logBuffer, JschPipeline pipeline, SerializableBiConsumer<JschPipeline, String> output, String pwd) throws IOException, SftpException {
        // 可以创建多级目录
        String finalFilePath = String.format("%s%s%s", pwd, StringPool.SLASH, targetFile.getName());
        //如果目标文件是一个目录 获取目录下所文件
        if (targetFile.isDirectory()) {
            finalFilePath = pwd;
            String dirName = targetFile.getName();
            pwd = String.format("%s/%s", pwd, dirName);
            writeExecLog(String.format("target file [%s] is directory , will upload child path file", pwd), logBuffer, pipeline, output);
            List<File> childFiles = Optional.ofNullable(targetFile.listFiles()).map(Arrays::asList).orElse(Collections.emptyList());
            //目录下文件不为空,遍历文件上传
            if (CollectionUtil.isNotEmpty(childFiles)) {
                for (File aList : childFiles) {
                    Integer tempResult = copyFile(sftpChannel, aList, logBuffer, pipeline, output, pwd);
                    if (Objects.equals(PipeExecStatusEnum.FINISH_WITH_ERROR.getValue(), tempResult)) {
                        return tempResult;
                    }
                }
            }
        }
        //如果目标文件是一个文件直接上传
        if (!targetFile.isDirectory()) {
            //创建文件夹防止路径不存在
            touchDir(pwd,sftpChannel);
            sftpChannel.cd(pwd);
            try (InputStream fileInputStream = new FileInputStream(targetFile)) {
                writeExecLog(String.format("uploading file [%s/%s] ...", pwd, targetFile.getName()), logBuffer, pipeline, output);
                sftpChannel.put(fileInputStream, targetFile.getName());
            }
        }
        return checkRemoteFileExits(finalFilePath, sftpChannel, logBuffer, pipeline, output);
    }


    /**
     * 检查远端文件是否存在
     *
     * @param finalFilePath 文件路径
     * @param sftpChannel   sftp通道
     * @return 是否上传文件成功 bool
     */
    private static Boolean checkRemoteDirectoryExits(String finalFilePath, ChannelSftp sftpChannel) {
        try {
            sftpChannel.stat(finalFilePath);
        } catch (SftpException e) {
            return Boolean.FALSE;
        }
        return Boolean.TRUE;
    }

    /**
     * 检查远端文件是否存在
     *
     * @param finalFilePath 文件路径
     * @param sftpChannel   sftp通道
     * @param logBuffer     日志 buffer
     * @param pipeline      命令
     * @param output        命令执行响应输出对象
     * @return 是否上传文件成功
     * @throws IOException IO异常
     */
    private Integer checkRemoteFileExits(String finalFilePath, ChannelSftp sftpChannel, StringBuilder logBuffer, JschPipeline pipeline, SerializableBiConsumer<JschPipeline, String> output) throws IOException {
        try {
            sftpChannel.stat(finalFilePath);
        } catch (SftpException e) {
            writeExecLog(String.format("file [%s] upload failure.", finalFilePath), logBuffer, pipeline, output);
            return PipeExecStatusEnum.FINISH_WITH_ERROR.getValue();
        }
        writeExecLog(String.format("file [%s] upload success.", finalFilePath), logBuffer, pipeline, output);
        return PipeExecStatusEnum.FINISH.getValue();
    }

    /**
     * 创建文件夹
     *
     * @param pwd         路径
     * @param sftpChannel sftp
     */
    private static void touchDir(String pwd, ChannelSftp sftpChannel) {
        //如果文件夹已存在 , 则不创建文件夹
        if (checkRemoteDirectoryExits(pwd,sftpChannel)) {
            return;
        }
        String[] path = pwd.split(StringPool.SLASH);
        String qwq = StringPool.EMPTY;
        for (String p : path) {
            if (StringUtil.isBlank(p)) {
                continue;
            }
            qwq = StringUtil.concat(qwq, StringPool.SLASH, p);
            try {
                sftpChannel.mkdir(qwq);
            } catch (SftpException ignore) {
            }
        }
    }
}
