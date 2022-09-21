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
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Pruebas de logica de Resenha
 *
 * @author Mariana Ruiz
 */

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
	
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from ResenhaEntity").executeUpdate();
	}

	
	private void insertData() {
		for (int i = 0; i < 3; i++) {
			PeliculaEntity peliculaEntity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(peliculaEntity);
			peliculaList.add(peliculaEntity);
		}
		for (int i = 0; i < 3; i++) {
			ResenhaEntity resenhaEntity = factory.manufacturePojo(ResenhaEntity.class);
			resenhaEntity.setPelicula(peliculaList.get(0));
			entityManager.persist(resenhaEntity);
			resenhaList.add(resenhaEntity);
		}
	}

	
	@Test
	void testCreateResenha() throws co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException{
		ResenhaEntity newEntity = factory.manufacturePojo(ResenhaEntity.class);
		newEntity.setPelicula(peliculaList.get(0));

		ResenhaEntity result = resenhaService.createResenha(newEntity);
		assertNotNull(result);

		ResenhaEntity entity = entityManager.find(ResenhaEntity.class, result.getId());

		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getEstrellas(), entity.getEstrellas());
		assertEquals(newEntity.getNumCaracteres(), entity.getNumCaracteres());
		assertEquals(newEntity.getTexto(), entity.getTexto());
	}
	
	@Test
	void testGetResenhas() {
		List<ResenhaEntity> resenhasList = resenhaService.getResenhas();
		assertEquals(resenhaList.size(), resenhasList.size());

		for (ResenhaEntity resenhaEntity : resenhasList) {
			boolean found = false;
			for (ResenhaEntity storedEntity : resenhaList) {
				if (resenhaEntity.getId().equals(storedEntity.getId())) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

	@Test
	void testGetResenha() throws EntityNotFoundException {
		ResenhaEntity resenhaEntity = resenhaList.get(0);

		ResenhaEntity resultEntity = resenhaService.getResenha(resenhaEntity.getId());
		assertNotNull(resultEntity);

		assertEquals(resenhaEntity.getId(), resultEntity.getId());
		assertEquals(resenhaEntity.getEstrellas(), resultEntity.getEstrellas());
		assertEquals(resenhaEntity.getNumCaracteres(), resultEntity.getNumCaracteres());
		assertEquals(resenhaEntity.getTexto(), resultEntity.getTexto());
	}

	@Test
	void testGetInvalidResenha() {
		assertThrows(EntityNotFoundException.class, ()->{
			resenhaService.getResenha(0L);
		});
	}

	@Test
	void testUpdateResenha() throws EntityNotFoundException {
		ResenhaEntity entity = resenhaList.get(0);
		ResenhaEntity newEntity = factory.manufacturePojo(ResenhaEntity.class);

		newEntity.setId(entity.getId());

		resenhaService.updateResenha(entity.getId(), newEntity);

		ResenhaEntity response = entityManager.find(ResenhaEntity.class, entity.getId());

		assertEquals(newEntity.getId(), response.getId());
		assertEquals(newEntity.getEstrellas(), response.getEstrellas());
		assertEquals(newEntity.getNumCaracteres(), response.getNumCaracteres());
		assertEquals(newEntity.getTexto(), response.getTexto());
	}
	
	@Test
	void testUpdateInvalidResenha()  {
		assertThrows(EntityNotFoundException.class, ()->{
			ResenhaEntity pojoEntity = factory.manufacturePojo(ResenhaEntity.class);
			resenhaService.updateResenha(0L, pojoEntity);	
		});
	}

	@Test
	void testDeleteResenha() throws EntityNotFoundException, IllegalOperationException {
		ResenhaEntity resenhaEntity = resenhaList.get(0);
		resenhaService.deleteResenha(resenhaEntity.getId());
		ResenhaEntity deleted = entityManager.find(ResenhaEntity.class, resenhaEntity.getId());
		assertNull(deleted);
	}
	
	@Test
	void testDeleteInvalidResenha() {
		assertThrows(EntityNotFoundException.class, ()->{
			resenhaService.deleteResenha(0L);
		});
	}

}