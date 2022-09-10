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

import co.edu.uniandes.dse.arte7.entities.NominacionEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Pruebas de logica de la relacion Pelicula - Nominacion
 *
 * @nominacion Federico Melo Barrero
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(PeliculaNominacionService.class)
public class PeliculaNominacionServiceTest {
	
	@Autowired
	private PeliculaNominacionService peliculaNominacionService;
	
	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private PeliculaEntity pelicula = new PeliculaEntity();
	private List<NominacionEntity> nominacionList = new ArrayList<>();

	
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}
	
	/**
	 * Limpia las tablas que están implicadas en la prueba.
	 */
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from NominacionEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {

		pelicula = factory.manufacturePojo(PeliculaEntity.class);
		entityManager.persist(pelicula);

		for (int i = 0; i < 3; i++) {
			NominacionEntity entity = factory.manufacturePojo(NominacionEntity.class);
			entityManager.persist(entity);
			entity.getPeliculas().add(pelicula);
			nominacionList.add(entity);
			pelicula.getNominaciones().add(entity);	
		}
	}

	/**
	 * Prueba para asociar un nominacion a una pelicula.
	 *
	 */
	@Test
	void testAddNominacion() throws EntityNotFoundException, IllegalOperationException {
		PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
		entityManager.persist(newPelicula);
		
		NominacionEntity nominacion = factory.manufacturePojo(NominacionEntity.class);
		entityManager.persist(nominacion);
		
		peliculaNominacionService.addNominacion(newPelicula.getId(), nominacion.getId());
		
		NominacionEntity lastNominacion = peliculaNominacionService.getNominacion(newPelicula.getId(), nominacion.getId());
		assertEquals(nominacion.getId(), lastNominacion.getId());
		assertEquals(nominacion.getNombre(), lastNominacion.getNombre());
		assertEquals(nominacion.getAnho(), lastNominacion.getAnho());
		assertEquals(nominacion.getCategoria(), lastNominacion.getCategoria());
	}
	
	/**
	 * Prueba para asociar un nominacion que no existe a una pelicula.
	 *
	 */
	@Test
	void testAddInvalidNominacion() {
		assertThrows(EntityNotFoundException.class, ()->{
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(newPelicula);
			peliculaNominacionService.addNominacion(newPelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para asociar un nominacion a una pelicula que no existe.
	 *
	 */
	@Test
	void testAddNominacionInvalidPelicula() throws EntityNotFoundException, IllegalOperationException {
		assertThrows(EntityNotFoundException.class, ()->{
			NominacionEntity nominacion = factory.manufacturePojo(NominacionEntity.class);
			entityManager.persist(nominacion);
			peliculaNominacionService.addNominacion(0L, nominacion.getId());
		});
	}

	/**
	 * Prueba para consultar la lista de nominaciones de una pelicula.
	 */
	@Test
	void testGetNominaciones() throws EntityNotFoundException {
		List<NominacionEntity> nominacionEntities = peliculaNominacionService.getNominaciones(pelicula.getId());

		assertEquals(nominacionList.size(), nominacionEntities.size());

		for (int i = 0; i < nominacionList.size(); i++) {
			assertTrue(nominacionEntities.contains(nominacionList.get(0)));
		}
	}
	
	/**
	 * Prueba para consultar la lista de nominaciones de una pelicula que no existe.
	 */
	@Test
	void testGetNominacionesInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaNominacionService.getNominaciones(0L);
		});
	}

	/**
	 * Prueba para consultar un nominacion de una pelicula.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetNominacion() throws EntityNotFoundException, IllegalOperationException {
		NominacionEntity nominacionEntity = nominacionList.get(0);
		NominacionEntity nominacion = peliculaNominacionService.getNominacion(pelicula.getId(), nominacionEntity.getId());
		assertNotNull(nominacion);

		assertEquals(nominacionEntity.getId(), nominacion.getId());
		assertEquals(nominacionEntity.getAnho(), nominacion.getAnho());
		assertEquals(nominacionEntity.getCategoria(), nominacion.getCategoria());
		assertEquals(nominacionEntity.getNombre(), nominacion.getNombre());
	}
	
	/**
	 * Prueba para consultar un nominacion que no existe de una pelicula.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetInvalidNominacion()  {
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaNominacionService.getNominacion(pelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para consultar un nominacion de una pelicula que no existe.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetNominacionInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, ()->{
			NominacionEntity nominacionEntity = nominacionList.get(0);
			peliculaNominacionService.getNominacion(0L, nominacionEntity.getId());
		});
	}
	
	/**
	 * Prueba para obtener un nominacion no asociado a una pelicula.
	 *
	 */
	@Test
	void testGetNotAssociatedNominacion() {
		assertThrows(IllegalOperationException.class, ()->{
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(newPelicula);
			NominacionEntity nominacion = factory.manufacturePojo(NominacionEntity.class);
			entityManager.persist(nominacion);
			peliculaNominacionService.getNominacion(newPelicula.getId(), nominacion.getId());
		});
	}

	/**
	 * Prueba para actualizar los nominaciones de una pelicula.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceNominaciones() throws EntityNotFoundException {
		List<NominacionEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			NominacionEntity entity = factory.manufacturePojo(NominacionEntity.class);
			entityManager.persist(entity);
			pelicula.getNominaciones().add(entity);
			nuevaLista.add(entity);
		}
		peliculaNominacionService.replaceNominaciones(pelicula.getId(), nuevaLista);
		
		List<NominacionEntity> nominacionEntities = peliculaNominacionService.getNominaciones(pelicula.getId());
		for (NominacionEntity aNuevaLista : nuevaLista) {
			assertTrue(nominacionEntities.contains(aNuevaLista));
		}
	}
	
	/**
	 * Prueba para actualizar los nominaciones de una pelicula.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceNominaciones2() throws EntityNotFoundException {
		List<NominacionEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			NominacionEntity entity = factory.manufacturePojo(NominacionEntity.class);
			entityManager.persist(entity);
			nuevaLista.add(entity);
		}
		peliculaNominacionService.replaceNominaciones(pelicula.getId(), nuevaLista);
		
		List<NominacionEntity> nominacionEntities = peliculaNominacionService.getNominaciones(pelicula.getId());
		for (NominacionEntity aNuevaLista : nuevaLista) {
			assertTrue(nominacionEntities.contains(aNuevaLista));
		}
	}
	
	
	/**
	 * Prueba para actualizar los nominaciones de una pelicula que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceNominacionesInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<NominacionEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				NominacionEntity entity = factory.manufacturePojo(NominacionEntity.class);
				entity.getPeliculas().add(pelicula);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			peliculaNominacionService.replaceNominaciones(0L, nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar los nominaciones que no existen de una pelicula.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceInvalidNominaciones() {
		assertThrows(EntityNotFoundException.class, ()->{
			List<NominacionEntity> nuevaLista = new ArrayList<>();
			NominacionEntity entity = factory.manufacturePojo(NominacionEntity.class);
			entity.setId(0L);
			nuevaLista.add(entity);
			peliculaNominacionService.replaceNominaciones(pelicula.getId(), nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar un nominacion de una pelicula que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceNominacionesInvalidNominacion(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<NominacionEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				NominacionEntity entity = factory.manufacturePojo(NominacionEntity.class);
				entity.getPeliculas().add(pelicula);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			peliculaNominacionService.replaceNominaciones(0L, nuevaLista);
		});
	}

	/**
	 * Prueba desasociar un nominacion con una pelicula.
	 *
	 */
	@Test
	void testRemoveNominacion() throws EntityNotFoundException {
		for (NominacionEntity nominacion : nominacionList) {
			peliculaNominacionService.removeNominacion(pelicula.getId(), nominacion.getId());
		}
		assertTrue(peliculaNominacionService.getNominaciones(pelicula.getId()).isEmpty());
	}
	
	/**
	 * Prueba desasociar un nominacion que no existe con una pelicula.
	 *
	 */
	@Test
	void testRemoveInvalidNominacion(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaNominacionService.removeNominacion(pelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba desasociar un nominacion con una pelicula que no existe.
	 *
	 */
	@Test
	void testRemoveNominacionInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaNominacionService.removeNominacion(0L, nominacionList.get(0).getId());
		});
	}

}