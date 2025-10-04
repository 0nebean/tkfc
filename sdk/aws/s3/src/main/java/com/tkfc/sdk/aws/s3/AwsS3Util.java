package com.tkfc.sdk.aws.s3;


import com.tkfc.compress.toolkit.CompressUtil;
import com.tkfc.core.constants.StringPool;
import com.tkfc.core.enums.SystemTypeEnum;
import com.tkfc.core.function.SerializableBiConsumer;
import com.tkfc.core.toolkit.EnvUtil;
import com.tkfc.core.toolkit.IoUtil;
import com.tkfc.core.toolkit.StringUtil;
import com.tkfc.sdk.aws.s3.config.S3Config;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Objects;

/**
 * 操作aws s3
 *
 * @author 0neBean
 * @since 2023-04-09 15:48:04
 */
@Slf4j
public class AwsS3Util {


    private final static String COMMON_SYSTEM_SEPARATOR = EnvUtil.CURRENT_OPERATING_SYSTEM_SEPARATOR;
    private final static String OPERATE_SYSTEM_SEPARATOR = Objects.equals(SystemTypeEnum.WINDOWS, EnvUtil.getOsType()) ? COMMON_SYSTEM_SEPARATOR + COMMON_SYSTEM_SEPARATOR : COMMON_SYSTEM_SEPARATOR;

    /**
     * 上传目录下所有文件到OSS
     *
     * @param config     s3配置
     * @param bucketName 储存桶名称
     * @param filePath   本地文件路径
     * @param targetPath 上传目标目录
     * @param zipStatic  压缩静态资源
     */
    public static void uploadAllFileToS3(S3Config config, String bucketName, String filePath, String targetPath, Boolean zipStatic) throws IOException {
        uploadAllFileToS3(config, bucketName, filePath, targetPath, null);
    }


    /**
     * 上传目录下所有文件到OSS
     *
     * @param config       s3配置
     * @param bucketName   s3文件篮
     * @param filePath     本地文件路径
     * @param targetPath   上传目标目录
     * @param zipStatic    压缩静态资源
     * @param beforeUpload 上传之前的逻辑
     */
    public static void uploadAllFileToS3(S3Config config, String bucketName, String filePath, String targetPath, Boolean zipStatic, SerializableBiConsumer<String, File> beforeUpload) throws IOException {
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
            AwsS3Util.uploadFileToS3(config, bucketName, file, ossPath, zipStatic);
        }
    }

    /**
     * 上传文件到 aliyun oss
     *
     * @param config     s3配置
     * @param bucketName s3文件篮
     * @param uploadFile 上传的文件
     * @param targetPath 上传目标目录
     * @param zipStatic  压缩静态资源
     */
    public static void uploadFileToS3(S3Config config, String bucketName, File uploadFile, String targetPath, Boolean zipStatic) throws IOException {
        File tempFile = uploadFile;
        boolean notMinFile = IoUtil.notMinFile(uploadFile.getPath());
        boolean staticFile = IoUtil.isCSSFile(uploadFile.getPath()) || IoUtil.isJSFile(uploadFile.getPath());
        try (S3Client s3Client = initS3Client(config)) {
            if (zipStatic && notMinFile && staticFile) {
                tempFile = IoUtil.copyTempFile(uploadFile);
                CompressUtil.compress(uploadFile, tempFile);
            }
            PutObjectRequest.Builder putOb = PutObjectRequest.builder();
            putOb.bucket(bucketName);
            putOb.key(targetPath);
            s3Client.putObject(putOb.build(), Paths.get(tempFile.getPath()));
        } finally {
            if (zipStatic && notMinFile && staticFile) {
                IoUtil.deleteQuietly(tempFile.getParentFile());
            }
        }
    }

    /**
     * 初始化s3 client
     *
     * @param config s3配置
     * @return S3Client
     */
    private static S3Client initS3Client(S3Config config) {
        return S3Client.builder()
                .credentialsProvider(AwsCredentialsProviderChain
                        .builder()
                        .addCredentialsProvider(() -> AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey())).build())
                .accelerate(Boolean.FALSE)
                .region(Region.of(config.getRegion()))
                .build();
    }

}
