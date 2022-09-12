package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.entities.PlataformaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import co.edu.uniandes.dse.arte7.repositories.PlataformaRepository;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class PlataformaPeliculaService {
    
    @Autowired
    PlataformaRepository plataformaRepository;

    @Autowired
    PeliculaRepository peliculaRepository;

    @Transactional
	public PeliculaEntity addPelicula(Long plataformaId, Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de asociarle una pelicula la plataforma con id = {0}", plataformaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encontro una pleicula con esa Id.");

		Optional<PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataformaId);
		if (plataformaEntity.isEmpty())
			throw new EntityNotFoundException("No se encontro la plataforma con esa Id.");

        if (plataformaEntity.isPresent())
		    plataformaEntity.get().getPeliculas().add(peliculaEntity.get());
            
		log.info("Termina proceso de asociarle una pelicula a la plataforma con id = {0}", plataformaId);
		return peliculaEntity.get();
	}

    public List<PeliculaEntity> getPeliculas(Long plataformaId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todoas las peliculas de una plataforma con id = {0}", plataformaId);
		Optional<PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataformaId);
		if (plataformaEntity.isEmpty())
			throw new EntityNotFoundException("Plataforma no encontrada.");
		log.info("Finaliza proceso consultar todoas las peliculas de una plataforma con id = {0}", plataformaId);
		return plataformaEntity.get().getPeliculas();
	}

    @Transactional
	public PeliculaEntity getPelicula(Long plataformaId, Long peliculaId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar una pelicula de la plataforma con id = {0}", plataformaId);
		Optional<PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataformaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

        if (plataformaEntity.isEmpty())
			throw new EntityNotFoundException("No se encontro la plataforma.");

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encontro la pelicula.");

		log.info("Termina proceso de consultar una pelicula de la plataforma con id = {0}", peliculaId);
		if (plataformaEntity.get().getPeliculas().contains(peliculaEntity.get()))
			return peliculaEntity.get();

		throw new IllegalOperationException("La pelicula no esta asociada a la plataforma.");
	}

    @Transactional
    public List<PeliculaEntity> replacePeliculas(Long plataformaId, List<PeliculaEntity> list) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las peliculas de la plataforma con id = {0}", plataformaId);
		Optional<PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataformaId);
		if (plataformaEntity.isEmpty())
			throw new EntityNotFoundException("Plataforma no encontrada");

		for (PeliculaEntity pelicula : list) {
			Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(pelicula.getId());
			if (peliculaEntity.isEmpty())
				throw new EntityNotFoundException("Pelicula no encontrada.");

			if (!plataformaEntity.get().getPeliculas().contains(peliculaEntity.get()))
				plataformaEntity.get().getPeliculas().add(peliculaEntity.get());
		}
		log.info("Termina proceso de reemplazar las peliculas de la plataforma con id = {0}", plataformaId);
		return getPeliculas(plataformaId);
	}

    @Transactional
    public void removePelicula(Long plataformaId, Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar una pelicula de la plataforma con id = {0}", plataformaId);
		Optional<PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataformaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("plataforma no encontrada.");

		if (plataformaEntity.isEmpty())
			throw new EntityNotFoundException("pelicula no encontrada.");

		plataformaEntity.get().getPeliculas().remove(peliculaEntity.get());

		log.info("Termina proceso de borrar una pelicula de la plataforma con id = {0}", peliculaId);
	}
}
