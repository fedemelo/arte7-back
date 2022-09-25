package co.edu.uniandes.dse.arte7.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.entities.ActorEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.ErrorMessage;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import co.edu.uniandes.dse.arte7.repositories.ActorRepository;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class PeliculaActorService {
    

    @Autowired
	private PeliculaRepository peliculaRepository;

	@Autowired
	private ActorRepository actorRepository;

    /**
	 * Asocia un Actor existente a un Pelicula
	 *
	 * @param peliculaId   Identificador de la instancia de Pelicula
	 * @param actorId Identificador de la instancia de Actor
	 * @return Instancia de ActorEntity que fue asociada a Pelicula
	 */

    @Transactional
	public ActorEntity addActor(Long peliculaId, Long actorId) throws EntityNotFoundException {
		log.info("Inicia proceso de asociarle un actor a la pelicula con id = {0}", peliculaId);
		Optional<ActorEntity> actorEntity = actorRepository.findById(actorId);
		if (actorEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ACTOR_NOT_FOUND);

		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		actorEntity.get().getPeliculas().add(peliculaEntity.get());
		log.info("Termina proceso de asociarle un actor a la pelicula con id = {0}", peliculaId);
		return actorEntity.get();
	}


    /**
	 * Obtiene una colección de instancias de ActorEntity asociadas a una instancia
	 * de Pelicula
	 *
	 * @param peliculaId Identificador de la instancia de Pelicula
	 * @return Colección de instancias de ActorEntity asociadas a la instancia de
	 *         Pelicula
	 */
	@Transactional
	public List<ActorEntity> getActores(Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todos los actores de la pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		List<ActorEntity> actores = actorRepository.findAll();
		List<ActorEntity> actorList = new ArrayList<>();

		for (ActorEntity a : actores) {
			if (a.getPeliculas().contains(peliculaEntity.get())) {
				actorList.add(a);
			}
		}
		log.info("Termina proceso de consultar todos los libros del autor con id = {0}", peliculaId);
		return actorList;
	}

    /**
	 * Obtiene una instancia de ActorEntity asociada a una instancia de Pelicula
	 *
	 * @param peliculaId   Identificador de la instancia de Pelicula
	 * @param actorId Identificador de la instancia de Actor
	 * @return La entidad del Autor asociada al libro
	 */
	@Transactional
	public ActorEntity getActor(Long peliculaId, Long actorId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar un actor de la pelicula con id = {0}", peliculaId);
		Optional<ActorEntity> actorEntity = actorRepository.findById(actorId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (actorEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ACTOR_NOT_FOUND);

		if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		log.info("Termina proceso de consultar un actor de la pelicula con id = {0}", peliculaId);
		if (actorEntity.get().getPeliculas().contains(peliculaEntity.get()))
			return actorEntity.get();

		throw new IllegalOperationException("El actor no esta asociado con la pelicula");
	}

    @Transactional
	/**
	 * Remplaza las instancias de Actor asociadas a una instancia de Pelicula
	 *
	 * @param peliculaId Identificador de la instancia de Pelicula
	 * @param list    Colección de instancias de ActorEntity a asociar a instancia
	 *                de Pelicula
	 * @return Nueva colección de ActorEntity asociada a la instancia de Pelicula
	 */
	public List<ActorEntity> replaceActores(Long peliculaId, List<ActorEntity> list) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar los actores de la pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		for (ActorEntity actor : list) {
			Optional<ActorEntity> actorEntity = actorRepository.findById(actor.getId());
			if (actorEntity.isEmpty())
                throw new EntityNotFoundException(ErrorMessage.ACTOR_NOT_FOUND);

			if (!actorEntity.get().getPeliculas().contains(peliculaEntity.get()))
				actorEntity.get().getPeliculas().add(peliculaEntity.get());
		}
		log.info("Termina proceso de reemplazar los actores de la pelicula con id = {0}", peliculaId);
		return list;
	}

    @Transactional
	/**
	 * Desasocia un Actor existente de un Pelicula existente
	 *
	 * @param peliculaId   Identificador de la instancia de Pelicula
	 * @param actorId Identificador de la instancia de Actor
	 */
	public void removeActor(Long peliculaId, Long actorId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar un actor de la pelicula con id = {0}", peliculaId);
		Optional<ActorEntity> actorEntity = actorRepository.findById(actorId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (actorEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.ACTOR_NOT_FOUND);


		if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		actorEntity.get().getPeliculas().remove(peliculaEntity.get());

		log.info("Termina proceso de borrar un actor de la pelicula con id = {0}", peliculaId);
	}

}
