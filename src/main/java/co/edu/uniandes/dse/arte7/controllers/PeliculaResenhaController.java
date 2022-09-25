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

import co.edu.uniandes.dse.arte7.services.PeliculaResenhaService;
import co.edu.uniandes.dse.arte7.dto.ResenhaDTO;
import co.edu.uniandes.dse.arte7.entities.ResenhaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;


@RestController
@RequestMapping("/peliculas")
public class PeliculaResenhaController {

	@Autowired
	private PeliculaResenhaService peliculaResenhaService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Busca y devuelve el libro con el ID recibido en la URL, relativo a un autor.
	 *
	 * @param peliculaId El ID de la pelicula de la cual se busca lareseña 
	 * @param resenhaId   El ID del reseña que se busca.
	 * @return {@link resenhaDetailDTO} - La reseña encontrado en la pelicula.
	 */
	@GetMapping(value = "/{peliculaId}/resenhas/{resenhaId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResenhaDTO getResenha(@PathVariable("peliculaId") Long peliculaId, @PathVariable("resenhaId") Long resenhaId)
			throws EntityNotFoundException, IllegalOperationException {
		ResenhaEntity resenhaEntity = peliculaResenhaService.getResenha(peliculaId, resenhaId);
		return modelMapper.map(resenhaEntity, ResenhaDTO.class);
	}

	/**
	 * Busca y devuelve todos las reseñas que existen en una pelicula.
	 *
	 * @param peliculasId El ID de la pelicula a la cual se buscan las reseñas.
	 * @return JSONArray {@link resenhaDetailDTO} - Las reseñas encontradas en la pelicula.
	 *         Si no hay ninguno retorna una lista vacía.
	 */
	@GetMapping(value = "/{peliculaId}/resenhas")
	@ResponseStatus(code = HttpStatus.OK)
	public List<ResenhaDTO> getResenhas(@PathVariable("peliculaId") Long peliculaId) throws EntityNotFoundException {
		List<ResenhaEntity> resenhaEntity = peliculaResenhaService.getResenhas(peliculaId);
		return modelMapper.map(resenhaEntity, new TypeToken<List<ResenhaDTO>>() {}.getType());
	}

	@PutMapping(value = "/{peliculaId}/resenhas")
	@ResponseStatus(code = HttpStatus.OK)
	public List<ResenhaDTO> addAuthors(@PathVariable("peliculaId") Long peliculaId, @RequestBody List<ResenhaDTO> resenhas)
			throws EntityNotFoundException {
		List<ResenhaEntity> entities = modelMapper.map(resenhas, new TypeToken<List<ResenhaEntity>>() {
		}.getType());
		List<ResenhaEntity> authorsList = peliculaResenhaService.replaceResenhas(peliculaId, entities);
		return modelMapper.map(authorsList, new TypeToken<List<ResenhaDTO>>() {
		}.getType());
	}
	/**
	 * Asocia una reseña existente con una pelicula existente.
	 *
	 * @param peliculaId El ID de la pelicual al cual se le va a asociar la reseña.
	 * @param resenhaId   El ID de la reseña que se asocia.
	 * @return JSON {@link resenhaDetailDTO} - La reseña asociado.
	 */
	@PostMapping(value = "/{peliculaId}/resenhas/{resenhaId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResenhaDTO addresenha(@PathVariable("peliculaId") Long peliculaId, @PathVariable("resenhaId") Long resenhaId)throws EntityNotFoundException {
		ResenhaEntity resenhaEntity = peliculaResenhaService.addResenha(peliculaId, resenhaId);
		return modelMapper.map(resenhaEntity, ResenhaDTO.class);
	}


	/**
	 * Elimina la conexión entre la reseña y la pelicula recibidos en la URL.
	 *
	 * @param peliculaId El ID de la pelicula al cual se le va a desasociar la reseña
	 * @param resenhaId   El ID del reseña que se desasocia
	 */
	@DeleteMapping(value = "/{peliculaId}/resenhas/{resenhaId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeresenha(@PathVariable("peliculaId") Long peliculaId, @PathVariable("resenhaId") Long resenhaId)
			throws EntityNotFoundException {
		peliculaResenhaService.removeResenha(peliculaId, resenhaId);
	}
}
