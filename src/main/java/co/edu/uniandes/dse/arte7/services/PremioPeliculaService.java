package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.repositories.PremioRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;


import co.edu.uniandes.dse.arte7.entities.PremioEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.ErrorMessage;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service

public class PremioPeliculaService {
    @Autowired
    private PremioRepository premioRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Transactional
	public PeliculaEntity addPelicula(Long premioId, Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de asociarle una película al premio con id = {0}", premioId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		Optional<PremioEntity> premioEntity = premioRepository.findById(premioId);
		if (premioEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PREMIO_NOT_FOUND);

		if(!premioEntity.get().getPeliculas().contains(peliculaEntity.get())){
			premioEntity.get().getPeliculas().add(peliculaEntity.get());
		}
		

		log.info("Termina proceso de asociarle una película al premio con id = {0}", premioId);
		return peliculaEntity.get();
	}

	@Transactional
	public List<PeliculaEntity> addPeliculas(Long premioId, List<PeliculaEntity> peliculas) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las peliculas asociadas al actor con id = {0}", premioId);
        for (PeliculaEntity pelicula: peliculas) {
            
			addPelicula(premioId, pelicula.getId());
        }
		log.info("Finaliza proceso de reemplazar las peliculas asociadas al actor con id = {0}", premioId);
		return peliculas;
    }



    @Transactional
	public List<PeliculaEntity> getPeliculas(Long premioId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todas las peliculas del actor con id = {0}", premioId);
		Optional<PremioEntity> premioEntity = premioRepository.findById(premioId);
		if (premioEntity.isEmpty()) {
            throw new EntityNotFoundException(ErrorMessage.PREMIO_NOT_FOUND);   
        }
            
		List<PeliculaEntity> peliculas = peliculaRepository.findAll();
		List<PeliculaEntity> peliculaList = new ArrayList<>();

		for (PeliculaEntity b : peliculas) {
			if (premioEntity.get().getPeliculas().contains(b)) {
				peliculaList.add(b);
			}
		}
		log.info("Termina proceso de consultar todas las peliculas del actor con id = {0}", premioId);
		return peliculaList;
	}


    @Transactional
	public PeliculaEntity getPelicula(Long premioId, Long peliculaId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar una película del premio con id = {0}", premioId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		Optional<PremioEntity> premioEntity = premioRepository.findById(premioId);

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		if (premioEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PREMIO_NOT_FOUND);

		log.info("Termina proceso de consultar una película del premio con id = {0}", premioId);
		if (premioEntity.get().getPeliculas().contains(peliculaEntity.get()))
			return peliculaEntity.get();

		throw new IllegalOperationException("La película no está asociada al premio");
	}

   

    @Transactional
    public void removePelicula(Long premioId, Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar una pelicula del premio con id = {0}", premioId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		Optional<PremioEntity> premioEntity = premioRepository.findById(premioId);

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

		if (premioEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PREMIO_NOT_FOUND);

            premioEntity.get().getPeliculas().remove(peliculaEntity.get());

		log.info("Termina proceso de borrar una pelicula del premio con id = {0}",premioId);
	}
}
