package com.web;

import com.config.CrawlerProperties;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.UnsupportedMimeTypeException;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
public class PageLoader {

    private final CrawlerProperties crawlerProperties;

    private final JsoupClient jsoupClient;

    public PageLoader(CrawlerProperties crawlerProperties, JsoupClient jsoupClient) {
        this.crawlerProperties = crawlerProperties;
        this.jsoupClient = jsoupClient;
    }

    public List<String> loadLines(String urlString) {
        boolean success = false;
        int retries = 0;
        while (!success && retries < this.crawlerProperties.getPageRetries()) {
            try {
                return getUrlsFromDoc(urlString);
            } catch (HttpStatusException hse) {
                if (hse.getStatusCode() == 404) {
                    return List.of();
                }
                if (hse.getStatusCode() == 429) {
                    retries = handleThrottling(retries);
                }
            } catch (UnsupportedMimeTypeException umte) { // .mp3, .pdf and so on
                return List.of();
            } catch (IOException ioe) {
                throw new RuntimeException(ioe.getMessage(), ioe.getCause());
            }
        }
        return List.of();
    }

    private int handleThrottling(int retries) {
        try {
            retries++;
            log.warn(String.format("Seems like we've been throttled, sleep. Retries %d \n", retries));
            Thread.sleep(this.crawlerProperties.getSleepWhenThrottled());
            return retries;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e.getCause());
        }
    }

    private List<String> getUrlsFromDoc(String urlString) throws IOException {
        var doc = this.jsoupClient.getDoc(urlString);
        Elements links = doc.select("a[href]");
        return links.stream()
                .map(link -> link.absUrl("href"))
                .collect(Collectors.toList());
    }

    /**
     * Added for testability of the code that uses Jsoup.
     */
    public static class JsoupClient {
        public Document getDoc(String urlString) throws IOException {
            return Jsoup.connect(urlString).get();
        }
    }
}
