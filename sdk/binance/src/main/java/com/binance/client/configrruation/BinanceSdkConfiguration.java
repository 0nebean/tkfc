package com.binance.client.configrruation;

import com.binance.client.RequestOptions;
import com.binance.client.SubscriptionClient;
import com.binance.client.SyncRequestClient;
import com.tkfc.core.toolkit.PropUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 初始化配置类
 *
 * @author 0neBean
 * @version 1.0
 * @since 2021/6/26 22:20
 */
@Configuration(proxyBeanMethods = false)
public class BinanceSdkConfiguration {

    private final static String API_KEY = PropUtil.getInstance().getConfig("binance.api.key");
    private final static String SECRET_KEY = PropUtil.getInstance().getConfig("binance.api.secret");

    @Bean(name = "syncRequestClient")
    public SyncRequestClient syncRequestClient() {
        return SyncRequestClient.create(API_KEY, SECRET_KEY, new RequestOptions());
    }

    @Bean(name = "subscriptionClient")
    public SubscriptionClient subscriptionClient() {
        return SubscriptionClient.create();
    }
}
