package com.lgtoledo;

import org.junit.jupiter.api.Test;

import com.lgtoledo.utils.Utils;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class UtilsTest {

    @Test
    public void testIsValidUrl() {
        String longUrlRegex = "^https://.*$";
        assertFalse(Utils.isValidUrl(Optional.of("http://www.mercadolibre.com"), longUrlRegex));
        assertTrue(Utils.isValidUrl(Optional.of("https://www.mercadolibre.com"), longUrlRegex));
        assertFalse(Utils.isValidUrl(Optional.of("www.mercadolibre.com"), longUrlRegex));
        assertFalse(Utils.isValidUrl(Optional.of("http://www.mercadolibre"), longUrlRegex));
        assertFalse(Utils.isValidUrl(Optional.of("http://www.mercadolibre."), longUrlRegex));
        assertFalse(Utils.isValidUrl(Optional.of("http://www.mercadolibre.com/"), longUrlRegex));
        assertFalse(Utils.isValidUrl(Optional.empty(), longUrlRegex));
    }

    @Test
    public void testExtractLastPart() {
        String shortUrl = "http://localhost:8080/abc123";
        String lastPart = Utils.extractLastPart(shortUrl);
        assertEquals("abc123", lastPart);
    }

    @Test
    public void testGetCurrentUtcDateTime() {
        LocalDateTime dateTime = Utils.getCurrentUtcDateTime();
        assertNotNull(dateTime);
    }

    @Test
    public void testValidShortLink() {
        String baseUrl = "https://short.link";
        assertTrue(Utils.isShortLinkValid(baseUrl, "https://short.link/l/abc123"));
    }

    @Test
    public void testInvalidShortLinkWrongLength() {
        String baseUrl = "https://short.link";
        assertFalse(Utils.isShortLinkValid(baseUrl, "https://short.link/l/abc12345"));
    }

    @Test
    public void testInvalidShortLinkWrongChars() {
        String baseUrl = "https://short.link";
        assertFalse(Utils.isShortLinkValid(baseUrl, "https://short.link/l/abc_!@"));
    }

    @Test
    public void testInvalidShortLinkWithoutL() {
        String baseUrl = "https://short.link";
        assertFalse(Utils.isShortLinkValid(baseUrl, "https://short.link/abc123"));
    }

    // test if short link is null
    @Test
    public void testInvalidShortLinkNull() {
        String baseUrl = "https://short.link";
        assertFalse(Utils.isShortLinkValid(baseUrl, null));
    }

    // test if short link is empty
    @Test
    public void testInvalidShortLinkEmpty() {
        String baseUrl = "https://short.link";
        assertFalse(Utils.isShortLinkValid(baseUrl, ""));
    }

}