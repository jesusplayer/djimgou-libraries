package com.djimgou.security.core.model.dto.role;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpMethod;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityDto {

    // String name;

    String name;
    String url;

    HttpMethod httpMethod;

    public AuthorityDto(String name, String url) {
        this.url = url;
        this.name = name;
    }

    public String getUrls() {
        return null;
    }
}
