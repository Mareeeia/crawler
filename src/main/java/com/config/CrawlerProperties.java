package com.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties
public class CrawlerProperties {

    private final String permittedDomain = "monzo.com";
    private final String startingWebsite = "http://monzo.com";
    private final Integer threadsMin = 10;
    private final Integer threadsMax = 50;
    private final Long keepAlive = 100L;
    private final Long blockTime = 1000L;
    private final Integer queueCapacity = 500000;
    private final Long sleepWhenThrottled = 30L;
    private final Integer pageRetries = 3;

}
