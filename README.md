# Crawler

## Quick introduction

To run tests:

```
gradle build
```

To run the crawler:

```
gradle bootRun
```

To change any of the settings for wait times, retries and thread pool properties, use the class CrawlerProperties.

## General Architecture

`ThreadedWebCrawler` - core business logic. Takes the links from a page and puts them in the queue of
a `ThreadPoolExecutor`.

`URLExtractor` - concerned with taking urls and passing them to the ThreadedWebCrawler while also storing them to print
in the end.

`URLValidator` - Validates the URL passes our requirements, e.g. is in the correct domain. Can be extended to
cover for anything the validations in the parsing library miss.

`PageLoader` - Uses JSoup to parse HTML and acquire links. Contains detailed exception handling.

`WebCrawlerConfiguration` - creates key application beans.

`CrawlerProperties` - data class that can be configured to read from application properties files.

## Notes & Points to clarify

1. This crawler is using JSoup for HTML parsing and thus will not execute some JavaScript.
2. How are we expecting to scale up? This version works fine for the current size of monzo.com, but in larger websites,
   or if we crawl multiple domains, heap size can become a problem.
3. In my experience with crawlers throttling is a big problem as most websites defend against scraping, therefore
   developing this I made the assumption we will be rate-limited.