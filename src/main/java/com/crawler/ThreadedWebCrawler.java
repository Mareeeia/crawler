package com.crawler;

import com.config.CrawlerProperties;
import com.config.WebCrawlerConfiguration;
import com.parsing.URLExtractor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Import;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

@Slf4j
@Getter
@Service
@Import({WebCrawlerConfiguration.class})
public class ThreadedWebCrawler {

    private final URLExtractor urlExtractor;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final ConcurrentLinkedQueue<String> toCrawl;
    private final ConcurrentHashMap<String, Integer> visited;  //Will be used as a set

    public ThreadedWebCrawler(URLExtractor urlExtractor,
                              ThreadPoolExecutor threadPoolExecutor,
                              CrawlerProperties crawlerProperties) {
        this.urlExtractor = urlExtractor;
        this.threadPoolExecutor = threadPoolExecutor;
        this.toCrawl = new ConcurrentLinkedQueue<>(List.of(crawlerProperties.getStartingWebsite()));
        this.visited = new ConcurrentHashMap<>();
    }

    @EventListener(ApplicationReadyEvent.class)
    public void concurrentCrawl() {
        try {
//            while (this.toCrawl.size() <= this.threadPoolExecutor.getMaximumPoolSize()) {
//                this.crawlDFS(); //TODO: Do I need this? Make sure doesn't loop infinitely
//            }
            while (!this.toCrawl.isEmpty() && !this.threadPoolExecutor.isTerminated()) {
                this.threadPoolExecutor.execute(this::crawlDFS);
            }
        } finally {
            this.threadPoolExecutor.shutdown();
        }
    }

    private void crawlDFS() {
        var url = this.toCrawl.poll();
        if (url != null && !this.visited.containsKey(url)) {
            Set<String> urls = this.urlExtractor.extractUrlsFromPage(url).stream()
                    .filter(newUrl -> !this.visited.containsKey(newUrl)).collect(Collectors.toSet());
            this.toCrawl.addAll(urls);
            this.visited.put(url, 1);
            log.info(generateLogString());
        }
    }

    private String generateLogString() {
        return String.format("[monitor] [%d/%d] Active: %d, Completed: %d, Task: %d, ToVisit: %d, Visited: %d",
                this.threadPoolExecutor.getPoolSize(),
                this.threadPoolExecutor.getCorePoolSize(),
                this.threadPoolExecutor.getActiveCount(),
                this.threadPoolExecutor.getCompletedTaskCount(),
                this.threadPoolExecutor.getTaskCount(),
                this.toCrawl.size(),
                this.visited.size());
    }
}
