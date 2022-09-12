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
 * Pruebas de logica de Usuario
 *
 * @author Mariana Ruiz
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(UsuarioService.class)
public class UsuarioServiceTest {

    @Autowired
	private UsuarioService usuarioService;

	@Autowired
	private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

	private List<UsuarioEntity> usuarioList = new ArrayList<>();

	private List<ResenhaEntity> resenhaList = new ArrayList<>();

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
		entityManager.getEntityManager().createQuery("delete from ResenhaEntity");
		entityManager.getEntityManager().createQuery("delete from UsuarioEntity");
	}

    /**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {

		for (int i = 0; i < 3; i++) {
			UsuarioEntity usuarioEntity = factory.manufacturePojo(UsuarioEntity.class);
			entityManager.persist(usuarioEntity);
			usuarioList.add(usuarioEntity);
		}

		for (int i = 0; i < 3; i++) {
			ResenhaEntity resenhaEntity = factory.manufacturePojo(ResenhaEntity.class);
			entityManager.persist(resenhaEntity);
			resenhaList.add(resenhaEntity);
		}
		resenhaList.get(0).setCritico(usuarioList.get(0));
		usuarioList.get(0).getResenhas().add(resenhaList.get(0));
	}

    /**
	 * Prueba para crear un Usuario.
	 *
	 * @throws EntityNotFoundException, IllegalOperationException
	 */
	@Test
	void testCreateUsuario() throws EntityNotFoundException, IllegalOperationException {
		UsuarioEntity newEntity = factory.manufacturePojo(UsuarioEntity.class);
		UsuarioEntity result = usuarioService.createUsuario(newEntity);
		assertNotNull(result);

		UsuarioEntity entity = entityManager.find(UsuarioEntity.class, result.getId());
		assertEquals(newEntity.getId(), entity.getId());
		assertEquals(newEntity.getUsername(), entity.getUsername());
	}

    /**
	 * Prueba para crear un Usuario con el mismo nombre de un Usuario que ya
	 * existe.
	 *
	 * @throws IllegalOperationException
	 */
	@Test
	void testCreateUsuarioWithSameUsername() {
		assertThrows(IllegalOperationException.class, () -> {
			UsuarioEntity newEntity = factory.manufacturePojo(UsuarioEntity.class);
			newEntity.setUsername(usuarioList.get(0).getUsername());
			usuarioService.createUsuario(newEntity);
		});
	}

    /**
	 * Prueba para consultar la lista de Usuarios.
	 */
	@Test
	void testGetUsuarios() {
		List<UsuarioEntity> list = usuarioService.getUsuarios();
		assertEquals(usuarioList.size(), list.size());
		for (UsuarioEntity entity : list) {
			boolean found = false;
			for (UsuarioEntity storedEntity : usuarioList) {
				if (entity.getId().equals(storedEntity.getId())) {
					found = true;
				}
			}
			assertTrue(found);
		}
	}

    /**
	 * Prueba para consultar un Usuario.
	 * 
	 * @throws EntityNotFoundException
	 * 
	 */
	@Test
	void testGetUsuario() throws EntityNotFoundException {
		UsuarioEntity entity = usuarioList.get(0);
		UsuarioEntity resultEntity = usuarioService.getUsuario(entity.getId());
		assertNotNull(resultEntity);
		assertEquals(entity.getId(), resultEntity.getId());
		assertEquals(entity.getUsername(), resultEntity.getUsername());
	}

    /**
	 * Prueba para consultar un Usuario que no existe.
	 * 
	 * @throws EntityNotFoundException
	 * 
	 */
	@Test
	void testGetUsuarioInvalid() {
		assertThrows(EntityNotFoundException.class, ()->{
			usuarioService.getUsuario(0L);
		});
	}

    /**
	 * Prueba para actualizar un Usuario
	 */
	@Test
	void testUpdateUsuario() throws EntityNotFoundException {
		UsuarioEntity entity = usuarioList.get(0);
		UsuarioEntity pojoEntity = factory.manufacturePojo(UsuarioEntity.class);
		pojoEntity.setId(entity.getId());
		usuarioService.updateUsuario(entity.getId(), pojoEntity);
		UsuarioEntity resp = entityManager.find(UsuarioEntity.class, entity.getId());
		assertEquals(pojoEntity.getId(), resp.getId());
		assertEquals(pojoEntity.getUsername(), resp.getUsername());
	}

    /**
	 * Prueba para actualizar un Usuario que no existe.
	 */
	@Test
	void testUpdateUsuarioInvalid() {
		assertThrows(EntityNotFoundException.class, ()->{
			UsuarioEntity pojoEntity = factory.manufacturePojo(UsuarioEntity.class);
			pojoEntity.setId(0L);
			usuarioService.updateUsuario(0L, pojoEntity);
		});
	}

    /**
	 * Prueba para eliminar un Usuario.
	 */
	@Test
	void testDeleteUsuario() throws EntityNotFoundException, IllegalOperationException {
		UsuarioEntity entity = usuarioList.get(1);
		usuarioService.deleteUsuario(entity.getId());
		UsuarioEntity deleted = entityManager.find(UsuarioEntity.class, entity.getId());
		assertNull(deleted);
	}

    /**
	 * Prueba para eliminar un Usuario que no existe.
	 */
	@Test
	void testDeleteUsuarioInvalid(){
		assertThrows(EntityNotFoundException.class, ()->{
			usuarioService.deleteUsuario(0L);
		});
	}

    /**
	 * Prueba para eliminar un Usuario con resenhas asociadas.
	 */
	@Test
	void testDeleteUsuarioWithBooks() {
		assertThrows(IllegalOperationException.class, () -> {
			UsuarioEntity entity = usuarioList.get(0);
			usuarioService.deleteUsuario(entity.getId());
		});
	}
}
