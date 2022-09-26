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

import co.edu.uniandes.dse.arte7.entities.ResenhaEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(PeliculaResenhaService.class)
public class PeliculaResenhaServiceTest {
	
	@Autowired
	private PeliculaResenhaService peliculaResenhaService;
	
	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private PeliculaEntity pelicula = new PeliculaEntity();
	private List<ResenhaEntity> resenhaList = new ArrayList<>();

	
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}
	
	/**
	 * Limpia las tablas que están implicadas en la prueba.
	 */
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from ResenhaEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {

		pelicula = factory.manufacturePojo(PeliculaEntity.class);
		entityManager.persist(pelicula);

		for (int i = 0; i < 3; i++) {
			ResenhaEntity entity = factory.manufacturePojo(ResenhaEntity.class);
			entityManager.persist(entity);
			resenhaList.add(entity);
			entity.setPelicula(pelicula);
		
		}
	}

	/**
	 * Prueba para asociar un Resenha a una pelicula.
	 *
	 */
	@Test
	void testAddResenha() throws EntityNotFoundException, IllegalOperationException {
		PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
		entityManager.persist(newPelicula);
		
		ResenhaEntity resenha = factory.manufacturePojo(ResenhaEntity.class);
		entityManager.persist(resenha);
		
		peliculaResenhaService.addResenha(newPelicula.getId(), resenha.getId());
		
		ResenhaEntity lastResenha = peliculaResenhaService.getResenha(newPelicula.getId(), resenha.getId());
		assertEquals(resenha.getId(), lastResenha.getId());
		assertEquals(resenha.getTexto(), lastResenha.getTexto());
		assertEquals(resenha.getEstrellas(), lastResenha.getEstrellas());
		assertEquals(resenha.getNumCaracteres(), lastResenha.getNumCaracteres());
	}
	
	/**
	 * Prueba para asociar un Resenha que no existe a una pelicula.
	 *
	 */
	@Test
	void testAddInvalidResenha() {
		assertThrows(EntityNotFoundException.class, ()->{
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(newPelicula);
			peliculaResenhaService.addResenha(newPelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para asociar un Resenha a una pelicula que no existe.
	 *
	 */
	@Test
	void testAddResenhaInvalidPelicula() throws EntityNotFoundException, IllegalOperationException {
		assertThrows(EntityNotFoundException.class, ()->{
			ResenhaEntity resenha = factory.manufacturePojo(ResenhaEntity.class);
			entityManager.persist(resenha);
			peliculaResenhaService.addResenha(0L, resenha.getId());
		});
	}

	/**
	 * Prueba para consultar la lista de Resenha de una pelicula.
	 */
	@Test
	void testGetResenhas() throws EntityNotFoundException {
		List<ResenhaEntity> resenhaEntities = peliculaResenhaService.getResenhas(pelicula.getId());

		assertEquals(resenhaList.size(), resenhaEntities.size());

		for (int i = 0; i < resenhaList.size(); i++) {
			assertTrue(resenhaEntities.contains(resenhaList.get(0)));
		}
	}
	
	/**
	 * Prueba para consultar la lista de Resenhas de una pelicula que no existe.
	 */
	@Test
	void testGetResenhasInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaResenhaService.getResenhas(0L);
		});
	}

	/**
	 * Prueba para consultar un Resenha de una pelicula.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetResenha() throws EntityNotFoundException, IllegalOperationException {
		ResenhaEntity resenhaEntity = resenhaList.get(0);
		ResenhaEntity resenha = peliculaResenhaService.getResenha(pelicula.getId(), resenhaEntity.getId());
		assertNotNull(resenha);

		assertEquals(resenhaEntity.getId(), resenha.getId());
		assertEquals(resenhaEntity.getEstrellas(), resenha.getEstrellas());
		assertEquals(resenhaEntity.getNumCaracteres(), resenha.getNumCaracteres());
		assertEquals(resenhaEntity.getTexto(), resenha.getTexto());
	}
	
	/**
	 * Prueba para consultar un resenha que no existe de una pelicula.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetInvalidResenha()  {
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaResenhaService.getResenha(pelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para consultar un resenha de una pelicula que no existe.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetResenhaInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, ()->{
			ResenhaEntity resenhaEntity = resenhaList.get(0);
			peliculaResenhaService.getResenha(0L, resenhaEntity.getId());
		});
	}
	
	/**
	 * Prueba para obtener un resenha no asociado a una pelicula.
	 *
	 */
	@Test
	void testGetNotAssociatedResenha() {
		assertThrows(IllegalOperationException.class, ()->{
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(newPelicula);
			ResenhaEntity resenha = factory.manufacturePojo(ResenhaEntity.class);
			entityManager.persist(resenha);
			peliculaResenhaService.getResenha(newPelicula.getId(), resenha.getId());
		});
	}
	

	/**
	 * Prueba desasociar un resenha con una pelicula.
	 *
	 */
	@Test
	void testRemoveResenha() throws EntityNotFoundException {
		for (ResenhaEntity resenha : resenhaList) {
			peliculaResenhaService.removeResenha(pelicula.getId(), resenha.getId());
		}
		assertTrue(peliculaResenhaService.getResenhas(pelicula.getId()).isEmpty());
	}
	
	/**
	 * Prueba desasociar un resenha que no existe con una pelicula.
	 *
	 */
	@Test
	void testRemoveInvalidResenha(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaResenhaService.removeResenha(pelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba desasociar un resenha con una pelicula que no existe.
	 *
	 */
	@Test
	void testRemoveResenhaInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaResenhaService.removeResenha(0L, resenhaList.get(0).getId());
		});
	}

}
