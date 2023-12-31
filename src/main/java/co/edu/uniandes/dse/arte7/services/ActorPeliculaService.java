package co.edu.uniandes.dse.arte7.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.ActorEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.ErrorMessage;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.ActorRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ActorPeliculaService {
    
    @Autowired
    private ActorRepository actorRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    /**
	 * Asocia un Pelicula existente a un Actor
	 *
	 * @param actorId Identificador de la instancia de Actor
	 * @param peliculaId   Identificador de la instancia de Pelicula
	 * @return Instancia de PeliculaEntity que fue asociada a Actor
	 */


     @Transactional
    public PeliculaEntity addPelicula(Long actorId, Long peliculaId) throws EntityNotFoundException {
        log.info("Inicia proceso de asociarle una pelicula al actor con id = {0}", actorId);
        Optional<ActorEntity> actorEntity = actorRepository.findById(actorId);
        Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
        
        if (actorEntity.isEmpty()) {
			throw new EntityNotFoundException(ErrorMessage.ACTOR_NOT_FOUND);
        }
        
        if (peliculaEntity.isEmpty()) {   
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
        }

		actorEntity.get().getPeliculas().add(peliculaEntity.get());
		log.info("Termina proceso de asociarle una pelicula al actor con id = {0}", actorId);
		return peliculaEntity.get();
    }

    /**
	 * Obtiene una colección de instancias de PeliculaEntity asociadas a una instancia
	 * de Actor
	 *
	 * @param actoresId Identificador de la instancia de Actor
	 * @return Colección de instancias de PeliculaEntity asociadas a la instancia de
	 *         Actor
	 */
	@Transactional
	public List<PeliculaEntity> getPeliculas(Long actorId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todas las peliculas del actor con id = {0}", actorId);
		Optional<ActorEntity> actorEntity = actorRepository.findById(actorId);
		if (actorEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.ACTOR_NOT_FOUND);   
        }
            
		List<PeliculaEntity> peliculas = peliculaRepository.findAll();
		List<PeliculaEntity> peliculaList = new ArrayList<>();

		for (PeliculaEntity b : peliculas) {
			if (actorEntity.get().getPeliculas().contains(b)) {
				peliculaList.add(b);
			}
		}
		log.info("Termina proceso de consultar todas las peliculas del actor con id = {0}", actorId);
		return peliculaList;
	}

    
    /**
	 * Obtiene una instancia de PeliculaEntity asociada a una instancia de Actor
	 *
	 * @param actoresId Identificador de la instancia de Actor
	 * @param peliculasId   Identificador de la instancia de Pelicula
	 * @return La entidad de Pelicula del actor
	 */
	@Transactional
	public PeliculaEntity getPelicula(Long actorId, Long peliculaId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar la pelicula con id = {0} del actor con id = " + actorId, peliculaId);
		Optional<ActorEntity> actorEntity = actorRepository.findById(actorId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (actorEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.ACTOR_NOT_FOUND);
        }
            
		if (peliculaEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
        }

		log.info("Termina proceso de consultar la pelicula con id = {0} del actor con id = " + actorId, peliculaId);
		if (actorEntity.get().getPeliculas().contains(peliculaEntity.get())) {
            return peliculaEntity.get();
        }

		throw new IllegalOperationException("La pelicula no esta asociada al autor");
	}

    /**
	 * Remplaza las instancias de Pelicula asociadas a una instancia de Actor
	 *
	 * @param actorId Identificador de la instancia de Actor
	 * @param peliculas    Colección de instancias de PeliculaEntity a asociar a instancia
	 *                 de Actor
	 * @return Nueva colección de PeliculaEntity asociada a la instancia de Actor
	 */
	@Transactional
	public List<PeliculaEntity> replacePeliculas(Long actorId, List<PeliculaEntity> peliculas) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las peliculas asociadas al actor con id = {0}", actorId);
        for (PeliculaEntity pelicula: peliculas) {
            addPelicula(actorId, pelicula.getId());
        }
		log.info("Finaliza proceso de reemplazar las peliculas asociadas al actor con id = {0}", actorId);
		return peliculas;
    }


    /**
	 * Desasocia un Pelicula existente de un Actor existente
	 *
	 * @param actoresId Identificador de la instancia de Actor
	 * @param peliculasId   Identificador de la instancia de Pelicula
	 */
	@Transactional
	public void removePelicula(Long actorId, Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar una pelicula del actor con id = {0}", actorId);
		Optional<ActorEntity> actorEntity = actorRepository.findById(actorId);
		if (actorEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.ACTOR_NOT_FOUND);
        }
            
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
        }
        
		actorEntity.get().getPeliculas().remove(peliculaEntity.get());
		log.info("Finaliza proceso de borrar una pelicula del actor con id = {0}", actorId);
	}
}
