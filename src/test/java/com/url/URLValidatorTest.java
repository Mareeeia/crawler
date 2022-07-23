package com.url;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;

public class URLValidatorTest {

    @Test
    public void testValidatesCorrectDomain() {
        var urlValidator = new URLValidator("sample.com");
        var variousDomainsList = List.of("http://www.notsample.com",
                "http://www.sample.com",
                "http://www.thing.sample.com",
                "http://sample.com",
                "http://sample.com?=something/sample");

        var resultList = variousDomainsList.stream()
                .filter(urlValidator::isRequiredUrl).toList();

        assertThat(resultList,
                containsInAnyOrder("http://www.sample.com",
                        "http://sample.com",
                        "http://sample.com?=something/sample"));
    }
}
