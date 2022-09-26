package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.entities.PlataformaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.ErrorMessage;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import co.edu.uniandes.dse.arte7.repositories.PlataformaRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PeliculaPlataformaService {
    
    @Autowired
    PlataformaRepository plataformaRepository;

    @Autowired
    PeliculaRepository peliculaRepository;

    @Transactional
	public PlataformaEntity addPlataforma(Long peliculaId, Long plataformaId) throws EntityNotFoundException {
		log.info("Inicia proceso de asociarle una plataforma a la pelicula con id = {0}", peliculaId);
		Optional<PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataformaId);
		if (plataformaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PLATAFORMA_NOT_FOUND);

		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		plataformaEntity.get().getPeliculas().add(peliculaEntity.get());
		log.info("Termina proceso de asociarle una plataforma a la pelicula con id = {0}", peliculaId);
		return plataformaEntity.get();
	}

    public List<PlataformaEntity> getPlataformas(Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todoas las plataformas de una pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
		log.info("Finaliza proceso consultar todoas las plataformas de una pelicula con id = {0}", peliculaId);
		return peliculaEntity.get().getPlataformas();
	}

    @Transactional
	public PlataformaEntity getPlataforma(Long peliculaId, Long plataformaId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar una plataforma de la pelicula con id = {0}", peliculaId);
		Optional<PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataformaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (plataformaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PLATAFORMA_NOT_FOUND);

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		log.info("Termina proceso de consultar una plataforma de la pelicula con id = {0}", peliculaId);
		if (plataformaEntity.get().getPeliculas().contains(peliculaEntity.get()))
			return plataformaEntity.get();

		throw new IllegalOperationException("La plataforma no esta asociada a la pelicula.");
	}

    @Transactional
    public List<PlataformaEntity> replacePlataformas(Long peliculaId, List<PlataformaEntity> list) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las plataformas de la pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		for (PlataformaEntity plataforma : list) {
			Optional<PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataforma.getId());
			if (plataformaEntity.isEmpty())
				throw new EntityNotFoundException(ErrorMessage.PLATAFORMA_NOT_FOUND);

			if (!plataformaEntity.get().getPeliculas().contains(peliculaEntity.get()))
				plataformaEntity.get().getPeliculas().add(peliculaEntity.get());
		}
		log.info("Termina proceso de reemplazar las plataformas de la pelicula con id = {0}", peliculaId);
		return getPlataformas(peliculaId);
	}



	








	
    @Transactional
    public void removePlataforma(Long peliculaId, Long plataformaId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar una plataforma de una pelicula con id = {0}", peliculaId);
		Optional<PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataformaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (plataformaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PLATAFORMA_NOT_FOUND);

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		plataformaEntity.get().getPeliculas().remove(peliculaEntity.get());

		log.info("Termina proceso de borrar una plataforma de una pelicula con id = {0}", peliculaId);
	}
}
