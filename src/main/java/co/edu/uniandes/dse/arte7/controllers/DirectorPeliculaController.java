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

import co.edu.uniandes.dse.arte7.dto.PeliculaDTO;
import co.edu.uniandes.dse.arte7.dto.PeliculaDetailDTO;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.services.DirectorPeliculaService;

/**
 * Clase que implementa el recurso "directores/{id}/peliculas".
 *
 * @author Federico Melo Barrero
 */
@RestController
@RequestMapping("/directores")
public class DirectorPeliculaController {

	@Autowired
	private DirectorPeliculaService directorPeliculaService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Asocia un autor existente con un director existente
	 *
	 * @param peliculaId El ID del autor que se va a asociar
	 * @param directorId   El ID del director al cual se le va a asociar el autor
	 * @return JSON {@link PeliculaDetailDTO} - El autor asociado.
	 */
	@PostMapping(value = "/{directorId}/peliculas/{peliculaId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PeliculaDetailDTO addPelicula(@PathVariable("peliculaId") Long peliculaId, @PathVariable("directorId") Long directorId)
			throws EntityNotFoundException {
		PeliculaEntity peliculaEntity = directorPeliculaService.addPelicula(directorId, peliculaId);
		return modelMapper.map(peliculaEntity, PeliculaDetailDTO.class);
	}

	/**
	 * Busca y devuelve el autor con el ID recibido en la URL, relativo a un director.
	 *
	 * @param peliculaId El ID del autor que se busca
	 * @param directorId   El ID del director del cual se busca el autor
	 * @return {@link PeliculaDetailDTO} - El autor encontrado en el director.
	 */
	@GetMapping(value = "/{directorId}/peliculas/{peliculaId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PeliculaDetailDTO getPelicula(@PathVariable("peliculaId") Long peliculaId, @PathVariable("directorId") Long directorId)
			throws EntityNotFoundException, IllegalOperationException {
		PeliculaEntity peliculaEntity = directorPeliculaService.getPelicula(directorId, peliculaId);
		return modelMapper.map(peliculaEntity, PeliculaDetailDTO.class);
	}

	/**
	 * Actualiza la lista de peliculas de un director con la lista que se recibe en el
	 * cuerpo.
	 *
	 * @param directorId  El ID del director al cual se le va a asociar la lista de peliculas
	 * @param peliculas JSONArray {@link PeliculaDTO} - La lista de peliculas que se desea
	 *                guardar.
	 * @return JSONArray {@link PeliculaDetailDTO} - La lista actualizada.
	 */
	@PutMapping(value = "/{directorId}/peliculas")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PeliculaDetailDTO> addPeliculas(@PathVariable("directorId") Long directorId, @RequestBody List<PeliculaDTO> peliculas)
			throws EntityNotFoundException {
		List<PeliculaEntity> entities = modelMapper.map(peliculas, new TypeToken<List<PeliculaEntity>>() {
		}.getType());
		List<PeliculaEntity> peliculasList = directorPeliculaService.replacePeliculas(directorId, entities);
		return modelMapper.map(peliculasList, new TypeToken<List<PeliculaDetailDTO>>() {
		}.getType());
	}

	/**
	 * Busca y devuelve todos los peliculas que existen en un director.
	 *
	 * @param directoresd El ID del director del cual se buscan los peliculas
	 * @return JSONArray {@link PeliculaDetailDTO} - Los peliculas encontrados en el
	 *         director. Si no hay ninguno retorna una lista vacía.
	 */
	@GetMapping(value = "/{directorId}/peliculas")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PeliculaDetailDTO> getPeliculas(@PathVariable("directorId") Long directorId) throws EntityNotFoundException {
		List<PeliculaEntity> peliculaEntity = directorPeliculaService.getPeliculas(directorId);
		return modelMapper.map(peliculaEntity, new TypeToken<List<PeliculaDetailDTO>>() {
		}.getType());
	}

	/**
	 * Elimina la conexión entre el autor y el director recibidos en la URL.
	 *
	 * @param directorId   El ID del director al cual se le va a desasociar el autor
	 * @param peliculaId El ID del autor que se desasocia
	 */
	@DeleteMapping(value = "/{directorId}/peliculas/{peliculaId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removePelicula(@PathVariable("peliculaId") Long peliculaId, @PathVariable("directorId") Long directorId)
			throws EntityNotFoundException {
		directorPeliculaService.removePelicula(directorId, peliculaId);
	}
}
