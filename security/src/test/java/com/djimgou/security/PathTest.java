package com.djimgou.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PathTest {
    @DisplayName("test matching des urls")
    @Test
    void testMatch(){
        AppCorsFilter filter = new AppCorsFilter(
                "*//*",
                null
        );
        assertTrue(filter.matchOrigin("http://localhost:4200"));

        assertTrue(filter.matchOrigin("http://172.20.165.23.25:4200"));

        AppCorsFilter filter2 = new AppCorsFilter(
                "*//172.20.{p1:[0-9]+}.{p2:[0-9]+}:{port:[0-9]+}",
                null
        );
        assertTrue(filter2.matchOrigin("http://172.20.165.23:4200"));
        assertFalse(filter2.matchOrigin("http://172.20.165.23.25:4200"));
    }
}
