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
	 * Asocia un actor existente con una pelicula existente
	 *
	 * @param actorId El ID del actor que se va a asociar
	 * @param peliculaId   El ID de la pelicula a la cual se le va a asociar el actor
	 * @return JSON {@link ActorDetailDTO} - El actor asociado.
	 */
	@PostMapping(value = "/{peliculaId}/actores/{actorId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ActorDetailDTO addActor(@PathVariable("actorId") Long actorId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException {
		ActorEntity actorEntity = peliculaActorService.addActor(peliculaId, actorId);
		return modelMapper.map(actorEntity, ActorDetailDTO.class);
	}

	/**
	 * Busca y devuelve el actor con el ID recibido en la URL, relativo a una pelicula.
	 *
	 * @param actorId El ID del actor que se busca
	 * @param peliculaId   El ID del pelicula del cual se busca el actor
	 * @return {@link ActorDetailDTO} - El actor encontrado en la pelicula.
	 */
	@GetMapping(value = "/{peliculaId}/actores/{actorId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ActorDetailDTO getActor(@PathVariable("actorId") Long actorId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException, IllegalOperationException {
		ActorEntity actorEntity = peliculaActorService.getActor(peliculaId, actorId);
		return modelMapper.map(actorEntity, ActorDetailDTO.class);
	}

	/**
	 * Actualiza la lista de actores de una pelicula con la lista que se recibe en el
	 * cuerpo.
	 *
	 * @param peliculaId  El ID del pelicula al cual se le va a asociar la lista de actores
	 * @param actors JSONArray {@link ActorDTO} - La lista de actores que se desea
	 *                guardar.
	 * @return JSONArray {@link ActorDetailDTO} - La lista actualizada.
	 */
	@PutMapping(value = "/{peliculaId}/actores")
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
	 * Busca y devuelve todos los actores que existen en una pelicula.
	 *
	 * @param peliculasd El ID de la pelicula del cual se buscan los actores
	 * @return JSONArray {@link ActorDetailDTO} - Los actores encontrados en la
	 *         pelicula. Si no hay ninguno retorna una lista vacía.
	 */
	@GetMapping(value = "/{peliculaId}/actores")
	@ResponseStatus(code = HttpStatus.OK)
	public List<ActorDetailDTO> getActores(@PathVariable("peliculaId") Long peliculaId) throws EntityNotFoundException {
		List<ActorEntity> actorEntity = peliculaActorService.getActores(peliculaId);
		return modelMapper.map(actorEntity, new TypeToken<List<ActorDetailDTO>>() {
		}.getType());
	}

	/**
	 * Elimina la conexión entre el actor y la pelicula recibidos en la URL.
	 *
	 * @param peliculaId   El ID de la pelicula al cual se le va a desasociar el actor
	 * @param actorId El ID del actor que se desasocia
	 */
	@DeleteMapping(value = "/{peliculaId}/actores/{actorId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeActor(@PathVariable("actorId") Long actorId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException {
		peliculaActorService.removeActor(peliculaId, actorId);
	}
}
