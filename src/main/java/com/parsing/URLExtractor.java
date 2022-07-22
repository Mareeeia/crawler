package com.parsing;

import com.web.PageLoader;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * This class exists to provide a layer of logic between our HTML parsing libraries
 * and the core classes, so that we don't depend on JSoup or the classes that use it.
 */
@Slf4j
public class URLExtractor {

    private final PageLoader pageLoader;
    private final URLValidator urlValidator;

    public URLExtractor(PageLoader pageLoader,
                        URLValidator urlValidator) {
        this.pageLoader = pageLoader;
        this.urlValidator = urlValidator;
    }

    public List<String> extractUrlsFromPage(String urlString) {
        var urlList = this.pageLoader.loadLines(urlString);
//        log.info(String.format("Page %s contains the links: %s", urlString, urlList));
        return urlList.stream()
                .filter(url -> this.urlValidator.isRequiredUrl(url))
                .toList();
    }
}
