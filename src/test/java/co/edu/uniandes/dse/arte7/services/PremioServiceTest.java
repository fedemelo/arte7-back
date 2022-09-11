package co.edu.uniandes.dse.arte7.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.PremioEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;

import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(PremioService.class)
class AuthorServiceTest {

	@Autowired
	private PremioService premioService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<PremioEntity> premioList = new ArrayList<>();

	
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PremioEntity").executeUpdate();
	}

	
	private void insertData() {
		for (int i = 0; i < 3; i++) {
			PremioEntity premioEntity = factory.manufacturePojo(PremioEntity.class);
			entityManager.persist(premioEntity);
			premioList.add(premioEntity);
		}

		PremioEntity premioEntity = premioList.get(1);
		PeliculaEntity peliculaEntity = factory.manufacturePojo(PeliculaEntity.class);
		peliculaEntity.getPremios().add(premioEntity);
		entityManager.persist(peliculaEntity);

		premioEntity.getPeliculas().add(peliculaEntity);
	}

	
	@Test
	void testCreatePremio() throws IllegalOperationException {
		PremioEntity newEntity = factory.manufacturePojo(PremioEntity.class);
		
		PremioEntity result = premioService.createPremio(newEntity);
		assertNotNull(result);

		PremioEntity entity = entityManager.find(PremioEntity.class, result.getId());

		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getAnho(), entity.getAnho());
		assertEquals(newEntity.getCategoria(), entity.getCategoria());
		assertEquals(newEntity.getNombre(), entity.getNombre());
	}
	
	@Test
	void testGetPremios() {
		List<PremioEntity> premiosList = premioService.getPremios();
		assertEquals(premioList.size(), premiosList.size());

		for (PremioEntity premioEntity : premiosList) {
			boolean found = false;
			for (PremioEntity storedEntity : premioList) {
				if (premioEntity.getId().equals(storedEntity.getId())) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

	@Test
	void testGetPremio() throws EntityNotFoundException {
		PremioEntity premioEntity = premioList.get(0);

		PremioEntity resultEntity = premioService.getPremio(premioEntity.getId());
		assertNotNull(resultEntity);

		assertEquals(premioEntity.getId(), resultEntity.getId());
		assertEquals(premioEntity.getAnho(), resultEntity.getAnho());
		assertEquals(premioEntity.getCategoria(), resultEntity.getCategoria());
		assertEquals(premioEntity.getNombre(), resultEntity.getNombre());
	}

	@Test
	void testGetInvalidPremio() {
		assertThrows(EntityNotFoundException.class, ()->{
			premioService.getPremio(0L);
		});
	}

	@Test
	void testUpdatePremio() throws EntityNotFoundException {
		PremioEntity premioEntity = premioList.get(0);
		PremioEntity pojoEntity = factory.manufacturePojo(PremioEntity.class);

		pojoEntity.setId(premioEntity.getId());

		premioService.updatePremio(premioEntity.getId(), pojoEntity);

		PremioEntity response = entityManager.find(PremioEntity.class, premioEntity.getId());

		assertEquals(pojoEntity.getId(), response.getId());
		assertEquals(pojoEntity.getAnho(), response.getAnho());
		assertEquals(pojoEntity.getCategoria(), response.getCategoria());
		assertEquals(pojoEntity.getNombre(), response.getNombre());
	}
	
	@Test
	void testUpdateInvalidPremioEntity()  {
		assertThrows(EntityNotFoundException.class, ()->{
			PremioEntity pojoEntity = factory.manufacturePojo(PremioEntity.class);
			premioService.updatePremio(0L, pojoEntity);	
		});
	}

	@Test
	void testDeletePremio() throws EntityNotFoundException, IllegalOperationException {
		PremioEntity premioEntity = premioList.get(0);
		premioService.deletePremio(premioEntity.getId());
		PremioEntity deleted = entityManager.find(PremioEntity.class, premioEntity.getId());
		assertNull(deleted);
	}
	
	@Test
	void testDeleteInvalidPremio() {
		assertThrows(EntityNotFoundException.class, ()->{
			premioService.deletePremio(0L);
		});
	}

	@Test
	void testDeletePremioWithPeliculas() {
		assertThrows(IllegalOperationException.class, () -> {
			premioService.deletePremio(premioList.get(2).getId());
		});
	}

}

