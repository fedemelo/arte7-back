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


@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(PlataformaPeliculaService.class)
class plataformapeliculaServiceTest {
	
	@Autowired
    private PlataformaPeliculaService plataformapeliculaService;

    @Autowired
	private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

	private List<PeliculaEntity> peliculaList = new ArrayList<>();

	private List<DirectorEntity> directorList = new ArrayList<>();

	private List<ActorEntity> actorList = new ArrayList<>();

    private List<PlataformaEntity> plataformaList = new ArrayList<>();


	
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
	void testAddPelicula() throws EntityNotFoundException, IllegalOperationException {
		PlataformaEntity newplataforma = factory.manufacturePojo(PlataformaEntity.class);
		entityManager.persist(newplataforma);
		
		PeliculaEntity pelicula = factory.manufacturePojo(PeliculaEntity.class);
		entityManager.persist(pelicula);
		
		plataformapeliculaService.addPelicula(newplataforma.getId(), pelicula.getId());
		
		PeliculaEntity lastPelicula = plataformapeliculaService.getPelicula(newplataforma.getId(), pelicula.getId());
		assertEquals(pelicula.getId(), lastPelicula.getId());
		assertEquals(pelicula.getNombre(), lastPelicula.getNombre());
	}
	
	/**
	 * Prueba para asociar un autor que no existe a un libro.
	 *
	 */
	@Test
	void testAddInvalidPelicula() {
		assertThrows(EntityNotFoundException.class, ()->{
			PlataformaEntity newPlataforma = factory.manufacturePojo(PlataformaEntity.class);
			entityManager.persist(newPlataforma);
			plataformapeliculaService.addPelicula(newPlataforma.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para asociar un autor a un libro que no existe.
	 *
	 */
	@Test
	void testAddPeliculaInvalidPlataforma() throws EntityNotFoundException, IllegalOperationException {
		assertThrows(EntityNotFoundException.class, ()->{
			PeliculaEntity Pelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(Pelicula);
			plataformapeliculaService.addPelicula(0L, Pelicula.getId());
		});
	}

	/**
	 * Prueba para consultar la lista de autores de un libro.
	 */
	@Test
	void testGetPeliculas() throws EntityNotFoundException {
        PlataformaEntity plataforma = plataformaList.get(0);
		List<PeliculaEntity> peliculaEntities = plataformapeliculaService.getPeliculas(plataforma.getId());

		assertEquals(peliculaList.size(), peliculaEntities.size());

		for (PeliculaEntity pelicula : peliculaList) {
			assertTrue(peliculaEntities.contains(pelicula));
		}
	}
	
	/**
	 * Prueba para consultar la lista de autores de un libro que no existe.
	 */
	@Test
	void testGetPeliculasInvalidPlataforma(){
		assertThrows(EntityNotFoundException.class, ()->{
			plataformapeliculaService.getPeliculas(0L);
		});
	}

	/**
	 * Prueba para consultar un autor de un libro.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPelicula() throws EntityNotFoundException, IllegalOperationException {
		PlataformaEntity plataforma = plataformaList.get(0);
        PeliculaEntity peliculaEntity = peliculaList.get(0);
		PeliculaEntity pelicula = plataformapeliculaService.getPelicula(plataforma.getId(), peliculaEntity.getId());
		assertNotNull(pelicula);

		assertEquals(peliculaEntity.getId(), pelicula.getId());
		assertEquals(peliculaEntity.getNombre(), pelicula.getNombre());

	}
	
	/**
	 * Prueba para consultar un autor que no existe de un libro.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetInvalidPelicula()  {
		assertThrows(EntityNotFoundException.class, ()->{
            PlataformaEntity plataforma = plataformaList.get(0);
			plataformapeliculaService.getPelicula(plataforma.getId(), 0L);
		});
	}
	
	/**
	 * Prueba para consultar un autor de un libro que no existe.
	 *
	 * @throws throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testGetPeliculaInvalidPlataforma() {
		assertThrows(EntityNotFoundException.class, ()->{
			PeliculaEntity PeliculaEntity = peliculaList.get(0);
			plataformapeliculaService.getPelicula(0L, PeliculaEntity.getId());
		});
	}
	
	/**
	 * Prueba para obtener un autor no asociado a un libro.
	 *
	 */
	@Test
	void testGetNotAssociatedPelicula() {
		assertThrows(IllegalOperationException.class, ()->{
			PlataformaEntity newplataforma = factory.manufacturePojo(PlataformaEntity.class);
			entityManager.persist(newplataforma);
			PeliculaEntity pelicula = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(pelicula);
			plataformapeliculaService.getPelicula(newplataforma.getId(), pelicula.getId());
		});
	}

	/**
	 * Prueba para actualizar los autores de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplacePeliculas() throws EntityNotFoundException {
        PlataformaEntity plataforma = plataformaList.get(0);
		List<PeliculaEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(entity);
			plataforma.getPeliculas().add(entity);
			nuevaLista.add(entity);
		}
		plataformapeliculaService.replacePeliculas(plataforma.getId(), nuevaLista);
		
		List<PeliculaEntity> PeliculaEntities = plataformapeliculaService.getPeliculas(plataforma.getId());
		for (PeliculaEntity aNuevaLista : nuevaLista) {
			assertTrue(PeliculaEntities.contains(aNuevaLista));
		}
	}
	
	/**
	 * Prueba para actualizar los autores de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplacePeliculas2() throws EntityNotFoundException {
        PlataformaEntity plataforma = plataformaList.get(0);
		List<PeliculaEntity> nuevaLista = new ArrayList<>();
		for (int i = 0; i < 3; i++) {
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(entity);
			nuevaLista.add(entity);
		}
		plataformapeliculaService.replacePeliculas(plataforma.getId(), nuevaLista);
		
		List<PeliculaEntity> PeliculaEntities = plataformapeliculaService.getPeliculas(plataforma.getId());
		for (PeliculaEntity aNuevaLista : nuevaLista) {
			assertTrue(PeliculaEntities.contains(aNuevaLista));
		}
	}
	
	
	/**
	 * Prueba para actualizar los autores de un libro que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplacePeliculasInvalidPlataforma(){
		assertThrows(EntityNotFoundException.class, ()->{
            PlataformaEntity plataforma = plataformaList.get(0);
			List<PeliculaEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
				entity.getPlataformas().add(plataforma);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			plataformapeliculaService.replacePeliculas(0L, nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar los autores que no existen de un libro.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceInvalidPeliculas() {
        PlataformaEntity plataforma = plataformaList.get(0);
		assertThrows(EntityNotFoundException.class, ()->{
			List<PeliculaEntity> nuevaLista = new ArrayList<>();
			PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
			entity.setId(0L);
			nuevaLista.add(entity);
			plataformapeliculaService.replacePeliculas(plataforma.getId(), nuevaLista);
		});
	}
	
	/**
	 * Prueba para actualizar un autor de un libro que no existe.
	 *
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplacePeliculasInvalidPelicula(){
        PlataformaEntity plataforma = plataformaList.get(0);
		assertThrows(EntityNotFoundException.class, ()->{
			List<PeliculaEntity> nuevaLista = new ArrayList<>();
			for (int i = 0; i < 3; i++) {
				PeliculaEntity entity = factory.manufacturePojo(PeliculaEntity.class);
				entity.getPlataformas().add(plataforma);		
				entityManager.persist(entity);
				nuevaLista.add(entity);
			}
			plataformapeliculaService.replacePeliculas(0L, nuevaLista);
		});
	}

	/**
	 * Prueba desasociar un autor con un libro.
	 *
	 */
	@Test
	void testRemovePelicula() throws EntityNotFoundException {
        PlataformaEntity plataforma = plataformaList.get(0);
		for (PeliculaEntity Pelicula : peliculaList) {
			plataformapeliculaService.removePelicula(plataforma.getId(), Pelicula.getId());
		}
		assertTrue(plataformapeliculaService.getPeliculas(plataforma.getId()).isEmpty());
	}
	
	/**
	 * Prueba desasociar un autor que no existe con un libro.
	 *
	 */
	@Test
	void testRemoveInvalidPelicula(){
        PlataformaEntity plataforma = plataformaList.get(0);
		assertThrows(EntityNotFoundException.class, ()->{
			plataformapeliculaService.removePelicula(plataforma.getId(), 0L);
		});
	}
	
	/**
	 * Prueba desasociar un autor con un libro que no existe.
	 *
	 */
	@Test
	void testRemovePeliculaInvalidPlataforma(){
		assertThrows(EntityNotFoundException.class, ()->{
			plataformapeliculaService.removePelicula(0L, peliculaList.get(0).getId());
		});
	}

}