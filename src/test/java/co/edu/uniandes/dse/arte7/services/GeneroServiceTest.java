package co.edu.uniandes.dse.arte7.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import co.edu.uniandes.dse.arte7.entities.GeneroEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
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
    private List<PeliculaEntity> peliculaList = new ArrayList<>();
    
    
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

        for (int i = 0; i < 3; i++) {
			PeliculaEntity peliculaEntity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(peliculaEntity);
			peliculaList.add(peliculaEntity);
        }

		generoList.get(0).getPeliculas().add(peliculaList.get(0));

    }

    /** Test: Crear un género */
    @Test
    void testCreateGenero() throws co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException {
        GeneroEntity newEntity = factory.manufacturePojo(GeneroEntity.class);
        GeneroEntity result = generoService.createGenero(newEntity);
        assertNotNull(result);
        GeneroEntity entity = entityManager.find(GeneroEntity.class, result.getId());
    
        assertEquals(newEntity.getId(), entity.getId());
        assertEquals(newEntity.getNombre(), entity.getNombre());
    }

    /** Test: Obtener todos los generos */
    @Test
    void testGetGeneros() {
        List < GeneroEntity > generosList = generoService.getGeneros();
        assertEquals(generosList.size(), generosList.size());

        for (GeneroEntity generoEntity: generosList) {
            boolean found = false;
            for (GeneroEntity storedEntity: generoList) {
                if (generoEntity.getId().equals(storedEntity.getId())) {
                    found = true;
                }
            }
            assertTrue(found);
        }
    }

    /**Test: Obtener género dado su ID */
    @Test
    void testGetGenero() throws EntityNotFoundException, co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException {
        GeneroEntity generoEntity = generoList.get(0);
    
        GeneroEntity resultEntity = generoService.getGenero(generoEntity.getId());
        assertNotNull(resultEntity);
    
        assertEquals(generoEntity.getId(), resultEntity.getId());
        assertEquals(generoEntity.getNombre(), resultEntity.getNombre());
    }

    /** Test: Obtener genero que NO existe */
    @Test
    void testGetInvalidGenero() {
        assertThrows(EntityNotFoundException.class, () -> {
            generoService.getGenero(0L);
        });
    }

    /**Test: Actualizar género */
    @Test
    void testUpdateGenero() throws EntityNotFoundException, co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException {
        GeneroEntity generoEntity = generoList.get(0);
        GeneroEntity pojoEntity = factory.manufacturePojo(GeneroEntity.class);
    
        pojoEntity.setId(generoEntity.getId());
    
        generoService.updateGenero(generoEntity.getId(), pojoEntity);
    
        GeneroEntity response = entityManager.find(GeneroEntity.class, generoEntity.getId());
    
        assertEquals(pojoEntity.getId(), response.getId());
        assertEquals(pojoEntity.getNombre(), response.getNombre());
    }

    /** Test: Actualizar género invalido */
    @Test
    void testUpdateInvalidGenero() {
        assertThrows(EntityNotFoundException.class, ()-> {
            GeneroEntity pojoEntity = factory.manufacturePojo(GeneroEntity.class);    
            generoService.updateGenero(0L, pojoEntity);
        });
    }

    /**Test:  Borrar género*/
    @Test
    void testDeleteGenero() throws EntityNotFoundException, IllegalOperationException {
        GeneroEntity generoEntity = generoList.get(0);
        generoService.deleteGenero(generoEntity.getId());
        GeneroEntity deleted = entityManager.find(GeneroEntity.class, generoEntity.getId());
        assertNull(deleted);
    }

    /**Test: Borrar género invalido */
    @Test
    void testDeleteInvalidGenero() {
        assertThrows(EntityNotFoundException.class, () -> {
            generoService.deleteGenero(0L);
        });
    }

   /**Test: Borrar algún genero con películas asociadas */
   @Test
    void testDeleteGeneroConPeliculas() {
        assertThrows(IllegalOperationException.class, () -> {
            GeneroEntity entity = generoList.get(0);
            generoService.deleteGenero(entity.getId());
        });
    }
}