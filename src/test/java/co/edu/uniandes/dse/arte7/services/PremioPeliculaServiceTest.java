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
@Import({PremioPeliculaService.class,PeliculaService.class})
class PremioPeliculaServiceTest {
	
	@Autowired
	private PremioPeliculaService premioPeliculaService;
	
    @Autowired
	private PeliculaService peliculaService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private PremioEntity premio = new PremioEntity();

    private List<PeliculaEntity> peliculaList = new ArrayList<>();

	

	
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}
	
	
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from PremioEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {
		
		premio = factory.manufacturePojo(PremioEntity.class);
		entityManager.persist(premio);

		for (int i = 0; i < 3; i++) {

			PeliculaEntity entityPelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityPelicula.getPremios().add(premio);
            entityManager.persist(entityPelicula);
			peliculaList.add(entityPelicula);
			premio.getPeliculas().add(entityPelicula);
		}
	}

	/**
	 * Prueba para asociar un premio a una peliculka.
	 *
	 */
	@Test
	void testAddPelicula() throws EntityNotFoundException, IllegalOperationException {
		PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
		
        
		peliculaService.createPelicula(newPelicula);

        PeliculaEntity lastPelicula = premioPeliculaService.addPelicula(premio.getId(), newPelicula.getId());
		assertNotNull(lastPelicula);

		assertEquals(newPelicula.getId(), lastPelicula.getId());
		assertEquals(newPelicula.getDuracionSec(), lastPelicula.getDuracionSec());
		assertEquals(newPelicula.getFechaEstreno(), lastPelicula.getFechaEstreno());
		assertEquals(newPelicula.getNombre(),lastPelicula.getNombre());
        assertEquals(newPelicula.getPais(),lastPelicula.getPais());
        assertEquals(newPelicula.getVisitas(),lastPelicula.getVisitas());
        assertEquals(newPelicula.getUrl(),lastPelicula.getUrl());
        assertEquals(newPelicula.getPoster(),lastPelicula.getPoster());
        assertEquals(newPelicula.getEstrellasPromedio(),lastPelicula.getEstrellasPromedio());
	}
	
	/**
	 * Prueba para asociar un autor que no existe a un libro.
	 *
	 */
	@Test
	void testAddInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, ()->{
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(newPelicula);
			premioPeliculaService.addPelicula(newPelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para asociar un autor a un libro que no existe.
	 *
	 */
	@Test
	void testAddPeliculaInvalidPremio() throws EntityNotFoundException, IllegalOperationException {
		assertThrows(EntityNotFoundException.class, ()->{
			PeliculaEntity pelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(pelicula);
			premioPeliculaService.addPelicula(0L, pelicula.getId());
		});
	}

	/**
	 * Prueba para consultar la lista de autores de un libro.
	 */
	@Test
	void testGetPeliculas() throws EntityNotFoundException {
		List<PeliculaEntity> peliculaEntities = premioPeliculaService.getPeliculas(premio.getId());

		assertEquals(peliculaList.size(), peliculaEntities.size());

		for (int i = 0; i < peliculaList.size(); i++) {
			assertTrue(peliculaEntities.contains(peliculaList.get(0)));
		}
	}
	
	/**
	 * Prueba para consultar la lista de autores de un libro que no existe.
	 */
	@Test
	void testGetPeliuclasInvalidPremio(){
		assertThrows(EntityNotFoundException.class, ()->{
			premioPeliculaService.getPeliculas(0L);
		});
	}

	/**
	 * Prueba para consultar un autor de un libro.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPelicula() throws EntityNotFoundException, IllegalOperationException {
		PeliculaEntity peliculaEntity = peliculaList.get(0);
		PeliculaEntity pelicula = premioPeliculaService.getPelicula(premio.getId(), peliculaEntity.getId());
		assertNotNull(pelicula);

		assertEquals(peliculaEntity.getId(), pelicula.getId());
		assertEquals(peliculaEntity.getDuracionSec(), pelicula.getDuracionSec());
		assertEquals(peliculaEntity.getFechaEstreno(), pelicula.getFechaEstreno());
		assertEquals(peliculaEntity.getNombre(),pelicula.getNombre());
        assertEquals(peliculaEntity.getPais(),pelicula.getPais());
        assertEquals(peliculaEntity.getVisitas(),pelicula.getVisitas());
        assertEquals(peliculaEntity.getUrl(),pelicula.getUrl());
        assertEquals(peliculaEntity.getPoster(),pelicula.getPoster());
        assertEquals(peliculaEntity.getEstrellasPromedio(),pelicula.getEstrellasPromedio());
	}
	
	/**
	 * Prueba para consultar un autor que no existe de un libro.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetInvalidPremio()  {
		assertThrows(EntityNotFoundException.class, ()->{
			premioPeliculaService.getPelicula(premio.getId(), 0L);
		});
	}
	
	@Test
	void testGetPeliculaInvalidPremio() {
		assertThrows(EntityNotFoundException.class, () -> {
			PeliculaEntity peliculaEntity = peliculaList.get(0);
			premioPeliculaService.getPelicula(0L, peliculaEntity.getId());
		});
	}

    @Test
	void testGetInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, () -> {
			premioPeliculaService.getPelicula(premio.getId(), 0L);
		});
	}

    @Test
	void testGetPeliculaNotAssociatedPremio() {
		assertThrows(IllegalOperationException.class, () -> {
			PremioEntity premioEntity = factory.manufacturePojo(PremioEntity.class);
			entityManager.persist(premioEntity);

			PeliculaEntity peliculaEntity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(peliculaEntity);

			premioPeliculaService.getPelicula(premioEntity.getId(), peliculaEntity.getId());
		});
	}

    @Test
	void testReplacePeliculas() throws EntityNotFoundException, IllegalOperationException {
		List<PeliculaEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(entity);
            premio.getPeliculas().add(entity);
			nuevaLista.add(entity);
		}
		premioPeliculaService.addPeliculas(premio.getId(), nuevaLista);
		List<PeliculaEntity> peliculaEntities = premioPeliculaService.getPeliculas(premio.getId());
		for (PeliculaEntity aNuevaLista : nuevaLista) {
			assertTrue(peliculaEntities.contains(aNuevaLista));
		}
	}

    @Test
	void testReplacePeliculasInvalidPremio() {
		assertThrows(EntityNotFoundException.class, () -> {
			List<PeliculaEntity> nuevaLista = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
                entityManager.persist(entity);
                premio.getPeliculas().add(entity);
                nuevaLista.add(entity);
            }
			premioPeliculaService.addPeliculas(0L, nuevaLista);
		});
    }
    
        @Test
        void testReplaceInvalidPeliculas() {
            assertThrows(EntityNotFoundException.class, () -> {
                List<PeliculaEntity> nuevaLista = new ArrayList<>();
                PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
                entity.getPremios().add(premio);
                entity.setId(0L);
                nuevaLista.add(entity);
                premioPeliculaService.addPeliculas(premio.getId(), nuevaLista);
            });
        }  

        @Test
	    void testRemovePelicula() throws EntityNotFoundException {
		for (PeliculaEntity pelicula : peliculaList) {
			premioPeliculaService.removePelicula(premio.getId(), pelicula.getId());
		}
		assertTrue(premioPeliculaService.getPeliculas(premio.getId()).isEmpty());
	}

    @Test
	void testRemovePeliculaInvalidPremio() {
		assertThrows(EntityNotFoundException.class, () -> {
			for (PeliculaEntity pelicula : peliculaList) {
				premioPeliculaService.removePelicula(0L, pelicula.getId());
			}
		});
	}

    @Test
	void testRemoveInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, () -> {
			premioPeliculaService.removePelicula(premio.getId(), 0L);
		});
	}



        
}






