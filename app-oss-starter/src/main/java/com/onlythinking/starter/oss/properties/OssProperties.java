package com.onlythinking.starter.oss.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * <p> The describe </p>
 *
 * @author Li Xingping
 */
@Data
@ConfigurationProperties("app.oss")
public class OssProperties {

    private String endpoint;
    private String domainUrl;
    private String accessKeyId;
    private String secretAccessKey;

    private String defaultBucket;

    private ClientProperties client = new ClientProperties();

    @Data
    public static class ClientProperties {
        private int requestTimeout = 5 * 60 * 1000;
        private int connectionTimeout = -1;
        private int socketTimeout = 50 * 1000;
        private int idleTimeout = 60 * 1000;
        private int maxConnections = 1024;
        private int maxRetry = 3;
    }
}
