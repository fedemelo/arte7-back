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
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

/**
 * Pruebas de logica de la relacion Resenha - Usuario
 *
 * @author Mariana Ruiz
 */
@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import({ ResenhaService.class, ResenhaUsuarioService.class })

public class ResenhaUsuarioServiceTest {

    @Autowired
	private TestEntityManager entityManager;

	@Autowired
	private ResenhaUsuarioService resenhaUsuarioService;

	@Autowired
	private ResenhaService resenhaService;

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
        entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {
		for (int i = 0; i < 3; i++) {
			ResenhaEntity resenhas = factory.manufacturePojo(ResenhaEntity.class);
			entityManager.persist(resenhas);
			resenhasList.add(resenhas);
		}
		for (int i = 0; i < 3; i++) {
			UsuarioEntity entity = factory.manufacturePojo(UsuarioEntity.class);
			entityManager.persist(entity);
			usuariosList.add(entity);
			if (i == 0) {
				resenhasList.get(i).setCritico(entity);
			}
		}
	}

	/**
	 * Prueba para remplazar las instancias de Resenha asociadas a una instancia de
	 * Usuario.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceUsuario() throws EntityNotFoundException {
		ResenhaEntity entity = resenhasList.get(0);
		resenhaUsuarioService.replaceUsuario(entity.getId(), usuariosList.get(1).getId());
		entity = resenhaService.getResenha(entity.getPelicula().getId(), entity.getId());
		assertEquals(entity.getCritico(), usuariosList.get(1));
	}
	
	/**
	 * Prueba para remplazar las instancias de Resenha asociadas a una instancia de
	 * Usuario con una resenha que no existe
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceUsuarioInvalidResenha() {
		assertThrows(EntityNotFoundException.class, ()->{
			resenhaUsuarioService.replaceUsuario(0L, usuariosList.get(1).getId());
		});
	}
	
	/**
	 * Prueba para remplazar las instancias de Resenha asociadas a una instancia de
	 * Usuario que no existe.
	 * 
	 * @throws EntityNotFoundException
	 */
	@Test
	void testReplaceInvalidUsuario() {
		assertThrows(EntityNotFoundException.class, ()->{
			ResenhaEntity entity = resenhasList.get(0);
			resenhaUsuarioService.replaceUsuario(entity.getId(), 0L);
		});
	}

	/**
	 * Prueba para desasociar un Resenha existente de un Usuario existente
	 * 
	 * @throws EntityNotFoundException
	 *
	 * @throws co.edu.uniandes.csw.resenhastore.exceptions.BusinessLogicException
	 */
	@Test
	void testRemoveUsuario() throws EntityNotFoundException {
		resenhaUsuarioService.removeUsuario(resenhasList.get(0).getId());
		ResenhaEntity response = resenhaService.getResenha(resenhasList.get(0).getPelicula().getId(), resenhasList.get(0).getId());
		assertNull(response.getCritico());
	}
	
	/**
	 * Prueba para desasociar un Resenha que no existe de un Usuario
	 * 
	 * @throws EntityNotFoundException
	 *
	 * @throws co.edu.uniandes.csw.resenhastore.exceptions.BusinessLogicException
	 */
	@Test
	void testRemoveUsuarioInvalidResenha() throws EntityNotFoundException {
		assertThrows(EntityNotFoundException.class, ()->{
			resenhaUsuarioService.removeUsuario(0L);
		});
	}
    
}
