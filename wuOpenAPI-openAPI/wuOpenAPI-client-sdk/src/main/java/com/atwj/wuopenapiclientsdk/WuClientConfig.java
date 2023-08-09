package com.atwj.wuopenapiclientsdk;

import com.atwj.wuopenapiclientsdk.client.WuApiClient;
import lombok.Data;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
// 能读取 application 中的配置属性
@ConfigurationProperties("wj.client")
@Data
public class WuClientConfig {

    private String accessKey;
    private String secretKey;

    @Bean
    public WuApiClient WuApiClient(){
        return new WuApiClient(accessKey, secretKey);
    }

}
