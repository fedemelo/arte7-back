package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.entities.ResenhaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.ErrorMessage;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import co.edu.uniandes.dse.arte7.repositories.ResenhaRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PeliculaResenhaService {
    @Autowired
	private PeliculaRepository peliculaRepository;

	@Autowired
	private ResenhaRepository resenhaRepository;

	@Transactional
	public ResenhaEntity addResenha(Long peliculaId, Long resenhaId) throws EntityNotFoundException {
		log.info("Inicia proceso de asociarle una resenha a la pelicula con id = {0}", peliculaId);
		Optional<ResenhaEntity> resenhaEntity = resenhaRepository.findById(resenhaId);
		if (resenhaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.RESENHA_NOT_FOUND);

		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		resenhaEntity.get().setPelicula(peliculaEntity.get());
		log.info("Termina proceso de asociarle una resenha a la pelicula con id = {0}", peliculaId);
		return resenhaEntity.get();
	}

	@Transactional
	public List<ResenhaEntity> getResenhas(Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todos las resenhas de la pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
		log.info("Finaliza proceso de consultar todos las resenhas de la pelicula con id = {0}", peliculaId);
		return peliculaEntity.get().getResenhas();
	}

	@Transactional
	public List<ResenhaEntity> replaceResenhas(Long peliculaId, List<ResenhaEntity> list) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar los directores de la pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		for (ResenhaEntity resenha : list) {
			Optional<ResenhaEntity> resenhaEntity = resenhaRepository.findById(resenha.getId());
			if (resenhaEntity.isEmpty())
				throw new EntityNotFoundException(ErrorMessage.RESENHA_NOT_FOUND);

		}

		peliculaEntity.get().setResenhas(list);

		
		log.info("Termina proceso de reemplazar los directores de la pelicula con id = {0}", peliculaId);
		return getResenhas(peliculaId);
	}
	@Transactional
	public ResenhaEntity getResenha(Long peliculaId, Long resenhaId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar una resenha de la pelicula con id = {0}", peliculaId);
		Optional<ResenhaEntity> resenhaEntity = resenhaRepository.findById(resenhaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (resenhaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.RESENHA_NOT_FOUND);

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
		log.info("Termina proceso de consultar una resenha de la pelicula con id = {0}", peliculaId);
		if (peliculaEntity.get().getResenhas().contains(resenhaEntity.get()))
			return resenhaEntity.get();

		throw new IllegalOperationException("La resenha no se encuentra asociada a la pelicula.");
	}

	public void removeResenha(Long peliculaId, Long resenhaId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar una resenha de la pelicula con id = {0}", peliculaId);
		Optional<ResenhaEntity> resenhaEntity = resenhaRepository.findById(resenhaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (resenhaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.RESENHA_NOT_FOUND);

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		peliculaEntity.ifPresent(pelicula -> {
			resenhaEntity.get().setPelicula(null);
			pelicula.getResenhas().remove(resenhaEntity.get());
		});

		log.info("Termina proceso de borrar una resenha de la pelicula con id = {0}", peliculaId);
	}
    
}
