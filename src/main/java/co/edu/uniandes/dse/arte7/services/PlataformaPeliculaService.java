package co.edu.uniandes.dse.arte7.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.PlataformaEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.ErrorMessage;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.PlataformaRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PlataformaPeliculaService {
    
    @Autowired
    private PlataformaRepository plataformaRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    // Asocia un pelicula existente a una plataforma
    @Transactional
    public PeliculaEntity addPelicula(Long plataformaId, Long peliculaId) throws EntityNotFoundException {
        log.info("Inicia proceso de asociarle una pelicula a una plataforma con id = {0}", plataformaId);
        Optional<PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataformaId);
        Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
        
        if (plataformaEntity.isEmpty()) {
			throw new EntityNotFoundException(ErrorMessage.PLATAFORMA_NOT_FOUND);
        }
        
        if (peliculaEntity.isEmpty()) {   
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
        }

		plataformaEntity.get().getPeliculas().add(peliculaEntity.get());
		log.info("Termina proceso de asociarle una pelicula a la plataforma con id = {0}", plataformaId);
		return peliculaEntity.get();
    }

    // Obtiene una colección de peliculas asociadas a una plataforma
	@Transactional
	public List<PeliculaEntity> getPeliculas(Long plataformaId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todas las peliculas de la plataforma con id = {0}", plataformaId);
		Optional<PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataformaId);
		if (plataformaEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.PLATAFORMA_NOT_FOUND);   
        }
            
		List<PeliculaEntity> peliculas = peliculaRepository.findAll();
		List<PeliculaEntity> peliculaList = new ArrayList<>();

		for (PeliculaEntity b : peliculas) {
			if (plataformaEntity.get().getPeliculas().contains(b)) {
				peliculaList.add(b);
			}
		}
		log.info("Termina proceso de consultar todas las peliculas de la plataforma con id = {0}", plataformaId);
		return peliculaList;
	}

    
    //Obtiene una pelicula asociada a una plataforma
	@Transactional
	public PeliculaEntity getPelicula(Long plataformaId, Long peliculaId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar la pelicula con id = {0} de la plataforma con id = " + plataformaId, peliculaId);
		Optional<PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataformaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (plataformaEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.PLATAFORMA_NOT_FOUND);
        }
            
		if (peliculaEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
        }

		log.info("Termina proceso de consultar la pelicula con id = {0} de la plataforma con id = " + plataformaId, peliculaId);
		if (plataformaEntity.get().getPeliculas().contains(peliculaEntity.get())) {
            return peliculaEntity.get();
        }

		throw new IllegalOperationException("La pelicula no esta asociada a la plataforma");
	}

    //Remplaza las instancias de Pelicula asociadas a una instancia de Plataforma
	@Transactional
	public List<PeliculaEntity> replacePeliculas(Long plataformaId, List<PeliculaEntity> peliculas) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las peliculas asociadas a la plataforma con id = {0}", plataformaId);
        for (PeliculaEntity pelicula: peliculas) {
            addPelicula(plataformaId, pelicula.getId());
        }
		log.info("Finaliza proceso de reemplazar las peliculas asociadas a la plataforma con id = {0}", plataformaId);
		return peliculas;
    }


    //Elimina un pelicula existente de un plataforma existente
	@Transactional
	public void removePelicula(Long plataformaId, Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar una pelicula de la plataforma con id = {0}", plataformaId);
		Optional<PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataformaId);
		if (plataformaEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.PLATAFORMA_NOT_FOUND);
        }
            
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
        }
        
		plataformaEntity.get().getPeliculas().remove(peliculaEntity.get());
		log.info("Finaliza proceso de borrar una pelicula de la plataforma con id = {0}", plataformaId);
	}
}
