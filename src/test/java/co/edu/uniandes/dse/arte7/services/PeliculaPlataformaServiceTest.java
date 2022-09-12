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

import co.edu.uniandes.dse.arte7.entities.ActorEntity;
import co.edu.uniandes.dse.arte7.entities.DirectorEntity;
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
@Import(PeliculaPlataformaService.class)
public class PeliculaPlataformaServiceTest {

    @Autowired
    private PeliculaPlataformaService peliculaplataformaService;

    @Autowired
	private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

	private List<PeliculaEntity> peliculaList = new ArrayList<>();

	private List<DirectorEntity> directorList = new ArrayList<>();

	private List<ActorEntity> actorList = new ArrayList<>();

    private List<PlataformaEntity> plataformaList = new ArrayList<>();

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
		entityManager.getEntityManager().createQuery("delete from DirectorEntity").executeUpdate();
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

		for (int i = 0; i < 3; i++) {
			DirectorEntity directorEntity = factory.manufacturePojo(DirectorEntity.class);
			entityManager.persist(directorEntity);
			directorList.add(directorEntity);
		}

		for (int i = 0; i < 3; i++) {
			PeliculaEntity peliculaEntity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(peliculaEntity);
			peliculaList.add(peliculaEntity);
		}

        for (int i = 0; i < 3; i++) {
			PlataformaEntity plataformaEntity = factory.manufacturePojo(PlataformaEntity.class);
			entityManager.persist(plataformaEntity);
			plataformaList.add(plataformaEntity);
		}


		PeliculaEntity peliculaEntity = peliculaList.get(0);
		peliculaEntity.setActores(actorList);
        peliculaEntity.setDirectores(directorList);
        peliculaEntity.setPlataformas(plataformaList);
		entityManager.persist(peliculaEntity);

        PlataformaEntity plataformaEntity = plataformaList.get(0);
		plataformaEntity.setPeliculas(peliculaList);
        entityManager.persist(plataformaEntity);


	}

    /**
	 * Prueba para asociar un autor a un libro.
	 *
	 */
	@Test
	void testAddPlataforma() throws EntityNotFoundException, IllegalOperationException {
		PlataformaEntity newPlataforma = factory.manufacturePojo(PlataformaEntity.class);
		entityManager.persist(newPlataforma);
		
		PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
		entityManager.persist(newPelicula);
		
		peliculaplataformaService.addPlataforma(newPelicula.getId(), newPlataforma.getId());
		
		PlataformaEntity result = peliculaplataformaService.getPlataforma(newPelicula.getId(), newPlataforma.getId());
		
        assertEquals(newPlataforma.getId(), result.getId());
        assertEquals(newPlataforma.getNombre(), result.getNombre());
		
	}

    /**
	 * Prueba para asociar un autor que no existe a un libro.
	 *
	 */
	@Test
	void testAddInvalidPlataforma() {
		assertThrows(EntityNotFoundException.class, ()->{
			PeliculaEntity newPelicula = peliculaList.get(1);
			entityManager.persist(newPelicula);
			peliculaplataformaService.addPlataforma(newPelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para asociar un autor que no existe a un libro.
	 *
	 */
	@Test
	void testAddInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, ()->{
			PlataformaEntity newPlataforma = plataformaList.get(1);
			entityManager.persist(newPlataforma);
			peliculaplataformaService.addPlataforma(0l, newPlataforma.getId());
		});
	}


    /**
	 * Prueba para consultar la lista de autores de un libro.
	 */
	@Test
	void testGetPlataformas() throws EntityNotFoundException {
		List<PlataformaEntity> plataformaEntities = peliculaplataformaService.getPlataformas(peliculaList.get(0).getId());

		assertEquals(plataformaList.size(), plataformaEntities.size());

		for (PlataformaEntity plataforma : plataformaEntities) {
			assertTrue(plataformaEntities.contains(plataforma));
		}
	}
	
	/**
	 * Prueba para consultar la lista de autores de un libro que no existe.
	 */
	@Test
	void testGetPlataformasInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaplataformaService.getPlataformas(0L);
		});
	}

    /**
	 * Prueba para consultar un autor de un libro.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPlataforma() throws EntityNotFoundException, IllegalOperationException {
		PlataformaEntity plataformaEntity = plataformaList.get(0);
        PeliculaEntity peliculaEntity = peliculaList.get(0);
		PlataformaEntity plataforma = peliculaplataformaService.getPlataforma(peliculaEntity.getId(), plataformaEntity.getId());
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
	void testGetInvalidPlataforma()  {
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaplataformaService.getPlataforma(peliculaList.get(1).getId(), 0L);
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
			peliculaplataformaService.getPlataforma(0L, plataformaList.get(1).getId());
		});
	}

    
	/**
	 * Prueba para obtener un autor no asociado a un libro.
	 *
	 */
	@Test
	void testGetNotAssociatedPlataforma() {
		assertThrows(IllegalOperationException.class, ()->{
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);

			entityManager.persist(newPelicula);
			PlataformaEntity plataforma = factory.manufacturePojo(PlataformaEntity.class);
			entityManager.persist(plataforma);
			peliculaplataformaService.getPlataforma(newPelicula.getId(), plataforma.getId());
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
		List<PlataformaEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PlataformaEntity entity = factory.manufacturePojo(PlataformaEntity.class);
			entityManager.persist(entity);
			pelicula.getPlataformas().add(entity);
			nuevaLista.add(entity);
		}
		peliculaplataformaService.replacePlataformas(pelicula.getId(), nuevaLista);
		
		List<PlataformaEntity> plataformaEntities = peliculaplataformaService.getPlataformas(pelicula.getId());
		for (PlataformaEntity aNuevaLista : nuevaLista) {
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
			List<PlataformaEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				PlataformaEntity entity = factory.manufacturePojo(PlataformaEntity.class);
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			peliculaplataformaService.replacePlataformas(0L, nuevaLista);
		});
	}

	/**
	 * Prueba desasociar un autor con un libro.
	 *
	 */
	@Test
	void testRemoveplataforma() throws EntityNotFoundException {
        PeliculaEntity pelicula = peliculaList.get(0);
		PlataformaEntity plataforma = pelicula.getPlataformas().get(0);
        
		peliculaplataformaService.removePlataforma(pelicula.getId(), plataforma.getId());
		
		assertFalse(peliculaplataformaService.getPlataformas(pelicula.getId()).contains(plataforma));
	}
	
	/**
	 * Prueba desasociar un autor que no existe con un libro.
	 *
	 */
	@Test
	void testRemoveInvalidplataforma(){
        PeliculaEntity pelicula = peliculaList.get(0);
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaplataformaService.removePlataforma(pelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba desasociar un autor con un libro que no existe.
	 *
	 */
	@Test
	void testRemoveplataformaInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaplataformaService.removePlataforma(0L, plataformaList.get(0).getId());
		});
	}
}
