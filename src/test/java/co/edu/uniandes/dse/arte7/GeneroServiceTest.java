package co.edu.uniandes.dse.arte7;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import co.edu.uniandes.dse.arte7.entities.GeneroEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.services.GeneroService;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(GeneroService.class)

public class GeneroServiceTest {
    
    @Autowired
    private GeneroService generoService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<GeneroEntity> generoList = new ArrayList<>();
    
    @BeforeEach
    void setUp() {
        clearData();
        insertData();
    }

    private void clearData() {
        entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from GeneroEntity").executeUpdate();
    }

    private void insertData() {
        for (int i = 0; i < 3; i++) {
            GeneroEntity generoEntity = factory.manufacturePojo(GeneroEntity.class);
            entityManager.persist(generoEntity);
            generoList.add(generoEntity);
        }

    }

    @Test
    void testCrearGenero(){
        GeneroEntity newEntity = factory.manufacturePojo(GeneroEntity.class);
        GeneroEntity result = generoService.crearGenero(newEntity);
        assertNotNull(result);
    
        GeneroEntity entity = entityManager.find(GeneroEntity.class, result.getId());
    
        assertEquals(newEntity.getId(), entity.getId());
        assertEquals(newEntity.getNombre(), entity.getNombre());
    }

    @Test
    void testCrearGeneroInvalido() {
        assertThrows(IllegalOperationException.class, () -> {
                GeneroEntity newEntity = factory.manufacturePojo(GeneroEntity.class);
                newEntity.setNombre(null);
                generoService.crearGenero(newEntity);
        });
    }
}
