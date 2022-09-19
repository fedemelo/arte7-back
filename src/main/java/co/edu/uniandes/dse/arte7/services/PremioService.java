package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.repositories.PremioRepository;
import lombok.extern.slf4j.Slf4j;

import co.edu.uniandes.dse.arte7.entities.PremioEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;

import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.ErrorMessage;

@Slf4j
@Service

public class PremioService {
    
    @Autowired
    PremioRepository premioRepository;

    @Transactional
	public PremioEntity createPremio(PremioEntity premio) throws IllegalOperationException {
		log.info("Inicia proceso de creación de premio");
		return premioRepository.save(premio);
	}

    /** Obtención de todas los premios */
    @Transactional
	public List<PremioEntity> getPremios() {
		log.info("Inicia proceso de consultar todos los premios");
		return premioRepository.findAll();
	}


    /** Obtención de un premio específico por ID */
    @Transactional
	public PremioEntity getPremio(Long premioId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar el premio con id = {0}", premioId);
		Optional<PremioEntity> premioEntity = premioRepository.findById(premioId);
		if (premioEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PREMIO_NOT_FOUND);
		log.info("Termina proceso de consultar el premio con id = {0}", premioId);
		return premioEntity.get();
	}

    @Transactional
	public PremioEntity updatePremio(Long premioId, PremioEntity premio) throws EntityNotFoundException {
		log.info("Inicia proceso de actualizar el premio con id = {0}", premioId);
		Optional<PremioEntity> premioEntity = premioRepository.findById(premioId);
		if (premioEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PREMIO_NOT_FOUND);
		log.info("Termina proceso de actualizar el premio con id = {0}", premioId);
		premio.setId(premioId);
		return premioRepository.save(premio); 
	}

    public void deletePremio(Long premioId) throws IllegalOperationException, EntityNotFoundException {
		log.info("Inicia proceso de borrar el premio con id = {0}", premioId);
		Optional<PremioEntity> premioEntity = premioRepository.findById(premioId);
		if (premioEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PREMIO_NOT_FOUND);

		List<PeliculaEntity> peliculas = premioEntity.get().getPeliculas();
		if (!peliculas.isEmpty())
			throw new IllegalOperationException("No se puede borrar premio porque tiene películas asociadas");

		premioRepository.deleteById(premioId);
		log.info("Termina proceso de borrar el premio con id = {0}", premioId);
	}

}
