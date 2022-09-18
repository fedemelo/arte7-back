package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.entities.ResenhaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.ErrorMessage;
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
	 * Se encarga de crear un Resenha en la base de datos.
	 *
	 * @param resenhaEntity Objeto de ResenhaEntity con los datos nuevos
	 * @param peliculaId       id del Pelicula el cual sera padre del nuevo Resenha.
	 * @return Objeto de ResenhaEntity con los datos nuevos y su ID.
	 * @throws EntityNotFoundException si el pelicula no existe.
	 *
	 */
	@Transactional
	public ResenhaEntity createResenha(Long peliculaId, ResenhaEntity resenhaEntity) throws EntityNotFoundException {
		log.info("Inicia proceso de crear resenha");
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		resenhaEntity.setPelicula(peliculaEntity.get());

		log.info("Termina proceso de creación del resenha");
		return resenhaRepository.save(resenhaEntity);
	}

	/**
	 * Obtiene la lista de los registros de Resenha que pertenecen a un Pelicula.
	 *
	 * @param peliculaId id del Pelicula el cual es padre de los Resenhas.
	 * @return Colección de objetos de ResenhaEntity.
	 */

	@Transactional
	public List<ResenhaEntity> getResenhas(Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar los resenhas asociados al pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException("No se encontro la pelicula.");

		log.info("Termina proceso de consultar los resenhas asociados al pelicula con id = {0}", peliculaId);
		return peliculaEntity.get().getResenhas();
	}

	/**
	 * Obtiene los datos de una instancia de Resenha a partir de su ID. La existencia
	 * del elemento padre Pelicula se debe garantizar.
	 *
	 * @param peliculaId   El id del Libro buscado
	 * @param resenhaId Identificador de la Reseña a consultar
	 * @return Instancia de ResenhaEntity con los datos del Resenha consultado.
	 *
	 */
	@Transactional
	public ResenhaEntity getResenha(Long peliculaId, Long resenhaId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar el resenha con id = {0} del libro con id = " + peliculaId,
				resenhaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException("No se encontro la pelicula.");

		Optional<ResenhaEntity> resenhaEntity = resenhaRepository.findById(resenhaId);
		if (resenhaEntity.isEmpty())
            throw new EntityNotFoundException("No se encontro la resenha.");

		log.info("Termina proceso de consultar el resenha con id = {0} del libro con id = " + peliculaId,
				resenhaId);
		return resenhaRepository.findByPeliculaIdAndId(peliculaId, resenhaId);
	}

	/**
	 * Actualiza la información de una instancia de Resenha.
	 *
	 * @param resenhaEntity Instancia de ResenhaEntity con los nuevos datos.
	 * @param peliculaId       id del Pelicula el cual sera padre del Resenha actualizado.
	 * @param resenhaId     id de la resenha que será actualizada.
	 * @return Instancia de ResenhaEntity con los datos actualizados.
	 *
	 */
	@Transactional
	public ResenhaEntity updateResenha(Long peliculaId, Long resenhaId, ResenhaEntity resenha) throws EntityNotFoundException {
		log.info("Inicia proceso de actualizar el resenha con id = {0} del libro con id = " + peliculaId,
				resenhaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		Optional<ResenhaEntity> resenhaEntity = resenhaRepository.findById(resenhaId);
		if (resenhaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.RESENHA_NOT_FOUND);

		resenha.setId(resenhaId);
		resenha.setPelicula(peliculaEntity.get());
		log.info("Termina proceso de actualizar el resenha con id = {0} del libro con id = " + peliculaId,
				resenhaId);
		return resenhaRepository.save(resenha);
	}

	/**
	 * Elimina una instancia de Resenha de la base de datos.
	 *
	 * @param resenhaId Identificador de la instancia a eliminar.
	 * @param peliculaId   id del Pelicula el cual es padre del Resenha.
	 * @throws EntityNotFoundException Si la reseña no esta asociada al libro.
	 *
	 */
	@Transactional
	public void deleteResenha(Long peliculaId, Long resenhaId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar el resenha con id = {0} del libro con id = " + peliculaId,
				resenhaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		ResenhaEntity resenha = getResenha(peliculaId, resenhaId);
		if (resenha == null) {
			throw new EntityNotFoundException(ErrorMessage.RESENHA_NOT_FOUND);
		}
		resenhaRepository.deleteById(resenhaId);
		log.info("Termina proceso de borrar el resenha con id = {0} del libro con id = " + peliculaId,
				resenhaId);
	}
}
