package co.edu.uniandes.dse.arte7.services;    

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
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
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.entities.PlataformaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import({PeliculaService.class, PeliculaPlataformaService.class})
public class PeliculaServiceTest {

	@Autowired
	private PeliculaService peliculaService;

    @Autowired
    private PeliculaPlataformaService peliculaplataformaService;

	@Autowired
	private TestEntityManager entityManager;


	private PodamFactory factory = new PodamFactoryImpl();

	private List<PeliculaEntity> peliculaList = new ArrayList<>();

	private List<DirectorEntity> directorList = new ArrayList<>();

	private List<ActorEntity> actorList = new ArrayList<>();


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
		entityManager.getEntityManager().createQuery("delete from DirectorEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from ActorEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {
    
		for (int i = 0; i < 3; i++) {
			ActorEntity actorEntity = factory.manufacturePojo(ActorEntity.class);
			entityManager.persist(actorEntity);
			
			actorList.add(actorEntity);
		}

		for (int i = 0; i < 3; i++) {
			DirectorEntity directorEntity = factory.manufacturePojo(DirectorEntity.class);
			entityManager.persist(directorEntity);
			directorList.add(directorEntity);
		}

		for (int i = 0; i < 3; i++) {
			PeliculaEntity peliculaEntity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(peliculaEntity);
			peliculaList.add(peliculaEntity);
		}

		PeliculaEntity peliculaEntity = peliculaList.get(0);
		peliculaEntity.setActores(actorList);
        peliculaEntity.setDirectores(directorList);
		entityManager.persist(peliculaEntity);

	}

	/**
	 * Prueba para crear una Pelicula.
	 * @throws IllegalOperationException 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testCreatePelicula() throws IllegalOperationException, EntityNotFoundException {
		PeliculaEntity newEntity = factory.manufacturePojo(PeliculaEntity.class);
		
		
		PeliculaEntity result = peliculaService.createPelicula(newEntity);
		assertNotNull(result);

		PeliculaEntity entity = entityManager.find(PeliculaEntity.class, result.getId());

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
                PeliculaEntity newEntity = factory.manufacturePojo(PeliculaEntity.class);
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
                PeliculaEntity newEntity = factory.manufacturePojo(PeliculaEntity.class);
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
		List<PeliculaEntity> PpeliculasList = peliculaService.getPeliculas();
		assertEquals(peliculaList.size(), PpeliculasList.size());

		for (PeliculaEntity peliculaEntity : PpeliculasList) {
			boolean found = false;
			for (PeliculaEntity storedEntity : peliculaList) {
				if (peliculaEntity.getId().equals(storedEntity.getId())) {
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
		PeliculaEntity peliculaEntity = peliculaList.get(0);

		PeliculaEntity resultEntity = peliculaService.getPelicula(peliculaEntity.getId());
		assertNotNull(resultEntity);

		assertEquals(peliculaEntity.getId(), resultEntity.getId());
		assertEquals(peliculaEntity.getNombre(), resultEntity.getNombre());

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
		PeliculaEntity peliculaEntity = peliculaList.get(0);
		PeliculaEntity pojoEntity = factory.manufacturePojo(PeliculaEntity.class);

		pojoEntity.setId(peliculaEntity.getId());

		peliculaService.updatePelicula(peliculaEntity.getId(), pojoEntity);

		PeliculaEntity response = entityManager.find(PeliculaEntity.class, peliculaEntity.getId());

		assertEquals(pojoEntity.getId(), response.getId());
		assertEquals(pojoEntity.getNombre(), response.getNombre());
	}
	
	/**
	 * Prueba para actualizar un Pelicula que no existe.
	 */
	@Test
	void testUpdateInvalidDirector()  {
		assertThrows(EntityNotFoundException.class, ()->{
			PeliculaEntity pojoEntity = factory.manufacturePojo(PeliculaEntity.class);
			peliculaService.updatePelicula(0L, pojoEntity);	
		});
	}

	/**
	 * Prueba para eliminar un Director
	 *
	 */
	@Test
	void testDeletePelicula() throws EntityNotFoundException, IllegalOperationException {
		PeliculaEntity peliculaEntity = peliculaList.get(0);

        //elminar plataformas
        List <PlataformaEntity> plataformaList= peliculaEntity.getPlataformas();
        for (PlataformaEntity storedEntity : plataformaList) {
            peliculaplataformaService.removePlataforma(peliculaEntity.getId(), storedEntity.getId());
            
        }
        peliculaService.deletePelicula(peliculaEntity.getId());
		PeliculaEntity deleted = entityManager.find(PeliculaEntity.class, peliculaEntity.getId());
		assertNull(deleted);
	}

	/**
	 * Prueba para eliminar una pelicula no existente
	 *
	 */
	@Test
	void testDeletePeliculaWithAssociations() {
		assertThrows(IllegalOperationException.class, ()->{
			PeliculaEntity peliculaEntity = peliculaList.get(0);
			peliculaService.deletePelicula(peliculaEntity.getId());
		});
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