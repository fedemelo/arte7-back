package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;

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
		Optional<PremioEntity> premioEntity = premioRepository.findById(premioId);
		if (premioEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PREMIO_NOT_FOUND);

		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		peliculaEntity.get().getPremios().add(premioEntity.get());
		log.info("Termina proceso de asociarle una premio a la pelicula con id = {0}", peliculaId);
		return premioEntity.get();
	}

	@Transactional
	public List<PremioEntity> getPremios(Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todos las premios de la pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
		log.info("Finaliza proceso de consultar todos las premios de la pelicula con id = {0}", peliculaId);
		return peliculaEntity.get().getPremios();
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
	public List<PremioEntity> replacePremios(Long peliculaId, List<PremioEntity> list) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las premios de la pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		for (PremioEntity premio : list) {
			Optional<PremioEntity> premioEntity = premioRepository.findById(premio.getId());
			if (premioEntity.isEmpty())
				throw new EntityNotFoundException(ErrorMessage.PREMIO_NOT_FOUND);

			if (!peliculaEntity.get().getPremios().contains(premioEntity.get()))
				peliculaEntity.get().getPremios().add(premioEntity.get());
		}
		log.info("Termina proceso de reemplazar las premio de la pelicula con id = {0}", peliculaId);
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

		peliculaEntity.get().getPremios().remove(premioEntity.get());

		log.info("Termina proceso de borrar una premio de la pelicula con id = {0}", peliculaId);
	}
}
