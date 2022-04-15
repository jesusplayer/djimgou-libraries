package com.act.core.testing.proxy.client;

import com.act.core.testing.app.FakeBuilder;
import com.act.core.testing.initilizer.DbManager;
import com.act.core.testing.app.model.Categorie;
import com.act.core.testing.app.model.Marque;
import com.act.core.testing.app.model.Produit;
import com.act.core.testing.app.model.Reduction;
import com.act.core.testing.app.model.dto.produit.Localisation;
import com.act.core.testing.app.model.dto.produit.ProduitDto;
import com.act.core.testing.app.model.dto.produit.ProduitDtoWithLocalisation;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.StreamUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Arrays;
import java.util.UUID;

public class ProduitHttpClientProxy extends IHttpClientProxy<Produit, ProduitDto> {

    public ProduitHttpClientProxy(TestRestTemplate restTemplate, String rootUrl, String apiUrl) {
        super(restTemplate, rootUrl, apiUrl);
    }

    public ResponseEntity<Produit> create(ProduitDtoWithLocalisation dto) {
        return getRestTemplate().postForEntity(getApiUrl() +
                "/creerAvecLocalisation", dto, getClasse());
    }

    @Override
    public ResponseEntity<Produit> getById(UUID id) {
        id = id != null ? id : DbManager.map.get(Produit.class.getName()).getValue().getId();
        return super.getById(id);
    }

    public ResponseEntity<Produit> upload3Image(UUID produitId, String customData, Resource image1, Resource image2, Resource image3) {
        LinkedMultiValueMap parameters = new LinkedMultiValueMap();
        //parameters.add("fichiers", new Resource[]{image1, image2, image3});
        parameters.add("fichiers", image1);
        parameters.add("fichiers", image2);
        parameters.add("fichiers", image3);

        parameters.add("produitId", produitId.toString());
        parameters.add("customData", customData);

        return super.upload(getApiUrl() + "/upload3Images", parameters);
    }

    public ResponseEntity<Produit> uploadDocuments(UUID produitId, String customData, LinkedMultiValueMap<String, Object> parameters) {
        //parameters.add("fichiers", new Resource[]{image1, image2, image3});
        parameters.add("produitId", produitId.toString());
        parameters.add("customData", customData);

        return super.upload(getApiUrl() + "/uploadIDocuments", parameters);
    }

    public ResponseEntity<Produit> uploadImages(UUID produitId, String customData, LinkedMultiValueMap<String, Object> parameters) {
        parameters.add("produitId", produitId.toString());
        parameters.add("customData", customData);

        return super.upload(getApiUrl() + "/uploadImages", parameters);
    }

    public ResponseEntity<Produit> deleteAllDocuments(String urlPrefix, UUID productId) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Produit> entity = new HttpEntity<>(null, headers);
        String url = getApiUrl() + urlPrefix + productId;
        return getRestTemplate().exchange(url, HttpMethod.DELETE, entity, getClasse());
    }


    public ProduitDtoWithLocalisation fakeWithLocalisationDto(
            UUID regionId,
            UUID villeId,
            UUID quartierId
    ) {
        ProduitDtoWithLocalisation produitDto = new ProduitDtoWithLocalisation();
        FakeBuilder.fake(produitDto);
        Reduction r = new Reduction();
        r.setInfOuEgal(Integer.MAX_VALUE);
        r.setSupOuEgal(Integer.MAX_VALUE);
        r.setValeur(456789.0);
        produitDto.setReductions(Arrays.asList(r));

        Localisation localisationDto = new Localisation();
        FakeBuilder.fake(localisationDto);
        localisationDto.setVilleId(villeId);
        localisationDto.setQuartierId(quartierId);
        localisationDto.setRegionId(regionId);
        produitDto.setLocalisation(localisationDto);

        Categorie categorie = (Categorie) DbManager.map.get(Categorie.class.getName()).getValue();
        UUID categorieId = categorie.getId();
        produitDto.setCategorieId(categorieId);

        Marque marque = (Marque) DbManager.map.get(Marque.class.getName()).getValue();
        UUID marqueId = marque.getId();
        produitDto.setMarqueId(marqueId);

        return produitDto;
    }

    public File downloadFile(String url) {
        return getRestTemplate().execute(url, HttpMethod.GET, null, clientHttpResponse -> {
            File ret = File.createTempFile("download", "tmp");
            StreamUtils.copy(clientHttpResponse.getBody(), new FileOutputStream(ret));
            return ret;
        });
    }

    public String getFileFullUrl(String folder, String fileName) {
        String url = getRootUrl() + "/fichier/afficherDansDossier/" + fileName + "?dossier=" + folder;
        return url;
    }

    public ResponseEntity<Produit> deleteByCustomdata(UUID productId, String customData) {
        String url = getApiUrl() + "/supprimerToutesImagesParCustomData/" + productId + "?customData=" + customData;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return getRestTemplate().exchange(url, HttpMethod.DELETE, entity, getClasse());

    }

    public ResponseEntity<Produit> deleteImageById(UUID productId, UUID imageId) {
        String url = getApiUrl() + "/supprimerImage/" + productId + "?imageId=" + imageId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        return getRestTemplate().exchange(url, HttpMethod.DELETE, entity, getClasse());
    }
}
