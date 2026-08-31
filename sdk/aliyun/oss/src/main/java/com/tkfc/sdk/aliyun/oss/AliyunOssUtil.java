package com.tkfc.sdk.aliyun.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.*;
import com.tkfc.compress.toolkit.CompressUtil;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.enums.SystemTypeEnum;
import com.tkfc.core.function.SerializableBiConsumer;
import com.tkfc.core.toolkit.EnvUtil;
import com.tkfc.core.toolkit.IoUtil;
import com.tkfc.core.toolkit.StringUtil;
import com.tkfc.sdk.aliyun.oss.config.OssConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.compress.utils.IOUtils;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 操作oss SDK
 *
 * @author 0neBean
 * @since 2023-04-09 15:48:04
 */
@Slf4j
public class AliyunOssUtil {

    private final static String COMMON_SYSTEM_SEPARATOR = EnvUtil.CURRENT_OPERATING_SYSTEM_SEPARATOR;
    private final static String OPERATE_SYSTEM_SEPARATOR = Objects.equals(SystemTypeEnum.WINDOWS, EnvUtil.getOsType()) ? COMMON_SYSTEM_SEPARATOR + COMMON_SYSTEM_SEPARATOR : COMMON_SYSTEM_SEPARATOR;

    /**
     * 上传目录下所有文件到OSS
     *
     * @param config     连接配置
     * @param bucketName 储存桶名称
     * @param filePath   本地文件路径
     * @param targetPath 上传目标目录
     * @param zipStatic  压缩静态资源
     */
    public static void uploadAllFileToOss(OssConfig config, String bucketName, String filePath, String targetPath, Boolean zipStatic) throws Exception {
        uploadAllFileToOss(config, bucketName, filePath, targetPath, zipStatic, null);
    }


    /**
     * 上传目录下所有文件到OSS
     *
     * @param config       连接配置
     * @param bucketName   oss文件篮
     * @param filePath     本地文件路径
     * @param targetPath   上传目标目录
     * @param zipStatic    压缩静态资源
     * @param beforeUpload 上传之前的逻辑
     */
    public static void uploadAllFileToOss(OssConfig config, String bucketName, String filePath, String targetPath, Boolean zipStatic, SerializableBiConsumer<String, File> beforeUpload) throws Exception {
        targetPath = covertTargetPath(targetPath);
        Collection<File> files = FileUtils.listFiles(new File(filePath), null, Boolean.TRUE);
        for (File file : files) {
            String fileName = file.getPath().replace(filePath, StringPool.EMPTY);
            fileName = (fileName.startsWith(COMMON_SYSTEM_SEPARATOR)) ? fileName.replaceFirst(OPERATE_SYSTEM_SEPARATOR, StringPool.EMPTY) : fileName;
            String[] pathArray = fileName.split(OPERATE_SYSTEM_SEPARATOR);
            StringBuilder pathBuilder = new StringBuilder();
            for (int i = 0; i < pathArray.length; i++) {
                String temp = pathArray[i];
                if (i != pathArray.length - 1) {
                    pathBuilder.append(temp).append(COMMON_SYSTEM_SEPARATOR);
                }
            }
            String ossPath = pathBuilder.toString().replace(StringPool.BACK_SLASH, StringPool.SLASH);
            ossPath = StringUtil.concat(targetPath, StringPool.SLASH, ossPath, file.getName());
            if (Objects.nonNull(beforeUpload)) {
                beforeUpload.accept(ossPath, file);
            }
            AliyunOssUtil.uploadFileToOss(config, bucketName, file, ossPath, zipStatic);
        }
    }

    /**
     * 上传文件到oss
     *
     * @param config          连接配置
     * @param bucketName      oss文件篮
     * @param tempFilePath    临时文件目录
     * @param fileOriginalUrl 源文件的URL
     * @param targetPath      上传目标目录
     * @param fileName        文件名称
     * @param zipStatic       压缩静态资源
     * @throws IOException IO异常
     */
    public static void downloadFileAndUploadToOss(OssConfig config, String bucketName, String tempFilePath, String fileOriginalUrl, String targetPath, String fileName, Boolean zipStatic) throws Exception {
        // 获取图片输入流
        InputStream in = new URL(fileOriginalUrl).openStream();
        // 将文件转换成字节数组
        byte[] bytes = IOUtils.toByteArray(in);
        String localFilePath = java.lang.String.format("%s%s", tempFilePath, fileName);
        File localFile = new File(localFilePath);
        // 导出路径和文件格式
        FileUtils.writeByteArrayToFile(localFile, bytes);
        uploadFileToOss(config, bucketName, localFile, targetPath, zipStatic);
        localFile.deleteOnExit();
    }

    /**
     * 上传文件到 aliyun oss
     *
     * @param config     连接配置
     * @param bucketName oss文件篮
     * @param uploadFile 上传的文件
     * @param targetPath 目标目录
     * @param zipStatic  压缩静态资源
     */
    public static void uploadFileToOss(OssConfig config, String bucketName, File uploadFile, String targetPath, Boolean zipStatic) throws Exception {
        OSS ossClient = initClient(config);
        File tempFile = uploadFile;
        boolean notMinFile = IoUtil.notMinFile(uploadFile.getPath());
        boolean staticFile = IoUtil.isCSSFile(uploadFile.getPath()) || IoUtil.isJSFile(uploadFile.getPath());
        try {
            if (zipStatic && notMinFile && staticFile) {
                tempFile = IoUtil.copyTempFile(uploadFile);
                CompressUtil.compress(uploadFile, tempFile);
            }
            UploadFileRequest uploadFileRequest = getUploadFileRequest(bucketName, tempFile, targetPath);
            ossClient.uploadFile(uploadFileRequest);
        } catch (Throwable e) {
            throw new Exception(e);
        } finally {
            ossClient.shutdown();
            if (zipStatic && notMinFile && staticFile) {
                IoUtil.deleteQuietly(tempFile.getParentFile());
            }
        }
    }

    /**
     * 构上传请求
     *
     * @param bucketName 桶名称
     * @param uploadFile 上传文件
     * @param targetPath 目标路口
     * @return 上传请求
     */
    private static UploadFileRequest getUploadFileRequest(String bucketName, File uploadFile, String targetPath) {
        UploadFileRequest uploadFileRequest = new UploadFileRequest(bucketName, targetPath);
        // The local file to upload---it must exist.
        uploadFileRequest.setUploadFile(uploadFile.getPath());
        // Sets the concurrent upload task number to 5.
        uploadFileRequest.setTaskNum(5);
        // Sets the part size to 1MB.
        uploadFileRequest.setPartSize(1024 * 1024);
        // Enables the checkpoint file. By default, it's off.
        uploadFileRequest.setEnableCheckpoint(true);
        return uploadFileRequest;
    }


    /**
     * 删除阿里云oss上的文件
     *
     * @param config     连接配置
     * @param bucketName oss文件篮
     * @param fileName   文件名
     */
    public static void deleteFileOnOss(OssConfig config, String bucketName, String fileName) {
        deleteFileOnOss(config, bucketName, Collections.singletonList(fileName));
    }

    /**
     * 删除阿里云oss上的文件
     *
     * @param config     连接配置
     * @param bucketName oss文件篮
     * @param fileNames  文件名
     */
    public static void deleteFileOnOss(OssConfig config, String bucketName, List<String> fileNames) {
        OSS ossClient = initClient(config);
        try {
            fileNames = fileNames.stream().map(AliyunOssUtil::covertTargetPath).collect(Collectors.toList());
            DeleteObjectsResult deleteObjectsResult = ossClient.deleteObjects(new DeleteObjectsRequest(bucketName).withKeys(fileNames));
            List<String> deletedObjects = deleteObjectsResult.getDeletedObjects();
            for (String objectKey : deletedObjects) {
                log.info("deleting file [ {} ] on aliyun oss", objectKey);
            }
        } finally {
            ossClient.shutdown();
        }
    }

    /**
     * 下载指定文件目录的所有文件, 并下载到本地返回文件集合
     * 注: 本方法仅支持目标oss目录下，不存在其余目录
     *
     * @param config        连接配置
     * @param bucketName    OSS文件篮
     * @param directoryPath OSS目录名字
     * @param localFilePath 本地目录路径
     * @return 下载到本地的文件集合
     * @throws IOException 抛出IO异常
     */
    public static List<File> downloadDirectoryAllFile(OssConfig config, String bucketName, String directoryPath, String localFilePath) throws IOException {
        OSS ossClient = initClient(config);
        List<File> downloadedFiles = new ArrayList<>();
        try {
            ObjectListing objectListing = ossClient.listObjects(bucketName, directoryPath);
            List<OSSObjectSummary> objectSummaries = objectListing.getObjectSummaries();

            for (OSSObjectSummary objectSummary : objectSummaries) {
                String key = objectSummary.getKey();
                String fileName = key.substring(key.lastIndexOf("/") + 1);
                OSSObject object = ossClient.getObject(bucketName, key);
                // 构造本地文件路径
                File downloadedFile = getFile(localFilePath, fileName, object);
                downloadedFiles.add(downloadedFile);
            }
        } finally {
            ossClient.shutdown(); // 关闭OSS客户端连接
        }

        return downloadedFiles;
    }

    /**
     * 获取文件对象
     *
     * @param localFilePath 本地文件路径
     * @param fileName      文件名
     * @param ossObject     oss 对象
     * @return 本地的对象
     * @throws IOException IO异常
     */
    private static File getFile(String localFilePath, String fileName, OSSObject ossObject) throws IOException {
        IoUtil.createDirectoryIfNotExists(localFilePath);
        String localFilePathName = localFilePath + File.separator + fileName;
        // 下载文件到本地
        InputStream inputStream = ossObject.getObjectContent();
        OutputStream outputStream = new FileOutputStream(new File(localFilePathName));

        int bytesRead;
        byte[] buffer = new byte[4096];
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
        outputStream.flush();
        outputStream.close();
        inputStream.close();
        return new File(localFilePathName);
    }

    /**
     * 初始化oss配置
     *
     * @param config 配置
     * @return OSS Client
     */
    private static OSS initClient(OssConfig config) {
        return new OSSClientBuilder().build(config.getEndpoint(), config.getAccessId(), config.getAccessKey());
    }

    /**
     * 包装目标路径
     *
     * @param targetPath 目标路径
     * @return 包装后的目标路径
     */
    private static String covertTargetPath(String targetPath) {
        targetPath = targetPath.replace(StringPool.BACK_SLASH, StringPool.SLASH);
        if (targetPath.startsWith(StringPool.SLASH)) {
            targetPath = targetPath.substring(1);
        }
        return targetPath;
    }

    /**
     * 私有文件生成临时访问路径
     * @param key 访问文件 实例(20220331/2f5837e548674a288f6ecdbb2f0012dc.jpg)
     * @return 可以访问的URL
     */
    public static String covertAccessUrl(OssConfig config, String bucketName, String key) {
        return covertAccessUrl(config, bucketName, key, null);
    }

    /**
     * 私有文件生成临时访问路径
     * @param config 访问配置
     * @param bucketName 桶名称
     * @param filePath 文件路径
     * @param expiration 过期时间
     * @return 访问路径
     */
    public static String covertAccessUrl(OssConfig config, String bucketName, String filePath, Long expiration)  {
        OSS ossClient = initClient(config);
        // 设置URL过期时间为1小时
        if (Objects.isNull(expiration)) {
            expiration = 3600 * 1000L;
        }
        Date expirationDate = new Date(new Date().getTime() + expiration);
        GeneratePresignedUrlRequest generatePresignedUrlRequest;
        generatePresignedUrlRequest = new GeneratePresignedUrlRequest(bucketName, filePath);
        generatePresignedUrlRequest.setExpiration(expirationDate);
        URL url = ossClient.generatePresignedUrl(generatePresignedUrlRequest);
        URI uri;
        try {
            uri = url.toURI();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return uri.getRawPath() + (uri.getRawQuery() != null ? "?" + uri.getRawQuery() : "");
    }

}
