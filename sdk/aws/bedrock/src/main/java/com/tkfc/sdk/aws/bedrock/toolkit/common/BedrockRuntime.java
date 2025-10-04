package com.tkfc.sdk.aws.bedrock.toolkit.common;

import com.tkfc.sdk.aws.bedrock.constant.Constants;
import com.tkfc.sdk.aws.bedrock.pojo.config.BedrockConfig;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProviderChain;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.http.apache.ProxyConfiguration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.bedrockruntime.BedrockRuntimeClient;

import java.net.URI;
import java.time.Duration;
import java.util.Objects;

public class BedrockRuntime {

    /**
     * 初始化Claude client
     *
     * @param config Claude配置
     * @return Claude client
     */
    protected static BedrockRuntimeClient initClaudeClient(BedrockConfig config) {
        // 创建 ApacheHttpClient，并设置超时
        ApacheHttpClient.Builder httpClientBuilder = ApacheHttpClient.builder()
                .connectionTimeout(Duration.ofSeconds(Objects.nonNull(config.getConnectionTimeout()) ? config.getConnectionTimeout() : Constants.bedrock.CONNECTION_TIMEOUT)) // 设置连接超时
                .socketTimeout(Duration.ofSeconds(Objects.nonNull(config.getSocketTimeout()) ? config.getSocketTimeout() : Constants.bedrock.SOCKET_TIMEOUT));   // 设置请求超时

        if (Objects.nonNull(config.getProxyEndpoint())) {
            httpClientBuilder.proxyConfiguration(ProxyConfiguration.builder()
                    .endpoint(URI.create(config.getProxyEndpoint()))
                    .build());
        }

        return BedrockRuntimeClient.builder()
                .credentialsProvider(AwsCredentialsProviderChain
                        .builder()
                        .addCredentialsProvider(() -> AwsBasicCredentials.create(config.getAccessKey(), config.getSecretKey())).build())
                .httpClient(httpClientBuilder.build())
                .region(Region.of(config.getRegion()))
                .build();
    }
}
