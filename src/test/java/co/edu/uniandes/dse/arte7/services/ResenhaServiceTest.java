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

import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.entities.ResenhaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.services.ResenhaService;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(ResenhaService.class)
class ResenhaServiceTest {

    @Autowired
	private ResenhaService resenhaService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<ResenhaEntity> resenhaList = new ArrayList<>();
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
		entityManager.getEntityManager().createQuery("delete from ResenhaEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {

		for (int i = 0; i < 3; i++) {
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(entity);
			peliculaList.add(entity);
		}

		for (int i = 0; i < 3; i++) {
			ResenhaEntity entity = factory.manufacturePojo(ResenhaEntity.class);
			entity.setPelicula(peliculaList.get(0));
			peliculaList.get(0).getResenhas().add(entity);
			entityManager.persist(entity);
			resenhaList.add(entity);
		}
	}

	/**
	 * Prueba para crear un Resenha.
	 */
	@Test
	void testCreateResenha() throws EntityNotFoundException {
		ResenhaEntity newEntity = factory.manufacturePojo(ResenhaEntity.class);
		newEntity.setPelicula(peliculaList.get(1));
		ResenhaEntity result = resenhaService.createResenha(resenhaList.get(1).getId(), newEntity);
		assertNotNull(result);
		ResenhaEntity entity = entityManager.find(ResenhaEntity.class, result.getId());
		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getEstrellas(), entity.getEstrellas());
		assertEquals(newEntity.getNumCaracteres(), entity.getNumCaracteres());
		assertEquals(newEntity.getTexto(), entity.getTexto());
	}

	/**
	 * Prueba para crear un Resenha con un Pelicula que no existe.
	 */
	@Test
	void testCreateResenhaInvalidPelicula() throws EntityNotFoundException {
		assertThrows(EntityNotFoundException.class, () -> {
			ResenhaEntity newEntity = factory.manufacturePojo(ResenhaEntity.class);
			resenhaService.createResenha(0L, newEntity);
		});
	}

	/**
	 * Prueba para consultar la lista de Resenhas.
	 */
	@Test
	void testGetResenhas() throws EntityNotFoundException {
		List<ResenhaEntity> list = resenhaService.getResenhas(peliculaList.get(0).getId());
		assertEquals(resenhaList.size(), list.size());
		for (ResenhaEntity entity : list) {
			boolean found = false;
			for (ResenhaEntity storedEntity : resenhaList) {
				if (entity.getId().equals(storedEntity.getId())) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

	/**
	 * Prueba para consultar la lista de Resenhas de un Pelicula que no existe.
	 */
	@Test
	void testGetResenhasInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, () -> {
			resenhaService.getResenhas(0L);
		});
	}

	/**
	 * Prueba para consultar un Resenha.
	 */
	@Test
	void testGetResenha() throws EntityNotFoundException {
		ResenhaEntity entity = resenhaList.get(0);
		ResenhaEntity resultEntity = resenhaService.getResenha(peliculaList.get(0).getId(), entity.getId());
		assertNotNull(resultEntity);
		assertEquals(entity.getId(), resultEntity.getId());
		assertEquals(entity.getEstrellas(), resultEntity.getEstrellas());
		assertEquals(entity.getNumCaracteres(), resultEntity.getNumCaracteres());
		assertEquals(entity.getTexto(), resultEntity.getTexto());
	}

	/**
	 * Prueba para consultar un Resenha de un Pelicula que no existe.
	 */
	@Test
	void testGetResenhaInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, () -> {
			ResenhaEntity entity = resenhaList.get(0);
			resenhaService.getResenha(0L, entity.getId());
		});
	}

	/**
	 * Prueba para consultar un Resenha que no existe de un Pelicula.
	 */
	@Test
	void testGetInvalidResenha() {
		assertThrows(EntityNotFoundException.class, () -> {
			resenhaService.getResenha(peliculaList.get(0).getId(), 0L);
		});
	}

	/**
	 * Prueba para actualizar un Resenha.
	 */
	@Test
	void testUpdateResenha() throws EntityNotFoundException {
		ResenhaEntity entity = resenhaList.get(0);
		ResenhaEntity pojoEntity = factory.manufacturePojo(ResenhaEntity.class);

		pojoEntity.setId(entity.getId());

		resenhaService.updateResenha(peliculaList.get(1).getId(), entity.getId(), pojoEntity);

		ResenhaEntity resp = entityManager.find(ResenhaEntity.class, entity.getId());

		assertEquals(pojoEntity.getId(), resp.getId());
		assertEquals(pojoEntity.getEstrellas(), resp.getEstrellas());
		assertEquals(pojoEntity.getNumCaracteres(), resp.getNumCaracteres());
		assertEquals(pojoEntity.getTexto(), resp.getTexto());
	}

	/**
	 * Prueba para actualizar un Resenha de un Pelicula que no existe.
	 */
	@Test
	void testUpdateResenhaInvalidPelicula() throws EntityNotFoundException {
		assertThrows(EntityNotFoundException.class, ()->{
			ResenhaEntity entity = resenhaList.get(0);
			ResenhaEntity pojoEntity = factory.manufacturePojo(ResenhaEntity.class);
			pojoEntity.setId(entity.getId());
			resenhaService.updateResenha(0L, entity.getId(), pojoEntity);
		});
		
	}
	
	/**
	 * Prueba para actualizar un Resenha que no existe de un Pelicula.
	 */
	@Test
	void testUpdateInvalidResenha(){
		assertThrows(EntityNotFoundException.class, ()->{
			ResenhaEntity pojoEntity = factory.manufacturePojo(ResenhaEntity.class);
			resenhaService.updateResenha(peliculaList.get(1).getId(), 0L, pojoEntity);
		});
	}

	/**
     * Prueba para eliminar un Resenha.
     */
	@Test
	void testDeleteResenha() throws EntityNotFoundException {
		ResenhaEntity entity = resenhaList.get(0);
		resenhaService.deleteResenha(peliculaList.get(0).getId(), entity.getId());
		ResenhaEntity deleted = entityManager.find(ResenhaEntity.class, entity.getId());
		assertNull(deleted);
	}
	
	/**
     * Prueba para eliminar un Resenham de un Pelicula que no existe.
     */
	@Test
	void testDeleteResenhaInvalidPelicula()  {
		assertThrows(EntityNotFoundException.class, ()->{
			ResenhaEntity entity = resenhaList.get(0);
			resenhaService.deleteResenha(0L, entity.getId());
		});
	}

	 /**
     * Prueba para eliminarle un resenha a un pelicula del cual no pertenece.
     */
	@Test
	void testDeleteResenhaWithNoAssociatedPelicula() {
		assertThrows(EntityNotFoundException.class, () -> {
			ResenhaEntity entity = resenhaList.get(0);
			resenhaService.deleteResenha(peliculaList.get(1).getId(), entity.getId());
		});
	}

    

}
