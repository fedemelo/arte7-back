package co.edu.uniandes.dse.arte7.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.GeneroEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.ErrorMessage;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.GeneroRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class GeneroPeliculaService {
    
    @Autowired
    private GeneroRepository generoRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    // Asocia un Pelicula existente a un Genero
    @Transactional
    public PeliculaEntity addPelicula(Long generoId, Long peliculaId) throws EntityNotFoundException {
        log.info("Inicia proceso de asociarle una pelicula a un genero con id = {0}", generoId);
        Optional<GeneroEntity> generoEntity = generoRepository.findById(generoId);
        Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
        
        if (generoEntity.isEmpty()) {
			throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);
        }
        
        if (peliculaEntity.isEmpty()) {   
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
        }

		generoEntity.get().getPeliculas().add(peliculaEntity.get());
		log.info("Termina proceso de asociarle una pelicula al genero con id = {0}", generoId);
		return peliculaEntity.get();
    }

    // Obtiene una colección de Peliculas asociadas a un genero
	@Transactional
	public List<PeliculaEntity> getPeliculas(Long generoId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todas las peliculas del genero con id = {0}", generoId);
		Optional<GeneroEntity> generoEntity = generoRepository.findById(generoId);
		if (generoEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);   
        }
            
		List<PeliculaEntity> peliculas = peliculaRepository.findAll();
		List<PeliculaEntity> peliculaList = new ArrayList<>();

		for (PeliculaEntity b : peliculas) {
			if (generoEntity.get().getPeliculas().contains(b)) {
				peliculaList.add(b);
			}
		}
		log.info("Termina proceso de consultar todas las peliculas del genero con id = {0}", generoId);
		return peliculaList;
	}

    
    //Obtiene una pelicula asociada a un genero
	@Transactional
	public PeliculaEntity getPelicula(Long generoId, Long peliculaId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar la pelicula con id = {0} del genero con id = " + generoId, peliculaId);
		Optional<GeneroEntity> generoEntity = generoRepository.findById(generoId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (generoEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);
        }
            
		if (peliculaEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
        }

		log.info("Termina proceso de consultar la pelicula con id = {0} del genero con id = " + generoId, peliculaId);
		if (generoEntity.get().getPeliculas().contains(peliculaEntity.get())) {
            return peliculaEntity.get();
        }

		throw new IllegalOperationException("La pelicula no esta asociada al genero");
	}

    //Remplaza las instancias de Pelicula asociadas a una instancia de Genero
	@Transactional
	public List<PeliculaEntity> replacePeliculas(Long generoId, List<PeliculaEntity> peliculas) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las peliculas asociadas al genero con id = {0}", generoId);
        for (PeliculaEntity pelicula: peliculas) {
            addPelicula(generoId, pelicula.getId());
        }
		log.info("Finaliza proceso de reemplazar las peliculas asociadas al genero con id = {0}", generoId);
		return peliculas;
    }


    //Elimina un Pelicula existente de un Genero existente
	@Transactional
	public void removePelicula(Long generoId, Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar una pelicula del genero con id = {0}", generoId);
		Optional<GeneroEntity> generoEntity = generoRepository.findById(generoId);
		if (generoEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);
        }
            
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
        }
        
		generoEntity.get().getPeliculas().remove(peliculaEntity.get());
		log.info("Finaliza proceso de borrar una pelicula del genero con id = {0}", generoId);
	}
}
