package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.NominacionEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.NominacionRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Conecta con la persistencia para la relacion de Pelicula a Nominacion.
 *
 * @author Federico Melo Barrero
 */

@Slf4j
@Service
public class PeliculaNominacionService {

    @Autowired
	private PeliculaRepository peliculaRepository;

	@Autowired
	private NominacionRepository nominacionRepository;

    /**
	 * Asocia una Nominacion existente a una Pelicula
	 *
	 * @param peliculaId   Identificador de la instancia de Pelicula
	 * @param nominacionId Identificador de la instancia de Nominacion
	 * @return Instancia de NominacionEntity que fue asociada a Pelicula
	 */
	@Transactional
	public NominacionEntity addNominacion(Long peliculaId, Long nominacionId) throws EntityNotFoundException {
		log.info("Inicia proceso de asociarle una nominacion a la pelicula con id = {0}", peliculaId);
		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);
		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra la nominacion con el id provisto.");

		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra la pelicula con el id provisto.");

		peliculaEntity.get().getNominaciones().add(nominacionEntity.get());
		log.info("Termina proceso de asociarle una nominacion a la pelicula con id = {0}", peliculaId);
		return nominacionEntity.get();
	}

	/**
	 * Obtiene una colección de instancias de NominacionEntity asociadas a una instancia
	 * de Pelicula
	 *
	 * @param peliculaId Identificador de la instancia de Pelicula
	 * @return Colección de instancias de NominacionEntity asociadas a la instancia de
	 *         Pelicula
	 */
	@Transactional
	public List<NominacionEntity> getNominaciones(Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todos las nominaciones de la pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra la pelicula con el id provisto.");
		log.info("Finaliza proceso de consultar todos las nominaciones de la pelicula con id = {0}", peliculaId);
		return peliculaEntity.get().getNominaciones();
	}

	/**
	 * Obtiene una instancia de NominacionEntity asociada a una instancia de Pelicula
	 *
	 * @param peliculaId   Identificador de la instancia de Pelicula
	 * @param nominacionId Identificador de la instancia de Nominacion
	 * @return La entidad del Autor asociada a la pelicula
	 */
	@Transactional
	public NominacionEntity getNominacion(Long peliculaId, Long nominacionId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar una nominacion de la pelicula con id = {0}", peliculaId);
		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra la nominacion con el id provisto.");

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra la pelicula con el id provisto.");
		log.info("Termina proceso de consultar una nominacion de la pelicula con id = {0}", peliculaId);
		if (peliculaEntity.get().getNominaciones().contains(nominacionEntity.get()))
			return nominacionEntity.get();

		throw new IllegalOperationException("La nominacion no se encuentra asociada a la pelicula.");
	}

	@Transactional
	/**
	 * Remplaza las instancias de Nominacion asociadas a una instancia de Pelicula
	 *
	 * @param peliculaId Identificador de la instancia de Pelicula
	 * @param list    Colección de instancias de NominacionEntity a asociar a instancia
	 *                de Pelicula
	 * @return Nueva colección de NominacionEntity asociada a la instancia de Pelicula
	 */
	public List<NominacionEntity> replaceNominacions(Long peliculaId, List<NominacionEntity> list) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las nominaciones de la pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra la pelicula con el id provisto.");

		for (NominacionEntity nominacion : list) {
			Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacion.getId());
			if (nominacionEntity.isEmpty())
				throw new EntityNotFoundException("No se encuentra la nominacion con el id provisto.");

			if (!peliculaEntity.get().getNominaciones().contains(nominacionEntity.get()))
				peliculaEntity.get().getNominaciones().add(nominacionEntity.get());
		}
		log.info("Termina proceso de reemplazar las nominaciones de la pelicula con id = {0}", peliculaId);
		return getNominaciones(peliculaId);
	}

	@Transactional
	/**
	 * Desasocia una Nominacion existente de una Pelicula existente
	 *
	 * @param peliculaId   Identificador de la instancia de Pelicula
	 * @param nominacionId Identificador de la instancia de Nominacion
	 */
	public void removeNominacion(Long peliculaId, Long nominacionId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar una nominacion de la pelicula con id = {0}", peliculaId);
		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra la nominacion con el id provisto.");

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra la pelicula con el id provisto.");

		peliculaEntity.get().getNominaciones().remove(nominacionEntity.get());

		log.info("Termina proceso de borrar una nominacion de la pelicula con id = {0}", peliculaId);
	}
}
