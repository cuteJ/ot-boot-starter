package com.onlythinking.starter.oss;

import com.aliyun.oss.OSSClient;
import com.obs.services.ObsClient;
import com.onlythinking.starter.oss.properties.OssProperties;
import com.onlythinking.starter.oss.client.AliyunAppOssClient;
import com.onlythinking.starter.oss.client.AppOssClient;
import com.onlythinking.starter.oss.client.HuaweiAppOssClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Properties;

/**
 * <p> Oss auto configuration </p>
 *
 * @author Li Xingping
 */
@Configuration
@ConditionalOnClass(AppOssClient.class)
@EnableConfigurationProperties(OssProperties.class)
public class OssAutoConfiguration {

    @Configuration
    @ConditionalOnClass({OSSClient.class})
    public static class AliyunConfiguration {
        private final OssProperties properties;

        @Autowired
        public AliyunConfiguration(OssProperties properties) {
            this.properties = properties;
        }

        @Bean(destroyMethod = "shutdown")
        public AppOssClient ossClient() {
            return new AliyunAppOssClient(
              properties.getEndpoint(),
              properties.getAccessKeyId(),
              properties.getSecretAccessKey(),
              applyProperties(properties.getClient())
            );
        }
    }

    @Configuration
    @ConditionalOnClass({ObsClient.class})
    public static class HuaweiConfiguration {
        private final OssProperties properties;

        @Autowired
        public HuaweiConfiguration(OssProperties properties) {
            this.properties = properties;
        }

        @Bean(destroyMethod = "shutdown")
        public AppOssClient ossClient() {
            return new HuaweiAppOssClient(
              properties.getEndpoint(),
              properties.getAccessKeyId(),
              properties.getSecretAccessKey(),
              applyProperties(properties.getClient())
            );
        }
    }

    private static Properties applyProperties(OssProperties.ClientProperties client) {
        Properties pop = new Properties();
        pop.setProperty(AppOssClient.REQUEST_TIMEOUT, String.valueOf(client.getRequestTimeout()));
        pop.setProperty(AppOssClient.CONNECTION_TIMEOUT, String.valueOf(client.getConnectionTimeout()));
        pop.setProperty(AppOssClient.SOCKET_TIMEOUT, String.valueOf(client.getSocketTimeout()));
        pop.setProperty(AppOssClient.IDLE_TIMEOUT, String.valueOf(client.getIdleTimeout()));
        pop.setProperty(AppOssClient.MAX_CONNECTIONS, String.valueOf(client.getMaxConnections()));
        pop.setProperty(AppOssClient.MAX_RETRY, String.valueOf(client.getMaxRetry()));
        return pop;
    }
}
