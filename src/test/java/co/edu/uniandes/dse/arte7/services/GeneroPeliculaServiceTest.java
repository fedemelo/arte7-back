package co.edu.uniandes.dse.arte7.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import({GeneroService.class, GeneroPeliculaService.class})

public class GeneroPeliculaServiceTest {
    
    @Autowired
    private GeneroPeliculaService generoPeliculaService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private GeneroEntity genero = new GeneroEntity();

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
        genero = factory.manufacturePojo(GeneroEntity.class);
		entityManager.persist(genero);

        for (int i = 0; i < 3; i++) {
            GeneroEntity generoEntity = factory.manufacturePojo(GeneroEntity.class);
            entityManager.persist(generoEntity);
            generoList.add(generoEntity);
        }

        GeneroEntity generoEntity = generoList.get(2);
        PeliculaEntity peliculaEntity = factory.manufacturePojo(PeliculaEntity.class);
        peliculaEntity.getGeneros().add(generoEntity);
        entityManager.persist(peliculaEntity);

        generoEntity.getPeliculas().add(peliculaEntity);
    } 

    /**Test: Asociar una pelicula a un genero existente */
    @Test
	void testAddPelicula() throws EntityNotFoundException {
        GeneroEntity entity = generoList.get(0);
		PeliculaEntity peliculaEntity = peliculaList.get(1);
		PeliculaEntity response = generoPeliculaService.addPelicula(peliculaEntity.getId(), entity.getId());

		assertNotNull(response);
		assertEquals(peliculaEntity.getId(), response.getId());
		assertEquals(peliculaEntity.getNombre(), response.getNombre());
	}

    /**Test: Asociar una pelicula que NO existe a un genero */

    @Test
	void testAddInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, ()->{
			GeneroEntity entity = generoList.get(0);
			generoPeliculaService.addPelicula(0L, entity.getId());
		});
	}
	
	/** Test: Asociar una película a un género que NO existe.*/
	@Test
	void testAddPeliculaInvalidGenero() {
		assertThrows(EntityNotFoundException.class, ()->{
			PeliculaEntity peliculaEntity = peliculaList.get(1);
			generoPeliculaService.addPelicula(peliculaEntity.getId(), 0L);
		});
	}

}
