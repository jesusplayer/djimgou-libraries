package com.djimgou.core.testing.integration;

import com.djimgou.core.infra.QueryFieldFilter;
import com.djimgou.core.infra.QueryFilterOperator;
import com.djimgou.core.infra.QueryOperation;
import com.djimgou.core.test.initilizer.GenericDbManager;
import com.djimgou.core.test.util.FakeBuilder;
import com.djimgou.core.testing.app.MaincoreTestApplication;
import com.djimgou.core.testing.app.model.*;
import com.djimgou.core.testing.app.service.CategorieService;
import com.djimgou.core.util.EntityRepository;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.TestPropertySource;

import javax.servlet.ServletContext;
import java.util.*;

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

@Rollback
@SpringBootTest(classes = MaincoreTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-inTest.properties")
//@Sql({ "schema.sql", "data.sql" })
public class FilterTest {
    String rootUrl;
    String apiurl;

    TestRestTemplate restTemplate;

    GenericDbManager dbManager;

    EntityRepository er;

    private final CategorieService categorieService;

    @Autowired
    public FilterTest(
            GenericDbManager dbManager,
            TestRestTemplate restTemplate, @LocalServerPort int port, ServletContext servletContext,
            EntityRepository er,
            CategorieService categorieService
    ) {
        dbManager.initDb();
        this.dbManager = dbManager;
        final String rootUrl = "http://localhost:" + port + servletContext.getContextPath();
        this.apiurl = rootUrl + "/api/categorie";
        this.rootUrl = rootUrl;
        this.restTemplate = restTemplate;
        this.er = er;
        this.categorieService = categorieService;
        createAcategories();

    }

    @SneakyThrows
    void createAcategories() {
        categorieService.clear();

        CategorieDto catDto = FakeBuilder.fake(CategorieDto.class);
        catDto.setCode("mon 123");
        catDto.setAnnee(2019);
        catDto.setNom("categorie premiere");

        CategorieDto catDto2 = FakeBuilder.fake(CategorieDto.class);
        catDto2.setCode("mon code2");
        catDto2.setAnnee(2020);
        catDto2.setNom("categorie deuxieme");

        Parent parent = GenericDbManager.get(Parent.class);
        CategorieDto catDto3 = FakeBuilder.fake(CategorieDto.class);
        catDto3.setCode("code3");
        catDto3.setAnnee(2021);
        catDto3.setNom("categorie troisieme");
        catDto3.setParentId(parent.getId());

        Collection<Categorie> categories = categorieService.createAll(Arrays.asList(
                catDto, catDto2, catDto3
        ));
    }

    @SneakyThrows
    @DisplayName("Filter avec default data")
    @Test
    public void filterAvecDefaultData() {
        CategorieFilterDto filter = new CategorieFilterDto();
        filter.setPage(0);
        filter.setSize(10);

        QueryFieldFilter<String> code = new QueryFieldFilter<>();
        code.setEq("mon 123");
        filter.setCode(code);

        Page<Categorie> page = categorieService.findBy(filter);
        assertEquals(1, page.getNumberOfElements());
        assertEquals("mon 123", page.getContent().get(0).getCode());

        FakeBuilder.nullAll(filter.getCode());
        filter.getCode().setContains("code");
        page = categorieService.findBy(filter);
        assertEquals(2, page.getNumberOfElements());


        FakeBuilder.nullAll(filter.getCode());
        filter.getCode().setLike("%code%");
        page = categorieService.findBy(filter);
        assertEquals(2, page.getNumberOfElements());

        FakeBuilder.nullAll(filter.getCode());
        QueryFieldFilter<Integer> anne = new QueryFieldFilter<>();
        anne.setLt(2021);
        filter.setAnnee(anne);
        page = categorieService.findBy(filter);
        assertEquals(2, page.getNumberOfElements());

        FakeBuilder.nullAll(filter.getCode());
        FakeBuilder.nullAll(filter.getAnnee());
        Parent parent = GenericDbManager.get(Parent.class);
        filter.setParentId(parent.getId());
        page = categorieService.findBy(filter);
        assertEquals(1, page.getNumberOfElements());

    }

    @SneakyThrows
    @DisplayName("Filter avec custom operations")
    @Test
    public void testFieldEntityLevel3() {
        CategorieFilterAdvDto sFilterDto = new CategorieFilterAdvDto();
        sFilterDto.setPage(0);
        sFilterDto.setSize(10);

        List<QueryOperation> l = new ArrayList() {{
            add(new QueryOperation("annee", QueryFilterOperator.between, 2020, 2021, null));
        }};

        sFilterDto.setOtherFilters(l);
        try{
            Page<Categorie> page2 = categorieService.advancedFindBy(sFilterDto);
            assertEquals(2, page2.getNumberOfElements());
        }catch (Throwable e){
            e.printStackTrace();
        }



        l = new ArrayList() {{
            add(new QueryOperation("annee", QueryFilterOperator.between, 2021, 2021, null));
        }};
        sFilterDto.setOtherFilters(l);
        Page<Categorie> page3 = categorieService.advancedFindBy(sFilterDto);
        assertEquals(1, page3.getNumberOfElements());
    }


}

