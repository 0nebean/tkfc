package com.tkfc.boot.starter.elasticsearch.config;

import com.tkfc.core.constants.StringPool;
import com.tkfc.core.toolkit.CollectionUtil;
import com.tkfc.core.toolkit.PropUtil;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * @author 0neBean
 * @since 2014/1/18
 */
@Configuration
@ConditionalOnProperty(value = "tkfc.elasticsearch.cluster-nodes")
public class ElasticsearchConfig {


//    @Resource
//    private ElasticsearchProperties elasticsearchProperties;

    @Bean
    public RestHighLevelClient initailizationRestHighLevelClient() {
        // 设置ES节点
        List<HttpHost> httpHosts = getHttpHosts();
        RestClientBuilder builder = RestClient.builder(httpHosts.toArray(new HttpHost[0]));
        final CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        String username = PropUtil.getInstance().getConfig("tkfc.elasticsearch.account.username");
        String password = PropUtil.getInstance().getConfig("tkfc.elasticsearch.account.password");
        credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(username, password));
        builder.setHttpClientConfigCallback(httpClientBuilder -> httpClientBuilder
                        // 线程数量
                        .setDefaultIOReactorConfig(IOReactorConfig.custom().setIoThreadCount(1).build())
                        // 认证设置
                        .setDefaultCredentialsProvider(credentialsProvider))
                // 超时时间
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder.setConnectTimeout(5000).setSocketTimeout(60000));
        return new RestHighLevelClient(builder);
    }

    /**
     * 构建http链接
     * @return 集群的http链接
     */
    private List<HttpHost> getHttpHosts() {
        List<HttpHost> httpHosts = new ArrayList<>();
        String clusterNodesStr = PropUtil.getInstance().getConfig("tkfc.elasticsearch.cluster-nodes");
        String schema = PropUtil.getInstance().getConfig("tkfc.elasticsearch.schema");
        List<String> clusterNodes = CollectionUtil.stringArrToList(clusterNodesStr.split(StringPool.COMMA));
        clusterNodes.forEach(node -> {
            try {
                String[] parts = StringUtils.split(node, ":");
                Assert.notNull(parts, "Must defined");
                Assert.state(parts.length == 2, "Must be defined as 'host:port'");
                httpHosts.add(new HttpHost(parts[0], Integer.parseInt(parts[1]), schema));
            } catch (Exception e) {
                throw new IllegalStateException("Invalid ES nodes " + "property '" + node + "'", e);
            }
        });
        return httpHosts;
    }
}
