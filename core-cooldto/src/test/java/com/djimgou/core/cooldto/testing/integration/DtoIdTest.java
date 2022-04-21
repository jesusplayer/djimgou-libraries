package com.djimgou.core.cooldto.testing.integration;

import com.djimgou.core.cooldto.annotations.Dto;
import com.djimgou.core.cooldto.annotations.DtoId;
import com.djimgou.core.cooldto.exception.DtoBadPropertyValueException;
import com.djimgou.core.cooldto.exception.DtoFieldNotFoundException;
import com.djimgou.core.cooldto.service.DtoSerializerService;
import com.djimgou.core.cooldto.testing.app.CoreCoolDtoTestApplication;
import com.djimgou.core.cooldto.testing.app.model.Marque;
import com.djimgou.core.cooldto.annotations.DtoFieldIdStrategyType;
import com.djimgou.core.test.initilizer.GenericDbManager;
import lombok.AccessLevel;
import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.util.*;

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
public class DtoIdTest {
    private final DtoSerializerService serializerService;

    @Autowired
    public DtoIdTest(DtoSerializerService serializerService, GenericDbManager dbManager) {
        this.serializerService = serializerService;
        DbManagerConfig.initDb(dbManager);
    }

    @SneakyThrows
    @DisplayName("DtoId: Avec un Id connu ")
    @Test
    public void testDtoId() {
        Marque marqueOld = GenericDbManager.get(Marque.class);

        @Dto()
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Data
        class MarqueDto {
            @DtoId()
            UUID id;
            String code;
            String nom;
        }

        MarqueDto marqueDto = new MarqueDto();
        marqueDto.setId(marqueOld.getId());

        Marque marque = new Marque();

        serializerService.serialize(marqueDto, marque);

        Assertions.assertNull(marqueDto.getCode());
        Assertions.assertNull(marqueDto.getNom());
        Assertions.assertEquals(marqueOld.getCode(), marque.getCode());
        Assertions.assertEquals(marqueOld.getNom(), marque.getNom());

    }

    @SneakyThrows
    @DisplayName("DtoId: Avec un Id connu et un dto à une propriété")
    @Test
    public void testDtoIdWithOneField() {
        Marque marqueOld = GenericDbManager.get(Marque.class);

        @Dto()
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Data
        class MarqueDto {
            @DtoId()
            UUID id;
        }

        MarqueDto marqueDto = new MarqueDto();
        marqueDto.setId(marqueOld.getId());

        Marque marque = new Marque();

        serializerService.serialize(marqueDto, marque);

        Assertions.assertEquals(marqueOld.getCode(), marque.getCode());
        Assertions.assertEquals(marqueOld.getNom(), marque.getNom());

    }

    @SneakyThrows
    @DisplayName("DtoId: Avec un Id connu existant et non null")
    @Test
    public void testFieldEntityLevel3() {
        Marque marqueOld = GenericDbManager.get(Marque.class);

        @Dto()
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Data
        class MarqueDto {
            @DtoId()
            UUID id;
            String code;
            String nom;
        }

        MarqueDto marqueDto = new MarqueDto();
        marqueDto.setId(marqueOld.getId());

        Marque marque = new Marque();

        serializerService.serialize(marqueDto, marque);

        Assertions.assertNull(marqueDto.getCode());
        Assertions.assertNull(marqueDto.getNom());
        Assertions.assertEquals(marqueOld.getCode(), marque.getCode());
        Assertions.assertEquals(marqueOld.getNom(), marque.getNom());

    }

    @SneakyThrows
    @DisplayName("DtoId: Avec un Id inexistant")
    @Test
    public void testIdunkwo() {
        Marque marqueOld = GenericDbManager.get(Marque.class);
        @Dto()
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Data
        class MarqueDto {
            @DtoId()
            UUID id;
            String code;
            String nom;
        }

        MarqueDto marqueDto = new MarqueDto();
        marqueDto.setId(UUID.randomUUID());
        Marque marque = new Marque();
        Assertions.assertThrows(DtoFieldNotFoundException.class, () -> {
            serializerService.serialize(marqueDto, marque);
        });

    }

    @DisplayName("DtoId: Avec un Id Null")
    @Test
    public void testIdNull() {
        @Dto()
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Data
        class MarqueDto {
            @DtoId()
            UUID id;
            String code;
            String nom;
        }

        MarqueDto marqueDto = new MarqueDto();
        Marque marque = new Marque();
        Assertions.assertThrows(DtoBadPropertyValueException.class, () -> {
            serializerService.serialize(marqueDto, marque);
        });

    }

    @SneakyThrows
    @DisplayName("DtoId: Avec un Id Null et avec l'attribut nullable=true")
    @Test
    public void testIdNullAndNullable() {
        @Dto()
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Data
        class MarqueDto {
            @DtoId(nullable = true)
            UUID id;
            String code;
            String nom;
        }

        MarqueDto marqueDto = new MarqueDto();
        marqueDto.setCode("TY");
        marqueDto.setNom("Toyota");
        Marque marque = new Marque();
        serializerService.serialize(marqueDto, marque);
        Assertions.assertNotNull(marque.getCode());
        Assertions.assertNotNull(marque.getNom());
        Assertions.assertEquals(marqueDto.getCode(), marque.getCode());
        Assertions.assertEquals(marqueDto.getNom(), marque.getNom());
    }

    @SneakyThrows
    @DisplayName("DtoId: Avec un Id connu et la strategie DtoFieldIdStrategyType.UPDATE")
    @Test
    public void testDtoIdUpdateStrategy() {
        Marque marqueOld = GenericDbManager.get(Marque.class);

        @Dto()
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Data
        class MarqueDto {
            @DtoId(strategy = DtoFieldIdStrategyType.UPDATE)
            UUID id;
            String code;
            String nom;
        }

        MarqueDto marqueDto = new MarqueDto();
        marqueDto.setId(marqueOld.getId());
        marqueDto.setCode("Nouveau code bien différent");
        marqueDto.setNom(null);

        Marque marque = new Marque();

        serializerService.serialize(marqueDto, marque);

        Assertions.assertEquals(marqueDto.getCode(), marque.getCode());
        Assertions.assertNull(marque.getNom());


    }

    @SneakyThrows
    @DisplayName("DtoId: Avec un Id=null et la strategie DtoFieldIdStrategyType.UPDATE et nullable=true")
    @Test
    public void testDtoIdUpdateStrategyAndNullable() {
        @Dto()
        @FieldDefaults(level = AccessLevel.PRIVATE)
        @Data
        class MarqueDto {
            @DtoId(strategy = DtoFieldIdStrategyType.UPDATE, nullable = true)
            UUID id;
            String code;
            String nom;
        }

        MarqueDto marqueDto = new MarqueDto();
        marqueDto.setId(null);
        marqueDto.setCode("Nouveau code bien différent");
        marqueDto.setNom("Un nom");

        Marque marque = new Marque();

        serializerService.serialize(marqueDto, marque);

        Assertions.assertEquals(marqueDto.getCode(), marque.getCode());
        Assertions.assertEquals(marqueDto.getNom(), marque.getNom());
    }

}

