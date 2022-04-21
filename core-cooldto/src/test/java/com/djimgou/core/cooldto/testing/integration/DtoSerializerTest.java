package com.djimgou.core.cooldto.testing.integration;

import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.annotations.DtoEntityField;
import com.djimgou.core.cooldto.annotations.DtoField;
import com.djimgou.core.cooldto.annotations.DtoFkId;
import com.djimgou.core.cooldto.exception.*;
import com.djimgou.core.cooldto.service.DtoSerializerService;
import com.djimgou.core.cooldto.testing.app.CoreCoolDtoTestApplication;
import com.djimgou.core.cooldto.testing.app.model.*;
import com.djimgou.core.cooldto.testing.app.model.dto.ville.VilleDto;
import com.djimgou.core.test.initilizer.GenericDbManager;
import com.djimgou.core.test.util.FakeBuilder;
import lombok.Data;
import lombok.SneakyThrows;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
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

@SpringBootTest(classes = CoreCoolDtoTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-inTest.properties")
//@Sql({ "schema.sql", "data.sql" })
public class DtoSerializerTest {
    @PersistenceContext
    EntityManager em;

    private final DtoSerializerService serializerService;

    @Autowired
    public DtoSerializerTest(DtoSerializerService serializerService, GenericDbManager dbManager) {
        this.serializerService = serializerService;
        DbManagerConfig.initDb(dbManager);
    }

    // Test DtoClass
    @DisplayName("DtoClass: aucune anotation DtoClass définie")
    @Test
    public void testNoDtoClassAnotation() {
        @Data
        class VilleDtoT {
            @DtoField(value = "code")
            String code;
            @DtoField(value = "nom")
            String nom;
        }
        VilleDtoT villeDto = new VilleDtoT();
        villeDto.setCode(FakeBuilder.faker.code().imei());
        villeDto.setNom(FakeBuilder.faker.name().firstName());
        Ville ville = new Ville();
        assertThrows(DtoNoDtoClassAnotationProvidedException.class, () -> {
            serializerService.serialize(villeDto, ville);
        });
    }

    @DisplayName("DtoClass: types différents")
    @Test
    public void testClassTypeDifferent() {
        @Dto(value = Categorie.class)
        @Data
        class VilleDtoT {
            @DtoField()
            String code;
            @DtoField()
            String nom;
        }
        VilleDtoT villeDto = new VilleDtoT();
        villeDto.setCode(FakeBuilder.faker.code().imei());
        villeDto.setNom(FakeBuilder.faker.name().firstName());
        Ville ville = new Ville();
        assertThrows(DtoTargetClassMissMatchException.class, () -> {
            serializerService.serialize(villeDto, ville);
        });
    }

    @SneakyThrows
    @DisplayName("DtoClass: l'annotaion @DtoClass est présente avec le bon Target")
    @Test
    public void testHasDtoClass()  {
        @Dto(Ville.class)
        @Data
        class VilleDtoT {
            @DtoField()
            String code;
            @DtoField()
            String nom;
        }
        VilleDtoT villeDto = new VilleDtoT();
        villeDto.setCode("234");
        villeDto.setNom("Christ");
        Ville ville = new Ville();
        serializerService.serialize(villeDto, ville);
        assertNotNull(ville.getCode());
        assertNotNull(ville.getNom());
        assertEquals(villeDto.getCode(), ville.getCode());
        assertEquals(villeDto.getNom(), ville.getNom());
    }

    // Test DtoField
    @SneakyThrows
    @DisplayName("DtoField: un nom de propriété présent avec targeKey défini")
    @Test
    public void testDtoFieldSerialiserUnChamp() {
        @Dto()
        @Data
        class VilleDtoT {
            @DtoField(value = "code")
            String code;
            @DtoField(value = "nom")
            String nom;
        }
        VilleDtoT villeDto = new VilleDtoT();
        villeDto.setCode(FakeBuilder.faker.code().imei());
        villeDto.setNom(FakeBuilder.faker.name().firstName());

        Ville ville = new Ville();
        serializerService.serialize(villeDto, ville);
        assertNotNull(ville.getCode());
        assertNotNull(ville.getNom());
        assertEquals(villeDto.getCode(), ville.getCode());
        assertEquals(villeDto.getNom(), ville.getNom());
    }

    @SneakyThrows
    @DisplayName("DtoField: un nom de propriété présent avec targeKey null")
    @Test
    public void testDtoPropPresentEtTargetkeyNull() {
        @Dto()
        @Data
        class VilleDtoT {
            @DtoField()
            String code;

            @DtoField()
            String nom;
        }
        VilleDtoT villeDto = new VilleDtoT();
        villeDto.setCode(FakeBuilder.faker.code().imei());
        villeDto.setNom(FakeBuilder.faker.name().firstName());

        Ville ville = new Ville();
        serializerService.serialize(villeDto, ville);
        assertNotNull(ville.getCode());
        assertNotNull(ville.getNom());
        assertEquals(villeDto.getCode(), ville.getCode());
        assertEquals(villeDto.getNom(), ville.getNom());
    }

    @SneakyThrows
    @DisplayName("DtoField: sans aucune anotation DtoField")
    @Test
    public void testNoDtoField() {
        @Dto()
        @Data
        class VilleDtoT {
            String code;
            String nom;
        }
        VilleDtoT villeDto = new VilleDtoT();
        villeDto.setCode(FakeBuilder.faker.code().imei());
        villeDto.setNom(FakeBuilder.faker.name().firstName());

        Ville ville = new Ville();
        serializerService.serialize(villeDto, ville);
        assertNotNull(ville.getCode());
        assertNotNull(ville.getNom());
        assertNull(ville.getRegion());
        assertEquals(villeDto.getCode(), ville.getCode());
        assertEquals(villeDto.getNom(), ville.getNom());
    }

    @DisplayName("DtoField: un nom de propriété avec targetKey Inexistant")
    @Test
    public void testDtoFieldTypeMismatchProvided() {
        Region region = GenericDbManager.get(Region.class);

        @Dto()
        @Data
        class VilleDtoT {
            String code;
            String nom;

            @DtoField(value = "region2")
            UUID regionId;
        }

        VilleDtoT villeDto = new VilleDtoT();
        villeDto.setCode(FakeBuilder.faker.code().imei());
        villeDto.setNom(FakeBuilder.faker.name().firstName());
        villeDto.setRegionId(region.getId());

        Ville ville = new Ville();
        assertThrows(DtoTargetEntityNotFound.class, () -> {
            serializerService.serialize(villeDto, ville);
        });
    }

    @SneakyThrows
    @DisplayName("DtoField: avec les noms de propriétés différents du targetKey")
    @Test
    public void testDtoFieldSerialiserUnChampSansLeNom() {

        @Dto()
        @Data
        class VilleDtoT {
            //@DtoField(targetKey = "nom")
            String code;

            @DtoField(value = "code")
            String nom;
        }

        VilleDtoT villeDto = new VilleDtoT();
        villeDto.setCode("654");
        villeDto.setNom("Jesus");

        Ville ville = new Ville();
        serializerService.serialize(villeDto, ville);
        assertNotNull(ville.getCode());
        assertNull(ville.getNom());
        //assertNotNull(ville.getNom());

        //assertNotEquals(villeDto.getCode(), ville.getCode());
        assertEquals(villeDto.getNom(), ville.getCode());

    }

    // Test DtoFieldDb
    @SneakyThrows
    @DisplayName("DtoFieldDb: un nom de propriété présent et une entité présente")
    @Test
    public void testSerialiserUnChamp() {
        Region region = GenericDbManager.get(Region.class);
        VilleDto villeDto = FakeBuilder.fake(VilleDto.class);
        villeDto.setRegionId(region.getId());
        Ville ville = new Ville();
        serializerService.serialize(villeDto, ville);
        assertNotNull(ville.getCode());
        assertNotNull(ville.getNom());
        assertEquals(villeDto.getCode(),ville.getCode());
        assertEquals(villeDto.getNom(),ville.getNom());
    }

    @DisplayName("DtoFieldDb: un nom de propriété présent et une entité non persistante")
    @Test
    public void testAvecEntiteNonPersistante() {
        Region region = GenericDbManager.get(Region.class);

        @Dto()
        @Data
        class EntiteVille {
            String code;
            String nom;
            // reduction est non persistente
            Reduction region;
        }

        @Dto()
        @Data
        class VilleDtoT {
            String code;
            String nom;

            @DtoFkId(value = "region")
            UUID regionId;
        }


        VilleDtoT villeDto = new VilleDtoT();
        villeDto.setCode(FakeBuilder.faker.code().imei());
        villeDto.setNom(FakeBuilder.faker.name().firstName());
        villeDto.setRegionId(region.getId());
        EntiteVille ville = new EntiteVille();
        assertThrows(DtoTargetEntityNotFound.class, () -> {
            serializerService.serialize(villeDto, ville);
        });
    }

    @DisplayName("DtoFieldDb: un nom de propriété présent avec targetKey Inexistant")
    @Test
    public void testTypeMismatchProvided() {
        Region region = GenericDbManager.get(Region.class);

        @Dto()
        @Data
        class VilleDtoT {
            String code;
            String nom;

            @DtoFkId(value = "region2")
            UUID villepkI;
        }

        VilleDtoT villeDto = new VilleDtoT();
        villeDto.setCode(FakeBuilder.faker.code().imei());
        villeDto.setNom(FakeBuilder.faker.name().firstName());
        villeDto.setVillepkI(region.getId());

        Ville ville = new Ville();
        assertThrows(DtoMappingException.class, () -> {
            serializerService.serialize(villeDto, ville);
        });
    }

    @DisplayName("DtoFieldDb: un nom de propriété null avec nullable=false et targetKey existant")
    @Test
    public void testNullSourcePropertyMismatchProvided() {
        @Dto()
        @Data
        class VilleDtoT {
            String code;
            String nom;

            @DtoFkId(value = "region")
            UUID villepkI;
        }

        VilleDtoT villeDto = new VilleDtoT();
        villeDto.setCode(FakeBuilder.faker.code().imei());
        villeDto.setNom(FakeBuilder.faker.name().firstName());
        villeDto.setVillepkI(null);

        Ville ville = new Ville();
        assertThrows(DtoBadPropertyValueException.class, () -> {
            serializerService.serialize(villeDto, ville);
        });
    }

    @SneakyThrows
    @DisplayName("DtoFieldDb: un nom de propriété null avec nullable=true et targetKey existant")
    @Test
    public void testNullSourcePropertyNullableTrue() {
        @Dto()
        @Data
        class VilleDtoT {
            String code;
            String nom;

            @DtoFkId(value = "region", nullable = true)
            UUID villepkI;
        }

        VilleDtoT villeDto = new VilleDtoT();
        villeDto.setCode(FakeBuilder.faker.code().imei());
        villeDto.setNom(FakeBuilder.faker.name().firstName());
        villeDto.setVillepkI(null);

        Ville ville = new Ville();
        serializerService.serialize(villeDto, ville);
        assertNotNull(ville.getCode());
        assertNotNull(ville.getNom());
        assertNull(ville.getRegion());

    }

    @SneakyThrows
    @DisplayName("DtoFieldDb: avec les noms de propriétés différents du targetKey")
    @Test
    public void testSerialiserUnChampSansLeNom() {
        Region region = GenericDbManager.get(Region.class);

        @Dto()
        @Data
        class VilleDtoT {
            String code;
            String nom;

            @DtoFkId(value = "region")
            UUID villepkI;
        }

        VilleDtoT villeDto = new VilleDtoT();
        villeDto.setCode(FakeBuilder.faker.code().imei());
        villeDto.setNom(FakeBuilder.faker.name().firstName());
        villeDto.setVillepkI(region.getId());

        Ville ville = new Ville();
        serializerService.serialize(villeDto, ville);
        assertNotNull(ville.getCode());
        assertNotNull(ville.getNom());

        assertNotNull(ville.getRegion());
        assertEquals(villeDto.getNom(), ville.getNom());
        assertEquals(villeDto.getCode(), ville.getCode());
    }

    // Test DtoFieldEntity
    @SneakyThrows
    @DisplayName("DtoFieldEntity: un nom de propriété présent et une entité présente")
    @Test
    public void testDtoField() {
        Region region = GenericDbManager.get(Region.class);


        @Dto(Ville.class)
        @Data
        class VilleDtoT {
            @DtoField(value = "code")
            String codeT;
            @DtoField(value = "nom")
            String nomT;

            @DtoFkId(value = "region")
            UUID regionId;
        }

        @Dto(Quartier.class)
        @Data
        class QuartierDtoT {
            String code;
            String nom;

            @DtoEntityField(value = "ville")
            VilleDtoT parent;
        }


        VilleDtoT villeDto = new VilleDtoT();
        villeDto.setRegionId(region.getId());
        villeDto.setCodeT("YDE");
        villeDto.setNomT("YAOUNDE");

        QuartierDtoT quartierDto = new QuartierDtoT();
        quartierDto.setCode("BASS");
        quartierDto.setNom("BIYESS ASSI");
        quartierDto.setParent(villeDto);

        Quartier quartier = new Quartier();
        serializerService.serialize(quartierDto, quartier);
        assertNotNull(quartier.getCode());
        assertNotNull(quartier.getNom());
        assertEquals(quartierDto.getCode(), quartier.getCode());
        assertEquals(quartierDto.getNom(), quartier.getNom());

        assertNotNull(quartier.getVille());
        assertEquals(quartierDto.getParent().getCodeT(), quartier.getVille().getCode());
        assertEquals(quartierDto.getParent().getNomT(), quartier.getVille().getNom());
        assertNotNull(quartier.getVille().getRegion());
    }

    @SneakyThrows
    @DisplayName("DtoFieldEntity: L3 un nom de propriété présent et une entité présente niveaux 3")
    @Test
    public void testFieldEntityLevel3() {
        @Dto()
        @Data
        class RegionDtoT {
            @DtoField(value = "code")
            String codeR;
            @DtoField(value = "nom")
            String nomR;
        }

        @Dto()
        @Data
        class VilleDtoT {
            @DtoField(value = "code")
            String codeT;
            @DtoField(value = "nom")
            String nomT;

            @DtoEntityField(value = "region")
            RegionDtoT regionDtoT;
        }

        @Dto()
        @Data
        class QuartierDtoT {
            String code;
            String nom;

            @DtoEntityField(value = "ville")
            VilleDtoT parent;
        }

        RegionDtoT regionDto = new RegionDtoT();
        regionDto.setCodeR("CE");
        regionDto.setNomR("CENTRE");

        VilleDtoT villeDto = new VilleDtoT();
        villeDto.setRegionDtoT(regionDto);
        villeDto.setCodeT("YDE");
        villeDto.setNomT("YAOUNDE");

        QuartierDtoT quartierDto = new QuartierDtoT();
        quartierDto.setCode("BASS");
        quartierDto.setNom("BIYESS ASSI");
        quartierDto.setParent(villeDto);

        Quartier quartier = new Quartier();
        serializerService.serialize(quartierDto, quartier);
        assertNotNull(quartier.getCode());
        assertNotNull(quartier.getNom());
        assertEquals(quartierDto.getCode(), quartier.getCode());
        assertEquals(quartierDto.getNom(), quartier.getNom());

        assertNotNull(quartier.getVille());
        assertEquals(quartierDto.getParent().getCodeT(), quartier.getVille().getCode());
        assertEquals(quartierDto.getParent().getNomT(), quartier.getVille().getNom());
        assertNotNull(quartier.getVille().getRegion());

        assertEquals(quartierDto.getParent().getRegionDtoT().getCodeR(),
                quartier.getVille().getRegion().getCode()
        );
        assertEquals(quartierDto.getParent().getRegionDtoT().getNomR(),
                quartier.getVille().getRegion().getNom()
        );
    }

}

