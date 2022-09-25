package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.PremioEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.ErrorMessage;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.PremioRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class PeliculaPremioService {

    @Autowired
	private PeliculaRepository peliculaRepository;

	@Autowired
	private PremioRepository premioRepository;

	@Transactional
	public PremioEntity addPremio(Long peliculaId, Long premioId) throws EntityNotFoundException {
		log.info("Inicia proceso de asociarle una premio a la pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		Optional<PremioEntity> premioEntity = premioRepository.findById(premioId);
		if (premioEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PREMIO_NOT_FOUND);

		
		
		if(!premioEntity.get().getPeliculas().contains(peliculaEntity.get())){
			premioEntity.get().getPeliculas().add(peliculaEntity.get());
		}
	
		log.info("Termina proceso de asociarle una premio a la pelicula con id = {0}", peliculaId);
		return premioEntity.get();
	}

	@Transactional
	public List<PremioEntity> getPremios(Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todos las premios de la pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

			List<PremioEntity> premios = premioRepository.findAll();
		List<PremioEntity> premioList = new ArrayList<>();

		for (PremioEntity p : premios) {
			if (p.getPeliculas().contains(peliculaEntity.get())) {
				premioList.add(p);
			}
		}
		log.info("Termina proceso de consultar todos los libros del autor con id = {0}", peliculaId);
		return premioList;
	}

	@Transactional
	public PremioEntity getPremio(Long peliculaId, Long premioId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar una premio de la pelicula con id = {0}", peliculaId);
		Optional<PremioEntity> premioEntity = premioRepository.findById(premioId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (premioEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PREMIO_NOT_FOUND);

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
		log.info("Termina proceso de consultar una premio de la pelicula con id = {0}", peliculaId);
		if (peliculaEntity.get().getPremios().contains(premioEntity.get()))
			return premioEntity.get();

		throw new IllegalOperationException("La premio no se encuentra asociada a la pelicula.");
	}

	@Transactional
	public List<PremioEntity> replacePremios(Long peliculaId, List<PremioEntity> premios) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las peliculas asociadas al actor con id = {0}", peliculaId);
        for (PremioEntity premi: premios) {
			addPremio(peliculaId, premi.getId());
        }
		log.info("Finaliza proceso de reemplazar las peliculas asociadas al actor con id = {0}", peliculaId);
		return getPremios(peliculaId);
    }




	public void removePremio(Long peliculaId, Long premioId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar una premio de la pelicula con id = {0}", peliculaId);
		Optional<PremioEntity> premioEntity = premioRepository.findById(premioId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (premioEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PREMIO_NOT_FOUND);

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

			premioEntity.get().getPeliculas().remove(peliculaEntity.get());

		log.info("Termina proceso de borrar una premio de la pelicula con id = {0}", peliculaId);
	}
}
