package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.repositories.PremioRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import lombok.extern.slf4j.Slf4j;

import co.edu.uniandes.dse.arte7.entities.PremioEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;

import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;

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
			throw new EntityNotFoundException("No se encontró la película a agregar");

		Optional<PremioEntity> premioEntity = premioRepository.findById(premioId);
		if (premioEntity.isEmpty())
			throw new EntityNotFoundException("No se encontró el premio");

		premioEntity.get().getPeliculas().add(peliculaEntity.get());
		log.info("Termina proceso de asociarle una película al premio con id = {0}", premioId);
		return peliculaEntity.get();
	}

	@Transactional
	public List<PeliculaEntity> addPeliculas(Long premioId, List<PeliculaEntity> peliculas) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las peliculas asociadas al premio con id = {0}", premioId);
		Optional<PremioEntity> premioEntity = premioRepository.findById(premioId);
		if (premioEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra el premio con el id provisto.");

		for (PeliculaEntity pelicula : peliculas) {
			Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(pelicula.getId());
			if (peliculaEntity.isEmpty())
				throw new EntityNotFoundException("No se encuentra la pelicula con el id provisto.");

			if (!peliculaEntity.get().getPremios().contains(premioEntity.get()))
				peliculaEntity.get().getPremios().add(premioEntity.get());
		}
		log.info("Finaliza proceso de reemplazar las peliculas asociados al premio con id = {0}", premioId);
		return peliculas;
	}


    @Transactional
	public List<PeliculaEntity> getPeliculas(Long premioId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todas las películas del premio con id = {0}", premioId);
		Optional<PremioEntity> premioEntity = premioRepository.findById(premioId);
		if (premioEntity.isEmpty())
			throw new EntityNotFoundException("No se encontró el premio");
		log.info("Finaliza proceso de consultar todas las películas del premio con id = {0}", premioId);
		return premioEntity.get().getPeliculas();
	}

    @Transactional
	public PeliculaEntity getPelicula(Long premioId, Long peliculaId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar una película del premio con id = {0}", premioId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		Optional<PremioEntity> premioEntity = premioRepository.findById(premioId);

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encontró la película");

		if (premioEntity.isEmpty())
			throw new EntityNotFoundException("No se encontró el premio");

		log.info("Termina proceso de consultar una película del premio con id = {0}", premioId);
		if (premioEntity.get().getPeliculas().contains(peliculaEntity.get()))
			return peliculaEntity.get();

		throw new IllegalOperationException("La película no está asociada al premio");
	}

    @Transactional
    public List<PeliculaEntity> replacePeliculas(Long premioId, List<PeliculaEntity> list) 
            throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las peliculas del premio con id = {0}", premioId);
		Optional<PremioEntity> premioEntity = premioRepository.findById(premioId);
		if (premioEntity.isEmpty())
			throw new EntityNotFoundException("no se encontró el premio");

		for (PeliculaEntity pelicula : list) {
			Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(pelicula.getId());
			if (peliculaEntity.isEmpty())
				throw new EntityNotFoundException("No se encontró la película");

			if (!premioEntity.get().getPeliculas().contains(peliculaEntity.get()))
				premioEntity.get().getPeliculas().add(peliculaEntity.get());
		}
		log.info("Termina proceso de reemplazar las peliculas del premio con id = {0}", premioId);
		return getPeliculas(premioId);
	}

    @Transactional
    public void removePelicula(Long premioId, Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar una pelicula del premio con id = {0}", premioId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		Optional<PremioEntity> premioEntity = premioRepository.findById(premioId);

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encontró la película");

		if (premioEntity.isEmpty())
			throw new EntityNotFoundException("No se encontró el premio");

            premioEntity.get().getPeliculas().remove(peliculaEntity.get());

		log.info("Termina proceso de borrar una pelicula del premio con id = {0}",premioId);
	}
}