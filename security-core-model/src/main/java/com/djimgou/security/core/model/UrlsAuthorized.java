package com.djimgou.security.core.model;

/**
 * @author DJIMGOU NKENNE DANY MARC 08/2020
 */
public enum UrlsAuthorized {
    LOGIN("/signin"),
    LOGIN_SUCCESS("/connected"),
    LOGIN_FAILURE("/?error=true"),
    //    REPORTS("/suivi-entite-list-risque-credit-export.jsf"),
    RESSOURCES("/files"),
    ALL_RESSOURCES("/files/**"),
    API_DOC("/v3/api-docs/**"),
    API_DOC_2("/v3/api-docs"),
    API_DOC_8("/v3/api-docs.yaml"),
    API_DOC_UI("/swagger-ui/**"),
    API_DOC_UI_2("/swagger-ui.html"),
    API_DOC_UI_10("/v3/api-docs/swagger-config"),
    API_DOC_3("/swagger-resources"),
    API_DOC_4("/swagger-resources/**"),
    API_DOC_5("/configuration/ui"),
    API_DOC_6("/configuration/security"),
    PUBLIC("/public/**"),
    STATIC("/static/**"),

    INDEX("/"),
    UNAUTHORIZED("/403"),
    CHANGE_PASSWORD("/change-password");


    private final String text;

    UrlsAuthorized(final String text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return text;
    }

}
