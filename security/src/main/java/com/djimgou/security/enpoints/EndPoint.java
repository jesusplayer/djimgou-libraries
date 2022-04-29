package com.djimgou.security.enpoints;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpMethod;

import java.util.Map;

@Data
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EndPoint {
    String fullname;

    String name;

    String description;

    String url;

    HttpMethod httpMethod;

    Map<String, Param> parameters;

    public String toSecurityUrl() {
        // "\\{\\w+\\}"
        String r = url.replaceAll("\\{\\w+\\}", "*");
        return r;
    }

    public boolean isGet() {
        return HttpMethod.GET.equals(httpMethod);
    }
}
