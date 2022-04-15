package com.act.core.testing.integration;

import com.act.core.dto.*;
import com.act.core.exception.*;
import com.act.core.model.BaseBdEntity;
import com.act.core.testing.app.ActCoreTestApplication;
import com.act.core.testing.app.FakeBuilder;
import com.act.core.testing.app.model.*;
import com.act.core.testing.app.model.dto.ville.VilleDto;
import com.act.core.testing.assertions.VilleAssertion;
import com.act.core.testing.initilizer.DbManager;
import lombok.Data;
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

@SpringBootTest(classes = ActCoreTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-inTest.properties")
//@Sql({ "schema.sql", "data.sql" })
public class DtoSerializerTest {
    @PersistenceContext
    EntityManager em;

    private DtoSerializerService serializerService;

    @Autowired
    public DtoSerializerTest(DtoSerializerService serializerService, DbManager dbManager) {
        this.serializerService = serializerService;
        DbManagerConfig.initDb(dbManager);
    }

    // Test DtoClass
    @DisplayName("DtoClass: aucune anotation DtoClass définie")
    @Test
    public void testNoDtoClassAnotation() {
        @Data
        class VilleDtoT {
            @DtoField(targetKey = "code")
            String code;
            @DtoField(targetKey = "nom")
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
        @DtoClass(value = Categorie.class)
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

    @DisplayName("DtoClass: l'annotaion @DtoClass est présente avec le bon Target")
    @Test
    public void testHasDtoClass() throws DtoMappingException {
        @DtoClass(Ville.class)
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
    @DisplayName("DtoField: un nom de propriété présent avec targeKey défini")
    @Test
    public void testDtoFieldSerialiserUnChamp() throws DtoMappingException {
        @DtoClass()
        @Data
        class VilleDtoT {
            @DtoField(targetKey = "code")
            String code;
            @DtoField(targetKey = "nom")
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

    @DisplayName("DtoField: un nom de propriété présent avec targeKey null")
    @Test
    public void testDtoPropPresentEtTargetkeyNull() throws DtoMappingException {
        @DtoClass()
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

    @DisplayName("DtoField: sans aucune anotation DtoField")
    @Test
    public void testNoDtoField() throws DtoMappingException {
        @DtoClass()
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
        Region region = DbManager.get(Region.class);

        @DtoClass()
        @Data
        class VilleDtoT {
            String code;
            String nom;

            @DtoField(targetKey = "region2")
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

    @DisplayName("DtoField: avec les noms de propriétés différents du targetKey")
    @Test
    public void testDtoFieldSerialiserUnChampSansLeNom() throws DtoMappingException {

        @DtoClass()
        @Data
        class VilleDtoT {
            //@DtoField(targetKey = "nom")
            String code;

            @DtoField(targetKey = "code")
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
    @DisplayName("DtoFieldDb: un nom de propriété présent et une entité présente")
    @Test
    public void testSerialiserUnChamp() throws DtoMappingException {
        Region region = DbManager.get(Region.class);
        VilleDto villeDto = FakeBuilder.fake(VilleDto.class);
        villeDto.setRegionId(region.getId());
        Ville ville = new Ville();
        serializerService.serialize(villeDto, ville);
        assertNotNull(ville.getCode());
        assertNotNull(ville.getNom());
        VilleAssertion.assertVilleEquals(villeDto, ville);
    }

    @DisplayName("DtoFieldDb: un nom de propriété présent et une entité non persistante")
    @Test
    public void testAvecEntiteNonPersistante() {
        Region region = DbManager.get(Region.class);

        @DtoClass()
        @Data
        class EntiteVille {
            String code;
            String nom;
            // reduction est non persistente
            Reduction region;
        }

        @DtoClass()
        @Data
        class VilleDtoT {
            String code;
            String nom;

            @DtoFieldDb(targetKey = "region")
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
        Region region = DbManager.get(Region.class);

        @DtoClass()
        @Data
        class VilleDtoT {
            String code;
            String nom;

            @DtoFieldDb(targetKey = "region2")
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
        @DtoClass()
        @Data
        class VilleDtoT {
            String code;
            String nom;

            @DtoFieldDb(targetKey = "region")
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

    @DisplayName("DtoFieldDb: un nom de propriété null avec nullable=true et targetKey existant")
    @Test
    public void testNullSourcePropertyNullableTrue() throws DtoMappingException {
        @DtoClass()
        @Data
        class VilleDtoT {
            String code;
            String nom;

            @DtoFieldDb(targetKey = "region", nullable = true)
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

    @DisplayName("DtoFieldDb: avec les noms de propriétés différents du targetKey")
    @Test
    public void testSerialiserUnChampSansLeNom() throws DtoMappingException {
        Region region = DbManager.get(Region.class);

        @DtoClass()
        @Data
        class VilleDtoT {
            String code;
            String nom;

            @DtoFieldDb(targetKey = "region")
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
    @DisplayName("DtoFieldEntity: un nom de propriété présent et une entité présente")
    @Test
    public void testDtoField() throws DtoMappingException {
        Region region = DbManager.get(Region.class);


        @DtoClass(Ville.class)
        @Data
        class VilleDtoT {
            @DtoField(targetKey = "code")
            String codeT;
            @DtoField(targetKey = "nom")
            String nomT;

            @DtoFieldDb(targetKey = "region")
            UUID regionId;
        }

        @DtoClass(Quartier.class)
        @Data
        class QuartierDtoT {
            String code;
            String nom;

            @DtoFieldEntity(targetKey = "ville")
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


    @DisplayName("DtoFieldEntity: L3 un nom de propriété présent et une entité présente niveaux 3")
    @Test
    public void testFieldEntityLevel3() throws DtoMappingException {
        @DtoClass()
        @Data
        class RegionDtoT {
            @DtoField(targetKey = "code")
            String codeR;
            @DtoField(targetKey = "nom")
            String nomR;
        }

        @DtoClass()
        @Data
        class VilleDtoT {
            @DtoField(targetKey = "code")
            String codeT;
            @DtoField(targetKey = "nom")
            String nomT;

            @DtoFieldEntity(targetKey = "region")
            RegionDtoT regionDtoT;
        }

        @DtoClass()
        @Data
        class QuartierDtoT {
            String code;
            String nom;

            @DtoFieldEntity(targetKey = "ville")
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

