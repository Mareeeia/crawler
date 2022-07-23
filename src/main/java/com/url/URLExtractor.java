package com.url;

import com.web.PageLoader;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * This class exists to provide a layer of logic between our HTML parsing libraries
 * and the core classes, so that we don't depend on JSoup or the classes that use it.
 */
@Slf4j
@Component
public class URLExtractor {

    private final PageLoader pageLoader;
    private final URLValidator urlValidator;
    private final Map<String, List<String>> resultMap;

    public URLExtractor(PageLoader pageLoader,
                        URLValidator urlValidator) {
        this.pageLoader = pageLoader;
        this.urlValidator = urlValidator;
        this.resultMap = new ConcurrentHashMap<>();
    }

    public List<String> extractUrlsFromPage(String urlString) {
        var urlList = this.pageLoader.loadLines(urlString);
        this.resultMap.put(urlString, urlList);
        return urlList.stream()
                .filter(url -> this.urlValidator.isRequiredUrl(url))
                .toList();
    }

    public void printResults() {
        this.resultMap
                .forEach((key, value) -> log.info(String.format("Page %s contains the links: %s", key, value)));
    }
}
