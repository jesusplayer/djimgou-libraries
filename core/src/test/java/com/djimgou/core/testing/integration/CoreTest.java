package com.djimgou.core.testing.integration;

import com.djimgou.core.test.initilizer.GenericDbManager;
import com.djimgou.core.test.util.FakeBuilder;
import com.djimgou.core.testing.app.MaincoreTestApplication;
import com.djimgou.core.testing.app.model.Categorie;
import com.djimgou.core.util.EntityRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.TestPropertySource;

import javax.servlet.ServletContext;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;


/**
 * https://reflectoring.io/spring-boot-test/
 * https://thepracticaldeveloper.com/guide-spring-boot-controller-tests/
 *
 * @author djimgou
 */
/* // Configuration pour Mock MVC
//@AutoConfigureJsonTesters
//@ExtendWith(MockitoExtension.class)
//@AutoConfigureMockMvc()
*/

@SpringBootTest(classes = MaincoreTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-inTest.properties")
//@Sql({ "schema.sql", "data.sql" })
public class CoreTest {
    String rootUrl;
    String apiurl;
    TestRestTemplate restTemplate;
    GenericDbManager dbManager;


    EntityRepository er;

    @Autowired
    public CoreTest(
            GenericDbManager dbManager,
            TestRestTemplate restTemplate, @LocalServerPort int port, ServletContext servletContext,
            EntityRepository er
    ) {
       // dbManager.initDb();
        this.dbManager = dbManager;
        final String rootUrl = "http://localhost:" + port + servletContext.getContextPath();
        this.apiurl = rootUrl + "/api/categorie";
        this.rootUrl = rootUrl;
        this.restTemplate = restTemplate;
        this.er = er;

    }

   /* @SneakyThrows
    @DisplayName("Get By Id with existing Id")
    @Test
    public void testFieldEntityLevel3() {
        Categorie cat = GenericDbManager.get(Categorie.class);
        ResponseEntity<Categorie> catResp = restTemplate.getForEntity(apiurl +
                "/" + cat.getId(), Categorie.class);

        assertNotNull(catResp);
        assertNotEquals(HttpStatus.NOT_FOUND, catResp.getStatusCode());
        Categorie newcat = catResp.getBody();
        assertEquals(cat.getCode(), newcat.getCode());
        assertEquals(cat.getNom(), newcat.getNom());
    }

    @SneakyThrows
    @DisplayName("Get By idCategorie")
    @Test
    public void testGetCatrename() {
        Categorie cat = GenericDbManager.get(Categorie.class);
        ResponseEntity<Categorie> catResp = restTemplate.getForEntity(rootUrl +
                "/api/categorieRename/" + cat.getId(), Categorie.class);

        assertNotNull(catResp);
        assertEquals(HttpStatus.OK, catResp.getStatusCode());
        Categorie newcat = catResp.getBody();
        assertEquals(cat.getCode(), newcat.getCode());
        assertEquals(cat.getNom(), newcat.getNom());
    }

    @SneakyThrows
    @DisplayName("Get By Id with null keyId name ")
    @Test
    public void testFieldE() {
        Categorie cat = GenericDbManager.get(Categorie.class);
        ResponseEntity<Categorie> catResp = restTemplate.getForEntity(rootUrl +
                "/api/noIdCategorie/" + cat.getId(), Categorie.class);

        assertNotNull(catResp);
        assertNotEquals(HttpStatus.NOT_FOUND, catResp.getStatusCode());
        Categorie newcat = catResp.getBody();
        assertEquals(cat.getCode(), newcat.getCode());
        assertEquals(cat.getNom(), newcat.getNom());
    }

    @SneakyThrows
    @DisplayName("Get Bad Id Path By Id with null keyId name ")
    @Test
    public void badIdpath() {
        Categorie cat = GenericDbManager.get(Categorie.class);
        ResponseEntity<String> catResp = restTemplate.getForEntity(rootUrl +
                "/api/badIdCategorie/" + cat.getId(), String.class);

        assertNotNull(catResp);
        assertEquals(HttpStatus.NOT_FOUND, catResp.getStatusCode());

    }

    @SneakyThrows
    @DisplayName("Get Null Id")
    @Test
    public void testNullId() {
        ResponseEntity<String> catResp = restTemplate.getForEntity(apiurl +
                "/", String.class);

        assertNotNull(catResp);
        assertEquals(HttpStatus.NOT_FOUND, catResp.getStatusCode());
    }

    @SneakyThrows
    @DisplayName("Get NotFound  Id")
    @Test
    public void testNotFoundId() {
        ResponseEntity<String> catResp = restTemplate.getForEntity(apiurl +
                "/" + UUID.randomUUID(), String.class);

        assertNotNull(catResp);
        assertEquals(HttpStatus.NOT_FOUND, catResp.getStatusCode());
    }

    @SneakyThrows
    @DisplayName("@DeleteById: Suppression avec un Id existant")
    @Test
    public void testDeleteByid() {
        Categorie cat = FakeBuilder.fake(Categorie.class);
        er.save(cat);
        //Categorie cat = dbManager.deepCreate(Categorie.class);;
        restTemplate.delete(apiurl + "/" + cat.getId());

        ResponseEntity<String> catResp = restTemplate.getForEntity(apiurl +
                "/" + cat.getId(), String.class);

        assertEquals(HttpStatus.NOT_FOUND, catResp.getStatusCode());

    }

    @SneakyThrows
    @DisplayName("Suppression avec un Id inexistant")
    @Test
    public void testDeleteByidNoId() {
        Categorie cat = FakeBuilder.fake(Categorie.class);
        er.save(cat);
        //Categorie cat = dbManager.deepCreate(Categorie.class);;
        restTemplate.delete(rootUrl + "/api/noIdCategorie/" + cat.getId());

        ResponseEntity<String> catResp = restTemplate.getForEntity(apiurl +
                "/" + cat.getId(), String.class);

        assertEquals(HttpStatus.NOT_FOUND, catResp.getStatusCode());

    }*/


}

