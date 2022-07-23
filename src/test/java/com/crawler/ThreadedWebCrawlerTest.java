package com.crawler;

import com.config.CrawlerProperties;
import com.url.URLExtractor;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.integration.util.CallerBlocksPolicy;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ThreadedWebCrawlerTest {
    private final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(2,
            4,
            100L,
            TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(100),
            new CallerBlocksPolicy(100L));
    @Mock
    private URLExtractor urlExtractor;
    @Mock
    private CrawlerProperties crawlerProperties;

    @Test
    public void testCrawlsAllLinks() throws InterruptedException {
        when(this.crawlerProperties.getStartingWebsite()).thenReturn("link1");
        ThreadedWebCrawler threadedWebCrawler
                = new ThreadedWebCrawler(this.urlExtractor,
                this.threadPoolExecutor,
                this.crawlerProperties);
        var list = generateNumberedStrings(20);
        when(this.urlExtractor.extractUrlsFromPage(any())).thenReturn(list);
        threadedWebCrawler.concurrentCrawl();

        assertThat(threadedWebCrawler.getVisited().keySet().size(), equalTo(20));
    }

    private List<String> generateNumberedStrings(Integer number) {
        var list = new ArrayList<String>();
        for (int i = 0; i < number; i++) {
            list.add("link" + i);
        }
        return list;
    }
}
