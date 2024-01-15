package com.djimgou.core.cooldto.testing.integration;

import com.djimgou.core.cooldto.exception.DtoTargetClassMissMatchException;
import com.djimgou.core.cooldto.testing.app.CoreCoolDtoTestApplication;
import com.djimgou.core.cooldto.testing.app.model.Categorie;
import com.djimgou.core.cooldto.testing.app.model.dto.categorie.CategorieDto;
import com.djimgou.core.cooldto.testing.app.model.dto.categorie.CategorieDto2;
import com.djimgou.core.cooldto.testing.app.repository.CategorieRepo;
import com.djimgou.core.cooldto.testing.app.repository.CategorieRepo2;
import com.djimgou.core.test.initilizer.GenericDbManager;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Optional;

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

@SpringBootTest(classes = CoreCoolDtoTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-inTest.properties")
//@Sql({ "schema.sql", "data.sql" })
public class DtoRepositoryTest {

    private final CategorieRepo categorieRepo;
    private final CategorieRepo2 categorieRepo2;

    @Autowired
    public DtoRepositoryTest(GenericDbManager dbManager,
                             CategorieRepo categorieRepo, CategorieRepo2 categorieRepo2
    ) {
        DbManagerConfig.initDb(dbManager);
        this.categorieRepo = categorieRepo;
        this.categorieRepo2 = categorieRepo2;
    }

    @SneakyThrows
    @DisplayName("Create with Dto")
    @Test
    public void testFieldEntityLevel3() {
        CategorieDto dto = new CategorieDto();
        dto.setCode("BRL");
        dto.setNom("Berline");
        Categorie cat = categorieRepo.dto().save(dto);
        Optional<Categorie> opt = categorieRepo.findById(cat.getId());
        assertTrue(opt.isPresent());
        Categorie cate = opt.get();
        assertEquals(dto.getCode(), cate.getCode());
        assertEquals(dto.getNom(), cate.getNom());
    }

    @SneakyThrows
    @DisplayName("Create with Dto")
    @Test
    public void testEntityClassNull() {

        CategorieDto2 dto = new CategorieDto2();
        dto.setCode("BRL");
        dto.setNom("Berline");
        assertThrows(DtoTargetClassMissMatchException.class, () -> {
            categorieRepo2.dto().save(dto);
        });

    }

}

