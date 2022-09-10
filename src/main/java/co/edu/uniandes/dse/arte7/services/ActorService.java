package co.edu.uniandes.dse.arte7.services;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.ActorEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.ActorRepository;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Service
public class ActorService {

	@Autowired
	ActorRepository actorRepository;
	
	/**
	 * Se encarga de crear un Actor en la base de datos.
	 *
	 * @param actor Objeto de ActorEntity con los datos nuevos
	 * @return Objeto de ActorEntity con los datos nuevos y su ID.
	 * @throws IllegalOperationException 
	 */
	@Transactional
	public ActorEntity createActor(ActorEntity actor) throws IllegalOperationException {
		log.info("Inicia proceso de creación del actor");

        /* Regla de negocio: Fecha de nacimiento debe ser anterior a fecha actual. */
		Calendar calendar = Calendar.getInstance();
		if(actor.getFechaNacimiento().compareTo(calendar.getTime()) > 0) {
			throw new IllegalOperationException("La fecha de nacimiento es posterior a la fecha actual.");
	    }
		
		return actorRepository.save(actor);
	}

	/**
	 * Obtiene la lista de los registros de Actor.
	 *
	 * @return Colección de objetos de ActorEntity.
	 */
	@Transactional
	public List<ActorEntity> getActores() {
		log.info("Inicia proceso de consultar todos los actores");
		return actorRepository.findAll();
	}

	/**
	 * Obtiene los datos de una instancia de Actor a partir de su ID.
	 *
	 * @param actorId Identificador de la instancia a consultar
	 * @return Instancia de ActorEntity con los datos del Actor consultado.
	 */
	@Transactional
	public ActorEntity getActor(Long actorId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar el actor con id = {0}", actorId);
		Optional<ActorEntity> actorEntity = actorRepository.findById(actorId);
		if (actorEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra el actor.");
		log.info("Termina proceso de consultar el actor con id = {0}", actorId);
		return actorEntity.get();
	}

	/**
	 * Actualiza la información de una instancia de Actor.
	 *
	 * @param actorId     Identificador de la instancia a actualizar
	 * @param actorEntity Instancia de ActorEntity con los nuevos datos.
	 * @return Instancia de ActorEntity con los datos actualizados.
	 */
	@Transactional
	public ActorEntity updateActor(Long actorId, ActorEntity actor) throws EntityNotFoundException {
		log.info("Inicia proceso de actualizar el actor con id = {0}", actorId);
		Optional<ActorEntity> actorEntity = actorRepository.findById(actorId);
		if (actorEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra el actor.");
		log.info("Termina proceso de actualizar el actor con id = {0}", actorId);
		actor.setId(actorId);
		return actorRepository.save(actor);
	}

	/**
	 * Elimina una instancia de Actor de la base de datos.
	 *
	 * @param actorId Identificador de la instancia a eliminar.
	 * @throws BusinessLogicException si el actor tiene libros asociados.
	 */
	@Transactional
	public void deleteActor(Long actorId) throws IllegalOperationException, EntityNotFoundException {
		log.info("Inicia proceso de borrar el actor con id = {0}", actorId);
		Optional<ActorEntity> actorEntity = actorRepository.findById(actorId);
		if (actorEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra el actor.");

		List<PeliculaEntity> peliculas = actorEntity.get().getPeliculas();
		if (!peliculas.isEmpty())
			throw new IllegalOperationException("No es posible eliminar el actor porque hay películas asociadas a este.");

		actorRepository.deleteById(actorId);
		log.info("Termina proceso de borrar el actor con id = {0}", actorId);
	}
}