package co.edu.uniandes.dse.arte7.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uniandes.dse.arte7.dto.DirectorDTO;
import co.edu.uniandes.dse.arte7.dto.DirectorDetailDTO;
import co.edu.uniandes.dse.arte7.entities.DirectorEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.services.PeliculaDirectorService;


/**
 * Clase que implementa el recurso "peliculas/{id}/directors".
 *
 * @director ISIS2603
 */
@RestController
@RequestMapping("/peliculas")
public class PeliculaDirectorController {

	@Autowired
	private PeliculaDirectorService peliculaDirectorService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Asocia un autor existente con un libro existente
	 *
	 * @param directorId El ID del autor que se va a asociar
	 * @param peliculaId   El ID del libro al cual se le va a asociar el autor
	 * @return JSON {@link DirectorDetailDTO} - El autor asociado.
	 */
	@PostMapping(value = "/{peliculaId}/directores/{directorId}")
	@ResponseStatus(code = HttpStatus.OK)
	public DirectorDetailDTO addDirector(@PathVariable("directorId") Long directorId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException {
		DirectorEntity directorEntity = peliculaDirectorService.addDirector(peliculaId, directorId);
		return modelMapper.map(directorEntity, DirectorDetailDTO.class);
	}

	/**
	 * Busca y devuelve el autor con el ID recibido en la URL, relativo a un libro.
	 *
	 * @param directorId El ID del autor que se busca
	 * @param peliculaId   El ID del libro del cual se busca el autor
	 * @return {@link DirectorDetailDTO} - El autor encontrado en el libro.
	 */
	@GetMapping(value = "/{peliculaId}/directores/{directorId}")
	@ResponseStatus(code = HttpStatus.OK)
	public DirectorDetailDTO getDirector(@PathVariable("directorId") Long directorId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException, IllegalOperationException {
		DirectorEntity directorEntity = peliculaDirectorService.getDirector(peliculaId, directorId);
		return modelMapper.map(directorEntity, DirectorDetailDTO.class);
	}

	/**
	 * Actualiza la lista de autores de un libro con la lista que se recibe en el
	 * cuerpo.
	 *
	 * @param peliculaId  El ID del libro al cual se le va a asociar la lista de autores
	 * @param directors JSONArray {@link DirectorDTO} - La lista de autores que se desea
	 *                guardar.
	 * @return JSONArray {@link DirectorDetailDTO} - La lista actualizada.
	 */
	@PutMapping(value = "/{peliculaId}/directores")
	@ResponseStatus(code = HttpStatus.OK)
	public List<DirectorDetailDTO> addDirectors(@PathVariable("peliculaId") Long peliculaId, @RequestBody List<DirectorDTO> directors)
			throws EntityNotFoundException {
		List<DirectorEntity> entities = modelMapper.map(directors, new TypeToken<List<DirectorEntity>>() {
		}.getType());
		List<DirectorEntity> directorsList = peliculaDirectorService.replaceDirectores(peliculaId, entities);
		return modelMapper.map(directorsList, new TypeToken<List<DirectorDetailDTO>>() {
		}.getType());
	}

	/**
	 * Busca y devuelve todos los autores que existen en un libro.
	 *
	 * @param peliculasd El ID del libro del cual se buscan los autores
	 * @return JSONArray {@link DirectorDetailDTO} - Los autores encontrados en el
	 *         libro. Si no hay ninguno retorna una lista vacía.
	 */
	@GetMapping(value = "/{peliculaId}/directores")
	@ResponseStatus(code = HttpStatus.OK)
	public List<DirectorDetailDTO> getDirectors(@PathVariable("peliculaId") Long peliculaId) throws EntityNotFoundException {
		List<DirectorEntity> directorEntity = peliculaDirectorService.getDirectores(peliculaId);
		return modelMapper.map(directorEntity, new TypeToken<List<DirectorDetailDTO>>() {
		}.getType());
	}

	/**
	 * Elimina la conexión entre el autor y el libro recibidos en la URL.
	 *
	 * @param peliculaId   El ID del libro al cual se le va a desasociar el autor
	 * @param directorId El ID del autor que se desasocia
	 */
	@DeleteMapping(value = "/{peliculaId}/directores/{directorId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeDirector(@PathVariable("directorId") Long directorId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException {
		peliculaDirectorService.removeDirector(peliculaId, directorId);
	}
}
