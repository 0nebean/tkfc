package com.tkfc.core.toolkit;

import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.support.spring.http.converter.FastJsonHttpMessageConverter;
import com.tkfc.core.converter.FastJson2HttpMessageConverterBuilder;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.HttpClient;
import org.apache.hc.client5.http.classic.methods.HttpUriRequestBase;
import org.apache.hc.client5.http.impl.classic.HttpClientBuilder;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpHost;
import org.apache.hc.core5.http.Method;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Objects;

/**
 * rest请求工具类
 *
 * @author 0neBean
 */
@Slf4j
public class RestUtil {

    @SuppressWarnings("all")
    private final static class HttpComponentsClientRestfulHttpRequestFactory extends HttpComponentsClientHttpRequestFactory {

        public HttpComponentsClientRestfulHttpRequestFactory() {
        }

        public HttpComponentsClientRestfulHttpRequestFactory(HttpClient httpClient) {
            super(httpClient);
        }

        @Override
        protected ClassicHttpRequest createHttpUriRequest(HttpMethod httpMethod, URI uri) {
            if (httpMethod == HttpMethod.GET) {
                return new HttpGetRequestWithEntity(uri);
            }
            return super.createHttpUriRequest(httpMethod, uri);
        }
    }

    private final static class HttpGetRequestWithEntity extends HttpUriRequestBase {
        public HttpGetRequestWithEntity(final URI uri) {
            super(Method.GET.name(), uri);
        }

        @Override
        public String getMethod() {
            return Method.GET.name();
        }
    }

    @SuppressWarnings("all")
    private final static class SimpleClientHttpRequestCustomFactory extends SimpleClientHttpRequestFactory {
        @Override
        protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
            super.prepareConnection(connection, httpMethod);
            boolean mayWrite = (
                    "POST".equals(httpMethod) ||
                            "PUT".equals(httpMethod) ||
                            "PATCH".equals(httpMethod) ||
                            "DELETE".equals(httpMethod) ||
                            "GET".equals(httpMethod)
            );
            connection.setDoOutput(mayWrite);
        }
    }

    static private final HttpHeaders common_headers = new HttpHeaders();
    private static volatile RestTemplate restTemplate;
    private static final Object REST_TEMPLATE_LOCK = new Object();
    private static volatile boolean headersInitialized = false;

    private RestUtil() {
    }

    /**
     * 初始化配置文件
     */
    private void initHeader() {
        if (headersInitialized) {
            return;
        }
        synchronized (REST_TEMPLATE_LOCK) {
            if (!headersInitialized) {
                common_headers.add("Content-Type", MediaType.APPLICATION_JSON.toString());
                common_headers.add("Accept", MediaType.APPLICATION_JSON.toString());
                headersInitialized = true;
            }
        }
    }

    /**
     * 合并通用请求头和定制请求头
     *
     * @param headers 定制请求头
     * @return 合并的请求头
     */
    private HttpHeaders mergeRequestHeader(JSONObject headers) {
        HttpHeaders h = JsonUtil.copyObject(common_headers, HttpHeaders.class);
        if (Objects.nonNull(headers)) {
            headers.forEach((k, v) -> {
                h.set(k, v.toString());
            });
        }
        return h;
    }

    /**
     * 当前对象实例
     */
    private static final RestUtil REST_UTILS = new RestUtil();

    /**
     * 获取当前对象实例
     *
     * @return 实例
     */
    public static RestUtil getInstanceWithProxy(String proxyHost, Integer proxyPort) {
        synchronized (REST_TEMPLATE_LOCK) {
            HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();
            httpClientBuilder.setProxy(new HttpHost(proxyHost, proxyPort));
            ClientHttpRequestFactory requestFactory = new HttpComponentsClientRestfulHttpRequestFactory(httpClientBuilder.build());
            DefaultUriBuilderFactory builderFactory = new DefaultUriBuilderFactory();
            builderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
            RestTemplate template = new RestTemplate(requestFactory);
            template.setUriTemplateHandler(builderFactory);
            setFastJson2MessageConverters(template);
            restTemplate = template;
            REST_UTILS.initHeader();
        }
        return REST_UTILS;
    }

    /**
     * 获取当前对象实例
     *
     * @return 实例
     */
    public static RestUtil getInstance() {
        if (restTemplate == null) {
            synchronized (REST_TEMPLATE_LOCK) {
                if (restTemplate == null) {
                    DefaultUriBuilderFactory builderFactory = new DefaultUriBuilderFactory();
                    builderFactory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
                    RestTemplate template = new RestTemplate(new HttpComponentsClientRestfulHttpRequestFactory());
                    template.setUriTemplateHandler(builderFactory);
                    setFastJson2MessageConverters(template);
                    REST_UTILS.initHeader();
                    restTemplate = template;
                }
            }
        }
        return REST_UTILS;
    }

    /**
     * post 请求返回 指定 ParameterizedTypeReference
     *
     * @param url          地址
     * @param responseType 返回类型
     * @param <T>          类型泛型
     * @return responseType 指定类型
     */
    public <T> T doPostForRef(String url, Object bodyParam, ParameterizedTypeReference<T> responseType) {
        return doPostForRef(url, bodyParam, new JSONObject(), responseType);
    }

    /**
     * post 请求返回 指定 ParameterizedTypeReference
     *
     * @param url          地址
     * @param bodyParam    body 参数
     * @param headers      自定义请求头
     * @param responseType 返回类型
     * @param <T>          类型泛型
     * @return responseType 指定类型
     */
    public <T> T doPostForRef(String url, Object bodyParam, JSONObject headers, ParameterizedTypeReference<T> responseType) {
        log.info("RestUtils doPostForRef url = " + url);
        HttpEntity<Object> httpEntity = new HttpEntity<>(bodyParam, mergeRequestHeader(headers));
        ResponseEntity<T> exchange = restTemplate.exchange(url, HttpMethod.POST, httpEntity, responseType);
        return exchange.getBody();
    }

    /**
     * get 请求返回 指定 ParameterizedTypeReference
     *
     * @param url          地址
     * @param bodyParam    body 参数
     * @param responseType 返回类型
     * @param <T>          类型泛型
     * @return responseType 指定类型
     */
    public <T> T doGetForRef(String url, Object bodyParam, ParameterizedTypeReference<T> responseType) {
        return doGetForRef(url, bodyParam, new JSONObject(), responseType);
    }

    /**
     * get 请求返回 指定 ParameterizedTypeReference
     *
     * @param url          地址
     * @param bodyParam    body 参数
     * @param uriVariables uri 参数
     * @param responseType 返回类型
     * @param <T>          类型泛型
     * @return responseType 指定类型
     */
    public <T> T doGetForRef(String url, Object bodyParam, JSONObject uriVariables, ParameterizedTypeReference<T> responseType) {
        return doGetForRef(url, bodyParam, uriVariables, new JSONObject(), responseType);
    }

    /**
     * get 请求返回 指定 ParameterizedTypeReference
     *
     * @param url          地址
     * @param bodyParam    body 参数
     * @param uriVariables uri 参数
     * @param headers      自定义请求头
     * @param responseType 返回类型
     * @param <T>          类型泛型
     * @return responseType 指定类型
     */
    public <T> T doGetForRef(String url, Object bodyParam, JSONObject uriVariables, JSONObject headers, ParameterizedTypeReference<T> responseType) {
        log.info("RestUtils doGetForRef url = " + url);
        HttpEntity<Object> httpEntity = new HttpEntity<>(bodyParam, mergeRequestHeader(headers));
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, responseType, uriVariables).getBody();
    }

    /**
     * post 请求返回 指定 class
     *
     * @param url       地址
     * @param bodyParam body 参数
     * @param clazz     返回类型
     * @param <T>       类型泛型
     * @return clazz 指定类型
     */
    public <T> T doPostForObj(String url, Object bodyParam, Class<T> clazz) {
        return doPostForObj(url, bodyParam, null, clazz);
    }

    /**
     * post 请求返回 指定 class
     *
     * @param url       地址
     * @param bodyParam body 参数
     * @param headers   自定义请求头
     * @param clazz     返回类型
     * @param <T>       类型泛型
     * @return clazz 指定类型
     */
    public <T> T doPostForObj(String url, Object bodyParam, JSONObject headers, Class<T> clazz) {
        log.info("RestUtils doPostForObj url = " + url);
        HttpEntity<Object> httpEntity = new HttpEntity<>(bodyParam, mergeRequestHeader(headers));
        return restTemplate.postForEntity(url, httpEntity, clazz).getBody();
    }

    /**
     * get 请求返回 指定 class
     *
     * @param url       地址
     * @param bodyParam body 参数
     * @param clazz     返回类型
     * @param <T>       类型泛型
     * @return clazz 指定类型
     */
    public <T> T doGetForObj(String url, Object bodyParam, Class<T> clazz) {
        return doGetForObj(url, bodyParam, new JSONObject(), new JSONObject(), clazz);
    }

    /**
     * get 请求返回 指定 class
     *
     * @param url       地址
     * @param bodyParam body 参数
     * @param headers   自定义请求头
     * @param clazz     返回类型
     * @param <T>       类型泛型
     * @return clazz 指定类型
     */
    public <T> T doGetForObj(String url, Object bodyParam, JSONObject headers, Class<T> clazz) {
        return doGetForObj(url, bodyParam, new JSONObject(), headers, clazz);
    }

    /**
     * get 请求返回 指定 class
     *
     * @param url          地址
     * @param bodyParam    body 参数
     * @param uriVariables uri 参数
     * @param headers      自定义请求头
     * @param clazz        返回类型
     * @param <T>          类型泛型
     * @return clazz 指定类型
     */
    public <T> T doGetForObj(String url, Object bodyParam, JSONObject uriVariables, JSONObject headers, Class<T> clazz) {
        log.info("RestUtils doGetForObj url = " + url);
        HttpEntity<Object> httpEntity = new HttpEntity<>(bodyParam, mergeRequestHeader(headers));
        return restTemplate.exchange(url, HttpMethod.GET, httpEntity, clazz, uriVariables).getBody();
    }

    /**
     * 发起post请求 接收xml 转 xml
     *
     * @param url       地址
     * @param bodyParam 参数
     * @return json
     */
    public JSONObject doPostForXml(String url, JSONObject bodyParam) {
        log.info("RestUtils doPostForXmlObj url = " + url);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("ContentType", MediaType.APPLICATION_XML.toString());
        httpHeaders.add("Accept", MediaType.APPLICATION_XML.toString());
        HttpEntity<String> formEntity;
        if (null != bodyParam) {
            String xmlParam = XmlUtil.jsonToXml(bodyParam, false);
            formEntity = new HttpEntity<>(xmlParam, httpHeaders);
        } else {
            formEntity = new HttpEntity<>(httpHeaders);
        }
        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, formEntity, String.class);
        String resXml = responseEntity.getBody();
        return XmlUtil.xmlToJson(resXml);
    }

    /**
     * 设置 Fastjson2 作为消息转换器，替换默认的 Jackson 转换器。
     * 此方法首先移除所有基于 Jackson 的消息转换器，然后将 Fastjson2 消息转换器添加到列表中，并置于首位以确保其优先级。
     */
    private static void setFastJson2MessageConverters(RestTemplate template) {
        template.getMessageConverters().removeIf(c -> c.getClass().getName().contains("MappingJackson2"));
        FastJsonHttpMessageConverter fastJsonHttpMessageConverter = FastJson2HttpMessageConverterBuilder.buildFastJson2HttpMessageConverter();
        template.getMessageConverters().addFirst(fastJsonHttpMessageConverter);
    }
}
