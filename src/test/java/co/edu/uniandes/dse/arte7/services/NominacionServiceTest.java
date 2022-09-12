package co.edu.uniandes.dse.arte7.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityNotFoundException;

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
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(NominacionService.class)
class NominacionServiceTest {

	@Autowired
	private NominacionService nominacionService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<NominacionEntity> nominacionList = new ArrayList<>();

	
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from NominacionEntity").executeUpdate();
	}

	
	private void insertData() {
		for (int i = 0; i < 3; i++) {
			NominacionEntity nominacionEntity = factory.manufacturePojo(NominacionEntity.class);
			entityManager.persist(nominacionEntity);
			nominacionList.add(nominacionEntity);
		}

		NominacionEntity nominacionEntity = nominacionList.get(2);
		PeliculaEntity peliculaEntity = factory.manufacturePojo(PeliculaEntity.class);
		peliculaEntity.getNominaciones().add(nominacionEntity);
		entityManager.persist(peliculaEntity);

		nominacionEntity.getPeliculas().add(peliculaEntity);
	}

	
	@Test
	void testCreateNominacion() throws IllegalOperationException {
		NominacionEntity newEntity = factory.manufacturePojo(NominacionEntity.class);
		
		NominacionEntity result = nominacionService.createNominacion(newEntity);
		assertNotNull(result);

		NominacionEntity entity = entityManager.find(NominacionEntity.class, result.getId());

		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getAnho(), entity.getAnho());
		assertEquals(newEntity.getCategoria(), entity.getCategoria());
		assertEquals(newEntity.getNombre(), entity.getNombre());
	}
	
	@Test
	void testGetNominaciones() {
		List<NominacionEntity> nominacionesList = nominacionService.getNominaciones();
		assertEquals(nominacionList.size(), nominacionesList.size());

		for (NominacionEntity nominacionEntity : nominacionesList) {
			boolean found = false;
			for (NominacionEntity storedEntity : nominacionList) {
				if (nominacionEntity.getId().equals(storedEntity.getId())) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

	@Test
	void testGetNominacion() throws EntityNotFoundException {
		NominacionEntity nominacionEntity = nominacionList.get(0);

		NominacionEntity resultEntity = nominacionService.getNominacion(nominacionEntity.getId());
		assertNotNull(resultEntity);

		assertEquals(nominacionEntity.getId(), resultEntity.getId());
		assertEquals(nominacionEntity.getAnho(), resultEntity.getAnho());
		assertEquals(nominacionEntity.getCategoria(), resultEntity.getCategoria());
		assertEquals(nominacionEntity.getNombre(), resultEntity.getNombre());
	}

	@Test
	void testGetInvalidNominacion() {
		assertThrows(EntityNotFoundException.class, ()->{
			nominacionService.getNominacion(0L);
		});
	}

	@Test
	void testUpdateNominacion() throws EntityNotFoundException {
		NominacionEntity nominacionEntity = nominacionList.get(0);
		NominacionEntity pojoEntity = factory.manufacturePojo(NominacionEntity.class);

		pojoEntity.setId(nominacionEntity.getId());

		nominacionService.updateNominacion(nominacionEntity.getId(), pojoEntity);

		NominacionEntity response = entityManager.find(NominacionEntity.class, nominacionEntity.getId());

		assertEquals(pojoEntity.getId(), response.getId());
		assertEquals(pojoEntity.getAnho(), response.getAnho());
		assertEquals(pojoEntity.getCategoria(), response.getCategoria());
		assertEquals(pojoEntity.getNombre(), response.getNombre());
	}
	
	@Test
	void testUpdateInvalidNominacionEntity()  {
		assertThrows(EntityNotFoundException.class, ()->{
			NominacionEntity pojoEntity = factory.manufacturePojo(NominacionEntity.class);
			nominacionService.updateNominacion(0L, pojoEntity);	
		});
	}

	@Test
	void testDeleteNominacion() throws EntityNotFoundException, IllegalOperationException {
		NominacionEntity nominacionEntity = nominacionList.get(0);
		nominacionService.deleteNominacion(nominacionEntity.getId());
		NominacionEntity deleted = entityManager.find(NominacionEntity.class, nominacionEntity.getId());
		assertNull(deleted);
	}
	
	@Test
	void testDeleteInvalidNominacion() {
		assertThrows(EntityNotFoundException.class, ()->{
			nominacionService.deleteNominacion(0L);
		});
	}

	@Test
	void testDeleteNominacionWithPeliculas() {
		assertThrows(IllegalOperationException.class, () -> {
			nominacionService.deleteNominacion(nominacionList.get(2).getId());
		});
	}
}
