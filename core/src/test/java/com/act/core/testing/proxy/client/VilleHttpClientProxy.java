package com.act.core.testing.proxy.client;

import com.act.core.testing.initilizer.DbManager;
import com.act.core.testing.app.model.Ville;
import com.act.core.testing.app.model.dto.ville.VilleDto;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

public class VilleHttpClientProxy extends IHttpClientProxy<Ville, VilleDto> {

    public VilleHttpClientProxy(TestRestTemplate restTemplate, String rootUrl, String apiUrl) {
        super(restTemplate, rootUrl, apiUrl);
    }

    @Override
    public ResponseEntity<Ville> getById(UUID id) {
        id = id != null ? id : DbManager.map.get(Ville.class.getName()).getValue().getId();
        return super.getById(id);
    }
}
