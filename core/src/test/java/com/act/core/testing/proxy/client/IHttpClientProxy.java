package com.act.core.testing.proxy.client;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.UUID;

/**
 * Ce code n'est pas SOLID. Mais pour faciliter les les traitements, on est obligé de traiter ainsi
 */
@Getter
public abstract class IHttpClientProxy<T, DTO> {

    private final TestRestTemplate restTemplate;

    @Getter
    @Setter
    private String rootUrl;

    @Getter
    @Setter
    private String apiUrl;


    protected IHttpClientProxy(TestRestTemplate restTemplate, String rootUrl, String apiUrl) {
        this.restTemplate = restTemplate;
        this.rootUrl = rootUrl;
        this.apiUrl = apiUrl;
    }

    public Class getClasse() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class cc = null;
        if (pt.getActualTypeArguments().length > 0) {
            cc = (Class) pt.getActualTypeArguments()[0];
        }
        return cc;
    }

    public ResponseEntity<T> getById(UUID id) {
        return getById("/detail/", id);
    }

    public ResponseEntity<T> getById(String prefixUrl, UUID id) {
        return restTemplate.getForEntity(apiUrl +
                prefixUrl + id, getClasse());
    }

    public T getOneById(UUID id) throws HttpClientErrorException {
        return (T) restTemplate.getForObject(getApiUrl() + "/detail/" + id, getClasse());
    }

    public ResponseEntity<List> getAll() {
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(getApiUrl() + "/",
                HttpMethod.GET, entity, List.class);
    }


    public ResponseEntity<T> create(DTO dto) {
        return restTemplate.postForEntity(getApiUrl() +
                "/creer", dto, getClasse());
    }

    public ResponseEntity<T> update(UUID id, Object dto) {
        final String url = getApiUrl() + "/modifier/" + id;
        return update(url, dto);
    }

    public ResponseEntity<T> update(String url, Object dto) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(dto, headers);
        try {
            return restTemplate.exchange(url, HttpMethod.PUT, entity, getClasse());
        } catch (RestClientException e) {
            e.printStackTrace();
        }
        return null;
    }

    public ResponseEntity delete(UUID id) {
        String url = getApiUrl() + "/supprimer/" + id;
        return delete(url);
    }

    public ResponseEntity delete(String url) {
        return delete(url, getClasse());
    }

    public ResponseEntity delete(String url, Class classe) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Object> entity = new HttpEntity<>(null, headers);
        return restTemplate.exchange(url, HttpMethod.DELETE, entity, classe);
    }

    /**
     * LinkedMultiValueMap<String, Object> parameters = new LinkedMultiValueMap<String, Object>();
     * parameters.add("file", new org.springframework.core.io.ClassPathResource("image.jpg"));
     *
     * @param parameters
     * @param url
     * @return
     */
    public ResponseEntity<T> upload(String url, LinkedMultiValueMap<String, Object> parameters) {
        HttpHeaders headers = new HttpHeaders();
        //headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        HttpEntity<LinkedMultiValueMap<String, Object>> entity = new HttpEntity<>(parameters, headers);
        ResponseEntity res = null;
        try {
            res = restTemplate.exchange(url, HttpMethod.POST, entity, getClasse(), "");
        } catch (RestClientException ex) {
            ex.printStackTrace();
        }
        return res;
    }

}
