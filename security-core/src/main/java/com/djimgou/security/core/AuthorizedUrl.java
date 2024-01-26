package com.djimgou.security.core;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpMethod;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.djimgou.core.util.AppUtils.has;

@Builder
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class AuthorizedUrl {
    String url;
    HttpMethod httpMethod;

    public boolean hasHttpMethod() {
        return has(httpMethod);
    }

    public static List<AuthorizedUrl> fomUrls(HttpMethod httpMethod, String... urls) {
        if (has(urls)) {
            return Stream.of(urls).map(s ->
                    AuthorizedUrl.builder().httpMethod(httpMethod).url(s).build()
            ).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public static List<AuthorizedUrl> fomUrls(String... urls) {
        if (has(urls)) {
            return Stream.of(urls).map(s ->
                    AuthorizedUrl.builder().url(s).build()
            ).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    public static AuthorizedUrl of(String url) {
        return AuthorizedUrl.builder().url(url).build();
    }

    public static AuthorizedUrl of(String url, HttpMethod httpMethod) {
        return AuthorizedUrl.builder().url(url).httpMethod(httpMethod).build();
    }
}
