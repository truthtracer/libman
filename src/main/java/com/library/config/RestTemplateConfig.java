package com.library.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
public class RestTemplateConfig {
    @Bean
    public RestTemplate restTemplate() throws Exception {
        return createNewRestTemplate(3000, 3000, 30000);
    }

    @Bean
    public RestTemplate reportRestTemplate() throws Exception {
        return createNewRestTemplate(3000, 5000, 30000, 2);
    }

    @Bean
    public RestTemplate behRestTemplate() throws Exception {
        return createNewRestTemplate(1000, 1000, 3000);
    }

    @Bean
    public RestTemplate publicRestTemplate() throws Exception {
        return createNewRestTemplate(1500, 1500, 3500);
    }

    private RestTemplate createNewRestTemplate(final int requestTimeout, final int connectTimeout,
        final int readTimeout) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return createNewRestTemplate(requestTimeout, connectTimeout, readTimeout, 1);
    }

    public RestTemplate createNewRestTemplate(final int requestTimeout, final int connectTimeout, final int readTimeout,
        final int maxPoolSize) throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        final HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
        factory.setConnectTimeout(connectTimeout);
        factory.setReadTimeout(readTimeout);
        factory.setConnectionRequestTimeout(requestTimeout);
        // https
        factory.setHttpClient(httpClient(maxPoolSize));

        final RestTemplate restTemplate = new RestTemplate(factory);
        // 配置JSON解析器
        final ObjectMapper objectMapper = new ObjectMapper();
        // 忽略不存在字段
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        final List<HttpMessageConverter<?>> messageConverters = restTemplate.getMessageConverters().stream()
            .filter(c -> !(c instanceof MappingJackson2HttpMessageConverter)).collect(Collectors.toList());
        final MappingJackson2HttpMessageConverter jsonMessageConverter = new MappingJackson2HttpMessageConverter();
        jsonMessageConverter.setObjectMapper(objectMapper);
        messageConverters.add(jsonMessageConverter);
        restTemplate.setMessageConverters(messageConverters);
        return restTemplate;
    }

    //默认单线程
    public static CloseableHttpClient httpClient()
        throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        return httpClient(1);
    }


    public static CloseableHttpClient httpClient(final int maxPoolSize)
        throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
        final SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, (X509Certificate[] x509Certificates, String s) -> true);
        final SSLConnectionSocketFactory socketFactory =
            new SSLConnectionSocketFactory(builder.build(), new String[] {"SSLv2Hello", "SSLv3", "TLSv1", "TLSv1.2"},
                null, NoopHostnameVerifier.INSTANCE);
        final Registry<ConnectionSocketFactory> registry =
            RegistryBuilder.<ConnectionSocketFactory>create().register("http", new PlainConnectionSocketFactory())
                .register("https", socketFactory).build();
        final PoolingHttpClientConnectionManager poolingHttpClientConnectionManager =
            new PoolingHttpClientConnectionManager(registry);
        poolingHttpClientConnectionManager.setMaxTotal(maxPoolSize);
        return HttpClients.custom().setSSLSocketFactory(socketFactory)
            .setConnectionManager(poolingHttpClientConnectionManager).setConnectionManagerShared(true).build();
    }
}