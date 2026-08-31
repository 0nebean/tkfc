package com.tkfc.sdk.tencent.cos;

import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.http.HttpProtocol;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import com.tkfc.compress.toolkit.CompressUtil;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.enums.SystemTypeEnum;
import com.tkfc.core.function.SerializableBiConsumer;
import com.tkfc.core.toolkit.EnvUtil;
import com.tkfc.core.toolkit.IoUtil;
import com.tkfc.core.toolkit.StringUtil;
import com.tkfc.sdk.tencent.cos.config.CosConfig;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 腾讯云 COS 上传
 *
 * @author 0neBean
 */
@Slf4j
public class TencentCosUtil {

    private final static String COMMON_SYSTEM_SEPARATOR = EnvUtil.CURRENT_OPERATING_SYSTEM_SEPARATOR;
    private final static String OPERATE_SYSTEM_SEPARATOR = Objects.equals(SystemTypeEnum.WINDOWS, EnvUtil.getOsType()) ? COMMON_SYSTEM_SEPARATOR + COMMON_SYSTEM_SEPARATOR : COMMON_SYSTEM_SEPARATOR;

    /**
     * 上传目录下所有文件到 COS
     */
    public static void uploadAllFileToCos(CosConfig config, String bucketName, String filePath, String targetPath, Boolean zipStatic) throws Exception {
        uploadAllFileToCos(config, bucketName, filePath, targetPath, zipStatic, null);
    }

    /**
     * 上传目录下所有文件到 COS
     */
    public static void uploadAllFileToCos(CosConfig config, String bucketName, String filePath, String targetPath, Boolean zipStatic, SerializableBiConsumer<String, File> beforeUpload) throws Exception {
        targetPath = covertTargetPath(targetPath);
        Path root = Paths.get(filePath);
        List<File> files;
        try (Stream<Path> walk = Files.walk(root)) {
            files = walk.filter(Files::isRegularFile).map(Path::toFile).collect(Collectors.toList());
        }
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
            String cosPath = pathBuilder.toString().replace(StringPool.BACK_SLASH, StringPool.SLASH);
            cosPath = StringUtil.concat(targetPath, StringPool.SLASH, cosPath, file.getName());
            if (Objects.nonNull(beforeUpload)) {
                beforeUpload.accept(cosPath, file);
            }
            uploadFileToCos(config, bucketName, file, cosPath, zipStatic);
        }
    }

    /**
     * 上传文件到腾讯云 COS
     *
     * @param config     连接配置
     * @param bucketName 存储桶名称
     * @param uploadFile 本地文件
     * @param targetPath 对象 Key
     * @param zipStatic  是否压缩 css/js
     */
    public static void uploadFileToCos(CosConfig config, String bucketName, File uploadFile, String targetPath, Boolean zipStatic) throws Exception {
        File tempFile = uploadFile;
        boolean notMinFile = IoUtil.notMinFile(uploadFile.getPath());
        boolean staticFile = IoUtil.isCSSFile(uploadFile.getPath()) || IoUtil.isJSFile(uploadFile.getPath());
        COSClient cosClient = initClient(config);
        try {
            if (Boolean.TRUE.equals(zipStatic) && notMinFile && staticFile) {
                tempFile = IoUtil.copyTempFile(uploadFile);
                CompressUtil.compress(uploadFile, tempFile);
            }
            String key = covertTargetPath(targetPath);
            PutObjectRequest request = new PutObjectRequest(bucketName, key, tempFile);
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(tempFile.length());
            request.setMetadata(metadata);
            cosClient.putObject(request);
            log.info("uploaded file [{}] to tencent cos bucket [{}]", key, bucketName);
        } finally {
            cosClient.shutdown();
            if (Boolean.TRUE.equals(zipStatic) && notMinFile && staticFile) {
                IoUtil.deleteQuietly(tempFile.getParentFile());
            }
        }
    }

    private static COSClient initClient(CosConfig config) {
        COSCredentials cred = new BasicCOSCredentials(config.getSecretId(), config.getSecretKey());
        ClientConfig clientConfig = new ClientConfig(new Region(config.getRegion()));
        clientConfig.setHttpProtocol(HttpProtocol.https);
        return new COSClient(cred, clientConfig);
    }

    private static String covertTargetPath(String targetPath) {
        if (targetPath == null) {
            return StringPool.EMPTY;
        }
        targetPath = targetPath.replace(StringPool.BACK_SLASH, StringPool.SLASH);
        while (targetPath.startsWith(StringPool.SLASH)) {
            targetPath = targetPath.substring(1);
        }
        return targetPath;
    }
}
