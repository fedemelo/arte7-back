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
import co.edu.uniandes.dse.arte7.entities.PlataformaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

//Finalizado
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import({PlataformaService.class, PlataformaPeliculaService.class})
public class PlataformaServiceTest {

	@Autowired
	private PlataformaService plataformaService;

    @Autowired
    private PlataformaPeliculaService plataformapeliculaService;

	@Autowired
	private TestEntityManager entityManager;


	private PodamFactory factory = new PodamFactoryImpl();

	private List<PlataformaEntity> plataformaList = new ArrayList<>();

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
		entityManager.getEntityManager().createQuery("delete from PlataformaEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {


		for (int i = 0; i < 3; i++) {
			PlataformaEntity plataformaEntity = factory.manufacturePojo(PlataformaEntity.class);
			entityManager.persist(plataformaEntity);
			plataformaList.add(plataformaEntity);
		}

		for (int i = 0; i < 3; i++) {
			PeliculaEntity peliculaEntity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(peliculaEntity);
			peliculaList.add(peliculaEntity);
		}



	}

	/**
	 * Prueba para crear una Plataforma.
	 * @throws IllegalOperationException 
	 */
	@Test
	void testCreatePlataforma() throws IllegalOperationException {
		PlataformaEntity newEntity = factory.manufacturePojo(PlataformaEntity.class);
		
		
		PlataformaEntity result = plataformaService.createPlataforma(newEntity);
		assertNotNull(result);

		PlataformaEntity entity = entityManager.find(PlataformaEntity.class, result.getId());

		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getNombre(), entity.getNombre());

	}
	
	/**
	 * Prueba para crear plataforma con nombre null
	 * @throws IllegalOperationException 
	 */
	@Test
    void testCreatePeliculaNullNombre() {
        assertThrows(IllegalOperationException.class, () -> {
                PlataformaEntity newEntity = factory.manufacturePojo(PlataformaEntity.class);
                newEntity.setNombre(null);
                plataformaService.createPlataforma(newEntity);
        });
    }


	/**
	 * Prueba para consultar la lista de Plataformas.
	 */
	@Test
	void testgetPlataformas() {
		List<PlataformaEntity> PplataformasList = plataformaService.getPlataformas();
		assertEquals(plataformaList.size(), PplataformasList.size());

		for (PlataformaEntity PlataformaEntity : PplataformasList) {
			boolean found = false;
			for (PlataformaEntity storedEntity : plataformaList) {
				if (PlataformaEntity.getId().equals(storedEntity.getId())) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

	/**
	 * Prueba para consultar una Plataforma.
	 */
	@Test
	void testgetPlataforma() throws EntityNotFoundException {
		PlataformaEntity plataformaEntity = plataformaList.get(0);

		PlataformaEntity resultEntity = plataformaService.getPlataforma(plataformaEntity.getId());
		assertNotNull(resultEntity);

		assertEquals(plataformaEntity.getId(), resultEntity.getId());
		assertEquals(plataformaEntity.getNombre(), resultEntity.getNombre());

	}

	/**
	 * Prueba para consultar una plataforma que no existe.
	 */
	@Test
	void testGetInvalidPlataforma() {
		assertThrows(EntityNotFoundException.class, ()->{
			plataformaService.getPlataforma(0L);
		});
	}

	/**
	 * Prueba para actualizar una Plataforma.
	 */
	@Test
	void testUpdatePlataforma() throws EntityNotFoundException {
		PlataformaEntity plataformaEntity = plataformaList.get(0);
		PlataformaEntity pojoEntity = factory.manufacturePojo(PlataformaEntity.class);

		pojoEntity.setId(plataformaEntity.getId());

		plataformaService.updatePlataforma(plataformaEntity.getId(), pojoEntity);

		PlataformaEntity response = entityManager.find(PlataformaEntity.class, plataformaEntity.getId());

		assertEquals(pojoEntity.getId(), response.getId());
		assertEquals(pojoEntity.getNombre(), response.getNombre());
	}
	
	/**
	 * Prueba para actualizar una plataforma que no existe.
	 */
	@Test
	void testUpdateInvalidPlataforma()  {
		assertThrows(EntityNotFoundException.class, ()->{
			PlataformaEntity pojoEntity = factory.manufacturePojo(PlataformaEntity.class);
			plataformaService.updatePlataforma(0L, pojoEntity);	
		});
	}

	/**
	 * Prueba para eliminar un Plataforma
	 *
	 */
	@Test
	void testDeletePlataforma() throws EntityNotFoundException, IllegalOperationException {
		PlataformaEntity plataformaEntity = plataformaList.get(0);

        //elminar plataformas
        List <PeliculaEntity> peliculaList= plataformaEntity.getPeliculas();
        for (PeliculaEntity storedEntity : peliculaList) {
            plataformapeliculaService.removePelicula(plataformaEntity.getId(), storedEntity.getId());
            
        }

		plataformaService.deletePlataforma(plataformaEntity.getId());

		PlataformaEntity deleted = entityManager.find(PlataformaEntity.class, plataformaEntity.getId());
		assertNull(deleted);
	}

	/**
	 * Prueba para eliminar una pelicula no existente
	 *
	 */
	@Test
	void testDeletePlataformaWithAsociations() {
		assertThrows(IllegalOperationException.class, ()->{
			PlataformaEntity plataformaEntity = plataformaList.get(0);
			plataformaEntity.setPeliculas(peliculaList);
			plataformaService.deletePlataforma(plataformaEntity.getId());
		});
	}
	
	/**
	 * Prueba para eliminar una pelicula no existente
	 *
	 */
	@Test
	void testDeleteInvalidPlataforma() {
		assertThrows(EntityNotFoundException.class, ()->{
			plataformaService.deletePlataforma(0l);
		});
	}

}
