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

import co.edu.uniandes.dse.arte7.dto.GeneroDTO;
import co.edu.uniandes.dse.arte7.dto.GeneroDetailDTO;
import co.edu.uniandes.dse.arte7.entities.GeneroEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.services.GeneroService;



    
@RestController
@RequestMapping("/generos")
public class GeneroController {
    
    @Autowired
	private GeneroService generoService;

	@Autowired
	private ModelMapper modelMapper;


    /**
	 * Busca y devuelve todos los generos que existen en la aplicacion.
	 *
	 * @return JSONArray {@link GeneroDetailDTO} - Los generos encontrados en la
	 *         aplicación. Si no hay ninguna retorna una lista vacía.
	 */
    @GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<GeneroDetailDTO> findAll() {
		List<GeneroEntity> generos = generoService.getGeneros();
		return modelMapper.map(generos, new TypeToken<List<GeneroDetailDTO>>() {
		}.getType());
	}

	/**
	 * Busca el genero con el id asociado recibido en la URL y lo devuelve.
	 *
	 * @param generoId Identificador del genero que se esta buscando. Este debe ser una
	 *               cadena de dígitos.
	 * @return JSON {@link GeneroDetailDTO} - El genero buscado
	 */
	@GetMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public GeneroDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
		GeneroEntity generoEntity = generoService.getGenero(id);
		return modelMapper.map(generoEntity, GeneroDetailDTO.class);
	}

	/**
	 * Crea un nuevo genero con la informacion que se recibe en el cuerpo de la
	 * petición y se regresa un objeto identico con un id auto-generado por la base
	 * de datos.
	 *
	 * @param genero {@link GeneroDTO} - EL genero que se desea guardar.
	 * @return JSON {@link GeneroDTO} - El genero guardado con el atributo id
	 *         autogenerado.
	 */
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public GeneroDTO create(@RequestBody GeneroDTO generoDTO) throws EntityNotFoundException {
		GeneroEntity generoEntity = generoService.createGenero(modelMapper.map(generoDTO, GeneroEntity.class));
		return modelMapper.map(generoEntity, GeneroDTO.class);
	}

	/**
	 * Actualiza el genero con el id recibido en la URL con la información que se
	 * recibe en el cuerpo de la petición.
	 *
	 * @param generoId Identificador del genero que se desea actualizar. Este debe ser
	 *               una cadena de dígitos.
	 * @param genero   {@link GeneroDTO} El genero que se desea guardar.
	 * @return JSON {@link GeneroDTO} - El genero guardada.
	 */
	@PutMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public GeneroDTO update(@PathVariable("id") Long id, @RequestBody GeneroDTO generoDTO)
			throws EntityNotFoundException {
		GeneroEntity generoEntity = generoService.updateGenero(id, modelMapper.map(generoDTO, GeneroEntity.class));
		return modelMapper.map(generoEntity, GeneroDTO.class);
	}

	/**
	 * Borra el genero con el id asociado recibido en la URL.
	 *
	 * @param generoId Identificador del genero que se desea borrar. Este debe ser una
	 *               cadena de dígitos.
	 */
	@DeleteMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") Long id) throws EntityNotFoundException, IllegalOperationException {
		generoService.deleteGenero(id);
	}
}
