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

import co.edu.uniandes.dse.arte7.entities.GeneroEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import({ ActorPeliculaService.class, PeliculaService.class })
class ActorPeliculaServiceTest {

	@Autowired
	private ActorPeliculaService actorPeliculaService;

	@Autowired
	private PeliculaService peliculaService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private ActorEntity actor = new ActorEntity();

	private List<ActorEntity> actorList = new ArrayList<>();
    private List<GeneroEntity> generoList = new ArrayList<>();
	private List<PeliculaEntity> peliculaList = new ArrayList<>();

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
		entityManager.getEntityManager().createQuery("delete from ActorEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {

        // Creo actor
		actor = factory.manufacturePojo(ActorEntity.class);
		entityManager.persist(actor);


		for (int i = 0; i < 3; i++) {
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			
            // Actor
            entity.getActores().add(actor);

            // Persisto la pelicula
			entityManager.persist(entity);
			peliculaList.add(entity);

			actor.getPeliculas().add(entity);
		}
	}

	/**
	 * Prueba para asociar una pelicula a un actor.
	 *
	 */
	@Test
	void testAddPelicula() throws EntityNotFoundException, IllegalOperationException {
		PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);

		peliculaService.createPelicula(newPelicula);

		PeliculaEntity peliculaEntity = actorPeliculaService.addPelicula(actor.getId(), newPelicula.getId());
		assertNotNull(peliculaEntity);

		assertEquals(peliculaEntity.getId(), newPelicula.getId());
		assertEquals(peliculaEntity.getNombre(), newPelicula.getNombre());
		assertEquals(peliculaEntity.getPoster(), newPelicula.getPoster());
		assertEquals(peliculaEntity.getPais(), newPelicula.getPais());
		assertEquals(peliculaEntity.getDuracionSec(), newPelicula.getDuracionSec());
        assertEquals(peliculaEntity.getFechaEstreno(), newPelicula.getFechaEstreno());
        assertEquals(peliculaEntity.getUrl(), newPelicula.getUrl());
        assertEquals(peliculaEntity.getVisitas(), newPelicula.getVisitas());
        assertEquals(peliculaEntity.getEstrellasPromedio(), newPelicula.getEstrellasPromedio());

		PeliculaEntity lastPelicula = actorPeliculaService.getPelicula(actor.getId(), newPelicula.getId());

		assertEquals(lastPelicula.getId(), newPelicula.getId());
		assertEquals(lastPelicula.getNombre(), newPelicula.getNombre());
		assertEquals(lastPelicula.getPoster(), newPelicula.getPoster());
		assertEquals(lastPelicula.getPais(), newPelicula.getPais());
		assertEquals(lastPelicula.getDuracionSec(), newPelicula.getDuracionSec());
        assertEquals(lastPelicula.getFechaEstreno(), newPelicula.getFechaEstreno());
        assertEquals(lastPelicula.getUrl(), newPelicula.getUrl());
        assertEquals(lastPelicula.getVisitas(), newPelicula.getVisitas());
        assertEquals(lastPelicula.getEstrellasPromedio(), newPelicula.getEstrellasPromedio());

	}
	

	/**
	 * Prueba para asociar una pelicula a un actor que no existe.
	 *
	 */

	@Test
	void testAddPeliculaInvalidActor() {
		assertThrows(EntityNotFoundException.class, () -> {
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
			newPelicula.setActores(actorList);
            newPelicula.setGeneros(generoList);
			peliculaService.createPelicula(newPelicula);
			actorPeliculaService.addPelicula(0L, newPelicula.getId());
		});
	}

	/**
	 * Prueba para asociar una pelicula que no existe a un actor.
	 *
	 */
	@Test
	void testAddInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, () -> {
			actorPeliculaService.addPelicula(actor.getId(), 0L);
		});
	}

	/**
	 * Prueba para consultar la lista de libros de un actor.
	 */
	@Test
	void testGetPeliculas() throws EntityNotFoundException {
		List<PeliculaEntity> peliculaEntities = actorPeliculaService.getPeliculas(actor.getId());

		assertEquals(peliculaList.size(), peliculaEntities.size());

		for (int i = 0; i < peliculaList.size(); i++) {
			assertTrue(peliculaEntities.contains(peliculaList.get(0)));
		}
	}

	/**
	 * Prueba para consultar la lista de libros de un actor que no existe.
	 */
	@Test
	void testGetPeliculasInvalidActor() {
		assertThrows(EntityNotFoundException.class, () -> {
			actorPeliculaService.getPeliculas(0L);
		});
	}

	/**
	 * Prueba para consultar una pelicula de un actor.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPelicula() throws EntityNotFoundException, IllegalOperationException {
		PeliculaEntity peliculaEntity = peliculaList.get(0);
		PeliculaEntity pelicula = actorPeliculaService.getPelicula(actor.getId(), peliculaEntity.getId());
		assertNotNull(pelicula);

		assertEquals(peliculaEntity.getId(), pelicula.getId());
		assertEquals(peliculaEntity.getNombre(), pelicula.getNombre());
		assertEquals(peliculaEntity.getPoster(), pelicula.getPoster());
		assertEquals(peliculaEntity.getPais(), pelicula.getPais());
		assertEquals(peliculaEntity.getDuracionSec(), pelicula.getDuracionSec());
        assertEquals(peliculaEntity.getFechaEstreno(), pelicula.getFechaEstreno());
        assertEquals(peliculaEntity.getUrl(), pelicula.getUrl());
        assertEquals(peliculaEntity.getVisitas(), pelicula.getVisitas());
        assertEquals(peliculaEntity.getEstrellasPromedio(), pelicula.getEstrellasPromedio());
	}

	/**
	 * Prueba para consultar una pelicula de un actor que no existe.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPeliculaInvalidActor() {
		assertThrows(EntityNotFoundException.class, () -> {
			PeliculaEntity peliculaEntity = peliculaList.get(0);
			actorPeliculaService.getPelicula(0L, peliculaEntity.getId());
		});
	}

	/**
	 * Prueba para consultar una pelicula que no existe de un actor.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, () -> {
			actorPeliculaService.getPelicula(actor.getId(), 0L);
		});
	}

	/**
	 * Prueba para consultar una pelicula que no está asociado a un actor.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPeliculaNotAssociatedActor() {
		assertThrows(IllegalOperationException.class, () -> {
			ActorEntity actorEntity = factory.manufacturePojo(ActorEntity.class);
			entityManager.persist(actorEntity);

			PeliculaEntity peliculaEntity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(peliculaEntity);

			actorPeliculaService.getPelicula(actorEntity.getId(), peliculaEntity.getId());
		});
	}

	/**
	 * Prueba para actualizar las peliculas de un actor.
	 *
	 * @throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testReplacePeliculas() throws EntityNotFoundException, IllegalOperationException {
		List<PeliculaEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(entity);
            actor.getPeliculas().add(entity);
			nuevaLista.add(entity);
		}
		actorPeliculaService.addPeliculas(actor.getId(), nuevaLista);
		List<PeliculaEntity> peliculaEntities = actorPeliculaService.getPeliculas(actor.getId());
		for (PeliculaEntity aNuevaLista : nuevaLista) {
			assertTrue(peliculaEntities.contains(aNuevaLista));
		}
	}
	
	/**
	 * Prueba para actualizar las peliculas de un actor.
	 *
	 * @throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testReplacePeliculas2() throws EntityNotFoundException, IllegalOperationException {
		List<PeliculaEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(entity);
            actor.getPeliculas().add(entity);
			nuevaLista.add(entity);
		}
		actorPeliculaService.addPeliculas(actor.getId(), nuevaLista);
		List<PeliculaEntity> peliculaEntities = actorPeliculaService.getPeliculas(actor.getId());
		for (PeliculaEntity aNuevaLista : nuevaLista) {
			assertTrue(peliculaEntities.contains(aNuevaLista));
		}
	}

	/**
	 * Prueba para actualizar las peliculas de un actor que no existe.
	 *
	 * @throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testReplacePeliculasInvalidActor() {
		assertThrows(EntityNotFoundException.class, () -> {
			List<PeliculaEntity> nuevaLista = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
                entityManager.persist(entity);
                actor.getPeliculas().add(entity);
                nuevaLista.add(entity);
            }
			actorPeliculaService.addPeliculas(0L, nuevaLista);
		});
	}

	/**
	 * Prueba para actualizar las peliculas que no existen de un actor.
	 *
	 * @throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testReplaceInvalidPeliculas() {
		assertThrows(EntityNotFoundException.class, () -> {
			List<PeliculaEntity> nuevaLista = new ArrayList<>();
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			entity.getActores().add(actor);
			entity.setActores(actorList);
			entity.setId(0L);
			nuevaLista.add(entity);
			actorPeliculaService.addPeliculas(actor.getId(), nuevaLista);
		});
	}

	/**
	 * Prueba desasociar una pelicula con un actor.
	 *
	 */
	@Test
	void testRemovePelicula() throws EntityNotFoundException {
		for (PeliculaEntity pelicula : peliculaList) {
			actorPeliculaService.removePelicula(actor.getId(), pelicula.getId());
		}
		assertTrue(actorPeliculaService.getPeliculas(actor.getId()).isEmpty());
	}

	/**
	 * Prueba desasociar una pelicula con un actor que no existe.
	 *
	 */
	@Test
	void testRemovePeliculaInvalidActor() {
		assertThrows(EntityNotFoundException.class, () -> {
			for (PeliculaEntity pelicula : peliculaList) {
				actorPeliculaService.removePelicula(0L, pelicula.getId());
			}
		});
	}

	/**
	 * Prueba desasociar una pelicula que no existe con un actor.
	 *
	 */
	@Test
	void testRemoveInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, () -> {
			actorPeliculaService.removePelicula(actor.getId(), 0L);
		});
	}
}