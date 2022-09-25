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

import co.edu.uniandes.dse.arte7.entities.ResenhaEntity;
import co.edu.uniandes.dse.arte7.entities.UsuarioEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Pruebas de logica de la relacion Usuario - Resenha
 *
 * @author ISIS2603
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import({ UsuarioService.class, UsuarioResenhaService.class })
public class UsuarioResenhaServiceTest {
    
    @Autowired
	private UsuarioResenhaService usuarioResenhaService;

	@Autowired
	private TestEntityManager entityManager;

	private PodamFactory factory = new PodamFactoryImpl();

	private List<UsuarioEntity> usuariosList = new ArrayList<>();
	private List<ResenhaEntity> resenhasList = new ArrayList<>();

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
		entityManager.getEntityManager().createQuery("delete from ResenhaEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from UsuarioEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {
		for (int i = 0; i < 3; i++) {
			ResenhaEntity resenha = factory.manufacturePojo(ResenhaEntity.class);
			entityManager.persist(resenha);
			resenhasList.add(resenha);
		}

		for (int i = 0; i < 3; i++) {
			UsuarioEntity entity = factory.manufacturePojo(UsuarioEntity.class);
			entityManager.persist(entity);
			usuariosList.add(entity);
			if (i == 0) {
				resenhasList.get(i).setCritico(entity);
				entity.getResenhas().add(resenhasList.get(i));
			}
		}
	}

	/**
	 * Prueba para asociar una Resenha existente a un Usuario.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testAddResenha() throws EntityNotFoundException {
		UsuarioEntity entity = usuariosList.get(0);
		ResenhaEntity resenhaEntity = resenhasList.get(1);
		ResenhaEntity response = usuarioResenhaService.addResenha(resenhaEntity.getId(), entity.getId());

		assertNotNull(response);
		assertEquals(resenhaEntity.getId(), response.getId());
	}
	
	/**
	 * Prueba para asociar una Resenha que no existe a un Usuario.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testAddInvalidResenha() {
		assertThrows(EntityNotFoundException.class, ()->{
			UsuarioEntity entity = usuariosList.get(0);
			usuarioResenhaService.addResenha(0L, entity.getId());
		});
	}
	
	/**
	 * Prueba para asociar una Resenha a un Usuario que no existe.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testAddResenhaInvalidUsuario() {
		assertThrows(EntityNotFoundException.class, ()->{
			ResenhaEntity resenhaEntity = resenhasList.get(1);
			usuarioResenhaService.addResenha(resenhaEntity.getId(), 0L);
		});
	}

	/**
	 * Prueba para obtener una colección de instancias de Resenhas asociadas a una
	 * instancia Usuario.
	 * 
	 * @throws EntityNotFoundException
	 */

	@Test
	void testGetResenhas() throws EntityNotFoundException {
		List<ResenhaEntity> list = usuarioResenhaService.getResenhas(usuariosList.get(0).getId());
		assertEquals(1, list.size());
	}
	
	/**
	 * Prueba para obtener una colección de instancias de Resenhas asociadas a una
	 * instancia Usuario que no existe.
	 * 
	 * @throws EntityNotFoundException
	 */

	@Test
	void testGetResenhasInvalidUsuario() {
		assertThrows(EntityNotFoundException.class,()->{
			usuarioResenhaService.getResenhas(0L);
		});
	}

	/**
	 * Prueba para obtener una instancia de Resenha asociada a una instancia Usuario.
	 * 
	 * @throws IllegalOperationException
	 * @throws EntityNotFoundException
	 *
	 * @throws co.edu.uniandes.csw.resenhastore.exceptions.BusinessLogicException
	 */
	@Test
	void testGetResenha() throws EntityNotFoundException, IllegalOperationException {
		UsuarioEntity entity = usuariosList.get(0);
		ResenhaEntity resenhaEntity = resenhasList.get(0);
		ResenhaEntity response = usuarioResenhaService.getResenha(entity.getId(), resenhaEntity.getId());

		assertEquals(resenhaEntity.getId(), response.getId());
		assertEquals(resenhaEntity.getEstrellas(), response.getEstrellas());
        assertEquals(resenhaEntity.getNumCaracteres(), response.getNumCaracteres());
        assertEquals(resenhaEntity.getTexto(), response.getTexto());
	}
	
	/**
	 * Prueba para obtener una instancia de Resenha asociada a una instancia Usuario que no existe.
	 * 
	 * @throws EntityNotFoundException
	 *
	 */
	@Test
	void testGetResenhaInvalidUsuario()  {
		assertThrows(EntityNotFoundException.class, ()->{
			ResenhaEntity resenhaEntity = resenhasList.get(0);
			usuarioResenhaService.getResenha(0L, resenhaEntity.getId());
		});
	}
	
	/**
	 * Prueba para obtener una instancia de Resenha que no existe asociada a una instancia Usuario.
	 * 
	 * @throws EntityNotFoundException
	 * 
	 */
	@Test
	void testGetInvalidResenha()  {
		assertThrows(EntityNotFoundException.class, ()->{
			UsuarioEntity entity = usuariosList.get(0);
			usuarioResenhaService.getResenha(entity.getId(), 0L);
		});
	}

	/**
	 * Prueba para obtener una instancia de Resenhas asociada a una instancia Usuario
	 * que no le pertenece.
	 *
	 * @throws co.edu.uniandes.csw.resenhastore.exceptions.BusinessLogicException
	 */
	@Test
	public void getResenhaNoAsociadoTest() {
		assertThrows(IllegalOperationException.class, () -> {
			UsuarioEntity entity = usuariosList.get(0);
			ResenhaEntity resenhaEntity = resenhasList.get(1);
			usuarioResenhaService.getResenha(entity.getId(), resenhaEntity.getId());
		});
	}

	/**
	 * Prueba para remplazar las instancias de Resenhas asociadas a una instancia de
	 * Usuario.
	 */
	@Test
	void testReplaceResenhas() throws EntityNotFoundException {
		UsuarioEntity entity = usuariosList.get(0);
		List<ResenhaEntity> list = resenhasList.subList(1, 3);
		usuarioResenhaService.replaceResenhas(entity.getId(), list);

		for (ResenhaEntity resenha : list) {
			ResenhaEntity r = entityManager.find(ResenhaEntity.class, resenha.getId());
			assertEquals(r.getCritico(), entity);
		}
	}
	
	/**
	 * Prueba para remplazar las instancias de Resenhas que no existen asociadas a una instancia de
	 * Usuario.
	 */
	@Test
	void testReplaceInvalidResenhas() {
		assertThrows(EntityNotFoundException.class, ()->{
			UsuarioEntity entity = usuariosList.get(0);
			
			List<ResenhaEntity> resenhas = new ArrayList<>();
			ResenhaEntity newResenha = factory.manufacturePojo(ResenhaEntity.class);
			newResenha.setId(0L);
			resenhas.add(newResenha);
			
			usuarioResenhaService.replaceResenhas(entity.getId(), resenhas);
		});
	}
	
	/**
	 * Prueba para remplazar las instancias de Resenhas asociadas a una instancia de
	 * Usuario que no existe.
	 */
	@Test
	void testReplaceResenhasInvalidUsuario() throws EntityNotFoundException {
		assertThrows(EntityNotFoundException.class, ()->{
			List<ResenhaEntity> list = resenhasList.subList(1, 3);
			usuarioResenhaService.replaceResenhas(0L, list);
		});
	}
}
