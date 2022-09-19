package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.ResenhaEntity;
import co.edu.uniandes.dse.arte7.entities.UsuarioEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.ErrorMessage;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.ResenhaRepository;
import co.edu.uniandes.dse.arte7.repositories.UsuarioRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase que implementa la conexión con la persistencia para la relación entre
 * la entidad Usuario y Resehna.
 *
 * @author ISIS2603
 */
@Slf4j
@Service
public class UsuarioResenhaService {

    @Autowired
    private ResenhaRepository resenhaRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
	 * Agregar una resenha a un usuario
	 *
	 * @param resenhaId      El id de la resenha a guardar
	 * @param usuarioId    El id del usuario en el cual se va a guardar la resenha.
	 * @return La resenha creada.
	 * @throws EntityNotFoundException 
	 */

    @Transactional
	public ResenhaEntity addResenha(Long resenhaId, Long usuarioId) throws EntityNotFoundException {
		log.info("Inicia proceso de agregarle una resenha al usuario con id = {0}", usuarioId);
		
		Optional<ResenhaEntity> resenhaEntity = resenhaRepository.findById(resenhaId);
		if(resenhaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.RESENHA_NOT_FOUND);
		
		Optional<UsuarioEntity> usuarioEntity = usuarioRepository.findById(usuarioId);
		if(usuarioEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.USUARIO_NOT_FOUND);
		
		resenhaEntity.get().setCritico(usuarioEntity.get());
		log.info("Termina proceso de agregarle una resehna al usuario con id = {0}", usuarioId);
		return resenhaEntity.get();
	}

    /**
	 * Retorna todas las resenhas asociadas a un usuario
	 *
	 * @param usuarioId El ID del usuario buscado
	 * @return La lista de resenhas del usuario
	 * @throws EntityNotFoundException si el usuario no existe
	 */
	@Transactional
	public List<ResenhaEntity> getResenhas(Long usuarioId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar las resenhas asociadas al usuario con id = {0}", usuarioId);
		Optional<UsuarioEntity> usuarioEntity = usuarioRepository.findById(usuarioId);
		if(usuarioEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.USUARIO_NOT_FOUND);
		
		return usuarioEntity.get().getResenhas();
	}

    /**
	 * Retorna una resenha asociada a un usuario
	 *
	 * @param usuarioId El id del usuario a buscar.
	 * @param resehnaId      El id de la resenha a buscar
	 * @return La resehna encontrada del usuario.
	 * @throws EntityNotFoundException Si la resenha no se encuentra en el usuario
	 * @throws IllegalOperationException Si la resenha no está asociada al usuario
	 */
	@Transactional
	public ResenhaEntity getResenha(Long usuarioId, Long resenhaId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar la resehna con id = {0} del usuario con id = " + resenhaId, usuarioId);
		
		Optional<UsuarioEntity> usuarioEntity = usuarioRepository.findById(usuarioId);
		if(usuarioEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.USUARIO_NOT_FOUND);
		
		Optional<ResenhaEntity> resenhaEntity = resenhaRepository.findById(resenhaId);
		if(resenhaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.RESENHA_NOT_FOUND);
				
		log.info("Termina proceso de consultar la resehna con id = {0} del usuario con id = " + resenhaId, usuarioId);
		
		if(usuarioEntity.get().getResenhas().contains(resenhaEntity.get()))
			return resenhaEntity.get();
		
		throw new IllegalOperationException("La resenha no se encuentra asociada al usuario");
	}
    
    /**
	 * Remplazar resenhas de un usuario
	 *
	 * @param resenhas       Lista de resenhas que serán los del usuario.
	 * @param usuarioId El id del usuario que se quiere actualizar.
	 * @return La lista de resenhas actualizada.
	 * @throws EntityNotFoundException Si el usuario o una resenha de la lista no se encuentran
	 */
	@Transactional
	public List<ResenhaEntity> replaceResenhas(Long usuarioId, List<ResenhaEntity> resenhas) throws EntityNotFoundException {
		log.info("Inicia proceso de actualizar las resenhas del usuario con id = {0}", usuarioId);
		Optional<UsuarioEntity> usuarioEntity = usuarioRepository.findById(usuarioId);
		if(usuarioEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.USUARIO_NOT_FOUND);
		
		for(ResenhaEntity resenha : resenhas) {
			Optional<ResenhaEntity> r = resenhaRepository.findById(resenha.getId());
			if(r.isEmpty())
				throw new EntityNotFoundException(ErrorMessage.RESENHA_NOT_FOUND);
			
			r.get().setCritico(usuarioEntity.get());
		}		
		return resenhas;
	}
}
