package com.url;

public class URLValidator {
    private final String domainRegex;
    private final String DOMAIN_PATTERN = "(https|http)://(www.)?%s.*";

    public URLValidator(String domain) {
        this.domainRegex = String.format(DOMAIN_PATTERN, domain);
    }

    public boolean isRequiredUrl(String url) {
        return isCorrectDomain(url);
    }

    private boolean isCorrectDomain(String url) {
        return url.matches(this.domainRegex);
    }
}
