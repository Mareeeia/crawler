package com.crawler;

import com.config.CrawlerProperties;
import com.config.WebCrawlerConfiguration;
import com.url.URLExtractor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
@Getter
@Service
@Import({WebCrawlerConfiguration.class, URLExtractor.class})
public class ThreadedWebCrawler {

    private final URLExtractor urlExtractor;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final String rootUrl;

    private final Semaphore semaphore = new Semaphore(1);
    private final ConcurrentHashMap<String, Boolean> visited;  // Will be used as a set
    private final ConcurrentHashMap<String, Boolean> toVisit;  // Will be used as a set

    public ThreadedWebCrawler(URLExtractor urlExtractor,
                              ThreadPoolExecutor threadPoolExecutor,
                              CrawlerProperties crawlerProperties) {
        this.urlExtractor = urlExtractor;
        this.threadPoolExecutor = threadPoolExecutor;
        this.rootUrl = crawlerProperties.getStartingWebsite();
        this.visited = new ConcurrentHashMap<>();
        this.toVisit = new ConcurrentHashMap<>();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void concurrentCrawl() throws InterruptedException {
        try {
            this.semaphore.acquire();
            this.toVisit.put(this.rootUrl, true);
            this.threadPoolExecutor.execute(() -> this.crawl(this.rootUrl));
        } finally {
            if (this.toVisit.size() > 0) {
                this.semaphore.acquire();
            }
            log.info("Finished crawling, printing results.");
            this.urlExtractor.printResults();
            this.threadPoolExecutor.shutdown();
        }
    }

    private void crawl(String url) {
        if (url == null || this.visited.containsKey(url)) return;
        try {
            this.visited.put(url, true);
            this.urlExtractor.extractUrlsFromPage(url).stream()
                    .filter(newUrl -> !this.visited.containsKey(newUrl) && !this.toVisit.containsKey(newUrl))
                    .forEach(this::queueNewUrl);
            log.info(generateLogString());
        } finally {
            this.toVisit.remove(url);
            if (this.toVisit.isEmpty()) {
                this.semaphore.release();
            }
        }
    }

    private void queueNewUrl(String newUrl) {
        this.toVisit.put(newUrl, true);
        try {
            this.threadPoolExecutor.execute(() -> this.crawl(newUrl));
        } catch (Exception e) {
            this.toVisit.remove(newUrl);
            throw e;
        }
    }

    private String generateLogString() {
        return String.format("[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, Visited: %d, ToVisit: %d",
                this.threadPoolExecutor.getPoolSize(),
                this.threadPoolExecutor.getCorePoolSize(),
                this.threadPoolExecutor.getActiveCount(),
                this.threadPoolExecutor.getCompletedTaskCount(),
                this.threadPoolExecutor.getTaskCount(),
                this.visited.size(),
                this.toVisit.size());
    }
}
