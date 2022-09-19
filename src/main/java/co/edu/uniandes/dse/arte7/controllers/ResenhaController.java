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

import co.edu.uniandes.dse.arte7.dto.ResenhaDTO;
import co.edu.uniandes.dse.arte7.entities.ResenhaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.services.ResenhaService;

@RestController
@RequestMapping("/resenhas")

public class ResenhaController {

    @Autowired
    private ResenhaService resenhaService;
    
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping(value = "/{peliculaId}/resenhas")
	@ResponseStatus(code = HttpStatus.CREATED)
	public ResenhaDTO createResenha(@PathVariable("peliculaId") Long peliculaId, @RequestBody ResenhaDTO resenha)
			throws EntityNotFoundException {
		ResenhaEntity resenhaEnity = modelMapper.map(resenha, ResenhaEntity.class);
		ResenhaEntity newResenha = resenhaService.createResenha(peliculaId, resenhaEnity);
		return modelMapper.map(newResenha, ResenhaDTO.class);
	}

	/**Busca y devuelve todas las reseñas que existen en la película */
	
    @GetMapping(value = "/{peliculaId}/resenhas")
	@ResponseStatus(code = HttpStatus.OK)
	public List<ResenhaDTO> getResenhas(@PathVariable("peliculaId") Long peliculaId) throws EntityNotFoundException {
		List<ResenhaEntity> resenhas = resenhaService.getResenhas(peliculaId);
		return modelMapper.map(resenhas, new TypeToken<List<ResenhaDTO>>() {
		}.getType());
	}

	/** Busca y devuelve la reseña con el ID recibido en la URL, relativa a la película */
	@GetMapping(value = "/{peliculaId}/resenhas/{resenhaId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResenhaDTO getResenha(@PathVariable("peliculaId") Long peliculaId, @PathVariable("resenhaId") Long resenhaId)
			throws EntityNotFoundException {
		ResenhaEntity entity = resenhaService.getResenha(peliculaId, resenhaId);
		return modelMapper.map(entity, ResenhaDTO.class);
	}

	/** Actualiza una reseña con la informacion que se recibe en el cuerpo de la petición y se regresa el objeto actualizado */
	@PutMapping(value = "/{peliculaId}/resenhas/{resenhasId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResenhaDTO updateResenha(@PathVariable("peliculaId") Long peliculaId, @PathVariable("resenhasId") Long resenhaId,
			@RequestBody ResenhaDTO resenha) throws EntityNotFoundException {
		ResenhaEntity resenhaEntity = modelMapper.map(resenha, ResenhaEntity.class);
		ResenhaEntity newEntity = resenhaService.updateResenha(peliculaId, resenhaId, resenhaEntity);
		return modelMapper.map(newEntity, ResenhaDTO.class);
	}

	/** Borra la reseña con el id asociado recibido en la URL */
	@DeleteMapping(value = "/{peliculaId}/resenhas/{resenhaId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void deleteResenha(@PathVariable("peliculaId") Long peliculaId, @PathVariable("resenhaId") Long resenhaId)
			throws EntityNotFoundException {
		resenhaService.deleteResenha(peliculaId, resenhaId);
	}
}
