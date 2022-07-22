package com.config;


import com.parsing.URLExtractor;
import com.parsing.URLValidator;
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
    public URLExtractor urlExtractor() {
        return new URLExtractor(this.pageLoader(), this.urlFilter());
    }

    @Bean
    public PageLoader pageLoader() {
        return new PageLoader(this.crawlerProperties, this.jsoupClient());
    }

    @Bean
    public URLValidator urlFilter() {
        //TODO: Maybe more domains?
        return new URLValidator(this.crawlerProperties.getPermittedDomain());
    }

    @Bean
    public PageLoader.JsoupClient jsoupClient() {
        return new PageLoader.JsoupClient();
    }

}
