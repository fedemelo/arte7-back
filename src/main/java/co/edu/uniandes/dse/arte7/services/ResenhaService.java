package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.entities.ResenhaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import co.edu.uniandes.dse.arte7.repositories.ResenhaRepository;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class ResenhaService {

	@Autowired
	ResenhaRepository resenhaRepository;

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
		}
		resenhaRepository.deleteById(resenhaId);
		log.info("Termina proceso de borrar el resenha con id = {0} del libro con id = " + peliculaId, resenhaId);
	}
}
