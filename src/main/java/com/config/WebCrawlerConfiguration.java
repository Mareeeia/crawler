package com.config;


import com.url.URLValidator;
import com.web.PageLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.util.CallerBlocksPolicy;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Configuration
@EnableConfigurationProperties({CrawlerProperties.class})
public class WebCrawlerConfiguration {

    @Autowired
    private CrawlerProperties crawlerProperties;

    @Bean
    public ThreadPoolExecutor threadPoolExecutor() {
        return new ThreadPoolExecutor(this.crawlerProperties.getThreadsMin(),
                this.crawlerProperties.getThreadsMax(),
                this.crawlerProperties.getKeepAlive(),
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(this.crawlerProperties.getQueueCapacity()),
                new CallerBlocksPolicy(this.crawlerProperties.getBlockTime()));
    }

    @Bean
    public PageLoader pageLoader() {
        return new PageLoader(this.crawlerProperties, new PageLoader.JsoupClient());
    }

    @Bean
    public URLValidator urlFilter() {
        return new URLValidator(this.crawlerProperties.getPermittedDomain());
    }

}
