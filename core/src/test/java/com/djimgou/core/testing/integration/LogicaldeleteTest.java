package com.djimgou.core.testing.integration;

import com.djimgou.core.test.initilizer.GenericDbManager;
import com.djimgou.core.test.util.FakeBuilder;
import com.djimgou.core.testing.app.MaincoreTestApplication;
import com.djimgou.core.testing.app.model.*;
import com.djimgou.core.testing.app.reposirtory.LogicaldeleteEntityRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;


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

//@Rollback
@SpringBootTest(classes = MaincoreTestApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(locations = "classpath:application-inTest.properties")
//@Sql({ "schema.sql", "data.sql" })
public class LogicaldeleteTest {


    private final LogicaldeleteEntityRepo repo;


    @Autowired
    public LogicaldeleteTest(LogicaldeleteEntityRepo repo) {
        this.repo = repo;
    }

    @DisplayName("test suppression logique")
    @Test
    void createAcategories() {

       /* LogicalDeleEntity log = new LogicalDeleEntity("LO_DEL1", "Logical entity1", false);
        LogicalDeleEntity log2 = new LogicalDeleEntity("LO_DEL2", "Logical entity2", false);
        log = repo.save(log);
        repo.save(log2);
        assert log.getId() != null;
        repo.delete(log);*/

    }


}

