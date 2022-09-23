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
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.services.ResenhaService;

@RestController
@RequestMapping("/resenhas")

public class ResenhaController {

    @Autowired
    private ResenhaService resenhaService;
    
    @Autowired
    private ModelMapper modelMapper;

	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<ResenhaDTO> findAll() {
		List<ResenhaEntity> resenhas = resenhaService.getResenhas();
		return modelMapper.map(resenhas, new TypeToken<List<ResenhaDTO>>() {
		}.getType());
	}

	/** Busca y devuelve la reseña con el ID recibido en la URL, relativa a la película */
	@GetMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResenhaDTO findOne(@PathVariable("id") Long id)
			throws EntityNotFoundException {
		ResenhaEntity entity = resenhaService.getResenha(id);
		return modelMapper.map(entity, ResenhaDTO.class);
	}

    @PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public ResenhaDTO create(@RequestBody ResenhaDTO resenhaDTO)
			throws IllegalOperationException {
		ResenhaEntity resenhaEnity = modelMapper.map(resenhaDTO, ResenhaEntity.class);
		return modelMapper.map(resenhaEnity, ResenhaDTO.class);
	}

	/**Busca y devuelve todas las reseñas que existen en la película */
	
   

	/** Actualiza una reseña con la informacion que se recibe en el cuerpo de la petición y se regresa el objeto actualizado */
	@PutMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResenhaDTO update(@PathVariable("id") Long id, @RequestBody ResenhaDTO resenhaDTO) throws EntityNotFoundException {
		ResenhaEntity resenhaEntity = resenhaService.updateResenha(id, modelMapper.map(resenhaDTO, ResenhaEntity.class));
		return modelMapper.map(resenhaEntity, ResenhaDTO.class);
	}

	/** Borra la reseña con el id asociado recibido en la URL */

	@DeleteMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") Long id) throws EntityNotFoundException, IllegalOperationException {
	    resenhaService.deleteResenha(id);
	}
}