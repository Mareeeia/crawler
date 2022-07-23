package com.url;

import com.web.PageLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class URLExtractorTest {

    private static final String SAMPLE_URL = "https://sample.com";
    @Mock
    PageLoader pageLoader;
    @Mock
    URLValidator urlValidator;

    @Test
    public void testExtractsValidUrlsFromPage() {
        final var urls = getSampleHtmlStringTwoUrls();
        var urlExtractor = new URLExtractor(pageLoader, urlValidator);
        when(this.pageLoader.loadLines(SAMPLE_URL)).thenReturn(urls);
        when(this.urlValidator.isRequiredUrl(SAMPLE_URL)).thenReturn(true);
        when(this.urlValidator.isRequiredUrl("https://connect.facebook.net/en_US/fbevents.js")).thenReturn(false);
        when(this.urlValidator.isRequiredUrl("https://www.googletagmanager.com/gtm.js?id=GTM-MRKTQTH")).thenReturn(false);

        assertThat(urlExtractor.extractUrlsFromPage(SAMPLE_URL), containsInAnyOrder(SAMPLE_URL));
    }

    private List<String> getSampleHtmlStringTwoUrls() {
        return List.of("https://connect.facebook.net/en_US/fbevents.js",
                "https://www.googletagmanager.com/gtm.js?id=GTM-MRKTQTH",
                SAMPLE_URL);
    }

}
