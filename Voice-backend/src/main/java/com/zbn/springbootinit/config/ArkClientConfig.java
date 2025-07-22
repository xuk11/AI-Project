package com.zbn.springbootinit.config;

import com.volcengine.ark.runtime.service.ArkService;
import lombok.Data;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = "ark.config")
@Data
public class ArkClientConfig {
    private String apiKey;

    private String baseUrl;

    static ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);

    static Dispatcher dispatcher = new Dispatcher();

    @Bean
    public ArkService arkService() {
        return ArkService.builder().dispatcher(dispatcher)
                .connectionPool(connectionPool)
                .baseUrl(baseUrl)
                .apiKey(apiKey).build();
    }
}
