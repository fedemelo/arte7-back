package co.edu.uniandes.dse.arte7.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uniandes.dse.arte7.entities.ActorEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
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
	 * Asocia un Book existente a un Author
	 *
	 * @param actorId Identificador de la instancia de Author
	 * @param peliculaId   Identificador de la instancia de Book
	 * @return Instancia de BookEntity que fue asociada a Author
	 */


     @Transactional
    public PeliculaEntity addPelicula(Long actorId, Long peliculaId) throws EntityNotFoundException {
        log.info("Inicia proceso de asociarle una pelicula al actor con id = {0}", actorId);
        Optional<ActorEntity> actorEntity = actorRepository.findById(actorId);
        Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
        
        if (actorEntity.isEmpty())
			throw new EntityNotFoundException("No se encontro un actor con ese Id.");

        if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encontro una pelicula con ese Id.");

        peliculaEntity.get().getActores().add(actorEntity.get());
		log.info("Termina proceso de asociarle una pelicula al actor con id = {0}", actorId);
		return peliculaEntity.get();
    }

    /**
	 * Obtiene una colección de instancias de BookEntity asociadas a una instancia
	 * de Author
	 *
	 * @param actoresId Identificador de la instancia de Author
	 * @return Colección de instancias de BookEntity asociadas a la instancia de
	 *         Author
	 */
	@Transactional
	public List<PeliculaEntity> getPeliculas(Long actorId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todas las peliculas del actor con id = {0}", actorId);
		Optional<ActorEntity> actorEntity = actorRepository.findById(actorId);
		if (actorEntity.isEmpty())
            throw new EntityNotFoundException("No se encontro un actor con ese Id.");   

		List<PeliculaEntity> peliculas = peliculaRepository.findAll();
		List<PeliculaEntity> peliculaList = new ArrayList<>();

		for (PeliculaEntity b : peliculas) {
			if (b.getActores().contains(actorEntity.get())) {
				peliculaList.add(b);
			}
		}
		log.info("Termina proceso de consultar todas las peliculas del actor con id = {0}", actorId);
		return peliculaList;
	}

    
    /**
	 * Obtiene una instancia de BookEntity asociada a una instancia de Author
	 *
	 * @param actoresId Identificador de la instancia de Author
	 * @param peliculasId   Identificador de la instancia de Book
	 * @return La entidadd de Libro del autor
	 */
	@Transactional
	public PeliculaEntity getPelicula(Long actorId, Long peliculaId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar la pelicula con id = {0} del actor con id = " + actorId, peliculaId);
		Optional<ActorEntity> actorEntity = actorRepository.findById(actorId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (actorEntity.isEmpty())
            throw new EntityNotFoundException("No se encontro el actor.");

		if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException("No se encontro la pelicula.");

		log.info("Termina proceso de consultar la pelicula con id = {0} del actor con id = " + actorId, peliculaId);
		if (peliculaEntity.get().getActores().contains(actorEntity.get()))
			return peliculaEntity.get();

		throw new IllegalOperationException("La pelicula no esta asociada al autor");
	}

    /**
	 * Remplaza las instancias de Book asociadas a una instancia de Author
	 *
	 * @param actorId Identificador de la instancia de Author
	 * @param peliculas    Colección de instancias de BookEntity a asociar a instancia
	 *                 de Author
	 * @return Nueva colección de BookEntity asociada a la instancia de Author
	 */
	@Transactional
	public List<PeliculaEntity> addPeliculas(Long actorId, List<PeliculaEntity> peliculas) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las peliculas asociadas al actor con id = {0}", actorId);
		Optional<ActorEntity> actorEntity = actorRepository.findById(actorId);
		if (actorEntity.isEmpty())
            throw new EntityNotFoundException("No se encontro el actor.");

		for (PeliculaEntity pelicula : peliculas) {
			Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(pelicula.getId());
			if (peliculaEntity.isEmpty())
                throw new EntityNotFoundException("No se encontro la pelicula.");

			if (!peliculaEntity.get().getActores().contains(actorEntity.get()))
				peliculaEntity.get().getActores().add(actorEntity.get());
		}
		log.info("Finaliza proceso de reemplazar las peliculas asociadas al actor con id = {0}", actorId);
		return peliculas;
    }

    /**
	 * Desasocia un Book existente de un Author existente
	 *
	 * @param actoresId Identificador de la instancia de Author
	 * @param peliculasId   Identificador de la instancia de Book
	 */
	@Transactional
	public void removePelicula(Long actorId, Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar una pelicula del actor con id = {0}", actorId);
		Optional<ActorEntity> actorEntity = actorRepository.findById(actorId);
		if (actorEntity.isEmpty())
            throw new EntityNotFoundException("No se encontro el actor.");

		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException("No se encontro la pelicula.");

		peliculaEntity.get().getActores().remove(actorEntity.get());
		log.info("Finaliza proceso de borrar una pelicula del actor con id = {0}", actorId);
	}
}
