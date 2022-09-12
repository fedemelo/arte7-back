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

import co.edu.uniandes.dse.arte7.entities.PremioEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(PeliculaPremioService.class)
public class PeliculaPremioServiceTest {
	
	@Autowired
	private PeliculaPremioService peliculaPremioService;
	
	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private PeliculaEntity pelicula = new PeliculaEntity();
	private List<PremioEntity> premioList = new ArrayList<>();

	
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}
	
	/**
	 * Limpia las tablas que están implicadas en la prueba.
	 */
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from PremioEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {

		pelicula = factory.manufacturePojo(PeliculaEntity.class);
		entityManager.persist(pelicula);

		for (int i = 0; i < 3; i++) {
			PremioEntity entity = factory.manufacturePojo(PremioEntity.class);
			entityManager.persist(entity);
			entity.getPeliculas().add(pelicula);
			premioList.add(entity);
			pelicula.getPremios().add(entity);	
		}
	}

	/**
	 * Prueba para asociar un Premio a una pelicula.
	 *
	 */
	@Test
	void testAddPremio() throws EntityNotFoundException, IllegalOperationException {
		PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
		entityManager.persist(newPelicula);
		
		PremioEntity premio = factory.manufacturePojo(PremioEntity.class);
		entityManager.persist(premio);
		
		peliculaPremioService.addPremio(newPelicula.getId(), premio.getId());
		
		PremioEntity lastPremio = peliculaPremioService.getPremio(newPelicula.getId(), premio.getId());
		assertEquals(premio.getId(), lastPremio.getId());
		assertEquals(premio.getNombre(), lastPremio.getNombre());
		assertEquals(premio.getAnho(), lastPremio.getAnho());
		assertEquals(premio.getCategoria(), lastPremio.getCategoria());
	}
	
	/**
	 * Prueba para asociar un Premio que no existe a una pelicula.
	 *
	 */
	@Test
	void testAddInvalidPremio() {
		assertThrows(EntityNotFoundException.class, ()->{
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(newPelicula);
			peliculaPremioService.addPremio(newPelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para asociar un Premio a una pelicula que no existe.
	 *
	 */
	@Test
	void testAddPremioInvalidPelicula() throws EntityNotFoundException, IllegalOperationException {
		assertThrows(EntityNotFoundException.class, ()->{
			PremioEntity premio = factory.manufacturePojo(PremioEntity.class);
			entityManager.persist(premio);
			peliculaPremioService.addPremio(0L, premio.getId());
		});
	}

	/**
	 * Prueba para consultar la lista de Premio de una pelicula.
	 */
	@Test
	void testGetPremios() throws EntityNotFoundException {
		List<PremioEntity> premioEntities = peliculaPremioService.getPremios(pelicula.getId());

		assertEquals(premioList.size(), premioEntities.size());

		for (int i = 0; i < premioList.size(); i++) {
			assertTrue(premioEntities.contains(premioList.get(0)));
		}
	}
	
	/**
	 * Prueba para consultar la lista de Premios de una pelicula que no existe.
	 */
	@Test
	void testGetPremiosInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaPremioService.getPremios(0L);
		});
	}

	/**
	 * Prueba para consultar un Premio de una pelicula.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPremio() throws EntityNotFoundException, IllegalOperationException {
		PremioEntity premioEntity = premioList.get(0);
		PremioEntity premio = peliculaPremioService.getPremio(pelicula.getId(), premioEntity.getId());
		assertNotNull(premio);

		assertEquals(premioEntity.getId(), premio.getId());
		assertEquals(premioEntity.getAnho(), premio.getAnho());
		assertEquals(premioEntity.getCategoria(), premio.getCategoria());
		assertEquals(premioEntity.getNombre(), premio.getNombre());
	}
	
	/**
	 * Prueba para consultar un premio que no existe de una pelicula.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetInvalidPremio()  {
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaPremioService.getPremio(pelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para consultar un premio de una pelicula que no existe.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPremioInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, ()->{
			PremioEntity premioEntity = premioList.get(0);
			peliculaPremioService.getPremio(0L, premioEntity.getId());
		});
	}
	
	/**
	 * Prueba para obtener un premio no asociado a una pelicula.
	 *
	 */
	@Test
	void testGetNotAssociatedPremio() {
		assertThrows(IllegalOperationException.class, ()->{
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(newPelicula);
			PremioEntity premio = factory.manufacturePojo(PremioEntity.class);
			entityManager.persist(premio);
			peliculaPremioService.getPremio(newPelicula.getId(), premio.getId());
		});
	}

	/**
	 * Prueba para actualizar los premios de una pelicula.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplacePremios() throws EntityNotFoundException {
		List<PremioEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PremioEntity entity = factory.manufacturePojo(PremioEntity.class);
			entityManager.persist(entity);
			pelicula.getPremios().add(entity);
			nuevaLista.add(entity);
		}
		peliculaPremioService.replacePremios(pelicula.getId(), nuevaLista);
		
		List<PremioEntity> premioEntities = peliculaPremioService.getPremios(pelicula.getId());
		for (PremioEntity aNuevaLista : nuevaLista) {
			assertTrue(premioEntities.contains(aNuevaLista));
		}
	}
	
	/**
	 * Prueba para actualizar los premios de una pelicula.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplacepremios2() throws EntityNotFoundException {
		List<PremioEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PremioEntity entity = factory.manufacturePojo(PremioEntity.class);
			entityManager.persist(entity);
			nuevaLista.add(entity);
		}
		peliculaPremioService.replacePremios(pelicula.getId(), nuevaLista);
		
		List<PremioEntity> premioEntities = peliculaPremioService.getPremios(pelicula.getId());
		for (PremioEntity aNuevaLista : nuevaLista) {
			assertTrue(premioEntities.contains(aNuevaLista));
		}
	}
	
	
	/**
	 * Prueba para actualizar los premio de una pelicula que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplacePremiosInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<PremioEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				PremioEntity entity = factory.manufacturePojo(PremioEntity.class);
				entity.getPeliculas().add(pelicula);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			peliculaPremioService.replacePremios(0L, nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar los premios que no existen de una pelicula.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceInvalidPremios() {
		assertThrows(EntityNotFoundException.class, ()->{
			List<PremioEntity> nuevaLista = new ArrayList<>();
			PremioEntity entity = factory.manufacturePojo(PremioEntity.class);
			entity.setId(0L);
			nuevaLista.add(entity);
			peliculaPremioService.replacePremios(pelicula.getId(), nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar un premio de una pelicula que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplacePremiosInvalidPremio(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<PremioEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				PremioEntity entity = factory.manufacturePojo(PremioEntity.class);
				entity.getPeliculas().add(pelicula);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			peliculaPremioService.replacePremios(0L, nuevaLista);
		});
	}

	/**
	 * Prueba desasociar un premio con una pelicula.
	 *
	 */
	@Test
	void testRemovePremio() throws EntityNotFoundException {
		for (PremioEntity premio : premioList) {
			peliculaPremioService.removePremio(pelicula.getId(), premio.getId());
		}
		assertTrue(peliculaPremioService.getPremios(pelicula.getId()).isEmpty());
	}
	
	/**
	 * Prueba desasociar un premio que no existe con una pelicula.
	 *
	 */
	@Test
	void testRemoveInvalidPremio(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaPremioService.removePremio(pelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba desasociar un premio con una pelicula que no existe.
	 *
	 */
	@Test
	void testRemovePremioInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaPremioService.removePremio(0L, premioList.get(0).getId());
		});
	}

}
