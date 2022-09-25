package co.edu.uniandes.dse.arte7.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

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

import co.edu.uniandes.dse.arte7.entities.GeneroEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import({GeneroService.class, GeneroPeliculaService.class})

public class GeneroPeliculaServiceTest {
    
    @Autowired
    private GeneroPeliculaService generoPeliculaService;

    @Autowired
    private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

    private GeneroEntity genero = new GeneroEntity();

	private List<GeneroEntity> generoList = new ArrayList<>();
	private List<PeliculaEntity> peliculaList = new ArrayList<>();

    @BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

    private void clearData() {
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from GeneroEntity").executeUpdate();
	}

    private void insertData() {
        genero = factory.manufacturePojo(GeneroEntity.class);
		entityManager.persist(genero);

        for (int i = 0; i < 3; i++){ 
			PeliculaEntity pelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(pelicula);
			peliculaList.add(pelicula);
		}

        for (int i = 0; i < 3; i++) {
			GeneroEntity entity = factory.manufacturePojo(GeneroEntity.class);
			entityManager.persist(entity);
			generoList.add(entity);
		}
	}
    

    /**Test: Asociar una pelicula a un genero existente */
    @Test
	void testAddPelicula() throws EntityNotFoundException,  IllegalOperationException {
        GeneroEntity newGenero = factory.manufacturePojo(GeneroEntity.class);
		entityManager.persist(newGenero);
		
		PeliculaEntity pelicula = factory.manufacturePojo(PeliculaEntity.class);
		entityManager.persist(pelicula);
		generoPeliculaService.addPelicula(newGenero.getId(), pelicula.getId());
		PeliculaEntity response = generoPeliculaService.addPelicula(newGenero.getId(), pelicula.getId());
		assertNotNull(response);
		assertEquals(pelicula.getId(), response.getId());
		assertEquals(pelicula.getNombre(), response.getNombre());
	}

    /**Test: Asociar una pelicula que NO existe a un genero */

    @Test
	void testAddInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, ()->{
			GeneroEntity entity = generoList.get(0);
			generoPeliculaService.addPelicula(0L, entity.getId());
		});
	}
	
	/** Test: Asociar una película a un género que NO existe.*/
	@Test
	void testAddPeliculaInvalidGenero() {
		assertThrows(EntityNotFoundException.class, ()->{
			PeliculaEntity peliculaEntity = peliculaList.get(1);
			generoPeliculaService.addPelicula(peliculaEntity.getId(), 0L);
		});
	}

    /**Test: Obtener colección de películas de un determinado género */
    @Test
	void testGetPeliculas() throws EntityNotFoundException {
		GeneroEntity genero = generoList.get(0);
        generoPeliculaService.updatePeliculas(genero.getId(), peliculaList);
        List<PeliculaEntity> listado = generoPeliculaService.getPeliculas(genero.getId());
		assertEquals(peliculaList.size(), listado.size());

		for (PeliculaEntity pelicula : peliculaList) {
			assertTrue(listado.contains(pelicula));
		}
	}
	
	/**
	 * Prueba para obtener una colección de instancias de Peliculas asociadas a una
	 * instancia Genero que no existe.
	 * 
	 * @throws EntityNotFoundException
	 */

	@Test
	void testGetPeliculasInvalidGenero() {
		assertThrows(EntityNotFoundException.class,()->{
			generoPeliculaService.getPeliculas(0L);
		});
	}

	/**
	 * Prueba para obtener una instancia de Pelicula asociada a una instancia Genero.
	 * 
	 * @throws IllegalOperationException
	 * @throws EntityNotFoundException
	 *
	 * @throws co.edu.uniandes.csw.peliculastore.exceptions.BusinessLogicException
	 */
	@Test
	void testGetPelicula() throws EntityNotFoundException, IllegalOperationException {
		GeneroEntity genero = generoList.get(0);
        PeliculaEntity peliculaEntity = peliculaList.get(0);
        generoPeliculaService.addPelicula(genero.getId(), peliculaEntity.getId());
		PeliculaEntity pelicula = generoPeliculaService.getPelicula(genero.getId(), peliculaEntity.getId());
        assertNotNull(pelicula);
		assertEquals(peliculaEntity.getId(), pelicula.getId());
		assertEquals(peliculaEntity.getNombre(), pelicula.getNombre());
	}
	
	/**
	 * Prueba para obtener una instancia de Pelicula asociada a una instancia Genero que no existe.
	 * 
	 * @throws EntityNotFoundException
	 *
	 */
	@Test
	void testGetPeliculaInvalidGenero()  {
		assertThrows(EntityNotFoundException.class, ()->{
			PeliculaEntity peliculaEntity = peliculaList.get(0);
			generoPeliculaService.getPelicula(0L, peliculaEntity.getId());
		});
	}
	
	/**
	 * Prueba para obtener una instancia de Pelicula que no existe asociada a una instancia Genero.
	 * 
	 * @throws EntityNotFoundException
	 * 
	 */
	@Test
	void testGetInvalidPelicula()  {
		assertThrows(EntityNotFoundException.class, ()->{
			GeneroEntity entity = generoList.get(0);
			generoPeliculaService.getPelicula(entity.getId(), 0L);
		});
	}

	/**
	 * Prueba para obtener una instancia de Peliculas asociada a una instancia Genero
	 * que no le pertenece.
	 *
	 * @throws co.edu.uniandes.csw.peliculastore.exceptions.BusinessLogicException
	 */
	@Test
	public void getPeliculaNoAsociadoTest() {
		assertThrows(IllegalOperationException.class, () -> {
			GeneroEntity entity = generoList.get(0);
			PeliculaEntity peliculaEntity = peliculaList.get(1);
			generoPeliculaService.getPelicula(entity.getId(), peliculaEntity.getId());
		});
	}

	/**
	 * Prueba para remplazar las instancias de Peliculas asociadas a una instancia de
	 * Genero.
	 */
	@Test
	void testReplacePeliculas() throws EntityNotFoundException, IllegalOperationException {
		ArrayList<PeliculaEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(entity);
            genero.getPeliculas().add(entity);
            generoPeliculaService.addPelicula(genero.getId(), entity.getId());
			nuevaLista.add(entity);
		}
		
		List<PeliculaEntity> peliculaEntities = generoPeliculaService.getPeliculas(genero.getId());
		for (PeliculaEntity aNuevaLista : nuevaLista) {
			assertTrue(peliculaEntities.contains(aNuevaLista));
		}
	}
	
	/**
	 * Prueba para remplazar las instancias de Peliculas que no existen asociadas a una instancia de
	 * Genero.
	 */
	@Test
	void testReplaceInvalidPeliculas() {
		assertThrows(EntityNotFoundException.class, ()->{
			GeneroEntity entity = generoList.get(0);
			
			List<PeliculaEntity> peliculas = new ArrayList<>();
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
			newPelicula.setId(0L);
			peliculas.add(newPelicula);
			
			generoPeliculaService.updatePeliculas(entity.getId(), peliculas);
		});
	}
	
	/**
	 * Prueba para remplazar las instancias de Peliculas asociadas a una instancia de
	 * Genero que no existe.
	 */
	@Test
	void testReplacePeliculasInvalidGenero() throws EntityNotFoundException {
		assertThrows(EntityNotFoundException.class, ()->{
			List<PeliculaEntity> list = peliculaList.subList(1, 3);
			generoPeliculaService.updatePeliculas(0L, list);
		});
	}
}
