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

import co.edu.uniandes.dse.arte7.entities.DirectorEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Pruebas de logica de Directores
 *
 * @author Federico Melo Barrero
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(DirectorService.class)
public class DirectorServiceTest {

	@Autowired
	private DirectorService directorService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<DirectorEntity> directorList = new ArrayList<>();

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
		entityManager.getEntityManager().createQuery("delete from PrizeEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from DirectorEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {
		for (int i = 0; i < 3; i++) {
			DirectorEntity directorEntity = factory.manufacturePojo(DirectorEntity.class);
			entityManager.persist(directorEntity);
			directorList.add(directorEntity);
		}

		DirectorEntity directorEntity = directorList.get(2);
		PeliculaEntity peliculaEntity = factory.manufacturePojo(PeliculaEntity.class);
		peliculaEntity.getDirectores().add(directorEntity);
		entityManager.persist(peliculaEntity);

		directorEntity.getPeliculas().add(peliculaEntity);
	}

	/**
	 * Prueba para crear un Director.
	 * @throws IllegalOperationException 
	 */
	@Test
	void testCreateDirector() throws IllegalOperationException {
		DirectorEntity newEntity = factory.manufacturePojo(DirectorEntity.class);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date()); 
		calendar.add(Calendar.DATE, -15);
		newEntity.setFechaNacimiento(calendar.getTime());
		DirectorEntity result = directorService.createDirector(newEntity);
		assertNotNull(result);

		DirectorEntity entity = entityManager.find(DirectorEntity.class, result.getId());

		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getNombre(), entity.getNombre());
		assertEquals(newEntity.getFechaNacimiento(), entity.getFechaNacimiento());
		assertEquals(newEntity.getBiografia(), entity.getBiografia());
	}
	
	/**
	 * Prueba para crear un Director con una fecha de nacimiento mayor que la fecha actual.
	 * @throws IllegalOperationException 
	 */
	@Test
	void testCreateDirectorInvalidFechaNacimiento() {
		assertThrows(IllegalOperationException.class, ()->{
			DirectorEntity newEntity = factory.manufacturePojo(DirectorEntity.class);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(new Date()); 
			calendar.add(Calendar.DATE, 15);
			newEntity.setFechaNacimiento(calendar.getTime());
			directorService.createDirector(newEntity);
		});
	}

	/**
	 * Prueba para consultar la lista de Directores.
	 */
	@Test
	void testGetDirectores() {
		List<DirectorEntity> directorsList = directorService.getDirectores();
		assertEquals(directorList.size(), directorsList.size());

		for (DirectorEntity directorEntity : directorsList) {
			boolean found = false;
			for (DirectorEntity storedEntity : directorList) {
				if (directorEntity.getId().equals(storedEntity.getId())) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

	/**
	 * Prueba para consultar un Director.
	 */
	@Test
	void testGetDirector() throws EntityNotFoundException {
		DirectorEntity directorEntity = directorList.get(0);

		DirectorEntity resultEntity = directorService.getDirector(directorEntity.getId());
		assertNotNull(resultEntity);

		assertEquals(directorEntity.getId(), resultEntity.getId());
		assertEquals(directorEntity.getNombre(), resultEntity.getNombre());
		assertEquals(directorEntity.getFechaNacimiento(), resultEntity.getFechaNacimiento());
		assertEquals(directorEntity.getBiografia(), resultEntity.getBiografia());
	}

	/**
	 * Prueba para consultar un Director que no existe.
	 */
	@Test
	void testGetInvalidDirector() {
		assertThrows(EntityNotFoundException.class, ()->{
			directorService.getDirector(0L);
		});
	}

	/**
	 * Prueba para actualizar un Director.
	 */
	@Test
	void testUpdateDirector() throws EntityNotFoundException {
		DirectorEntity directorEntity = directorList.get(0);
		DirectorEntity pojoEntity = factory.manufacturePojo(DirectorEntity.class);

		pojoEntity.setId(directorEntity.getId());

		directorService.updateDirector(directorEntity.getId(), pojoEntity);

		DirectorEntity response = entityManager.find(DirectorEntity.class, directorEntity.getId());

		assertEquals(pojoEntity.getId(), response.getId());
		assertEquals(pojoEntity.getNombre(), response.getNombre());
		assertEquals(pojoEntity.getFechaNacimiento(), response.getFechaNacimiento());
		assertEquals(pojoEntity.getBiografia(), response.getBiografia());
	}
	
	/**
	 * Prueba para actualizar un Director que no existe.
	 */
	@Test
	void testUpdateInvalidDirector()  {
		assertThrows(EntityNotFoundException.class, ()->{
			DirectorEntity pojoEntity = factory.manufacturePojo(DirectorEntity.class);
			directorService.updateDirector(0L, pojoEntity);	
		});
	}

	/**
	 * Prueba para eliminar un Director
	 *
	 */
	@Test
	void testDeleteDirector() throws EntityNotFoundException, IllegalOperationException {
		DirectorEntity directorEntity = directorList.get(0);
		directorService.deleteDirector(directorEntity.getId());
		DirectorEntity deleted = entityManager.find(DirectorEntity.class, directorEntity.getId());
		assertNull(deleted);
	}
	
	/**
	 * Prueba para eliminar un Director que no existe
	 *
	 */
	@Test
	void testDeleteInvalidDirector() {
		assertThrows(EntityNotFoundException.class, ()->{
			directorService.deleteDirector(0L);
		});
	}

	/**
	 * Prueba para eliminar un Director asociado a un libro
	 *
	 */
	@Test
	void testDeleteDirectorWithPeliculas() {
		assertThrows(IllegalOperationException.class, () -> {
			directorService.deleteDirector(directorList.get(2).getId());
		});
	}

	/**
	 * Prueba para eliminar un Director asociado a un premio
	 *
	 */
	@Test
	void testDeleteDirectorWithPrize() {
		assertThrows(IllegalOperationException.class, () -> {
			directorService.deleteDirector(directorList.get(1).getId());
		});
	}

}
