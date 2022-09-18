package co.edu.uniandes.dse.arte7.services;

import java.util.Calendar;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.DirectorEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.DirectorRepository;
import lombok.extern.slf4j.Slf4j;

/**
 * Clase que implementa la conexion con la persistencia para la entidad de
 * Director.
 *
 * @author Federico Melo Barrero
 */

@Slf4j
@Service
public class DirectorService {

    private String NO_ENCUENTRA_DIR = "No se encuentra el director con el id provisto.";

	@Autowired
	DirectorRepository directorRepository;
	
	/**
	 * Se encarga de crear un Director en la base de datos.
	 *
	 * @param director Objeto de DirectorEntity con los datos nuevos
	 * @return Objeto de DirectorEntity con los datos nuevos y su ID.
	 * @throws IllegalOperationException 
	 */
	@Transactional
	public DirectorEntity createDirector(DirectorEntity director) throws IllegalOperationException {
		log.info("Inicia proceso de creación del director");

        /* Regla de negocio: Fecha de nacimiento debe ser anterior a fecha actual. */
		Calendar calendar = Calendar.getInstance();
		if(director.getFechaNacimiento().compareTo(calendar.getTime()) > 0) {
			throw new IllegalOperationException("La fecha de nacimiento es posterior a la fecha actual.");
	    }
		
		return directorRepository.save(director);
	}

	/**
	 * Obtiene la lista de los registros de Director.
	 *
	 * @return Colección de objetos de DirectorEntity.
	 */
	@Transactional
	public List<DirectorEntity> getDirectores() {
		log.info("Inicia proceso de consultar todos los directores");
		return directorRepository.findAll();
	}

	/**
	 * Obtiene los datos de una instancia de Director a partir de su ID.
	 *
	 * @param directorId Identificador de la instancia a consultar
	 * @return Instancia de DirectorEntity con los datos del Director consultado.
	 */
	@Transactional
	public DirectorEntity getDirector(Long directorId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar el director con id = {0}", directorId);
		Optional<DirectorEntity> directorEntity = directorRepository.findById(directorId);
		if (directorEntity.isEmpty())
			throw new EntityNotFoundException(NO_ENCUENTRA_DIR);
		log.info("Termina proceso de consultar el director con id = {0}", directorId);
		return directorEntity.get();
	}

	/**
	 * Actualiza la información de una instancia de Director.
	 *
	 * @param directorId     Identificador de la instancia a actualizar
	 * @param directorEntity Instancia de DirectorEntity con los nuevos datos.
	 * @return Instancia de DirectorEntity con los datos actualizados.
	 */
	@Transactional
	public DirectorEntity updateDirector(Long directorId, DirectorEntity director) throws EntityNotFoundException {
		log.info("Inicia proceso de actualizar el director con id = {0}", directorId);
		Optional<DirectorEntity> directorEntity = directorRepository.findById(directorId);
		if (directorEntity.isEmpty())
			throw new EntityNotFoundException(NO_ENCUENTRA_DIR);
		log.info("Termina proceso de actualizar el director con id = {0}", directorId);
		director.setId(directorId);
		return directorRepository.save(director);
	}

	/**
	 * Elimina una instancia de Director de la base de datos.
	 *
	 * @param directorId Identificador de la instancia a eliminar.
	 * @throws BusinessLogicException si el director tiene libros asociados.
	 */
	@Transactional
	public void deleteDirector(Long directorId) throws IllegalOperationException, EntityNotFoundException {
		log.info("Inicia proceso de borrar el director con id = {0}", directorId);
		Optional<DirectorEntity> directorEntity = directorRepository.findById(directorId);
		if (directorEntity.isEmpty())
			throw new EntityNotFoundException(NO_ENCUENTRA_DIR);

		List<PeliculaEntity> peliculas = directorEntity.get().getPeliculas();
		if (!peliculas.isEmpty())
			throw new IllegalOperationException("No es posible eliminar el director porque hay películas asociadas a el o ella.");

		directorRepository.deleteById(directorId);
		log.info("Termina proceso de borrar el director con id = {0}", directorId);
	}
}
