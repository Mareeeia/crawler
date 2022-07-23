package com.web;

import com.config.CrawlerProperties;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.util.StopWatch;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PageLoaderTest {
    private static final String SAMPLE_URL = "https://sample.com";
    @Mock
    private PageLoader.JsoupClient jsoupClient;
    @Mock
    private CrawlerProperties crawlerProperties;

    @Test
    public void testReturnsUrlsFromHTML() throws IOException {
        var pageLoader = new PageLoader(crawlerProperties, jsoupClient);
        when(this.jsoupClient.getDoc(SAMPLE_URL)).thenReturn(getSampleDoc());
        when(this.crawlerProperties.getPageRetries()).thenReturn(3);

        assertThat(pageLoader.loadLines(SAMPLE_URL),
                containsInAnyOrder("https://sample.com/images/example.png", "https://sample.com/images/e.png"));
    }

    @Test
    public void testReturnsEmptyFor404() throws IOException {
        var pageLoader = new PageLoader(crawlerProperties, jsoupClient);
        when(this.jsoupClient.getDoc(SAMPLE_URL)).thenThrow(new HttpStatusException("404", 404, ""));
        when(this.crawlerProperties.getPageRetries()).thenReturn(3);

        assertThat(pageLoader.loadLines(SAMPLE_URL), empty());
    }

    @Test
    public void testReturnsEmptyForUnsupportedMedia() throws IOException {
        var pageLoader = new PageLoader(crawlerProperties, jsoupClient);
        when(this.jsoupClient.getDoc(SAMPLE_URL)).thenThrow(new UnsupportedMimeTypeException("mp3", "mp3", SAMPLE_URL));
        when(this.crawlerProperties.getPageRetries()).thenReturn(3);

        assertThat(pageLoader.loadLines(SAMPLE_URL), empty());
    }

    @Test
    public void testWaitsWhenThrottled() throws IOException {
        var pageLoader = new PageLoader(crawlerProperties, jsoupClient);
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        when(this.jsoupClient.getDoc(SAMPLE_URL)).thenThrow(new HttpStatusException("404", 429, ""));
        when(this.crawlerProperties.getPageRetries()).thenReturn(1);
        when(this.crawlerProperties.getSleepWhenThrottled()).thenReturn(3000L);
        var list = pageLoader.loadLines(SAMPLE_URL);
        stopWatch.stop();

        assertThat(list, empty());
        assertThat(stopWatch.getTotalTimeSeconds(), greaterThan(3D));
    }

    @Test
    public void testThrowsRuntimeExceptionWhenJsoupFails() throws IOException {
        var pageLoader = new PageLoader(crawlerProperties, jsoupClient);
        when(this.jsoupClient.getDoc(SAMPLE_URL)).thenThrow(new IOException());
        when(this.crawlerProperties.getPageRetries()).thenReturn(3);
        try {
            pageLoader.loadLines(SAMPLE_URL);
        } catch (Exception e) {
            assertThat(e, isA(RuntimeException.class));
        }
    }

    private Document getSampleDoc() {
        var doc = Jsoup.parse("<a href=\"/images/example.png\"> Link To Image</a>" +
                "<a href=\"/images/e.png\"> Link To Image</a>");
        doc.setBaseUri(SAMPLE_URL);
        return doc;
    }
}
