package co.edu.uniandes.dse.arte7.services;    

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import co.edu.uniandes.dse.arte7.entities.ActorEntity;
import co.edu.uniandes.dse.arte7.entities.DirectorEntity;
import co.edu.uniandes.dse.arte7.entities.PlataformaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(PeliculaService.class)
public class PlataformaServiceTest {

	@Autowired
	private PlataformaServiceTest plataformaService;

    @Autowired
    private PlataformaPeliculaService plataformapeliculaService;

	@Autowired
	private TestEntityManager entityManager;


	private PodamFactory factory = new PodamFactoryImpl();

	private List<PlataformaEntity> plataformaList = new ArrayList<>();


	/**
	 * Configuración inicial de la prueba.
	 */
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	/**
	 * Limpia las tablas que están implicadas en la prueba.
	 */
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from PlataformaEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {


		for (int i = 0; i < 3; i++) {
			PlataformaEntity plataformaEntity = factory.manufacturePojo(PlataformaEntity.class);
			entityManager.persist(plataformaEntity);
			plataformaList.add(plataformaEntity);
		}


	}

	/**
	 * Prueba para crear una Pelicula.
	 * @throws IllegalOperationException 
	 */
	@Test
	void testCreatePelicula() throws IllegalOperationException {
		PlataformaEntity newEntity = factory.manufacturePojo(PlataformaEntity.class);
		
		
		PlataformaEntity result = peliculaService.createPelicula(newEntity);
		assertNotNull(result);

		PlataformaEntity entity = entityManager.find(PlataformaEntity.class, result.getId());

		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getNombre(), entity.getNombre());

	}
	
	/**
	 * Prueba para crear pelicula con actor null
	 * @throws IllegalOperationException 
	 */
	@Test
    void testCreatePeliculaNullActor() {
        assertThrows(IllegalOperationException.class, () -> {
                PlataformaEntity newEntity = factory.manufacturePojo(PlataformaEntity.class);
                newEntity.setActores(null);
                peliculaService.createPelicula(newEntity);
        });
    }

    /**
	 * Prueba para crear pelicula con actor no valido.
	 * @throws EntityNotFoundException
	 */
	@Test
    void testCreatePeliculaNoValidActor() {
        assertThrows(EntityNotFoundException.class, () -> {
                PlataformaEntity newEntity = factory.manufacturePojo(PlataformaEntity.class);
                ActorEntity newActor= factory.manufacturePojo(ActorEntity.class);
                List <ActorEntity> listActor = new ArrayList <>();
                listActor.add(newActor); 
                newEntity.setActores(listActor);
                peliculaService.createPelicula(newEntity);
        });
    }

	/**
	 * Prueba para consultar la lista de Peliculas.
	 */
	@Test
	void testgetPelicuas() {
		List<PlataformaEntity> PpeliculasList = peliculaService.getPeliculas();
		assertEquals(plataformaList.size(), PpeliculasList.size());

		for (PlataformaEntity PlataformaEntity : PpeliculasList) {
			boolean found = false;
			for (PlataformaEntity storedEntity : plataformaList) {
				if (PlataformaEntity.getId().equals(storedEntity.getId())) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

	/**
	 * Prueba para consultar un Peliculas.
	 */
	@Test
	void testgetPelicula() throws EntityNotFoundException {
		PlataformaEntity PlataformaEntity = plataformaList.get(0);

		PlataformaEntity resultEntity = peliculaService.getPelicula(PlataformaEntity.getId());
		assertNotNull(resultEntity);

		assertEquals(PlataformaEntity.getId(), resultEntity.getId());
		assertEquals(PlataformaEntity.getNombre(), resultEntity.getNombre());

	}

	/**
	 * Prueba para consultar un Pelicula que no existe.
	 */
	@Test
	void testGetInvalidDirector() {
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaService.getPelicula(0L);
		});
	}

	/**
	 * Prueba para actualizar una Pelicula.
	 */
	@Test
	void testUpdateDirector() throws EntityNotFoundException {
		PlataformaEntity PlataformaEntity = plataformaList.get(0);
		PlataformaEntity pojoEntity = factory.manufacturePojo(PlataformaEntity.class);

		pojoEntity.setId(PlataformaEntity.getId());

		peliculaService.updatePelicula(PlataformaEntity.getId(), pojoEntity);

		PlataformaEntity response = entityManager.find(PlataformaEntity.class, PlataformaEntity.getId());

		assertEquals(pojoEntity.getId(), response.getId());
		assertEquals(pojoEntity.getNombre(), response.getNombre());
	}
	
	/**
	 * Prueba para actualizar un Pelicula que no existe.
	 */
	@Test
	void testUpdateInvalidDirector()  {
		assertThrows(EntityNotFoundException.class, ()->{
			PlataformaEntity pojoEntity = factory.manufacturePojo(PlataformaEntity.class);
			peliculaService.updatePelicula(0L, pojoEntity);	
		});
	}

	/**
	 * Prueba para eliminar un Director
	 *
	 */
	@Test
	void testDeletePelicula() throws EntityNotFoundException, IllegalOperationException {
		PlataformaEntity PlataformaEntity = plataformaList.get(0);

        //elminar plataformas
        List <PlataformaEntity> plataformaList= PlataformaEntity.getPlataformas();
        for (PlataformaEntity storedEntity : plataformaList) {
            peliculaplataformaService.removePlataforma(PlataformaEntity.getId(), storedEntity.getId());
            
        }

        //eliminar actores
        List <ActorEntity> actorList= PlataformaEntity.getActores();
        for (ActorEntity storedEntity : actorList) {
            peliculaplataformaService.removePlataforma(PlataformaEntity.getId(), storedEntity.getId());
            
        }

        //eliminar actores
        List <DirectorEntity> directorList= PlataformaEntity.getDirectores();
        for (DirectorEntity storedEntity : directorList) {
            peliculaplataformaService.removePlataforma(PlataformaEntity.getId(), storedEntity.getId());
            
        }

		PlataformaEntity deleted = entityManager.find(PlataformaEntity.class, PlataformaEntity.getId());
		assertNull(deleted);
	}
	
	/**
	 * Prueba para eliminar una pelicula no existente
	 *
	 */
	@Test
	void testDeleteInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaService.deletePelicula(0l);
		});
	}

}
