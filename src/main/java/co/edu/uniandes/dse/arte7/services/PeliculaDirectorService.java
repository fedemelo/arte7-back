package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.DirectorEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.DirectorRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Conecta con la persistencia para la relacion de Pelicula a Director.
 *
 * @author Federico Melo Barrero
 */

@Slf4j
@Service
public class PeliculaDirectorService {
	@Autowired
	private PeliculaRepository peliculaRepository;

	@Autowired
	private DirectorRepository directorRepository;
	
	/**
	 * Asocia un Director existente a una Pelicula
	 *
	 * @param peliculaId   Identificador de la instancia de Pelicula
	 * @param directorId Identificador de la instancia de Director
	 * @return Instancia de DirectorEntity que fue asociada a Pelicula
	 */
	@Transactional
	public DirectorEntity addDirector(Long peliculaId, Long directorId) throws EntityNotFoundException {
		log.info("Inicia proceso de asociarle un director a la pelicula con id = {0}", peliculaId);
		Optional<DirectorEntity> directorEntity = directorRepository.findById(directorId);
		if (directorEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra el director con el id provisto.");

		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra la pelicula con el id provisto.");

		peliculaEntity.get().getDirectores().add(directorEntity.get());
		log.info("Termina proceso de asociarle un director a la pelicula con id = {0}", peliculaId);
		return directorEntity.get();
	}

	/**
	 * Obtiene una colección de instancias de DirectorEntity asociadas a una instancia
	 * de Pelicula
	 *
	 * @param peliculaId Identificador de la instancia de Pelicula
	 * @return Colección de instancias de DirectorEntity asociadas a la instancia de
	 *         Pelicula
	 */
	@Transactional
	public List<DirectorEntity> getDirectores(Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todos los directores de la pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra la pelicula con el id provisto.");
		log.info("Finaliza proceso de consultar todos los directores de la pelicula con id = {0}", peliculaId);
		return peliculaEntity.get().getDirectores();
	}

	/**
	 * Obtiene una instancia de DirectorEntity asociada a una instancia de Pelicula
	 *
	 * @param peliculaId   Identificador de la instancia de Pelicula
	 * @param directorId Identificador de la instancia de Director
	 * @return La entidad del Director asociada a la pelicula
	 */
	@Transactional
	public DirectorEntity getDirector(Long peliculaId, Long directorId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar un director de la pelicula con id = {0}", peliculaId);
		Optional<DirectorEntity> directorEntity = directorRepository.findById(directorId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (directorEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra el director con el id provisto.");

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra la pelicula con el id provisto.");
		log.info("Termina proceso de consultar un director de la pelicula con id = {0}", peliculaId);
		if (peliculaEntity.get().getDirectores().contains(directorEntity.get()))
			return directorEntity.get();

		throw new IllegalOperationException("El director no esta asociado a la pelicula.");
	}

	@Transactional
	/**
	 * Remplaza las instancias de Director asociadas a una instancia de Pelicula
	 *
	 * @param peliculaId Identificador de la instancia de Pelicula
	 * @param list    Colección de instancias de DirectorEntity a asociar a instancia
	 *                de Pelicula
	 * @return Nueva colección de DirectorEntity asociada a la instancia de Pelicula
	 */
	public List<DirectorEntity> replaceDirectores(Long peliculaId, List<DirectorEntity> list) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar los directores de la pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra la pelicula con el id provisto.");

		for (DirectorEntity director : list) {
			Optional<DirectorEntity> directorEntity = directorRepository.findById(director.getId());
			if (directorEntity.isEmpty())
				throw new EntityNotFoundException("No se encuentra el director con el id provisto.");

			if (!peliculaEntity.get().getDirectores().contains(directorEntity.get()))
				peliculaEntity.get().getDirectores().add(directorEntity.get());
		}
		log.info("Termina proceso de reemplazar los directores de la pelicula con id = {0}", peliculaId);
		return getDirectores(peliculaId);
	}

	@Transactional
	/**
	 * Desasocia un Director existente de una Pelicula existente
	 *
	 * @param peliculaId   Identificador de la instancia de Pelicula
	 * @param directorId Identificador de la instancia de Director
	 */
	public void removeDirector(Long peliculaId, Long directorId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar un director de la pelicula con id = {0}", peliculaId);
		Optional<DirectorEntity> directorEntity = directorRepository.findById(directorId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (directorEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra el director con el id provisto.");

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra la pelicula con el id provisto.");

		peliculaEntity.get().getDirectores().remove(directorEntity.get());

		log.info("Termina proceso de borrar un director de la pelicula con id = {0}", peliculaId);
	}
}
