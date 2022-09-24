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

import co.edu.uniandes.dse.arte7.dto.ActorDTO;
import co.edu.uniandes.dse.arte7.dto.ActorDetailDTO;
import co.edu.uniandes.dse.arte7.entities.ActorEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.services.PeliculaActorService;


/**
 * Clase que implementa el recurso "peliculas/{id}/actores".
 *
 * @actor ISIS2603
 */
@RestController
@RequestMapping("/peliculas")
public class PeliculaActorController {

	@Autowired
	private PeliculaActorService peliculaActorService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Asocia un autor existente con un libro existente
	 *
	 * @param actorId El ID del autor que se va a asociar
	 * @param peliculaId   El ID del libro al cual se le va a asociar el autor
	 * @return JSON {@link ActorDetailDTO} - El autor asociado.
	 */
	@PostMapping(value = "/{peliculaId}/actors/{actorId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ActorDetailDTO addActor(@PathVariable("actorId") Long actorId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException {
		ActorEntity actorEntity = peliculaActorService.addActor(peliculaId, actorId);
		return modelMapper.map(actorEntity, ActorDetailDTO.class);
	}

	/**
	 * Busca y devuelve el autor con el ID recibido en la URL, relativo a un libro.
	 *
	 * @param actorId El ID del autor que se busca
	 * @param peliculaId   El ID del libro del cual se busca el autor
	 * @return {@link ActorDetailDTO} - El autor encontrado en el libro.
	 */
	@GetMapping(value = "/{peliculaId}/actors/{actorId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ActorDetailDTO getActor(@PathVariable("actorId") Long actorId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException, IllegalOperationException {
		ActorEntity actorEntity = peliculaActorService.getActor(peliculaId, actorId);
		return modelMapper.map(actorEntity, ActorDetailDTO.class);
	}

	/**
	 * Actualiza la lista de autores de un libro con la lista que se recibe en el
	 * cuerpo.
	 *
	 * @param peliculaId  El ID del libro al cual se le va a asociar la lista de autores
	 * @param actors JSONArray {@link ActorDTO} - La lista de autores que se desea
	 *                guardar.
	 * @return JSONArray {@link ActorDetailDTO} - La lista actualizada.
	 */
	@PutMapping(value = "/{peliculaId}/actors")
	@ResponseStatus(code = HttpStatus.OK)
	public List<ActorDetailDTO> addActores(@PathVariable("peliculaId") Long peliculaId, @RequestBody List<ActorDTO> actors)
			throws EntityNotFoundException {
		List<ActorEntity> entities = modelMapper.map(actors, new TypeToken<List<ActorEntity>>() {
		}.getType());
		List<ActorEntity> actorsList = peliculaActorService.replaceActores(peliculaId, entities);
		return modelMapper.map(actorsList, new TypeToken<List<ActorDetailDTO>>() {
		}.getType());
	}

	/**
	 * Busca y devuelve todos los autores que existen en un libro.
	 *
	 * @param peliculasd El ID del libro del cual se buscan los autores
	 * @return JSONArray {@link ActorDetailDTO} - Los autores encontrados en el
	 *         libro. Si no hay ninguno retorna una lista vacía.
	 */
	@GetMapping(value = "/{peliculaId}/actors")
	@ResponseStatus(code = HttpStatus.OK)
	public List<ActorDetailDTO> getActores(@PathVariable("peliculaId") Long peliculaId) throws EntityNotFoundException {
		List<ActorEntity> actorEntity = peliculaActorService.getActores(peliculaId);
		return modelMapper.map(actorEntity, new TypeToken<List<ActorDetailDTO>>() {
		}.getType());
	}

	/**
	 * Elimina la conexión entre el autor y el libro recibidos en la URL.
	 *
	 * @param peliculaId   El ID del libro al cual se le va a desasociar el autor
	 * @param actorId El ID del autor que se desasocia
	 */
	@DeleteMapping(value = "/{peliculaId}/actors/{actorId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeActor(@PathVariable("actorId") Long actorId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException {
		peliculaActorService.removeActor(peliculaId, actorId);
	}
}
