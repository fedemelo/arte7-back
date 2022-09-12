package co.edu.uniandes.dse.arte7.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

import co.edu.uniandes.dse.arte7.entities.GeneroEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(GeneroService.class)

public class PeliculaGeneroServiceTest {
    
    @Autowired
    private PeliculaGeneroService peliculaGeneroService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private List<PeliculaEntity> peliculaList = new ArrayList<>();
    private List<GeneroEntity> generoList = new ArrayList<>();

    @BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

    private void clearData() {
		entityManager.getEntityManager().createQuery("delete from GeneroEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
	}

    private void insertData() {

        for (int i = 0; i < 3; i++){ 
			PeliculaEntity pelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(pelicula);
			peliculaList.add(pelicula);
		}

        for (int i = 0; i < 3; i++) {
            GeneroEntity generoEntity = factory.manufacturePojo(GeneroEntity.class);
            entityManager.persist(generoEntity);
            generoList.add(generoEntity);
        }

    }

    /**
	 * Prueba para asociar un autor a un libro.
	 *
	 */
	@Test
	void testAddGenero() throws EntityNotFoundException, IllegalOperationException {
		GeneroEntity newGenero = factory.manufacturePojo(GeneroEntity.class);
		entityManager.persist(newGenero);
		
		PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
		entityManager.persist(newPelicula);
		
		peliculaGeneroService.addGenero(newPelicula.getId(), newGenero.getId());
		
		GeneroEntity result = peliculaGeneroService.getGenero(newPelicula.getId(), newGenero.getId());
		
        assertEquals(newGenero.getId(), result.getId());
        assertEquals(newGenero.getNombre(), result.getNombre());
		
	}

    /**
	 * Prueba para asociar un autor que no existe a un libro.
	 *
	 */
	@Test
	void testAddInvalidGenero() {
		assertThrows(EntityNotFoundException.class, ()->{
			PeliculaEntity newPelicula = peliculaList.get(1);
			entityManager.persist(newPelicula);
			peliculaGeneroService.addGenero(newPelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para asociar un autor que no existe a un libro.
	 *
	 */
	@Test
	void testAddInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, ()->{
			GeneroEntity newGenero = generoList.get(1);
			entityManager.persist(newGenero);
			peliculaGeneroService.addGenero(0l, newGenero.getId());
		});
	}


    /**
	 * Prueba para consultar la lista de autores de un libro.
	 */
	@Test
	void testGetGeneros() throws EntityNotFoundException {
		List<GeneroEntity> plataformaEntities = peliculaGeneroService.getGeneros(peliculaList.get(0).getId());

		assertEquals(generoList.size(), plataformaEntities.size());

		for (GeneroEntity plataforma : plataformaEntities) {
			assertTrue(plataformaEntities.contains(plataforma));
		}
	}
	
	/**
	 * Prueba para consultar la lista de autores de un libro que no existe.
	 */
	@Test
	void testGetGenerosInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaGeneroService.getGeneros(0L);
		});
	}

    /**
	 * Prueba para consultar un autor de un libro.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetGenero() throws EntityNotFoundException, IllegalOperationException {
		GeneroEntity plataformaEntity = generoList.get(0);
        PeliculaEntity peliculaEntity = peliculaList.get(0);
		GeneroEntity plataforma = peliculaGeneroService.getGenero(peliculaEntity.getId(), plataformaEntity.getId());
		assertNotNull(plataforma);

		assertEquals(plataforma.getId(), plataformaEntity.getId());
		assertEquals(plataforma.getNombre(), plataformaEntity.getNombre());

	}
	
	/**
	 * Prueba para consultar un autor que no existe de un libro.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetInvalidGenero()  {
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaGeneroService.getGenero(peliculaList.get(1).getId(), 0L);
		});
	}
	
	/**
	 * Prueba para consultar un autor de un libro que no existe.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetplataformaInvalidBook() {
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaGeneroService.getGenero(0L, generoList.get(1).getId());
		});
	}

    
	/**
	 * Prueba para obtener un autor no asociado a un libro.
	 *
	 */
	@Test
	void testGetNotAssociatedGenero() {
		assertThrows(IllegalOperationException.class, ()->{
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);

			entityManager.persist(newPelicula);
			GeneroEntity plataforma = factory.manufacturePojo(GeneroEntity.class);
			entityManager.persist(plataforma);
			peliculaGeneroService.getGenero(newPelicula.getId(), plataforma.getId());
		});
	}

	/**
	 * Prueba para actualizar los autores de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceplataformas() throws EntityNotFoundException {
        PeliculaEntity pelicula = peliculaList.get(1);
		ArrayList<GeneroEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			GeneroEntity entity = factory.manufacturePojo(GeneroEntity.class);
			entityManager.persist(entity);
			pelicula.getGeneros().add(entity);
			nuevaLista.add(entity);
            peliculaGeneroService.removeGenero(pelicula.getId(), entity.getId());
		}
		
		List<GeneroEntity> plataformaEntities = peliculaGeneroService.getGeneros(pelicula.getId());
		for (GeneroEntity aNuevaLista : nuevaLista) {
			assertTrue(plataformaEntities.contains(aNuevaLista));
		}
	}
	
	

	
	/**
	 * Prueba para actualizar un autor de un libro que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceplataformasInvalidpelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<GeneroEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				GeneroEntity entity = factory.manufacturePojo(GeneroEntity.class);
				entityManager.persist(entity);
				nuevaLista.add(entity);
                peliculaGeneroService.removeGenero(0L, entity.getId());
			}
		});
	}

	/**
	 * Prueba desasociar un autor con un libro.
	 *
	 */
	@Test
	void testRemoveplataforma() throws EntityNotFoundException {
        PeliculaEntity pelicula = peliculaList.get(0);
		GeneroEntity plataforma = pelicula.getGeneros().get(0);
        
		peliculaGeneroService.removeGenero(pelicula.getId(), plataforma.getId());
		
		assertFalse(peliculaGeneroService.getGeneros(pelicula.getId()).contains(plataforma));
	}
	
	/**
	 * Prueba desasociar un autor que no existe con un libro.
	 *
	 */
	@Test
	void testRemoveInvalidplataforma(){
        PeliculaEntity pelicula = peliculaList.get(0);
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaGeneroService.removeGenero(pelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba desasociar un autor con un libro que no existe.
	 *
	 */
	@Test
	void testRemoveplataformaInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaGeneroService.removeGenero(0L, generoList.get(0).getId());
		});
	}

}
