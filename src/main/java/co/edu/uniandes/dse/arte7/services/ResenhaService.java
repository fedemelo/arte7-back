package co.edu.uniandes.dse.arte7.services;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.ResenhaEntity;
import co.edu.uniandes.dse.arte7.repositories.ResenhaRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase que implementa la logica de 
 * Usuario
 */

@Slf4j
@Service
public class ResenhaService {

    @Autowired
    ResenhaRepository resenhaRepository;

<<<<<<< HEAD
	@Autowired
	PeliculaRepository peliculaRepository;
	
	
	/**
	 * Creación de una reseña en la base de datos */
	@Transactional
	public ResenhaEntity createResenha(Long peliculaId, ResenhaEntity resenhaEntity) throws EntityNotFoundException {
		log.info("Inicia el proceso de crear una reseña");
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No hay ninguna película");

		resenhaEntity.setPelicula(peliculaEntity.get());

		log.info("Termina el proceso de creación de la reseña");
		return resenhaRepository.save(resenhaEntity);
	}

	/** Obtiene la lista de reseñas que pertenecen a un pelicula */

	@Transactional
	public List<ResenhaEntity> getResenhas(Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia el proceso de consultar las reseñas que tiene la película con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No hay ninguna película");

		log.info("Termina proceso de consultar las reseñas asociadas al pelicula con id = {0}", peliculaId);
		return peliculaEntity.get().getResenhas();
	}

	/** Obtiene una reseña a partir de su ID, siempre y cuando exista para una película*/

	@Transactional
	public ResenhaEntity getResenha(Long peliculaId, Long resenhaId) throws EntityNotFoundException {
		log.info("Inicia el proceso de consultar la reseña con id = {0} de la película con id = " + peliculaId,
				resenhaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No hay ninguna película");

		Optional<ResenhaEntity> resenhaEntity = resenhaRepository.findById(resenhaId);
		if (resenhaEntity.isEmpty())
			throw new EntityNotFoundException("No hay ninguna reseña para esta película");

		log.info("Termina el proceso de consultar la reseña con id = {0} de la película con id = " + peliculaId, resenhaId);
		return resenhaRepository.findByPeliculaIdAndId(peliculaId, resenhaId);
	}

	/** Actualiza la información de una reseña */
	
	@Transactional
	public ResenhaEntity updateResenha(Long peliculaId, Long resenhaId, ResenhaEntity resenha) throws EntityNotFoundException {
		log.info("Inicia proceso de actualizar el resenha con id = {0} del libro con id = " + peliculaId,
				resenhaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No hay ninguna película");

		Optional<ResenhaEntity> resenhaEntity = resenhaRepository.findById(resenhaId);
		if (resenhaEntity.isEmpty())
			throw new EntityNotFoundException("No hay ninguna reseña para esta película");

		resenha.setId(resenhaId);
		resenha.setPelicula(peliculaEntity.get());
		log.info("Termina el proceso de actualizar la reseña con id = {0} de la película con id = " + peliculaId, resenhaId);
		return resenhaRepository.save(resenha);
	}

	/** Elimina una reseña de la database*/
	
	@Transactional
	public void deleteResenha(Long peliculaId, Long resenhaId) throws EntityNotFoundException {
		log.info("Inicia el proceso de borrar la reseña con id = {0} de la películacon id = " + peliculaId,
				resenhaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No hay ninguna película");

		ResenhaEntity resenha = getResenha(peliculaId, resenhaId);
		if (resenha == null) {
			throw new EntityNotFoundException("No hay ninguna reseña para esta película");
=======
     /**
	 * Crea una resenha en la persistencia.
	 *
	 * @param resenhaEntity La entidad que representa el resenha a persistir.
	 * @return La entidad del resenha luego de persistirla.
	 * @throws IllegalOperationException Si el resenha a persistir ya existe.
	 */
    @Transactional
	public ResenhaEntity createResenha(ResenhaEntity resenhaEntity) throws IllegalOperationException {
		log.info("Inicia proceso de creación de un resenha");
		if (!resenhaRepository.findByUsername(resenhaEntity.getUsername()).isEmpty()) {
			throw new IllegalOperationException("El nombre de resenha ya existe");
		}
		log.info("Termina proceso de creación de un resenha");
		return resenhaRepository.save(resenhaEntity);
	}

	/**
	 *
	 * Obtener todas los resenhas existentes en la base de datos.
	 *
	 * @return una lista de resenhas.
	 */
	@Transactional
	public List<ResenhaEntity> getResenhas() {
		log.info("Inicia proceso de consultar todas los resenhas");
		return resenhaRepository.findAll();
	}

    /**
	 *
	 * Obtener un resenha por medio de su id.
	 *
	 * @param resenhaId: id de la resenha para ser buscada.
	 * @return la resenha solicitado por medio de su id.
	 */
	@Transactional
	public ResenhaEntity getResenha(Long resenhaId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar el resenha con id = {0}", resenhaId);
		Optional<ResenhaEntity> resenha = resenhaRepository.findById(resenhaId);
		if (resenha.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.RESENHA_NOT_FOUND);
		log.info("Termina proceso de consultar el resenha con id = {0}", resenhaId);
		return resenha.get();
	}

    /**
	 *
	 * Actualizar un resenha.
	 *
	 * @param editorialId: id del resenha para buscarlo en la base de datos.
	 * @param editorial: resenha con los cambios para ser actualizado.
	 * @return el resenha con los cambios actualizados en la base de datos.
	 */
	@Transactional
	public ResenhaEntity updateResenha(Long resenhaId, ResenhaEntity resenha) throws EntityNotFoundException {
		log.info("Inicia proceso de actualizar el resenha con id = {0}", resenhaId);
		Optional<ResenhaEntity> resenhaEntity = resenhaRepository.findById(resenhaId);
		if (resenhaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.RESENHA_NOT_FOUND);

		resenha.setId(resenhaId);
		log.info("Termina proceso de actualizar el resenha con id = {0}", resenhaId);
		return resenhaRepository.save(resenha);
	}

    /**
	 * Borrar una resenha
	 *
	 * @param editorialId: id del resenha a borrar
	 * @throws BusinessLogicException Si el resenha a eliminar tiene resenhas.
	 */
	@Transactional
	public void deleteResenha(Long resenhaId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de borrar el resenha con id = {0}", resenhaId);
		Optional<ResenhaEntity> resenhaEntity = resenhaRepository.findById(resenhaId);
		if (resenhaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.RESENHA_NOT_FOUND);

		List<ResenhaEntity> resenhas = resenhaEntity.get().getResenhas();

		if (!resenhas.isEmpty()) {
			throw new IllegalOperationException(
					"Fue imposible eliminar el resenha con id = " + resenhaId + " porque tiene resenhas asociadas");
>>>>>>> develop
		}

		resenhaRepository.deleteById(resenhaId);
<<<<<<< HEAD
		log.info("Termina proceso de borrar el resenha con id = {0} del libro con id = " + peliculaId, resenhaId);
=======
		log.info("Termina proceso de borrar el resenha con id = {0}", resenhaId);
>>>>>>> develop
	}
    
}
