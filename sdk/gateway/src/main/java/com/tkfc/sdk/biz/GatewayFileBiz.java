package com.tkfc.sdk.biz;

import com.tkfc.core.constants.StringPool;
import com.tkfc.core.enums.SystemTypeEnum;
import com.tkfc.core.toolkit.*;
import com.tkfc.sdk.aliyun.oss.AliyunOssUtil;
import com.tkfc.sdk.aliyun.oss.config.OssConfig;
import com.tkfc.sdk.aws.s3.AwsS3Util;
import com.tkfc.sdk.aws.s3.config.S3Config;
import com.tkfc.sdk.tencent.cos.TencentCosUtil;
import com.tkfc.sdk.tencent.cos.config.CosConfig;
import com.tkfc.sdk.pojo.vo.BizFileTranVo;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Optional;

/**
 * 上传文件的服务
 *
 * @author 0neBean
 * @version 1.0
 * @since 2024-08-23 16:08:09
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GatewayFileBiz {

    private final static OssConfigLocal ossConfigLocal = loadOssConfig();
    private final static S3ConfigLocal s3ConfigLocal = loadS3Config();
    private final static CosConfigLocal cosConfigLocal = loadCosConfig();

    private final static String S3_CDN_TYPE = "s3";
    private final static String OSS_CDN_TYPE = "oss";
    private final static String COS_CDN_TYPE = "cos";


    /**
     * 实际上传文件方法
     *
     * @param file       需要上传的文件
     * @param targetPath 目标上传路径
     * @param fileName   文件名称
     * @return 上传后的url
     */
    public BizFileTranVo uploadFile(File file, String targetPath, String fileName) throws Exception {
        String accessUrl = null;
        String finalTargetPath;
        String fileSuffix = Optional.ofNullable(IoUtil.getFilePathTypeName(file.getName())).orElse(StringPool.EMPTY);
        String fileType = fileSuffix.replace(StringPool.DOT, StringPool.EMPTY);
        String fileFullName = String.format("%s%s", fileName, fileSuffix);
        // 按 cdn.type 选择 oss / s3 / cos
        String cdnType = PropUtil.getInstance().getConfigWithDefaultValue("cdn.type", OSS_CDN_TYPE);
        if (Objects.equals(cdnType, S3_CDN_TYPE)) {
            //上传s3
            finalTargetPath = String.format("%s%s/%s", s3ConfigLocal.getCommonPath(), targetPath, fileFullName);
            AwsS3Util.uploadFileToS3(s3ConfigLocal.getS3Config(), s3ConfigLocal.getBucketName(), file, finalTargetPath, isStaticFile(file.getName()));
            accessUrl = String.format("%s%s%s/%s", s3ConfigLocal.getAccessHost(), s3ConfigLocal.getCommonPath(), targetPath, fileFullName);
        } else if (Objects.equals(cdnType, COS_CDN_TYPE)) {
            finalTargetPath = String.format("%s%s/%s", cosConfigLocal.getCommonPath(), targetPath, fileFullName);
            TencentCosUtil.uploadFileToCos(cosConfigLocal.getCosConfig(), cosConfigLocal.getBucketName(), file, finalTargetPath, isStaticFile(file.getName()));
            accessUrl = String.format("%s/%s%s/%s", trimTrailingSlash(cosConfigLocal.getAccessHost()), cosConfigLocal.getCommonPath(), targetPath, fileFullName);
        } else {
            //上传oss
            finalTargetPath = String.format("%s%s/%s", ossConfigLocal.getCommonPath(), targetPath, fileFullName);
            AliyunOssUtil.uploadFileToOss(ossConfigLocal.getOssConfig(), ossConfigLocal.getBucketName(), file, finalTargetPath, isStaticFile(file.getName()));
            accessUrl = String.format("%s%s%s/%s", ossConfigLocal.getAccessHost(), ossConfigLocal.getCommonPath(), targetPath, fileFullName);
        }
        //获取文件尺寸信息
        Integer fileSize = Optional.of(Files.size(Paths.get(file.getPath()))).map(s -> s + "").map(Integer::parseInt).orElse(null);
        return BizFileTranVo.builder().url(accessUrl).fileSize(fileSize).fileType(fileType).build();
    }


    /**
     * 下载文件到服务器
     * @param fileUrl 文件下载链接
     * @param targetPath 目标路径
     * @param fileName 文件名称
     */
    public String downloadFileToServer(String fileUrl, String targetPath, String fileName) {
        String path = null;
        try {
            path = String.format("%s%s", targetPath, fileName);
            IoUtil.downloadFile(fileUrl, path);
        } catch (Exception e) {
            log.error("download file to server got and error = ", e);
        }
        return path;
    }

    /**
     * 下载文件到服务器
     * @param fileUrl 文件下载链接
     */
    public String downloadFileToServer(String fileUrl) {
        return downloadFileToServer(fileUrl, getUploadTempPath(), String.format("%s%s", SnowflakeIdUtil.generateId(), IoUtil.getFilePathTypeName(fileUrl)));
    }

    /**
     * 下载文件
     *
     * @param fileName            文件名
     * @param deleteAfterDownload 下载后删除
     */
    public void downloadFile(String fileName, Boolean deleteAfterDownload) {
        try {
            String filePath = getDownloadTempPath() + fileName;
            HttpServletResponse response = WebUtil.getHttpServletResponse();
            response.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            response.setContentLength(Optional.of(Files.size(Paths.get(filePath))).map(s -> s + "").map(Integer::parseInt).orElse(null));
            IoUtil.setAttachmentResponseHeader(response, fileName);
            IoUtil.writeBytes(filePath, response.getOutputStream());
            if (deleteAfterDownload) {
                IoUtil.deleteQuietly(filePath);
            }
        } catch (Exception e) {
            log.error("download file got and error = ", e);
        }
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class S3ConfigLocal {
        private S3Config s3Config;
        private String accessHost;
        private String bucketName;
        private String commonPath;
    }


    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class OssConfigLocal {
        private OssConfig ossConfig;
        private String accessHost;
        private String bucketName;
        private String commonPath;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    static class CosConfigLocal {
        private CosConfig cosConfig;
        private String accessHost;
        private String bucketName;
        private String commonPath;
    }

    /**
     * 获取oss配置对象
     *
     * @return oss配置对象
     */
    static OssConfigLocal loadOssConfig() {
        String endpoint = PropUtil.getInstance().getConfig("cdn.oss.endpoint");
        String accessId = PropUtil.getInstance().getConfig("cdn.oss.accessId");
        String accessKey = PropUtil.getInstance().getConfig("cdn.oss.accessKey");
        String bucketName = PropUtil.getInstance().getConfig("cdn.oss.bucketName");
        String commonPath = PropUtil.getInstance().getConfig("cdn.oss.common.path");
        String accessHost = PropUtil.getInstance().getConfig("cdn.oss.access.host");
        OssConfig ossConfig = OssConfig.builder().accessId(accessId).accessKey(accessKey).endpoint(endpoint).build();
        return OssConfigLocal.builder().bucketName(bucketName).commonPath(commonPath).ossConfig(ossConfig).accessHost(accessHost).build();
    }

    static CosConfigLocal loadCosConfig() {
        String region = PropUtil.getInstance().getConfig("cdn.cos.region");
        String secretId = PropUtil.getInstance().getConfig("cdn.cos.secretId");
        String secretKey = PropUtil.getInstance().getConfig("cdn.cos.secretKey");
        String bucketName = PropUtil.getInstance().getConfig("cdn.cos.bucketName");
        String commonPath = PropUtil.getInstance().getConfig("cdn.cos.common.path");
        String accessHost = PropUtil.getInstance().getConfig("cdn.cos.access.host");
        CosConfig cosConfig = CosConfig.builder().region(region).secretId(secretId).secretKey(secretKey).build();
        return CosConfigLocal.builder().bucketName(bucketName).commonPath(commonPath).cosConfig(cosConfig).accessHost(accessHost).build();
    }

    /**
     * 获取oss配置对象
     *
     * @return oss配置对象
     */
    static S3ConfigLocal loadS3Config() {
        String accessKey = PropUtil.getInstance().getConfig("cdn.s3.accessKey");
        String secretKey = PropUtil.getInstance().getConfig("cdn.s3.secretKey");
        String region = PropUtil.getInstance().getConfig("cdn.s3.region");
        String bucketName = PropUtil.getInstance().getConfig("cdn.s3.bucketName");
        String commonPath = PropUtil.getInstance().getConfig("cdn.s3.common.path");
        String accessHost = PropUtil.getInstance().getConfig("cdn.s3.access.host");
        S3Config s3Config = S3Config.builder().accessKey(accessKey).secretKey(secretKey).region(region).build();
        return S3ConfigLocal.builder().s3Config(s3Config).bucketName(bucketName).commonPath(commonPath).accessHost(accessHost).build();
    }

    /**
     * 获取上传临时目录
     *
     * @return 上传临时目录
     */
    public static String getUploadTempPath() {
        String uploadPath;
        if (Objects.equals(SystemTypeEnum.WINDOWS, EnvUtil.getOsType())) {
            uploadPath = PropUtil.getInstance().getConfig("cdn.upload.path.windows");
        } else {
            uploadPath = PropUtil.getInstance().getConfig("cdn.upload.path.unix");
        }
        IoUtil.createDirectoryIfNotExists(uploadPath);
        return uploadPath;
    }

    /**
     * 获取下载临时目录
     *
     * @return 下载临时目录
     */
    public static String getDownloadTempPath() {
        String downloadPath;
        if (Objects.equals(SystemTypeEnum.WINDOWS, EnvUtil.getOsType())) {
            downloadPath = PropUtil.getInstance().getConfig("cdn.download.path.windows");
        } else {
            downloadPath = PropUtil.getInstance().getConfig("cdn.download.path.unix");
        }
        IoUtil.createDirectoryIfNotExists(downloadPath);
        return downloadPath;
    }

    /**
     * 是否是静态文件
     *
     * @param fileName 文件名
     * @return bool
     */
    private static boolean isStaticFile(String fileName) {
        return fileName.endsWith(".css") || fileName.endsWith(".js") || fileName.endsWith(".json");
    }

    private static String trimTrailingSlash(String host) {
        if (host == null) {
            return StringPool.EMPTY;
        }
        return host.endsWith(StringPool.SLASH) ? host.substring(0, host.length() - 1) : host;
    }
}
