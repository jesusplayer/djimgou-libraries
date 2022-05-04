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
public class SecuredEndPoint {
    public static final String URL_ADVANCED_FILTER = "/advancedFilter";
    public static final String URL_FILTER = "/filter";
    public static final String URL_LIST_ALL = "/list";
    public static final String URL_SEARCH_UNPAGED = "/search";
    public static final String URL_SEARCH_PAGED = "/find";
    public static final String URL_CREATE = "/creer";

    String fullname;

    String name;

    String description;

    String url;

    HttpMethod httpMethod;

    Map<String, Param> parameters;
    /**
     * Dans le cadre d'une requete post qui fait un get, ceci peut être utile
     * de présiser readoly = true
     */
    @Builder.Default
    Boolean isReadOnlyMethod = false;

    public String toSecurityUrl() {
        // "\\{\\w+\\}"
        String r = url.replaceAll("\\{\\w+\\}", "*");
        return r;
    }

    public boolean isGet() {
        return HttpMethod.GET.equals(httpMethod);
    }

    public boolean isPost() {
        return HttpMethod.POST.equals(httpMethod);
    }
}
