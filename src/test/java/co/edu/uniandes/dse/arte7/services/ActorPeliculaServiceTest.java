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
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
// import co.edu.uniandes.dse.arte7.services.ActorPeliculaService;
// import co.edu.uniandes.dse.arte7.services.PeliculaService;

import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import({ ActorPeliculaService.class, PeliculaService.class })
public class ActorPeliculaServiceTest {
    @Autowired
	private ActorPeliculaService actorPeliculaService;

	@Autowired
	private PeliculaService bookService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private ActorEntity actor = new ActorEntity();
	private List<PeliculaEntity> bookList = new ArrayList<>();

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


		actor = factory.manufacturePojo(ActorEntity.class);
		entityManager.persist(actor);

		for (int i = 0; i < 3; i++) {
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			entity.getActores().add(actor);
			entityManager.persist(entity);
			bookList.add(entity);
			actor.getPeliculas().add(entity);
		}
	}

	/**
	 * Prueba para asociar un Pelicula a un actor.
	 *
	 */
	@Test
	void testAddPelicula() throws EntityNotFoundException, IllegalOperationException {
		PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
		bookService.createPelicula(newPelicula);

		PeliculaEntity bookEntity = actorPeliculaService.addPelicula(actor.getId(), newPelicula.getId());
		assertNotNull(bookEntity);

		assertEquals(bookEntity.getId(), newPelicula.getId());
		assertEquals(bookEntity.getNombre(), newPelicula.getNombre());
		assertEquals(bookEntity.getPoster(), newPelicula.getPoster());
		assertEquals(bookEntity.getDuracionSec(), newPelicula.getDuracionSec());
		assertEquals(bookEntity.getPais(), newPelicula.getPais());
        assertEquals(bookEntity.getFechaEstreno(), newPelicula.getFechaEstreno());
		assertEquals(bookEntity.getUrl(), newPelicula.getUrl());
		assertEquals(bookEntity.getVisitas(), newPelicula.getVisitas());
        assertEquals(bookEntity.getEstrellasPromedio(), newPelicula.getEstrellasPromedio());


		PeliculaEntity lastPelicula = actorPeliculaService.getPelicula(actor.getId(), newPelicula.getId());

		assertEquals(lastPelicula.getId(), newPelicula.getId());
		assertEquals(lastPelicula.getNombre(), newPelicula.getNombre());
		assertEquals(lastPelicula.getPoster(), newPelicula.getPoster());
		assertEquals(lastPelicula.getDuracionSec(), newPelicula.getDuracionSec());
		assertEquals(lastPelicula.getPais(), newPelicula.getPais());
        assertEquals(lastPelicula.getFechaEstreno(), newPelicula.getFechaEstreno());
		assertEquals(lastPelicula.getUrl(), newPelicula.getUrl());
		assertEquals(lastPelicula.getVisitas(), newPelicula.getVisitas());
		assertEquals(lastPelicula.getEstrellasPromedio(), newPelicula.getEstrellasPromedio());

	}
	

	/**
	 * Prueba para asociar un Pelicula a un actor que no existe.
	 *
	 */

	@Test
	void testAddPeliculaInvalidActor() {
		assertThrows(EntityNotFoundException.class, () -> {
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
			bookService.createPelicula(newPelicula);
			actorPeliculaService.addPelicula(0L, newPelicula.getId());
		});
	}

	/**
	 * Prueba para asociar un Pelicula que no existe a un actor.
	 *
	 */
	@Test
	void testAddInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, () -> {
			actorPeliculaService.addPelicula(actor.getId(), 0L);
		});
	}

	/**
	 * Prueba para consultar la lista de Peliculas de un actor.
	 */
	@Test
	void testGetPeliculas() throws EntityNotFoundException {
		List<PeliculaEntity> bookEntities = actorPeliculaService.getPeliculas(actor.getId());

		assertEquals(bookList.size(), bookEntities.size());

		for (int i = 0; i < bookList.size(); i++) {
			assertTrue(bookEntities.contains(bookList.get(0)));
		}
	}

	/**
	 * Prueba para consultar la lista de Peliculas de un actor que no existe.
	 */
	@Test
	void testGetPeliculasInvalidActor() {
		assertThrows(EntityNotFoundException.class, () -> {
			actorPeliculaService.getPeliculas(0L);
		});
	}

	/**
	 * Prueba para consultar un Pelicula de un actor.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPelicula() throws EntityNotFoundException, IllegalOperationException {
		PeliculaEntity bookEntity = bookList.get(0);
		PeliculaEntity book = actorPeliculaService.getPelicula(actor.getId(), bookEntity.getId());
		assertNotNull(book);

		assertEquals(bookEntity.getId(), book.getId());
		assertEquals(bookEntity.getNombre(), book.getNombre());
		assertEquals(bookEntity.getPoster(), book.getPoster());
		assertEquals(bookEntity.getDuracionSec(), book.getDuracionSec());
		assertEquals(bookEntity.getPais(), book.getPais());
        assertEquals(bookEntity.getFechaEstreno(), book.getFechaEstreno());
		assertEquals(bookEntity.getUrl(), book.getUrl());
		assertEquals(bookEntity.getVisitas(), book.getVisitas());
		assertEquals(bookEntity.getEstrellasPromedio(), book.getEstrellasPromedio());
	}

	/**
	 * Prueba para consultar un Pelicula de un actor que no existe.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPeliculaInvalidActor() {
		assertThrows(EntityNotFoundException.class, () -> {
			PeliculaEntity bookEntity = bookList.get(0);
			actorPeliculaService.getPelicula(0L, bookEntity.getId());
		});
	}

	/**
	 * Prueba para consultar un Pelicula que no existe de un actor.
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
	 * Prueba para consultar un Pelicula que no está asociado a un actor.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPeliculaNotAssociatedActor() {
		assertThrows(IllegalOperationException.class, () -> {
			ActorEntity actorEntity = factory.manufacturePojo(ActorEntity.class);
			entityManager.persist(actorEntity);

			PeliculaEntity bookEntity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(bookEntity);

			actorPeliculaService.getPelicula(actorEntity.getId(), bookEntity.getId());
		});
	}

	/**
	 * Prueba para actualizar los Peliculas de un actor.
	 *
	 * @throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testReplacePeliculas() throws EntityNotFoundException, IllegalOperationException {
		List<PeliculaEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			entity.getActores().add(actor);
			bookService.createPelicula(entity);
			nuevaLista.add(entity);
		}
		actorPeliculaService.addPeliculas(actor.getId(), nuevaLista);
		List<PeliculaEntity> bookEntities = actorPeliculaService.getPeliculas(actor.getId());
		for (PeliculaEntity aNuevaLista : nuevaLista) {
			assertTrue(bookEntities.contains(aNuevaLista));
		}
	}
	
	/**
	 * Prueba para actualizar los Peliculas de un actor.
	 *
	 * @throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testReplacePeliculas2() throws EntityNotFoundException, IllegalOperationException {
		List<PeliculaEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			bookService.createPelicula(entity);
			nuevaLista.add(entity);
		}
		actorPeliculaService.addPeliculas(actor.getId(), nuevaLista);
		List<PeliculaEntity> bookEntities = actorPeliculaService.getPeliculas(actor.getId());
		for (PeliculaEntity aNuevaLista : nuevaLista) {
			assertTrue(bookEntities.contains(aNuevaLista));
		}
	}

	/**
	 * Prueba para actualizar los Peliculas de un actor que no existe.
	 *
	 * @throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testReplacePeliculasInvalidActor() {
		assertThrows(EntityNotFoundException.class, () -> {
			List<PeliculaEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
				bookService.createPelicula(entity);
				nuevaLista.add(entity);
			}
			actorPeliculaService.addPeliculas(0L, nuevaLista);
		});
	}

	/**
	 * Prueba para actualizar los Peliculas que no existen de un actor.
	 *
	 * @throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testReplaceInvalidPeliculas() {
		assertThrows(EntityNotFoundException.class, () -> {
			List<PeliculaEntity> nuevaLista = new ArrayList<>();
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			entity.setId(0L);
			nuevaLista.add(entity);
			actorPeliculaService.addPeliculas(actor.getId(), nuevaLista);
		});
	}

	/**
	 * Prueba desasociar un Pelicula con un actor.
	 *
	 */
	@Test
	void testRemovePelicula() throws EntityNotFoundException {
		for (PeliculaEntity book : bookList) {
			actorPeliculaService.removePelicula(actor.getId(), book.getId());
		}
		assertTrue(actorPeliculaService.getPeliculas(actor.getId()).isEmpty());
	}

	/**
	 * Prueba desasociar un Pelicula con un actor que no existe.
	 *
	 */
	@Test
	void testRemovePeliculaInvalidActor() {
		assertThrows(EntityNotFoundException.class, () -> {
			for (PeliculaEntity book : bookList) {
				actorPeliculaService.removePelicula(0L, book.getId());
			}
		});
	}

	/**
	 * Prueba desasociar un Pelicula que no existe con un actor.
	 *
	 */
	@Test
	void testRemoveInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, () -> {
			actorPeliculaService.removePelicula(actor.getId(), 0L);
		});
	}
}
