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
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
// import co.edu.uniandes.dse.arte7.services.ActorPeliculaService;
// import co.edu.uniandes.dse.arte7.services.PeliculaService;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(PeliculaActorService.class)
class PeliculaActorServiceTest {
    @Autowired
	private PeliculaActorService peliculaActorService;
	
	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private PeliculaEntity pelicula = new PeliculaEntity();
	private List<ActorEntity> actorList = new ArrayList<>();

	
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}
	
	/**
	 * Limpia las tablas que están implicadas en la prueba.
	 */
	private void clearData() {
		entityManager.getEntityManager().createQuery("delete from ActorEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {


		pelicula = factory.manufacturePojo(PeliculaEntity.class);
		entityManager.persist(pelicula);

		for (int i = 0; i < 3; i++) {
			ActorEntity entity = factory.manufacturePojo(ActorEntity.class);
			entityManager.persist(entity);
			entity.getPeliculas().add(pelicula);
			actorList.add(entity);
			pelicula.getActores().add(entity);	
		}
	}

	/**
	 * Prueba para asociar un autor a un libro.
	 *
	 */
	@Test
	void testAddActor() throws EntityNotFoundException, IllegalOperationException {
		PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
		entityManager.persist(newPelicula);
		
		ActorEntity actor = factory.manufacturePojo(ActorEntity.class);
		entityManager.persist(actor);
		
		peliculaActorService.addActor(newPelicula.getId(), actor.getId());
		
		ActorEntity lastActor = peliculaActorService.getActor(newPelicula.getId(), actor.getId());
		assertEquals(actor.getId(), lastActor.getId());
		assertEquals(actor.getFechaNacimiento(), lastActor.getFechaNacimiento());
		assertEquals(actor.getNacionalidad(), lastActor.getNacionalidad());
		assertEquals(actor.getFotografia(), lastActor.getFotografia());
		assertEquals(actor.getNombre(), lastActor.getNombre());
        assertEquals(actor.getBiografia(), lastActor.getBiografia());
	}
	
	/**
	 * Prueba para asociar un autor que no existe a un libro.
	 *
	 */
	@Test
	void testAddInvalidActor() {
		assertThrows(EntityNotFoundException.class, ()->{
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(newPelicula);
			peliculaActorService.addActor(newPelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para asociar un autor a un libro que no existe.
	 *
	 */
	@Test
	void testAddActorInvalidPelicula() throws EntityNotFoundException, IllegalOperationException {
		assertThrows(EntityNotFoundException.class, ()->{
			ActorEntity actor = factory.manufacturePojo(ActorEntity.class);
			entityManager.persist(actor);
			peliculaActorService.addActor(0L, actor.getId());
		});
	}

	/**
	 * Prueba para consultar la lista de autores de un libro.
	 */
	@Test
	void testGetActores() throws EntityNotFoundException {
		List<ActorEntity> actorEntities = peliculaActorService.getActores(pelicula.getId());

		assertEquals(actorList.size(), actorEntities.size());

		for (int i = 0; i < actorList.size(); i++) {
			assertTrue(actorEntities.contains(actorList.get(0)));
		}
	}
	
	/**
	 * Prueba para consultar la lista de autores de un libro que no existe.
	 */
	@Test
	void testGetActoresInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaActorService.getActores(0L);
		});
	}

	/**
	 * Prueba para consultar un autor de un libro.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetActor() throws EntityNotFoundException, IllegalOperationException {
		ActorEntity actorEntity = actorList.get(0);
		ActorEntity actor = peliculaActorService.getActor(pelicula.getId(), actorEntity.getId());
		assertNotNull(actor);

		assertEquals(actorEntity.getId(), actor.getId());
		assertEquals(actorEntity.getNombre(), actor.getNombre());
		assertEquals(actorEntity.getBiografia(), actor.getBiografia());
		assertEquals(actorEntity.getFotografia(), actor.getFotografia());
		assertEquals(actorEntity.getFechaNacimiento(), actor.getFechaNacimiento());
        assertEquals(actorEntity.getNacionalidad(), actor.getNacionalidad());
	}
	
	/**
	 * Prueba para consultar un autor que no existe de un libro.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetInvalidActor()  {
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaActorService.getActor(pelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para consultar un autor de un libro que no existe.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetActorInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, ()->{
			ActorEntity actorEntity = actorList.get(0);
			peliculaActorService.getActor(0L, actorEntity.getId());
		});
	}
	
	/**
	 * Prueba para obtener un autor no asociado a un libro.
	 *
	 */
	@Test
	void testGetNotAssociatedActor() {
		assertThrows(IllegalOperationException.class, ()->{
			PeliculaEntity newPelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(newPelicula);
			ActorEntity actor = factory.manufacturePojo(ActorEntity.class);
			entityManager.persist(actor);
			peliculaActorService.getActor(newPelicula.getId(), actor.getId());
		});
	}

	/**
	 * Prueba para actualizar los autores de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceActores() throws EntityNotFoundException {
		List<ActorEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ActorEntity entity = factory.manufacturePojo(ActorEntity.class);
			entityManager.persist(entity);
			pelicula.getActores().add(entity);
			nuevaLista.add(entity);
		}
		peliculaActorService.replaceActores(pelicula.getId(), nuevaLista);
		
		List<ActorEntity> actorEntities = peliculaActorService.getActores(pelicula.getId());
		for (ActorEntity aNuevaLista : nuevaLista) {
			assertTrue(actorEntities.contains(aNuevaLista));
		}
	}
	
	/**
	 * Prueba para actualizar los autores de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceActores2() throws EntityNotFoundException {
		List<ActorEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			ActorEntity entity = factory.manufacturePojo(ActorEntity.class);
			entityManager.persist(entity);
			nuevaLista.add(entity);
		}
		peliculaActorService.replaceActores(pelicula.getId(), nuevaLista);
		
		List<ActorEntity> actorEntities = peliculaActorService.getActores(pelicula.getId());
		for (ActorEntity aNuevaLista : nuevaLista) {
			assertTrue(actorEntities.contains(aNuevaLista));
		}
	}
	
	
	/**
	 * Prueba para actualizar los autores de un libro que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceActoresInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<ActorEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				ActorEntity entity = factory.manufacturePojo(ActorEntity.class);
				entity.getPeliculas().add(pelicula);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			peliculaActorService.replaceActores(0L, nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar los autores que no existen de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceInvalidActores() {
		assertThrows(EntityNotFoundException.class, ()->{
			List<ActorEntity> nuevaLista = new ArrayList<>();
			ActorEntity entity = factory.manufacturePojo(ActorEntity.class);
			entity.setId(0L);
			nuevaLista.add(entity);
			peliculaActorService.replaceActores(pelicula.getId(), nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar un autor de un libro que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceActoresInvalidActor(){
		assertThrows(EntityNotFoundException.class, ()->{
			List<ActorEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				ActorEntity entity = factory.manufacturePojo(ActorEntity.class);
				entity.getPeliculas().add(pelicula);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			peliculaActorService.replaceActores(0L, nuevaLista);
		});
	}

	/**
	 * Prueba desasociar un autor con un libro.
	 *
	 */
	@Test
	void testRemoveActor() throws EntityNotFoundException {
		for (ActorEntity actor : actorList) {
			peliculaActorService.removeActor(pelicula.getId(), actor.getId());
		}
		assertTrue(peliculaActorService.getActores(pelicula.getId()).isEmpty());
	}
	
	/**
	 * Prueba desasociar un autor que no existe con un libro.
	 *
	 */
	@Test
	void testRemoveInvalidActor(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaActorService.removeActor(pelicula.getId(), 0L);
		});
	}
	
	/**
	 * Prueba desasociar un autor con un libro que no existe.
	 *
	 */
	@Test
	void testRemoveActorInvalidPelicula(){
		assertThrows(EntityNotFoundException.class, ()->{
			peliculaActorService.removeActor(0L, actorList.get(0).getId());
		});
	}
}
