package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.repositories.NominacionRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import lombok.extern.slf4j.Slf4j;

import co.edu.uniandes.dse.arte7.entities.NominacionEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;

import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.ErrorMessage;

@Slf4j
@Service

public class NominacionPeliculaService {
    @Autowired
    private NominacionRepository nominacionRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

	/**
	 * Asocia una Pelicula existente a una nominacion
	 * 
	 * @param nominacionId identificador de la instancia de nominacion
	 * @param peliculaId identificador de la instancia de pelicula
	 * @return Instancia de PeliculaEntity que fue asociada a una nominacion
	 * @throws EntityNotFoundException
	 */
    @Transactional
	public PeliculaEntity addPelicula(Long nominacionId, Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de asociarle una película a la nominación con id = {0}", nominacionId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);
		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.NOMINACION_NOT_FOUND);

            nominacionEntity.get().getPeliculas().add(peliculaEntity.get());
		log.info("Termina proceso de asociarle una película a la nominacion con id = {0}", nominacionId);
		return peliculaEntity.get();
	}

	/**
	 * Asocia las instancias de Pelicula a una instancia de Nominacion
	 * 
	 * @param nominacionId Identificador de la instancia de nominacion
	 * @param peliculas Identificador de la instancia de la pelicula
	 * @return Nueva colección de PeliculaEntity asociada a la instancia de Actor
	 * @throws EntityNotFoundException
	 */
	@Transactional
	public List<PeliculaEntity> addPeliculas(Long nominacionId, List<PeliculaEntity> peliculas) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las peliculas asociadas al nominacion con id = {0}", nominacionId);
		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);
		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.NOMINACION_NOT_FOUND);

		for (PeliculaEntity pelicula : peliculas) {
			Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(pelicula.getId());
			if (peliculaEntity.isEmpty())
				throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

			if (!peliculaEntity.get().getNominaciones().contains(nominacionEntity.get()))
				peliculaEntity.get().getNominaciones().add(nominacionEntity.get());
		}
		log.info("Finaliza proceso de reemplazar las peliculas asociados al nominacion con id = {0}", nominacionId);
		return peliculas;
	}

	/**
	 * Obtiene las peliculas asociadas a una instancia de nominacion
	 * 
	 * @param nominacionId Identificador de la instancia nominacion
	 * @return Conjunto de Peliculas asociadas a nominacionEntity
	 * @throws EntityNotFoundException
	 */
    @Transactional
	public List<PeliculaEntity> getPeliculas(Long nominacionId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todas las películas de nominacion con id = {0}", nominacionId);
		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);
		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.NOMINACION_NOT_FOUND);
		log.info("Finaliza proceso de consultar todas las películas del nominacion con id = {0}", nominacionId);
		return nominacionEntity.get().getPeliculas();
	}

	/**
	 * Obtiene una instancia de pelicula asociada a una instancia de nominacion
	 * 
	 * @param nominacionId Identificador de la instancia nominacion
	 * @param peliculaId Identificador de la instancia de la pelicula
	 * @return PeliculaEntity asociada a la nominacion
	 * @throws EntityNotFoundException
	 * @throws IllegalOperationException
	 */
    @Transactional
	public PeliculaEntity getPelicula(Long nominacionId, Long peliculaId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar una película del nominacion con id = {0}", nominacionId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.NOMINACION_NOT_FOUND);

		log.info("Termina proceso de consultar una película del nominacion con id = {0}", nominacionId);
		if (nominacionEntity.get().getPeliculas().contains(peliculaEntity.get()))
			return peliculaEntity.get();

		throw new IllegalOperationException("La película no está asociada a la nominación");
	}

	/**
	 * Reemplaza las peliculas asociadas a una instancia de nominacion
	 * 
	 * @param nominacionId Identificador de la instancia nominacion
	 * @param list Lista de nuevas peliculas que reeemplazarán a las ya existentes
	 * @return Conjunto de peliculas que reeemplazaron a las ya existentes
	 * @throws EntityNotFoundException
	 */
    @Transactional
    public List<PeliculaEntity> replacePeliculas(Long nominacionId, List<PeliculaEntity> list) 
            throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las peliculas del nominacion con id = {0}", nominacionId);
		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);
		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.NOMINACION_NOT_FOUND);

		for (PeliculaEntity pelicula : list) {
			Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(pelicula.getId());
			if (peliculaEntity.isEmpty())
				throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

			if (!nominacionEntity.get().getPeliculas().contains(peliculaEntity.get()))
            nominacionEntity.get().getPeliculas().add(peliculaEntity.get());
		}
		log.info("Termina proceso de reemplazar las peliculas del nominacion con id = {0}", nominacionId);
		return getPeliculas(nominacionId);
	}

	/**
	 * Remueve una instancia de pelicula asociada a una instancia de nominacion
	 * 
	 * @param nominacionId Identificador de la instancia nominacion
	 * @param peliculaId Identificador de la instancia pelicula
	 * @throws EntityNotFoundException
	 */
    @Transactional
    public void removePelicula(Long nominacionId, Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar una pelicula del nominacion con id = {0}", nominacionId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.NOMINACION_NOT_FOUND);

            nominacionEntity.get().getPeliculas().remove(peliculaEntity.get());

		log.info("Termina proceso de borrar una pelicula del nominacion con id = {0}",nominacionId);
	}
}