package co.edu.uniandes.dse.arte7.services;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uniandes.dse.arte7.entities.ResenhaEntity;
import co.edu.uniandes.dse.arte7.entities.UsuarioEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.repositories.ResenhaRepository;
import co.edu.uniandes.dse.arte7.repositories.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ResenhaUsuarioService {

    @Autowired
    private ResenhaRepository resenhaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
	 * Reemplazar el usuario de una resenha.
	 *
	 * @param resenhaId      id de la resenha que se quiere actualizar.
	 * @param usuarioId      El id del usuario que será de la resenha.
	 * @return la nueva resenha.
	 */

	@Transactional
	public ResenhaEntity replaceUsuario(Long resenhaId, Long usuarioId) throws EntityNotFoundException {
		log.info("Inicia proceso de actualizar resenha con id = {0}", resenhaId);
		Optional<ResenhaEntity> resenhaEntity = resenhaRepository.findById(resenhaId);
        if (resenhaEntity.isEmpty())
			throw new EntityNotFoundException("La resenha con el id dado no fue encontrada");

        Optional<UsuarioEntity> usuarioEntity = usuarioRepository.findById(usuarioId);
        if(usuarioEntity.isEmpty())
            throw new EntityNotFoundException("El usuario con el id dado no fue encontrado");

		resenhaEntity.get().setCritico(usuarioEntity.get());
		log.info("Termina proceso de actualizar resenha con id = {0}", resenhaId);

		return resenhaEntity.get();
	}

    /**
	 * Borrar una resenha de un usuario. Este metodo se utiliza para borrar la
	 * relacion de una resenha.
	 *
	 * @param resenhaId La resenha que se desea borrar el usuario.
	 */
	@Transactional
	public void removeUsuario(Long resenhaId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar el usuario de la resenha con id = {0}", resenhaId);
		Optional<ResenhaEntity> resenhaEntity = resenhaRepository.findById(resenhaId);
		if (resenhaEntity.isEmpty())
			throw new EntityNotFoundException("La resenha con el id dado no fue encontrada");

		Optional<UsuarioEntity> usuarioEntity = usuarioRepository
				.findById(resenhaEntity.get().getCritico().getId());
		usuarioEntity.ifPresent(usuario -> usuario.getResenhas().remove(resenhaEntity.get()));

		resenhaEntity.get().setCritico(null);
		log.info("Termina proceso de borrar el usuario de la resenha con id = {0}", resenhaId);
	}
    
}
