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
import co.edu.uniandes.dse.arte7.services.ActorService;

/**
 * Clase que implementa el recurso "actores".
 *
 * @author Federico Melo Barrero
 */
@RestController
@RequestMapping("/actores")
public class ActorController {

	@Autowired
	private ActorService actorService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Busca y devuelve todos los actores que existen en la aplicacion.
	 *
	 * @return JSONArray {@link ActorDetailDTO} - Los actores encontrados en la
	 *         aplicación. Si no hay ninguno retorna una lista vacía.
	 */
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<ActorDetailDTO> findAll() {
		List<ActorEntity> actores = actorService.getActores();
		return modelMapper.map(actores, new TypeToken<List<ActorDetailDTO>>() {
		}.getType());
	}

	/**
	 * Busca el actor con el id asociado recibido en la URL y lo devuelve.
	 *
	 * @param id Identificador del actor que se esta buscando. Este debe ser una
	 *           cadena de dígitos.
	 * @return JSON {@link ActorDetailDTO} - El actor buscado
	 */
	@GetMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public ActorDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
		ActorEntity actorEntity = actorService.getActor(id);
		return modelMapper.map(actorEntity, ActorDetailDTO.class);
	}

	/**
	 * Crea un nuevo actor con la informacion que se recibe en el cuerpo de la
	 * petición y se regresa un objeto identico con un id auto-generado por la base
	 * de datos.
	 *
	 * @param actorDTO {@link ActorDTO} - EL actor que se desea guardar.
	 * @return JSON {@link ActorDTO} - El actor guardado con el atributo id
	 *         autogenerado.
	 * @throws IllegalOperationException 
	 */
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public ActorDTO create(@RequestBody ActorDTO actorDTO) throws IllegalOperationException {
		ActorEntity actorEntity = actorService.createActor(modelMapper.map(actorDTO, ActorEntity.class));
		return modelMapper.map(actorEntity, ActorDTO.class);
	}

	/**
	 * Actualiza el actor con el id recibido en la URL con la información que se
	 * recibe en el cuerpo de la petición.
	 *
	 * @param id     Identificador del actor que se desea actualizar. Este debe ser
	 *               una cadena de dígitos.
	 * @param actor {@link ActorDTO} El actor que se desea guardar.
	 */
	@PutMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public ActorDTO update(@PathVariable("id") Long id, @RequestBody ActorDTO actorDTO)
			throws EntityNotFoundException {
		ActorEntity actorEntity = actorService.updateActor(id, modelMapper.map(actorDTO, ActorEntity.class));
		return modelMapper.map(actorEntity, ActorDTO.class);
	}

	/**
	 * Borra el actor con el id asociado recibido en la URL.
	 *
	 * @param id Identificador del actor que se desea borrar. Este debe ser una
	 *           cadena de dígitos.
	 */
	@DeleteMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") Long id) throws EntityNotFoundException, IllegalOperationException {
		actorService.deleteActor(id);
	}

}
