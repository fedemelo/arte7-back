package co.edu.uniandes.dse.arte7.services;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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

import co.edu.uniandes.dse.arte7.entities.ActorEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(ActorService.class)
class ActorServiceTest {

	@Autowired
	private ActorService actorService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<ActorEntity> actorList = new ArrayList<>();

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
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from ActorEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {
		for (int i = 0; i < 3; i++) {
			ActorEntity actorEntity = factory.manufacturePojo(ActorEntity.class);
			entityManager.persist(actorEntity);
			actorList.add(actorEntity);
		}

		ActorEntity actorEntity = actorList.get(2);
		PeliculaEntity peliculaEntity = factory.manufacturePojo(PeliculaEntity.class);
		peliculaEntity.getActores().add(actorEntity);
		entityManager.persist(peliculaEntity);

		actorEntity.getPeliculas().add(peliculaEntity);
	}

	/**
	 * Prueba para crear un Actor.
	 * @throws IllegalOperationException 
	 */
	@Test
	void testCreateActor() throws IllegalOperationException {
		ActorEntity newEntity = factory.manufacturePojo(ActorEntity.class);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date()); 
		calendar.add(Calendar.DATE, -15);
		newEntity.setFechaNacimiento(calendar.getTime());
		ActorEntity result = actorService.createActor(newEntity);
		assertNotNull(result);

		ActorEntity entity = entityManager.find(ActorEntity.class, result.getId());

		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getNombre(), entity.getNombre());
		assertEquals(newEntity.getFechaNacimiento(), entity.getFechaNacimiento());
		assertEquals(newEntity.getBiografia(), entity.getBiografia());
	}
	
	/**
	 * Prueba para crear un Actor con una fecha de nacimiento mayor que la fecha actual.
	 * @throws IllegalOperationException 
	 */
	@Test
	void testCreateActorInvalidFechaNacimiento() {
		assertThrows(IllegalOperationException.class, ()->{
			ActorEntity newEntity = factory.manufacturePojo(ActorEntity.class);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date()); 
			calendar.add(Calendar.DATE, 15);
			newEntity.setFechaNacimiento(calendar.getTime());
			actorService.createActor(newEntity);
		});
	}

	/**
	 * Prueba para consultar la lista de Actores.
	 */
	@Test
	void testGetActores() {
		List<ActorEntity> actorsList = actorService.getActores();
		assertEquals(actorList.size(), actorsList.size());

		for (ActorEntity actorEntity : actorsList) {
			boolean found = false;
			for (ActorEntity storedEntity : actorList) {
				if (actorEntity.getId().equals(storedEntity.getId())) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

	/**
	 * Prueba para consultar un Actor.
	 */
	@Test
	void testGetActor() throws EntityNotFoundException {
		ActorEntity actorEntity = actorList.get(0);

		ActorEntity resultEntity = actorService.getActor(actorEntity.getId());
		assertNotNull(resultEntity);

		assertEquals(actorEntity.getId(), resultEntity.getId());
		assertEquals(actorEntity.getNombre(), resultEntity.getNombre());
		assertEquals(actorEntity.getFechaNacimiento(), resultEntity.getFechaNacimiento());
		assertEquals(actorEntity.getBiografia(), resultEntity.getBiografia());
	}

	/**
	 * Prueba para consultar un Actor que no existe.
	 */
	@Test
	void testGetInvalidActor() {
		assertThrows(EntityNotFoundException.class, ()->{
			actorService.getActor(0L);
		});
	}

	/**
	 * Prueba para actualizar un Actor.
	 */
	@Test
	void testUpdateActor() throws EntityNotFoundException {
		ActorEntity actorEntity = actorList.get(0);
		ActorEntity pojoEntity = factory.manufacturePojo(ActorEntity.class);

		pojoEntity.setId(actorEntity.getId());

		actorService.updateActor(actorEntity.getId(), pojoEntity);

		ActorEntity response = entityManager.find(ActorEntity.class, actorEntity.getId());

		assertEquals(pojoEntity.getId(), response.getId());
		assertEquals(pojoEntity.getNombre(), response.getNombre());
		assertEquals(pojoEntity.getFechaNacimiento(), response.getFechaNacimiento());
		assertEquals(pojoEntity.getBiografia(), response.getBiografia());
	}
	
	/**
	 * Prueba para actualizar un Actor que no existe.
	 */
	@Test
	void testUpdateInvalidActor()  {
		assertThrows(EntityNotFoundException.class, ()->{
			ActorEntity pojoEntity = factory.manufacturePojo(ActorEntity.class);
			actorService.updateActor(0L, pojoEntity);	
		});
	}

	/**
	 * Prueba para eliminar un Actor
	 *
	 */
	@Test
	void testDeleteActor() throws EntityNotFoundException, IllegalOperationException {
		ActorEntity actorEntity = actorList.get(0);
		actorService.deleteActor(actorEntity.getId());
		ActorEntity deleted = entityManager.find(ActorEntity.class, actorEntity.getId());
		assertNull(deleted);
	}
	
	/**
	 * Prueba para eliminar un Actor que no existe
	 *
	 */
	@Test
	void testDeleteInvalidActor() {
		assertThrows(EntityNotFoundException.class, ()->{
			actorService.deleteActor(0L);
		});
	}

	/**
	 * Prueba para eliminar un Actor asociado a un pelicula
	 *
	 */
	@Test
	void testDeleteActorWithPeliculas() {
		assertThrows(IllegalOperationException.class, () -> {
			actorService.deleteActor(actorList.get(2).getId());
		});
	}

	/**
	 * Prueba para eliminar un Actor asociado a un premio
	 *
	 */
	

}