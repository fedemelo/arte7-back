package co.edu.uniandes.dse.arte7.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.DirectorEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.ErrorMessage;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.DirectorRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DirectorPeliculaService {
    
    @Autowired
    private DirectorRepository directorRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    /**
	 * Asocia un Pelicula existente a un Director
	 *
	 * @param directorId Identificador de la instancia de Director
	 * @param peliculaId   Identificador de la instancia de Pelicula
	 * @return Instancia de PeliculaEntity que fue asociada a Director
	 */


     @Transactional
    public PeliculaEntity addPelicula(Long directorId, Long peliculaId) throws EntityNotFoundException {
        log.info("Inicia proceso de asociarle una pelicula al director con id = {0}", directorId);
        Optional<DirectorEntity> directorEntity = directorRepository.findById(directorId);
        Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
        
        if (directorEntity.isEmpty()) {
			throw new EntityNotFoundException(ErrorMessage.DIRECTOR_NOT_FOUND);
        }
        
        if (peliculaEntity.isEmpty()) {   
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
        }

		directorEntity.get().getPeliculas().add(peliculaEntity.get());
		log.info("Termina proceso de asociarle una pelicula al director con id = {0}", directorId);
		return peliculaEntity.get();
    }

    /**
	 * Obtiene una colección de instancias de PeliculaEntity asociadas a una instancia
	 * de Director
	 *
	 * @param directoresId Identificador de la instancia de Director
	 * @return Colección de instancias de PeliculaEntity asociadas a la instancia de
	 *         Director
	 */
	@Transactional
	public List<PeliculaEntity> getPeliculas(Long directorId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todas las peliculas del director con id = {0}", directorId);
		Optional<DirectorEntity> directorEntity = directorRepository.findById(directorId);
		if (directorEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.DIRECTOR_NOT_FOUND);   
        }
            
		List<PeliculaEntity> peliculas = peliculaRepository.findAll();
		List<PeliculaEntity> peliculaList = new ArrayList<>();

		for (PeliculaEntity b : peliculas) {
			if (directorEntity.get().getPeliculas().contains(b)) {
				peliculaList.add(b);
			}
		}
		log.info("Termina proceso de consultar todas las peliculas del director con id = {0}", directorId);
		return peliculaList;
	}

    
    /**
	 * Obtiene una instancia de PeliculaEntity asociada a una instancia de Director
	 *
	 * @param directoresId Identificador de la instancia de Director
	 * @param peliculasId   Identificador de la instancia de Pelicula
	 * @return La entidad de Pelicula del director
	 */
	@Transactional
	public PeliculaEntity getPelicula(Long directorId, Long peliculaId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar la pelicula con id = {0} del director con id = " + directorId, peliculaId);
		Optional<DirectorEntity> directorEntity = directorRepository.findById(directorId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (directorEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.DIRECTOR_NOT_FOUND);
        }
            
		if (peliculaEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
        }

		log.info("Termina proceso de consultar la pelicula con id = {0} del director con id = " + directorId, peliculaId);
		if (directorEntity.get().getPeliculas().contains(peliculaEntity.get())) {
            return peliculaEntity.get();
        }

		throw new IllegalOperationException("La pelicula no esta asociada al autor");
	}

    /**
	 * Remplaza las instancias de Pelicula asociadas a una instancia de Director
	 *
	 * @param directorId Identificador de la instancia de Director
	 * @param peliculas    Colección de instancias de PeliculaEntity a asociar a instancia
	 *                 de Director
	 * @return Nueva colección de PeliculaEntity asociada a la instancia de Director
	 */
	@Transactional
	public List<PeliculaEntity> replacePeliculas(Long directorId, List<PeliculaEntity> peliculas) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las peliculas asociadas al director con id = {0}", directorId);
        for (PeliculaEntity pelicula: peliculas) {
            addPelicula(directorId, pelicula.getId());
        }
		log.info("Finaliza proceso de reemplazar las peliculas asociadas al director con id = {0}", directorId);
		return peliculas;
    }


    /**
	 * Desasocia un Pelicula existente de un Director existente
	 *
	 * @param directoresId Identificador de la instancia de Director
	 * @param peliculasId   Identificador de la instancia de Pelicula
	 */
	@Transactional
	public void removePelicula(Long directorId, Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar una pelicula del director con id = {0}", directorId);
		Optional<DirectorEntity> directorEntity = directorRepository.findById(directorId);
		if (directorEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.DIRECTOR_NOT_FOUND);
        }
            
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
        }
        
		directorEntity.get().getPeliculas().remove(peliculaEntity.get());
		log.info("Finaliza proceso de borrar una pelicula del director con id = {0}", directorId);
	}
}
