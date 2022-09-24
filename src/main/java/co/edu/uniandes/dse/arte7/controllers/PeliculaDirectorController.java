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
 * Clase que implementa el recurso "peliculas/{id}/directores".
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
	 * Asocia un director existente con una pelicula existente
	 *
	 * @param directorId El ID del director que se va a asociar
	 * @param peliculaId   El ID de la pelicula al cual se le va a asociar el director
	 * @return JSON {@link DirectorDetailDTO} - El director asociado.
	 */
	@PostMapping(value = "/{peliculaId}/directores/{directorId}")
	@ResponseStatus(code = HttpStatus.OK)
	public DirectorDetailDTO addDirector(@PathVariable("directorId") Long directorId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException {
		DirectorEntity directorEntity = peliculaDirectorService.addDirector(peliculaId, directorId);
		return modelMapper.map(directorEntity, DirectorDetailDTO.class);
	}

	/**
	 * Busca y devuelve el director con el ID recibido en la URL, relativo a una pelicula.
	 *
	 * @param directorId El ID del director que se busca
	 * @param peliculaId   El ID de la pelicula del cual se busca el director
	 * @return {@link DirectorDetailDTO} - El director encontrado en la pelicula.
	 */
	@GetMapping(value = "/{peliculaId}/directores/{directorId}")
	@ResponseStatus(code = HttpStatus.OK)
	public DirectorDetailDTO getDirector(@PathVariable("directorId") Long directorId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException, IllegalOperationException {
		DirectorEntity directorEntity = peliculaDirectorService.getDirector(peliculaId, directorId);
		return modelMapper.map(directorEntity, DirectorDetailDTO.class);
	}

	/**
	 * Actualiza la lista de directores de una pelicula con la lista que se recibe en el
	 * cuerpo.
	 *
	 * @param peliculaId  El ID de la pelicula al cual se le va a asociar la lista de directores
	 * @param directors JSONArray {@link DirectorDTO} - La lista de directores que se desea
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
	 * Busca y devuelve todos los directores que existen en una pelicula.
	 *
	 * @param peliculasd El ID de la pelicula del cual se buscan los directores
	 * @return JSONArray {@link DirectorDetailDTO} - Los directores encontrados en la
	 *         pelicula. Si no hay ninguno retorna una lista vacía.
	 */
	@GetMapping(value = "/{peliculaId}/directores")
	@ResponseStatus(code = HttpStatus.OK)
	public List<DirectorDetailDTO> getDirectors(@PathVariable("peliculaId") Long peliculaId) throws EntityNotFoundException {
		List<DirectorEntity> directorEntity = peliculaDirectorService.getDirectores(peliculaId);
		return modelMapper.map(directorEntity, new TypeToken<List<DirectorDetailDTO>>() {
		}.getType());
	}

	/**
	 * Elimina la conexión entre el director y la pelicula recibidos en la URL.
	 *
	 * @param peliculaId   El ID de la pelicula al cual se le va a desasociar el director
	 * @param directorId El ID del director que se desasocia
	 */
	@DeleteMapping(value = "/{peliculaId}/directores/{directorId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeDirector(@PathVariable("directorId") Long directorId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException {
		peliculaDirectorService.removeDirector(peliculaId, directorId);
	}
}
