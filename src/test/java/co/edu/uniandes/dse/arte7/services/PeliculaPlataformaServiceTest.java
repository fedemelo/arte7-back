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

import co.edu.uniandes.dse.arte7.entities.PlataformaEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(PeliculaPlataformaService.class)
public class PeliculaPlataformaServiceTest {
	
	@Autowired
	private PeliculaPlataformaService peliculaPlataformaService;
	
	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private PeliculaEntity pelicula = new PeliculaEntity();
	private List<PlataformaEntity> plataformaList = new ArrayList<>();

	
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

		pelicula = factory.manufacturePojo(PeliculaEntity.class);
		entityManager.persist(pelicula);

		for (int i = 0; i < 3; i++) {
			PlataformaEntity entity = factory.manufacturePojo(PlataformaEntity.class);
			entityManager.persist(entity);
			entity.getPeliculas().add(pelicula);
			plataformaList.add(entity);
			pelicula.getPlataformas().add(entity);	
		}
	}

	/**
	 * Prueba para asociar un Plataforma a una pelicula.
	 *
	 */
	@Test
	void testAddPlataforma() throws EntityNotFoundException, IllegalOperationException {
		PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
		entityManager.persist(newPelicula);
		
		PlataformaEntity plataforma = factory.manufacturePojo(PlataformaEntity.class);
		entityManager.persist(plataforma);
		
		peliculaPlataformaService.addPlataforma(newPelicula.getId(), plataforma.getId());
		
		PlataformaEntity lastPlataforma = peliculaPlataformaService.getPlataforma(newPelicula.getId(), plataforma.getId());
		assertEquals(plataforma.getId(), lastPlataforma.getId());
		assertEquals(plataforma.getNombre(), lastPlataforma.getNombre());
		assertEquals(plataforma.getUrl(), lastPlataforma.getUrl());

	}
	
	/**
	 * Prueba para asociar un Plataforma que no existe a una pelicula.
	 *
	 */
	@Test
	void testAddInvalidPlataforma() {
		assertThrows(EntityNotFoundException.class, ()->{
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(newPelicula);
			peliculaPlataformaService.addPlataforma(newPelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para asociar un Plataforma a una pelicula que no existe.
	 *
	 */
	@Test
	void testAddPlataformaInvalidPelicula() throws EntityNotFoundException, IllegalOperationException {
		assertThrows(EntityNotFoundException.class, ()->{
			PlataformaEntity plataforma = factory.manufacturePojo(PlataformaEntity.class);
			entityManager.persist(plataforma);
			peliculaPlataformaService.addPlataforma(0L, plataforma.getId());
		});
	}

	/**
	 * Prueba para consultar la lista de Plataforma de una pelicula.
	 */
	@Test
	void testGetPlataformas() throws EntityNotFoundException {
		List<PlataformaEntity> plataformaEntities = peliculaPlataformaService.getPlataformas(pelicula.getId());

		assertEquals(plataformaList.size(), plataformaEntities.size());

		for (int i = 0; i < plataformaList.size(); i++) {
			assertTrue(plataformaEntities.contains(plataformaList.get(0)));
		}
	}
	
	/**
	 * Prueba para consultar la lista de Plataformas de una pelicula que no existe.
	 */
	@Test
	void testGetPlataformasInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaPlataformaService.getPlataformas(0L);
		});
	}

	/**
	 * Prueba para consultar un Plataforma de una pelicula.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPlataforma() throws EntityNotFoundException, IllegalOperationException {
		PlataformaEntity plataformaEntity = plataformaList.get(0);
		PlataformaEntity plataforma = peliculaPlataformaService.getPlataforma(pelicula.getId(), plataformaEntity.getId());
		assertNotNull(plataforma);

		assertEquals(plataformaEntity.getId(), plataforma.getId());
		assertEquals(plataformaEntity.getUrl(), plataforma.getUrl());
		assertEquals(plataformaEntity.getNombre(), plataforma.getNombre());
	}
	
	/**
	 * Prueba para consultar un plataforma que no existe de una pelicula.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetInvalidPlataforma()  {
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaPlataformaService.getPlataforma(pelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para consultar un plataforma de una pelicula que no existe.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPlataformaInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, ()->{
			PlataformaEntity plataformaEntity = plataformaList.get(0);
			peliculaPlataformaService.getPlataforma(0L, plataformaEntity.getId());
		});
	}
	
	/**
	 * Prueba para obtener un plataforma no asociado a una pelicula.
	 *
	 */
	@Test
	void testGetNotAssociatedPlataforma() {
		assertThrows(IllegalOperationException.class, ()->{
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(newPelicula);
			PlataformaEntity plataforma = factory.manufacturePojo(PlataformaEntity.class);
			entityManager.persist(plataforma);
			peliculaPlataformaService.getPlataforma(newPelicula.getId(), plataforma.getId());
		});
	}

	/**
	 * Prueba para actualizar los plataformas de una pelicula.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplacePlataformas() throws EntityNotFoundException {
		List<PlataformaEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PlataformaEntity entity = factory.manufacturePojo(PlataformaEntity.class);
			entityManager.persist(entity);
			pelicula.getPlataformas().add(entity);
			nuevaLista.add(entity);
		}
		peliculaPlataformaService.replacePlataformas(pelicula.getId(), nuevaLista);
		
		List<PlataformaEntity> plataformaEntities = peliculaPlataformaService.getPlataformas(pelicula.getId());
		for (PlataformaEntity aNuevaLista : nuevaLista) {
			assertTrue(plataformaEntities.contains(aNuevaLista));
		}
	}
	
	/**
	 * Prueba para actualizar los plataformas de una pelicula.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceplataformas2() throws EntityNotFoundException {
		List<PlataformaEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PlataformaEntity entity = factory.manufacturePojo(PlataformaEntity.class);
			entityManager.persist(entity);
			nuevaLista.add(entity);
		}
		peliculaPlataformaService.replacePlataformas(pelicula.getId(), nuevaLista);
		
		List<PlataformaEntity> plataformaEntities = peliculaPlataformaService.getPlataformas(pelicula.getId());
		for (PlataformaEntity aNuevaLista : nuevaLista) {
			assertFalse(plataformaEntities.contains(aNuevaLista));
		}
	}
	
	
	/**
	 * Prueba para actualizar los plataforma de una pelicula que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplacePlataformasInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<PlataformaEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				PlataformaEntity entity = factory.manufacturePojo(PlataformaEntity.class);
				entity.getPeliculas().add(pelicula);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			peliculaPlataformaService.replacePlataformas(0L, nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar los plataformas que no existen de una pelicula.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceInvalidPlataformas() {
		assertThrows(EntityNotFoundException.class, ()->{
			List<PlataformaEntity> nuevaLista = new ArrayList<>();
			PlataformaEntity entity = factory.manufacturePojo(PlataformaEntity.class);
			entity.setId(0L);
			nuevaLista.add(entity);
			peliculaPlataformaService.replacePlataformas(pelicula.getId(), nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar un plataforma de una pelicula que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplacePlataformasInvalidPlataforma(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<PlataformaEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				PlataformaEntity entity = factory.manufacturePojo(PlataformaEntity.class);
				entity.getPeliculas().add(pelicula);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			peliculaPlataformaService.replacePlataformas(0L, nuevaLista);
		});
	}

	/**
	 * Prueba desasociar un plataforma con una pelicula.
	 *
	 */
	@Test
	void testRemovePlataforma() throws EntityNotFoundException {
		for (PlataformaEntity plataforma : plataformaList) {
			peliculaPlataformaService.removePlataforma(pelicula.getId(), plataforma.getId());
		}
		assertFalse(peliculaPlataformaService.getPlataformas(pelicula.getId()).isEmpty());
	}
	
	/**
	 * Prueba desasociar un plataforma que no existe con una pelicula.
	 *
	 */
	@Test
	void testRemoveInvalidPlataforma(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaPlataformaService.removePlataforma(pelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba desasociar un plataforma con una pelicula que no existe.
	 *
	 */
	@Test
	void testRemovePlataformaInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaPlataformaService.removePlataforma(0L, plataformaList.get(0).getId());
		});
	}

}
