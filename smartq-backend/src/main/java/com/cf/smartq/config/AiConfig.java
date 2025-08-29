package com.cf.smartq.config;


import com.zhipu.oapi.ClientV4;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.TimeUnit;

@Configuration
@ConfigurationProperties(prefix = "ai")
@Data
public class AiConfig {

    private String apiKey;

    // 这些默认值可以覆盖，建议配在 application.yml 里更灵活
    private int requestTimeout = 120; // 请求总超时时间（秒）
    private int connectTimeout = 10;  // 连接超时时间（秒）
    private int readTimeout = 10;     // 读取超时时间（秒）
    private int writeTimeout = 10;    // 写入超时时间（秒）

    @Bean
    public ClientV4 getClientV4() {
        return new ClientV4.Builder(apiKey)
                .networkConfig(
                        requestTimeout,
                        connectTimeout,
                        readTimeout,
                        writeTimeout,
                        TimeUnit.SECONDS
                )
                .build();
    }
}
