package com.djimgou.security.core.model.dto.role;

import com.djimgou.core.util.AppUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpMethod;

import java.util.Objects;
import java.util.stream.Stream;

import static com.djimgou.core.util.AppUtils.has;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityDto implements Comparable<AuthorityDto> {

    // String name;

    String name;
    String[] urls;
    HttpMethod httpMethod;
    String urlsJoin;

    public AuthorityDto(String name, String urls, HttpMethod httpMethod) {
        this.name = name;
        this.urls = has(urls) ? Stream.of(urls.split(",")).filter(AppUtils::has).toArray(String[]::new) : null;
        this.httpMethod = httpMethod;
        if (has(this.urls)) {
            this.urlsJoin = String.join("", this.urls);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AuthorityDto that = (AuthorityDto) o;

        if (!Objects.equals(name, that.name)) return false;
        if (!Objects.equals(urls, that.urls)) return false;
        return Objects.equals(httpMethod, that.httpMethod);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 15;
        result = 31 * result + (null != urlsJoin ? urlsJoin.hashCode() : 23);
        result = 31 * result + (httpMethod != null ? httpMethod.hashCode() : 10);
        return result;
    }

    @Override
    public int compareTo(AuthorityDto o) {
        if (urlsJoin == null && o.getUrlsJoin() == null) return 0;
        if (o.getUrlsJoin() == null) return 1;
        if (urlsJoin == null) return -1;
        return this.urlsJoin.compareTo(o.getUrlsJoin());
    }

    public boolean hasUrl() {
        return has(urls);
    }

    public boolean hasHttpMethod() {
        return httpMethod != null;
    }
}
