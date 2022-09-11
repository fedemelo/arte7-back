package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;
import co.edu.uniandes.dse.arte7.entities.ResenhaEntity;
import co.edu.uniandes.dse.arte7.entities.UsuarioEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.UsuarioRepository;

/**
 * Clase que implementa la logica de 
 * Usuario
 */

@Slf4j
@Service
public class UsuarioService {

    @Autowired
    UsuarioRepository usuarioRepository;

    /**
	 * Crea una usuario en la persistencia.
	 *
	 * @param usuarioEntity La entidad que representa el usuario a persistir.
	 * @return La entidad del usuario luego de persistirla.
	 * @throws IllegalOperationException Si el usuario a persistir ya existe.
	 */
    @Transactional
	public UsuarioEntity createUsuario(UsuarioEntity usuarioEntity) throws IllegalOperationException {
		log.info("Inicia proceso de creación de un usuario");
		if (!usuarioRepository.findByUsername(usuarioEntity.getUsername()).isEmpty()) {
			throw new IllegalOperationException("El nombre de usuario ya existe");
		}
		log.info("Termina proceso de creación de un usuario");
		return usuarioRepository.save(usuarioEntity);
	}

	/**
	 *
	 * Obtener todas los usuarios existentes en la base de datos.
	 *
	 * @return una lista de usuarios.
	 */
	@Transactional
	public List<UsuarioEntity> getUsuarios() {
		log.info("Inicia proceso de consultar todas los usuarios");
		return usuarioRepository.findAll();
	}

    /**
	 *
	 * Obtener un usuario por medio de su id.
	 *
	 * @param usuarioId: id del usuario para ser buscado.
	 * @return el usuario solicitado por medio de su id.
	 */
	@Transactional
	public UsuarioEntity getUsuario(Long usuarioId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar el usuario con id = {0}", usuarioId);
		Optional<UsuarioEntity> usuario = usuarioRepository.findById(usuarioId);
		if (usuario.isEmpty())
			throw new EntityNotFoundException("El usuario con el id dado no fue encontrado");
		log.info("Termina proceso de consultar el usuario con id = {0}", usuarioId);
		return usuario.get();
	}

    /**
	 *
	 * Actualizar un usuario.
	 *
	 * @param editorialId: id del usuario para buscarlo en la base de datos.
	 * @param editorial: usuario con los cambios para ser actualizado.
	 * @return el usuario con los cambios actualizados en la base de datos.
	 */
	@Transactional
	public UsuarioEntity updateUsuario(Long usuarioId, UsuarioEntity usuario) throws EntityNotFoundException {
		log.info("Inicia proceso de actualizar el usuario con id = {0}", usuarioId);
		Optional<UsuarioEntity> usuarioEntity = usuarioRepository.findById(usuarioId);
		if (usuarioEntity.isEmpty())
			throw new EntityNotFoundException("El usuario con el id dado no fue encontrado");

		usuario.setId(usuarioId);
		log.info("Termina proceso de actualizar el usuario con id = {0}", usuarioId);
		return usuarioRepository.save(usuario);
	}

    /**
	 * Borrar un usuario
	 *
	 * @param editorialId: id del usuario a borrar
	 * @throws BusinessLogicException Si el usuario a eliminar tiene resenhas.
	 */
	@Transactional
	public void deleteUsuario(Long usuarioId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de borrar el usuario con id = {0}", usuarioId);
		Optional<UsuarioEntity> usuarioEntity = usuarioRepository.findById(usuarioId);
		if (usuarioEntity.isEmpty())
			throw new EntityNotFoundException("El usuario con el id dado no fue encontrado");

		List<ResenhaEntity> resenhas = usuarioEntity.get().getResenhas();

		if (!resenhas.isEmpty()) {
			throw new IllegalOperationException(
					"Fue imposible eliminar el usuario con id = " + usuarioId + " porque tiene resenhas asociadas");
		}

		usuarioRepository.deleteById(usuarioId);
		log.info("Termina proceso de borrar el usuario con id = {0}", usuarioId);
	}

    
}
