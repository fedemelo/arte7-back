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
			throw new EntityNotFoundException("No se encontro una plataforma con esa Id.");

		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encontro la pelicula con esa Id.");

		peliculaEntity.get().getPlataformas().add(plataformaEntity.get());
		log.info("Termina proceso de asociarle una plataforma a la pelicula con id = {0}", peliculaId);
		return plataformaEntity.get();
	}

    public List<PlataformaEntity> getPlataformas(Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todoas las plataformas de una pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("Pelicula no encontrada.");
		log.info("Finaliza proceso consultar todoas las plataformas de una pelicula con id = {0}", peliculaId);
		return peliculaEntity.get().getPlataformas();
	}

    @Transactional
	public PlataformaEntity getPlataforma(Long peliculaId, Long plataformaId) throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar una plataforma de la pelicula con id = {0}", peliculaId);
		Optional<PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataformaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (plataformaEntity.isEmpty())
			throw new EntityNotFoundException("No se encontro la plataforma.");

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("No se encontro la pelicula.");

		log.info("Termina proceso de consultar una plataforma de la pelicula con id = {0}", peliculaId);
		if (peliculaEntity.get().getPlataformas().contains(plataformaEntity.get()))
			return plataformaEntity.get();

		throw new IllegalOperationException("La plataforma no esta asociada a la pelicula.");
	}

    @Transactional
    public List<PlataformaEntity> replacePlataformas(Long peliculaId, List<PlataformaEntity> list) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las plataformas de la pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("Pelicula no encontrada");

		for (PlataformaEntity plataforma : list) {
			Optional<PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataforma.getId());
			if (plataformaEntity.isEmpty())
				throw new EntityNotFoundException("Plataforma no encontrada.");

			if (!peliculaEntity.get().getPlataformas().contains(plataformaEntity.get()))
				peliculaEntity.get().getPlataformas().add(plataformaEntity.get());
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
			throw new EntityNotFoundException("plataforma no encontrada.");

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException("pelicula no encontrada.");

		peliculaEntity.get().getPlataformas().remove(plataformaEntity.get());

		log.info("Termina proceso de borrar una plataforma de una pelicula con id = {0}", peliculaId);
	}
}
