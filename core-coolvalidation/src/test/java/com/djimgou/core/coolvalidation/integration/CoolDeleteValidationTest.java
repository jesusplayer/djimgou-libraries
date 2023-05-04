package com.djimgou.core.coolvalidation.integration;

import com.djimgou.core.coolvalidation.app.CoreCoolValidationTestApplication;
import com.djimgou.core.coolvalidation.app.model.Categorie;
import com.djimgou.core.coolvalidation.app.model.Categorie2;
import com.djimgou.core.coolvalidation.app.model.Child;
import com.djimgou.core.coolvalidation.app.model.Parent;
import com.djimgou.core.coolvalidation.app.repository.CategorieRepo;
import com.djimgou.core.coolvalidation.app.repository.CategorieRepo2;
import com.djimgou.core.coolvalidation.app.repository.ChildRepo;
import com.djimgou.core.coolvalidation.app.repository.ParentRepo;
import com.djimgou.core.coolvalidation.exception.CoolValidationException;
import com.djimgou.core.coolvalidation.processors.ValidationParser;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

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

@SpringBootTest(classes = CoreCoolValidationTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-inTest.properties")
//@Sql({ "schema.sql", "data.sql" })
public class CoolDeleteValidationTest {
    private final ValidationParser validationParser;
    private final ParentRepo parentRepo;
    private final ChildRepo childRepo;

    @Autowired
    public CoolDeleteValidationTest(
            ValidationParser validationParser,
            ParentRepo parentRepo,
            ChildRepo childRepo) {
        this.validationParser = validationParser;
        this.parentRepo = parentRepo;
        this.childRepo = childRepo;
    }

    // Test DtoClass
    @DisplayName("@CanDelete: refuse si")
    @Test
    public void testCaseSensitive() {
        Parent parent = new Parent("Parent");
        parent = parentRepo.save(parent);
        Child child = new Child("Fils", parent);
        childRepo.save(child);

        /*assertThrows(CoolValidationException.class, () -> {
            validationParser.checkBeforeDelete(child);
        });*/

    }

    // Test DtoClass
    @DisplayName("@Unique: le code et le nom de la categorie doivent être unique sans la casse")
    @Test
    public void testUniqueIgnoreCase() {


    }


}

