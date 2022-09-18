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
import co.edu.uniandes.dse.arte7.services.DirectorService;

/**
 * Clase que implementa el recurso "directores".
 *
 * @author Federico Melo Barrero
 */
@RestController
@RequestMapping("/directores")
public class DirectorController {

	@Autowired
	private DirectorService directorService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Busca y devuelve todos los directores que existen en la aplicacion.
	 *
	 * @return JSONArray {@link DirectorDetailDTO} - Los directores encontrados en la
	 *         aplicación. Si no hay ninguno retorna una lista vacía.
	 */
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<DirectorDetailDTO> findAll() {
		List<DirectorEntity> directores = directorService.getDirectores();
		return modelMapper.map(directores, new TypeToken<List<DirectorDetailDTO>>() {
		}.getType());
	}

	/**
	 * Busca el director con el id asociado recibido en la URL y lo devuelve.
	 *
	 * @param id Identificador del director que se esta buscando. Este debe ser una
	 *           cadena de dígitos.
	 * @return JSON {@link DirectorDetailDTO} - El director buscado
	 */
	@GetMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public DirectorDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
		DirectorEntity directorEntity = directorService.getDirector(id);
		return modelMapper.map(directorEntity, DirectorDetailDTO.class);
	}

	/**
	 * Crea un nuevo director con la informacion que se recibe en el cuerpo de la
	 * petición y se regresa un objeto identico con un id auto-generado por la base
	 * de datos.
	 *
	 * @param directorDTO {@link DirectorDTO} - EL director que se desea guardar.
	 * @return JSON {@link DirectorDTO} - El director guardado con el atributo id
	 *         autogenerado.
	 * @throws IllegalOperationException 
	 */
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public DirectorDTO create(@RequestBody DirectorDTO directorDTO) throws IllegalOperationException {
		DirectorEntity directorEntity = directorService.createDirector(modelMapper.map(directorDTO, DirectorEntity.class));
		return modelMapper.map(directorEntity, DirectorDTO.class);
	}

	/**
	 * Actualiza el director con el id recibido en la URL con la información que se
	 * recibe en el cuerpo de la petición.
	 *
	 * @param id     Identificador del director que se desea actualizar. Este debe ser
	 *               una cadena de dígitos.
	 * @param director {@link DirectorDTO} El director que se desea guardar.
	 */
	@PutMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public DirectorDTO update(@PathVariable("id") Long id, @RequestBody DirectorDTO directorDTO)
			throws EntityNotFoundException {
		DirectorEntity directorEntity = directorService.updateDirector(id, modelMapper.map(directorDTO, DirectorEntity.class));
		return modelMapper.map(directorEntity, DirectorDTO.class);
	}

	/**
	 * Borra el director con el id asociado recibido en la URL.
	 *
	 * @param id Identificador del director que se desea borrar. Este debe ser una
	 *           cadena de dígitos.
	 */
	@DeleteMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") Long id) throws EntityNotFoundException, IllegalOperationException {
		directorService.deleteDirector(id);
	}

}
