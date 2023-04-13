package com.djimgou.core.coolvalidation.integration;

import com.djimgou.core.coolvalidation.app.CoreCoolValidationTestApplication;
import com.djimgou.core.coolvalidation.app.model.Categorie;
import com.djimgou.core.coolvalidation.app.model.Categorie2;
import com.djimgou.core.coolvalidation.app.repository.CategorieRepo;
import com.djimgou.core.coolvalidation.app.repository.CategorieRepo2;
import com.djimgou.core.coolvalidation.exception.CoolValidationException;
import com.djimgou.core.coolvalidation.processors.ValidationParser;
import com.djimgou.core.test.initilizer.GenericDbManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;


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

@SpringBootTest(classes = CoreCoolValidationTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-inTest.properties")
//@Sql({ "schema.sql", "data.sql" })
public class CoolValidationTest {
    private final ValidationParser validationParser;
    private final CategorieRepo categorieRepo;
    private final CategorieRepo2 categorieRepo2;

    @Autowired
    public CoolValidationTest(ValidationParser validationParser, CategorieRepo categorieRepo, CategorieRepo2 categorieRepo2) {
        this.validationParser = validationParser;
        this.categorieRepo = categorieRepo;
        this.categorieRepo2 = categorieRepo2;
    }

    // Test DtoClass
    @DisplayName("@Unique: le code et le nom de la categorie doivent être unique avec la casse")
    @Test
    public void testCaseSensitive() {
        Categorie cat = new Categorie("CAT", "Voiture");
        categorieRepo.save(cat);
        Categorie newCat = new Categorie("CAT", "Voiture");

        assertThrows(CoolValidationException.class, () -> {
            validationParser.validate(newCat);
        });
        try {
            validationParser.validate(newCat);
        } catch (CoolValidationException e) {
            assertEquals(Categorie.UNIQ_CODE_MSG, e.getMessage());
        }
        newCat.setCode("CAT1");
        assertThrows(CoolValidationException.class, () -> {
            validationParser.validate(newCat);
        });
        try {
            validationParser.validate(newCat);
        } catch (CoolValidationException e) {
            assertEquals(Categorie.UNIQ_NOM_MSG, e.getMessage());
        }

        newCat.setCode("CAT1");
        newCat.setNom("Camion");
        assertDoesNotThrow(() -> {
            validationParser.validate(newCat);
        });

    }

    // Test DtoClass
    @DisplayName("@Unique: le code et le nom de la categorie doivent être unique sans la casse")
    @Test
    public void testUniqueIgnoreCase() {
        Categorie2 cat = new Categorie2("POT", "pot voiture");
        categorieRepo2.save(cat);
        Categorie2 newCat = new Categorie2("poT", "pot voiture");

        assertThrows(CoolValidationException.class, () -> {
            validationParser.validate(newCat);
        });
        try {
            validationParser.validate(newCat);
        } catch (CoolValidationException e) {
            assertEquals(Categorie.UNIQ_CODE_MSG, e.getMessage());
        }
        newCat.setCode("POT1");
        assertThrows(CoolValidationException.class, () -> {
            validationParser.validate(newCat);
        });
        try {
            validationParser.validate(newCat);
        } catch (CoolValidationException e) {
            assertEquals(Categorie.UNIQ_NOM_MSG, e.getMessage());
        }

        newCat.setNom("pot voiture2");
        assertDoesNotThrow(() -> {
            validationParser.validate(newCat);
        });

    }


}

