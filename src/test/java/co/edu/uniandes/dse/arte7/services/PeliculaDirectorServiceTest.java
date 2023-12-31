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
 * Pruebas de logica de la relacion Pelicula - Director
 *
 * @director Federico Melo Barrero
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(PeliculaDirectorService.class)
public class PeliculaDirectorServiceTest {
	
	@Autowired
	private PeliculaDirectorService peliculaDirectorService;
	
	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private PeliculaEntity pelicula = new PeliculaEntity();
	private List<DirectorEntity> directorList = new ArrayList<>();

	
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

		pelicula = factory.manufacturePojo(PeliculaEntity.class);
		entityManager.persist(pelicula);

		for (int i = 0; i < 3; i++) {
			DirectorEntity entity = factory.manufacturePojo(DirectorEntity.class);
			entityManager.persist(entity);
			entity.getPeliculas().add(pelicula);
			directorList.add(entity);
			pelicula.getDirectores().add(entity);	
		}
	}

	/**
	 * Prueba para asociar un director a una pelicula.
	 *
	 */
	@Test
	void testAddDirector() throws EntityNotFoundException, IllegalOperationException {
		PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
		entityManager.persist(newPelicula);
		
		DirectorEntity director = factory.manufacturePojo(DirectorEntity.class);
		entityManager.persist(director);
		
		peliculaDirectorService.addDirector(newPelicula.getId(), director.getId());
		
		DirectorEntity lastDirector = peliculaDirectorService.getDirector(newPelicula.getId(), director.getId());
		assertEquals(director.getId(), lastDirector.getId());
		assertEquals(director.getNombre(), lastDirector.getNombre());
		assertEquals(director.getFotografia(), lastDirector.getFotografia());
		assertEquals(director.getNacionalidad(), lastDirector.getNacionalidad());
		assertEquals(director.getFechaNacimiento(), lastDirector.getFechaNacimiento());
        assertEquals(director.getBiografia(), lastDirector.getBiografia());
	}
	
	/**
	 * Prueba para asociar un director que no existe a una pelicula.
	 *
	 */
	@Test
	void testAddInvalidDirector() {
		assertThrows(EntityNotFoundException.class, ()->{
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(newPelicula);
			peliculaDirectorService.addDirector(newPelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para asociar un director a una pelicula que no existe.
	 *
	 */
	@Test
	void testAddDirectorInvalidPelicula() throws EntityNotFoundException, IllegalOperationException {
		assertThrows(EntityNotFoundException.class, ()->{
			DirectorEntity director = factory.manufacturePojo(DirectorEntity.class);
			entityManager.persist(director);
			peliculaDirectorService.addDirector(0L, director.getId());
		});
	}

	/**
	 * Prueba para consultar la lista de directores de una pelicula.
	 */
	@Test
	void testGetDirectores() throws EntityNotFoundException {
		List<DirectorEntity> directorEntities = peliculaDirectorService.getDirectores(pelicula.getId());

		assertEquals(directorList.size(), directorEntities.size());

		for (int i = 0; i < directorList.size(); i++) {
			assertTrue(directorEntities.contains(directorList.get(0)));
		}
	}
	
	/**
	 * Prueba para consultar la lista de directores de una pelicula que no existe.
	 */
	@Test
	void testGetDirectoresInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaDirectorService.getDirectores(0L);
		});
	}

	/**
	 * Prueba para consultar un director de una pelicula.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetDirector() throws EntityNotFoundException, IllegalOperationException {
		DirectorEntity directorEntity = directorList.get(0);
		DirectorEntity director = peliculaDirectorService.getDirector(pelicula.getId(), directorEntity.getId());
		assertNotNull(director);

		assertEquals(directorEntity.getId(), director.getId());
		assertEquals(directorEntity.getNombre(), director.getNombre());
		assertEquals(directorEntity.getBiografia(), director.getBiografia());
		assertEquals(directorEntity.getFotografia(), director.getFotografia());
		assertEquals(directorEntity.getFechaNacimiento(), director.getFechaNacimiento());
        assertEquals(directorEntity.getNacionalidad(), director.getNacionalidad());
	}
	
	/**
	 * Prueba para consultar un director que no existe de una pelicula.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetInvalidDirector()  {
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaDirectorService.getDirector(pelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para consultar un director de una pelicula que no existe.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetDirectorInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, ()->{
			DirectorEntity directorEntity = directorList.get(0);
			peliculaDirectorService.getDirector(0L, directorEntity.getId());
		});
	}
	
	/**
	 * Prueba para obtener un director no asociado a una pelicula.
	 *
	 */
	@Test
	void testGetNotAssociatedDirector() {
		assertThrows(IllegalOperationException.class, ()->{
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(newPelicula);

			DirectorEntity director = factory.manufacturePojo(DirectorEntity.class);
			entityManager.persist(director);
			
			peliculaDirectorService.getDirector(newPelicula.getId(), director.getId());
		});
	}

	/**
	 * Prueba para actualizar los directores de una pelicula.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceDirectores() throws EntityNotFoundException {
		List<DirectorEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			DirectorEntity entity = factory.manufacturePojo(DirectorEntity.class);
			entityManager.persist(entity);
			pelicula.getDirectores().add(entity);
			nuevaLista.add(entity);
		}
		peliculaDirectorService.replaceDirectores(pelicula.getId(), nuevaLista);
		
		List<DirectorEntity> directorEntities = peliculaDirectorService.getDirectores(pelicula.getId());
		for (DirectorEntity aNuevaLista : nuevaLista) {
			assertTrue(directorEntities.contains(aNuevaLista));
		}
	}
	
	/**
	 * Prueba para actualizar los directores de una pelicula.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceDirectores2() throws EntityNotFoundException {
		List<DirectorEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			DirectorEntity entity = factory.manufacturePojo(DirectorEntity.class);
			entityManager.persist(entity);
			nuevaLista.add(entity);
		}
		peliculaDirectorService.replaceDirectores(pelicula.getId(), nuevaLista);
		
		List<DirectorEntity> directorEntities = peliculaDirectorService.getDirectores(pelicula.getId());
		for (DirectorEntity aNuevaLista : nuevaLista) {
			assertTrue(directorEntities.contains(aNuevaLista));
		}
	}
	
	
	/**
	 * Prueba para actualizar los directores de una pelicula que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceDirectoresInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<DirectorEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				DirectorEntity entity = factory.manufacturePojo(DirectorEntity.class);
				entity.getPeliculas().add(pelicula);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			peliculaDirectorService.replaceDirectores(0L, nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar los directores que no existen de una pelicula.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceInvalidDirectores() {
		assertThrows(EntityNotFoundException.class, ()->{
			List<DirectorEntity> nuevaLista = new ArrayList<>();
			DirectorEntity entity = factory.manufacturePojo(DirectorEntity.class);
			entity.setId(0L);
			nuevaLista.add(entity);
			peliculaDirectorService.replaceDirectores(pelicula.getId(), nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar un director de una pelicula que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceDirectoresInvalidDirector(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<DirectorEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				DirectorEntity entity = factory.manufacturePojo(DirectorEntity.class);
				entity.getPeliculas().add(pelicula);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			peliculaDirectorService.replaceDirectores(0L, nuevaLista);
		});
	}

	/**
	 * Prueba desasociar un director con una pelicula.
	 *
	 */
	@Test
	void testRemoveDirector() throws EntityNotFoundException {
		for (DirectorEntity director : directorList) {
			peliculaDirectorService.removeDirector(pelicula.getId(), director.getId());
		}
		assertTrue(peliculaDirectorService.getDirectores(pelicula.getId()).isEmpty());
	}
	
	/**
	 * Prueba desasociar un director que no existe con una pelicula.
	 *
	 */
	@Test
	void testRemoveInvalidDirector(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaDirectorService.removeDirector(pelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba desasociar un director con una pelicula que no existe.
	 *
	 */
	@Test
	void testRemoveDirectorInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaDirectorService.removeDirector(0L, directorList.get(0).getId());
		});
	}

}