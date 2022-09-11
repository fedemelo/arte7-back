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

import co.edu.uniandes.dse.arte7.entities.NominacionEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;

import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;


@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import({NominacionPeliculaService.class,PeliculaService.class})
class NominacionPeliculaServiceTest {
	
	@Autowired
	private NominacionPeliculaService nominacionPeliculaService;
	
    @Autowired
	private PeliculaService peliculaService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private NominacionEntity nominacion = new NominacionEntity();

    private List<PeliculaEntity> peliculaList = new ArrayList<>();

	

	
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}
	
	
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
        entityManager.getEntityManager().createQuery("delete from NominacionEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {
		
		nominacion = factory.manufacturePojo(NominacionEntity.class);
		entityManager.persist(nominacion);

		for (int i = 0; i < 3; i++) {

			PeliculaEntity entityPelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityPelicula.getNominaciones().add(nominacion);
            entityManager.persist(entityPelicula);
			peliculaList.add(entityPelicula);
			nominacion.getPeliculas().add(entityPelicula);
		}
	}

	/**
	 * Prueba para asociar un Nominacion a una peliculka.
	 *
	 */
	@Test
	void testAddPelicula() throws EntityNotFoundException, IllegalOperationException {
		NominacionEntity nominacion = factory.manufacturePojo(NominacionEntity.class);
		entityManager.persist(nominacion);
        
        PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
		entityManager.persist(newPelicula);
		
		nominacionPeliculaService.addPelicula(newPelicula.getId(), nominacion.getId());
		
		PeliculaEntity lastPelicula = nominacionPeliculaService.getPelicula(nominacion.getId(), newPelicula.getId());
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
			nominacionPeliculaService.addPelicula(newPelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para asociar un autor a un libro que no existe.
	 *
	 */
	@Test
	void testAddPeliculaInvalidNominacion() throws EntityNotFoundException, IllegalOperationException {
		assertThrows(EntityNotFoundException.class, ()->{
			PeliculaEntity pelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(pelicula);
			nominacionPeliculaService.addPelicula(0L, pelicula.getId());
		});
	}

	/**
	 * Prueba para consultar la lista de autores de un libro.
	 */
	@Test
	void testGetPeliculas() throws EntityNotFoundException {
		List<PeliculaEntity> peliculaEntities = nominacionPeliculaService.getPeliculas(nominacion.getId());

		assertEquals(peliculaList.size(), peliculaEntities.size());

		for (int i = 0; i < peliculaList.size(); i++) {
			assertTrue(peliculaEntities.contains(peliculaList.get(0)));
		}
	}
	
	/**
	 * Prueba para consultar la lista de autores de un libro que no existe.
	 */
	@Test
	void testGetPeliuclasInvalidNominacion(){
		assertThrows(EntityNotFoundException.class, ()->{
			nominacionPeliculaService.getPeliculas(0L);
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
		PeliculaEntity pelicula = nominacionPeliculaService.getPelicula(nominacion.getId(), peliculaEntity.getId());
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
	void testGetInvalidNominacion()  {
		assertThrows(EntityNotFoundException.class, ()->{
			nominacionPeliculaService.getPelicula(nominacion.getId(), 0L);
		});
	}
	
	@Test
	void testGetPeliculaInvalidNominacion() {
		assertThrows(EntityNotFoundException.class, () -> {
			PeliculaEntity peliculaEntity = peliculaList.get(0);
			nominacionPeliculaService.getPelicula(0L, peliculaEntity.getId());
		});
	}

    @Test
	void testGetInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, () -> {
			nominacionPeliculaService.getPelicula(nominacion.getId(), 0L);
		});
	}

    @Test
	void testGetPeliculaNotAssociatedNominacion() {
		assertThrows(IllegalOperationException.class, () -> {
			NominacionEntity nominacionEntity = factory.manufacturePojo(NominacionEntity.class);
			entityManager.persist(nominacionEntity);

			PeliculaEntity peliculaEntity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(peliculaEntity);

			nominacionPeliculaService.getPelicula(nominacionEntity.getId(), peliculaEntity.getId());
		});
	}

    @Test
	void testReplacePeliculas() throws EntityNotFoundException, IllegalOperationException {
		List<PeliculaEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(entity);
            nominacion.getPeliculas().add(entity);
			nuevaLista.add(entity);
		}
		nominacionPeliculaService.addPeliculas(nominacion.getId(), nuevaLista);
		List<PeliculaEntity> peliculaEntities = nominacionPeliculaService.getPeliculas(nominacion.getId());
		for (PeliculaEntity aNuevaLista : nuevaLista) {
			assertTrue(peliculaEntities.contains(aNuevaLista));
		}
	}

    @Test
	void testReplacePeliculasInvalidNominacion() {
		assertThrows(EntityNotFoundException.class, () -> {
			List<PeliculaEntity> nuevaLista = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
                entityManager.persist(entity);
                nominacion.getPeliculas().add(entity);
                nuevaLista.add(entity);
            }
			nominacionPeliculaService.addPeliculas(0L, nuevaLista);
		});
    }
    
        @Test
        void testReplaceInvalidPeliculas() {
            assertThrows(EntityNotFoundException.class, () -> {
                List<PeliculaEntity> nuevaLista = new ArrayList<>();
                PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
                entity.getNominaciones().add(nominacion);
                entity.setId(0L);
                nuevaLista.add(entity);
                nominacionPeliculaService.addPeliculas(nominacion.getId(), nuevaLista);
            });
        }  

        @Test
	    void testRemovePelicula() throws EntityNotFoundException {
		for (PeliculaEntity pelicula : peliculaList) {
			nominacionPeliculaService.removePelicula(nominacion.getId(), pelicula.getId());
		}
		assertTrue(nominacionPeliculaService.getPeliculas(nominacion.getId()).isEmpty());
	}

    @Test
	void testRemovePeliculaInvalidNominacion() {
		assertThrows(EntityNotFoundException.class, () -> {
			for (PeliculaEntity pelicula : peliculaList) {
				nominacionPeliculaService.removePelicula(0L, pelicula.getId());
			}
		});
	}

    @Test
	void testRemoveInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, () -> {
			nominacionPeliculaService.removePelicula(nominacion.getId(), 0L);
		});
	}



        
}
