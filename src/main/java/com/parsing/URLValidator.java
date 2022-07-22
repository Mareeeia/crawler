package com.parsing;

public class URLValidator {
    private final String domainRegex;
    private final String DOMAIN_PATTERN = "(https|http)://(www.)?%s.*";
    private final String FILE_PATTERN = "(.*)(.doc|.docx|.pdf|.png|.jpg|.jpeg|.mp3|.avi|.mp4)$";

    public URLValidator(String domain) {
        this.domainRegex = String.format(DOMAIN_PATTERN, domain);
    }

    public boolean isRequiredUrl(String url) {
        return isCorrectDomain(url)
                && !isFile(url)
                && !isAppstoreLink(url);
    }

    private boolean isCorrectDomain(String url) {
        return url.matches(this.domainRegex);
    }

    private boolean isFile(String url) {
        return url.matches(FILE_PATTERN);
    }

    private boolean isAppstoreLink(String url) {
        return url.endsWith(".adjust_play_store_link%20%7D%7D")
                || url.endsWith(".adjust_app_store_link%20%7D%7D");
    }
}
