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

import co.edu.uniandes.dse.arte7.entities.DirectorEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Pruebas de logica de la relacion Director - Peliculas
 *
 * @director Federico Melo Barrero
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import({ DirectorPeliculaService.class, PeliculaService.class })
class DirectorPeliculaServiceTest {

	@Autowired
	private DirectorPeliculaService directorPeliculaService;

	@Autowired
	private PeliculaService peliculaService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private DirectorEntity director = new DirectorEntity();
	private EditorialEntity editorial = new EditorialEntity();
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
		entityManager.getEntityManager().createQuery("delete from DirectorEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {
		editorial = factory.manufacturePojo(EditorialEntity.class);
		entityManager.persist(editorial);

		director = factory.manufacturePojo(DirectorEntity.class);
		entityManager.persist(director);

		for (int i = 0; i < 3; i++) {
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			entity.setEditorial(editorial);
			entity.getDirectores().add(director);
			entityManager.persist(entity);
			peliculaList.add(entity);
			director.getPeliculas().add(entity);
		}
	}

	/**
	 * Prueba para asociar una pelicula a un director.
	 *
	 */
	@Test
	void testAddPelicula() throws EntityNotFoundException, IllegalOperationException {
		PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
		newPelicula.setEditorial(editorial);
		peliculaService.createPelicula(newPelicula);

		PeliculaEntity peliculaEntity = directorPeliculaService.addPelicula(director.getId(), newPelicula.getId());
		assertNotNull(peliculaEntity);

		assertEquals(peliculaEntity.getId(), newPelicula.getId());
		assertEquals(peliculaEntity.getNombre(), newPelicula.getNombre());
		assertEquals(peliculaEntity.getDescription(), newPelicula.getDescription());
		assertEquals(peliculaEntity.getIsbn(), newPelicula.getIsbn());
		assertEquals(peliculaEntity.getImage(), newPelicula.getImage());

		PeliculaEntity lastPelicula = directorPeliculaService.getPelicula(director.getId(), newPelicula.getId());

		assertEquals(lastPelicula.getId(), newPelicula.getId());
		assertEquals(lastPelicula.getNombre(), newPelicula.getNombre());
		assertEquals(lastPelicula.getDescription(), newPelicula.getDescription());
		assertEquals(lastPelicula.getIsbn(), newPelicula.getIsbn());
		assertEquals(lastPelicula.getImage(), newPelicula.getImage());

	}
	

	/**
	 * Prueba para asociar una pelicula a un director que no existe.
	 *
	 */

	@Test
	void testAddPeliculaInvalidDirector() {
		assertThrows(EntityNotFoundException.class, () -> {
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
			newPelicula.setEditorial(editorial);
			peliculaService.createPelicula(newPelicula);
			directorPeliculaService.addPelicula(0L, newPelicula.getId());
		});
	}

	/**
	 * Prueba para asociar una pelicula que no existe a un director.
	 *
	 */
	@Test
	void testAddInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, () -> {
			directorPeliculaService.addPelicula(director.getId(), 0L);
		});
	}

	/**
	 * Prueba para consultar la lista de libros de un director.
	 */
	@Test
	void testGetPeliculas() throws EntityNotFoundException {
		List<PeliculaEntity> peliculaEntities = directorPeliculaService.getPeliculas(director.getId());

		assertEquals(peliculaList.size(), peliculaEntities.size());

		for (int i = 0; i < peliculaList.size(); i++) {
			assertTrue(peliculaEntities.contains(peliculaList.get(0)));
		}
	}

	/**
	 * Prueba para consultar la lista de libros de un director que no existe.
	 */
	@Test
	void testGetPeliculasInvalidDirector() {
		assertThrows(EntityNotFoundException.class, () -> {
			directorPeliculaService.getPeliculas(0L);
		});
	}

	/**
	 * Prueba para consultar una pelicula de un director.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPelicula() throws EntityNotFoundException, IllegalOperationException {
		PeliculaEntity peliculaEntity = peliculaList.get(0);
		PeliculaEntity pelicula = directorPeliculaService.getPelicula(director.getId(), peliculaEntity.getId());
		assertNotNull(pelicula);

		assertEquals(peliculaEntity.getId(), pelicula.getId());
		assertEquals(peliculaEntity.getNombre(), pelicula.getNombre());
		assertEquals(peliculaEntity.getDescription(), pelicula.getDescription());
		assertEquals(peliculaEntity.getIsbn(), pelicula.getIsbn());
		assertEquals(peliculaEntity.getImage(), pelicula.getImage());
	}

	/**
	 * Prueba para consultar una pelicula de un director que no existe.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPeliculaInvalidDirector() {
		assertThrows(EntityNotFoundException.class, () -> {
			PeliculaEntity peliculaEntity = peliculaList.get(0);
			directorPeliculaService.getPelicula(0L, peliculaEntity.getId());
		});
	}

	/**
	 * Prueba para consultar una pelicula que no existe de un director.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, () -> {
			directorPeliculaService.getPelicula(director.getId(), 0L);
		});
	}

	/**
	 * Prueba para consultar una pelicula que no está asociado a un director.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPeliculaNotAssociatedDirector() {
		assertThrows(IllegalOperationException.class, () -> {
			DirectorEntity directorEntity = factory.manufacturePojo(DirectorEntity.class);
			entityManager.persist(directorEntity);

			PeliculaEntity peliculaEntity = factory.manufacturePojo(PeliculaEntity.class);
			peliculaEntity.setEditorial(editorial);
			entityManager.persist(peliculaEntity);

			directorPeliculaService.getPelicula(directorEntity.getId(), peliculaEntity.getId());
		});
	}

	/**
	 * Prueba para actualizar las peliculas de un director.
	 *
	 * @throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testReplacePeliculas() throws EntityNotFoundException, IllegalOperationException {
		List<PeliculaEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			entity.getDirectores().add(director);
			entity.setEditorial(editorial);
			peliculaService.createPelicula(entity);
			nuevaLista.add(entity);
		}
		directorPeliculaService.addPeliculas(director.getId(), nuevaLista);
		List<PeliculaEntity> peliculaEntities = directorPeliculaService.getPeliculas(director.getId());
		for (PeliculaEntity aNuevaLista : nuevaLista) {
			assertTrue(peliculaEntities.contains(aNuevaLista));
		}
	}
	
	/**
	 * Prueba para actualizar las peliculas de un director.
	 *
	 * @throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testReplacePeliculas2() throws EntityNotFoundException, IllegalOperationException {
		List<PeliculaEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			entity.setEditorial(editorial);
			peliculaService.createPelicula(entity);
			nuevaLista.add(entity);
		}
		directorPeliculaService.addPeliculas(director.getId(), nuevaLista);
		List<PeliculaEntity> peliculaEntities = directorPeliculaService.getPeliculas(director.getId());
		for (PeliculaEntity aNuevaLista : nuevaLista) {
			assertTrue(peliculaEntities.contains(aNuevaLista));
		}
	}

	/**
	 * Prueba para actualizar las peliculas de un director que no existe.
	 *
	 * @throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testReplacePeliculasInvalidDirector() {
		assertThrows(EntityNotFoundException.class, () -> {
			List<PeliculaEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
				entity.setEditorial(editorial);
				peliculaService.createPelicula(entity);
				nuevaLista.add(entity);
			}
			directorPeliculaService.addPeliculas(0L, nuevaLista);
		});
	}

	/**
	 * Prueba para actualizar las peliculas que no existen de un director.
	 *
	 * @throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testReplaceInvalidPeliculas() {
		assertThrows(EntityNotFoundException.class, () -> {
			List<PeliculaEntity> nuevaLista = new ArrayList<>();
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			entity.setEditorial(editorial);
			entity.setId(0L);
			nuevaLista.add(entity);
			directorPeliculaService.addPeliculas(director.getId(), nuevaLista);
		});
	}

	/**
	 * Prueba desasociar una pelicula con un director.
	 *
	 */
	@Test
	void testRemovePelicula() throws EntityNotFoundException {
		for (PeliculaEntity pelicula : peliculaList) {
			directorPeliculaService.removePelicula(director.getId(), pelicula.getId());
		}
		assertTrue(directorPeliculaService.getPeliculas(director.getId()).isEmpty());
	}

	/**
	 * Prueba desasociar una pelicula con un director que no existe.
	 *
	 */
	@Test
	void testRemovePeliculaInvalidDirector() {
		assertThrows(EntityNotFoundException.class, () -> {
			for (PeliculaEntity pelicula : peliculaList) {
				directorPeliculaService.removePelicula(0L, pelicula.getId());
			}
		});
	}

	/**
	 * Prueba desasociar una pelicula que no existe con un director.
	 *
	 */
	@Test
	void testRemoveInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, () -> {
			directorPeliculaService.removePelicula(director.getId(), 0L);
		});
	}
}
