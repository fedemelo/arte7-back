package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.repositories.NominacionRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import lombok.extern.slf4j.Slf4j;

import co.edu.uniandes.dse.arte7.entities.NominacionEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;

import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;

@Slf4j
@Service

public class NominacionPeliculaService {
    @Autowired
    private NominacionRepository nominacionRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    @Transactional
	public PeliculaEntity addPelicula(Long nominacionId, Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de asociarle una película a la nominación con id = {0}", nominacionId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encontró la película a agregar");

		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);
		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException("No se encontró el nominacion");

            nominacionEntity.get().getPeliculas().add(peliculaEntity.get());
		log.info("Termina proceso de asociarle una película a la nominacion con id = {0}", nominacionId);
		return peliculaEntity.get();
	}

	@Transactional
	public List<PeliculaEntity> addPeliculas(Long nominacionId, List<PeliculaEntity> peliculas) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las peliculas asociadas al nominacion con id = {0}", nominacionId);
		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);
		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException("No se encuentra el nominacion con el id provisto.");

		for (PeliculaEntity pelicula : peliculas) {
			Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(pelicula.getId());
			if (peliculaEntity.isEmpty())
				throw new EntityNotFoundException("No se encuentra la pelicula con el id provisto.");

			if (!peliculaEntity.get().getNominaciones().contains(nominacionEntity.get()))
				peliculaEntity.get().getNominaciones().add(nominacionEntity.get());
		}
		log.info("Finaliza proceso de reemplazar las peliculas asociados al nominacion con id = {0}", nominacionId);
		return peliculas;
	}

    @Transactional
	public List<PeliculaEntity> getPeliculas(Long nominacionId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todas las películas de nominacion con id = {0}", nominacionId);
		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);
		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException("No se encontró nominacion");
		log.info("Finaliza proceso de consultar todas las películas del nominacion con id = {0}", nominacionId);
		return nominacionEntity.get().getPeliculas();
	}

    @Transactional
	public PeliculaEntity getPelicula(Long nominacionId, Long peliculaId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar una película del nominacion con id = {0}", nominacionId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encontró la película");

		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException("No se encontró el nominacion");

		log.info("Termina proceso de consultar una película del nominacion con id = {0}", nominacionId);
		if (nominacionEntity.get().getPeliculas().contains(peliculaEntity.get()))
			return peliculaEntity.get();

		throw new IllegalOperationException("La película no está asociada a la nominación");
	}

    @Transactional
    public List<PeliculaEntity> replacePeliculas(Long nominacionId, List<PeliculaEntity> list) 
            throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las peliculas del nominacion con id = {0}", nominacionId);
		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);
		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException("no se encontró el nominacion");

		for (PeliculaEntity pelicula : list) {
			Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(pelicula.getId());
			if (peliculaEntity.isEmpty())
				throw new EntityNotFoundException("No se encontró la película");

			if (!nominacionEntity.get().getPeliculas().contains(peliculaEntity.get()))
            nominacionEntity.get().getPeliculas().add(peliculaEntity.get());
		}
		log.info("Termina proceso de reemplazar las peliculas del nominacion con id = {0}", nominacionId);
		return getPeliculas(nominacionId);
	}

    @Transactional
    public void removePelicula(Long nominacionId, Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar una pelicula del nominacion con id = {0}", nominacionId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encontró la película");

		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException("No se encontró el nominacion");

            nominacionEntity.get().getPeliculas().remove(peliculaEntity.get());

		log.info("Termina proceso de borrar una pelicula del nominacion con id = {0}",nominacionId);
	}
}