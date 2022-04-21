package com.djimgou.core.cooldto.testing.integration;

import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.annotations.DtoCollection;
import com.djimgou.core.cooldto.annotations.DtoField;
import com.djimgou.core.cooldto.exception.DtoMappingException;
import com.djimgou.core.cooldto.service.DtoSerializerService;
import com.djimgou.core.cooldto.testing.app.CoreCoolDtoTestApplication;
import com.djimgou.core.cooldto.testing.app.model.Categorie;
import com.djimgou.core.cooldto.testing.app.model.Marque;
import com.djimgou.core.test.initilizer.GenericDbManager;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.ArrayList;
import java.util.List;

import static com.djimgou.core.util.AppUtils.has;


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
public class DtoCollectionTest {

    private final DtoSerializerService serializerService;

    @Autowired
    public DtoCollectionTest(DtoSerializerService serializerService, GenericDbManager dbManager) {
        this.serializerService = serializerService;
        DbManagerConfig.initDb(dbManager);
    }

    @DisplayName("DtoFieldEntity: L3 un nom de propriété présent et une entité présente niveaux 3")
    @Test
    public void testFieldEntityLevel3() throws DtoMappingException {
        @Dto()
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Data
        class CategorieDtoT {
            @DtoField(value = "code")
            String codeT;
            @DtoField(value = "nom")
            String nomT;
        }


        @Dto()
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Data
        class MarqueDtoT {
            String code;
            String nom;

            @DtoCollection("categories")
            List<CategorieDtoT> childCats;

            public void add(CategorieDtoT categorieDtoT) {
                if (!has(childCats)) {
                    childCats = new ArrayList<>();
                }
                childCats.add(categorieDtoT);
            }
        }
        MarqueDtoT marqueDtoT = new MarqueDtoT();
        marqueDtoT.setCode("BASS");
        marqueDtoT.setNom("BIYESS ASSI");

        CategorieDtoT categorieDtoT = new CategorieDtoT();
        categorieDtoT.setCodeT("CE");
        categorieDtoT.setNomT("CENTRE");

        CategorieDtoT categorieDtoT1 = new CategorieDtoT();
        categorieDtoT1.setCodeT("YDE2");
        categorieDtoT1.setNomT("YAOUNDE2");

        marqueDtoT.add(categorieDtoT);
        marqueDtoT.add(categorieDtoT1);


        Marque marque = new Marque();
        serializerService.serialize(marqueDtoT, marque);
        Assertions.assertNotNull(marque.getCode());
        Assertions.assertNotNull(marque.getNom());
        Assertions.assertEquals(marqueDtoT.getCode(), marque.getCode());
        Assertions.assertEquals(marqueDtoT.getNom(), marque.getNom());
        Assertions.assertEquals(2, marque.getCategories().size());
        Assertions.assertArrayEquals(
                marqueDtoT.getChildCats().stream().map(CategorieDtoT::getCodeT).toArray(),
                marque.getCategories().stream().map(Categorie::getCode).toArray()
        );
        Assertions.assertArrayEquals(
                marqueDtoT.getChildCats().stream().map(CategorieDtoT::getNomT).toArray(),
                marque.getCategories().stream().map(Categorie::getNom).toArray()
        );

    }

}

