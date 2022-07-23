package com;

import com.crawler.ThreadedWebCrawler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"config", "com.config"})
public class WebCrawler {

    public static void main(String[] args) {
        SpringApplication.run(ThreadedWebCrawler.class, args);
    }

}
