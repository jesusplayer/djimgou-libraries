package com.djimgou.core.cooldto.testing.integration;

import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.annotations.DtoCollectionId;
import com.djimgou.core.cooldto.annotations.DtoFieldIdStrategyType;
import com.djimgou.core.cooldto.annotations.DtoId;
import com.djimgou.core.cooldto.exception.DtoMappingException;
import com.djimgou.core.cooldto.service.DtoSerializerService;
import com.djimgou.core.cooldto.testing.app.CoreCoolDtoTestApplication;
import com.djimgou.core.cooldto.testing.app.model.Categorie;
import com.djimgou.core.cooldto.testing.app.model.Marque;
import com.djimgou.core.cooldto.testing.app.repository.CategorieRepo2;
import com.djimgou.core.cooldto.testing.app.repository.MarqueRepo;
import com.djimgou.core.test.initilizer.GenericDbManager;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.djimgou.core.util.AppUtils.has;
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
public class DtoCollectionIdTest {

    private final DtoSerializerService serializerService;
    private final CategorieRepo2 categorieRepo2;
    private final MarqueRepo marqueRepo;

    @Autowired
    public DtoCollectionIdTest(DtoSerializerService serializerService, GenericDbManager dbManager, CategorieRepo2 categorieRepo2, MarqueRepo marqueRepo) {
        this.serializerService = serializerService;
        this.categorieRepo2 = categorieRepo2;
        this.marqueRepo = marqueRepo;
        DbManagerConfig.initDb(dbManager);
    }

    @DisplayName("Mode création: Ajout d'une collection de Ids")
    @Test
    public void testFieldEntityLevel3() throws DtoMappingException {
        Categorie cat = GenericDbManager.get(Categorie.class);

        Categorie cat1 = new Categorie();
        cat1.setCode("CE");
        cat1.setNom("CENTRE");

        Categorie cat2 = new Categorie();
        cat2.setCode("NO");
        cat2.setNom("NORD OUEST");

        List<Categorie> listCat = categorieRepo2.saveAll(Arrays.asList(cat1, cat2));

        @Dto()
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Data
        class MarqueDtoT {
            String code;
            String nom;

            @DtoCollectionId("categories")
            List<UUID> childCats;
        }

        MarqueDtoT marqueDtoT = new MarqueDtoT();
        marqueDtoT.setCode("BASS");
        marqueDtoT.setNom("BIYESS ASSI");
        marqueDtoT.setChildCats(listCat.stream().map(Categorie::getId).collect(Collectors.toList()));


        Marque marque = new Marque();
        serializerService.serialize(marqueDtoT, marque);

        assertNotNull(marque.getCode());
        assertNotNull(marque.getNom());
        assertEquals(marqueDtoT.getCode(), marque.getCode());
        assertEquals(marqueDtoT.getNom(), marque.getNom());
        assertNotNull(marque.getCategories());
        assertEquals(2, marque.getCategories().size());
        assertArrayEquals(
                listCat.stream().map(Categorie::getId).toArray(),
                marque.getCategories().stream().map(Categorie::getId).toArray()
        );
        assertArrayEquals(
                listCat.stream().map(Categorie::getCode).toArray(),
                marque.getCategories().stream().map(Categorie::getCode).toArray()
        );
        assertArrayEquals(
                listCat.stream().map(Categorie::getNom).toArray(),
                marque.getCategories().stream().map(Categorie::getNom).toArray()
        );

    }

    @DisplayName("Mode modification: Ajout d'une collection de Ids")
    @Test
    public void testModeModification() throws DtoMappingException {
        Categorie cat = GenericDbManager.get(Categorie.class);

        Marque marqueOld = new Marque();
        marqueOld.setCode("TY");
        marqueOld.setNom("Toyota");
        marqueRepo.save(marqueOld);

        Categorie cat1 = new Categorie();
        cat1.setCode("CEM");
        cat1.setNom("CENTREM");

        Categorie cat2 = new Categorie();
        cat2.setCode("NOM");
        cat2.setNom("NORD OUESTM");

        List<Categorie> listCat = categorieRepo2.saveAll(Arrays.asList(cat1, cat2));

        @Dto()
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Data
        class MarqueDtoT {
            @DtoId(strategy = DtoFieldIdStrategyType.UPDATE, nullable = true)
            UUID id;
            String code;
            String nom;

            @DtoCollectionId("categories")
            List<UUID> childCats;
        }

        MarqueDtoT marqueDtoT = new MarqueDtoT();
        marqueDtoT.setId(marqueOld.getId());
        marqueDtoT.setCode("BASS");
        marqueDtoT.setNom("BIYESS ASSI");
        marqueDtoT.setChildCats(listCat.stream().map(Categorie::getId).collect(Collectors.toList()));


        Marque marque = new Marque();
        serializerService.serialize(marqueDtoT, marque);

        assertNotNull(marque.getCode());
        assertNotNull(marque.getNom());
        assertEquals(marqueDtoT.getCode(), marque.getCode());
        assertEquals(marqueDtoT.getNom(), marque.getNom());
        assertNotNull(marque.getCategories());
        assertEquals(2, marque.getCategories().size());
        assertArrayEquals(
                listCat.stream().map(Categorie::getId).toArray(),
                marque.getCategories().stream().map(Categorie::getId).toArray()
        );
        assertArrayEquals(
                listCat.stream().map(Categorie::getCode).toArray(),
                marque.getCategories().stream().map(Categorie::getCode).toArray()
        );
        assertArrayEquals(
                listCat.stream().map(Categorie::getNom).toArray(),
                marque.getCategories().stream().map(Categorie::getNom).toArray()
        );

    }

    @DisplayName("Mode Modification:suppresion des Ids à retirer dans la destination")
    @Test
    public void testModeSuppression() throws DtoMappingException {

        Categorie cat1 = new Categorie();
        cat1.setCode("CEs");
        cat1.setNom("CENTREs");

        Categorie cat2 = new Categorie();
        cat2.setCode("NOs");
        cat2.setNom("NORD OUESTs");

        Categorie cat3 = new Categorie();
        cat3.setCode("NOs3");
        cat3.setNom("NORD OUESTs3");

        List<Categorie> listCat = categorieRepo2.saveAll(Arrays.asList(cat1, cat2, cat3));

        @Dto()
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Data
        class MarqueDtoT {
            String code;
            String nom;

            @DtoCollectionId("categories")
            List<UUID> childCats;
        }

        MarqueDtoT marqueDtoT = new MarqueDtoT();
        marqueDtoT.setCode("BASSs");
        marqueDtoT.setNom("BIYESS ASSIs");

        marqueDtoT.setChildCats(listCat.stream().map(Categorie::getId).collect(Collectors.toList()));


        Marque marque = new Marque();
        serializerService.serialize(marqueDtoT, marque);

        Marque newMarque = marqueRepo.save(marque);
        List<Categorie> categories = newMarque.getCategories();
        assertNotNull(categories);
        assertEquals(3, categories.size());

        @Dto()
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Data
        class MarqueDto2 {
            @DtoId(strategy = DtoFieldIdStrategyType.UPDATE, nullable = true)
            UUID id;
            String code;
            String nom;

            @DtoCollectionId("categories")
            List<UUID> childCats;
        }

        Categorie toRemove = categories.get(0);
        MarqueDto2 marqueDto = new MarqueDto2();
        marqueDto.setId(newMarque.getId());
        marqueDto.setCode("Ms2");
        marqueDto.setNom("MMMs");
        marqueDto.setChildCats(categories.stream().skip(1).map(Categorie::getId).collect(Collectors.toList()));
        assertEquals(2, marqueDto.getChildCats().size());

        Marque marque3 = new Marque();
        serializerService.serialize(marqueDto, marque3);
        assertEquals(2, marque3.getCategories().size());
        assertTrue(!marque3.getCategories().stream().anyMatch(categorie -> Objects.equals(categorie.getId(), toRemove.getId())));

    }

    @DisplayName("Mode Modification:suppresion totale")
    @Test
    public void testModeSuppressionTout() throws DtoMappingException {

        Categorie cat1 = new Categorie();
        cat1.setCode("CEss");
        cat1.setNom("CENTREss");

        Categorie cat2 = new Categorie();
        cat2.setCode("NOss");
        cat2.setNom("NORD OUESTss");

        Categorie cat3 = new Categorie();
        cat3.setCode("NOs3s");
        cat3.setNom("NORD OUESTs3s");

        List<Categorie> listCat = categorieRepo2.saveAll(Arrays.asList(cat1, cat2, cat3));

        @Dto()
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Data
        class MarqueDtoT {
            String code;
            String nom;

            @DtoCollectionId("categories")
            List<UUID> childCats;
        }

        MarqueDtoT marqueDtoT = new MarqueDtoT();
        marqueDtoT.setCode("BASSss");
        marqueDtoT.setNom("BIYESS ASSIss");

        marqueDtoT.setChildCats(listCat.stream().map(Categorie::getId).collect(Collectors.toList()));


        Marque marque = new Marque();
        serializerService.serialize(marqueDtoT, marque);

        Marque newMarque = marqueRepo.save(marque);
        List<Categorie> categories = newMarque.getCategories();
        assertNotNull(categories);
        assertEquals(3, categories.size());

        @Dto()
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Data
        class MarqueDto2 {
            @DtoId(strategy = DtoFieldIdStrategyType.UPDATE, nullable = true)
            UUID id;
            String code;
            String nom;

            @DtoCollectionId("categories")
            List<UUID> childCats;
        }

        MarqueDto2 marqueDto = new MarqueDto2();
        marqueDto.setId(newMarque.getId());
        marqueDto.setCode("Ms2s");
        marqueDto.setNom("MMMs");

        Marque marque3 = new Marque();
        serializerService.serialize(marqueDto, marque3);
        assertTrue(marque3.getCategories() == null || 0 == marque3.getCategories().size());

    }
}

