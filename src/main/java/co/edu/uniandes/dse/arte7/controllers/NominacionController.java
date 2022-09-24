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

import co.edu.uniandes.dse.arte7.dto.NominacionDTO;
import co.edu.uniandes.dse.arte7.dto.NominacionDetailDTO;
import co.edu.uniandes.dse.arte7.entities.NominacionEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.services.NominacionService;



    
@RestController
@RequestMapping("/nominaciones")
public class NominacionController {
    
    @Autowired
	private NominacionService nominacionService;

	@Autowired
	private ModelMapper modelMapper;


    /**
	 * Busca y devuelve todas las nominaciones que existen en la aplicacion.
	 *
	 * @return JSONArray {@link NominacionDetailDTO} - Los nominaciones encontrados en la
	 *         aplicación. Si no hay ninguna retorna una lista vacía.
	 */
    @GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<NominacionDetailDTO> findAll() {
		List<NominacionEntity> nominaciones = nominacionService.getNominaciones();
		return modelMapper.map(nominaciones, new TypeToken<List<NominacionDetailDTO>>() {
		}.getType());
	}

	/**
	 * Busca la nominacion con el id asociado recibido en la URL y lo devuelve.
	 *
	 * @param nominacionId Identificador dla nominacion que se esta buscando. Este debe ser una
	 *               cadena de dígitos.
	 * @return JSON {@link NominacionDetailDTO} - El nominacion buscado
	 */
	@GetMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public NominacionDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
		NominacionEntity nominacionEntity = nominacionService.getNominacion(id);
		return modelMapper.map(nominacionEntity, NominacionDetailDTO.class);
	}

	/**
	 * Crea un nueva nominacion con la informacion que se recibe en el cuerpo de la
	 * petición y se regresa un objeto identico con un id auto-generado por la base
	 * de datos.
	 *
	 * @param nominacion {@link NominacionDTO} - EL nominacion que se desea guardar.
	 * @return JSON {@link NominacionDTO} - El nominacion guardado con el atributo id
	 *         autogenerado.
	 */
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public NominacionDTO create(@RequestBody NominacionDTO nominacionDTO) throws IllegalOperationException, EntityNotFoundException {
		NominacionEntity nominacionEntity = nominacionService.createNominacion(modelMapper.map(nominacionDTO, NominacionEntity.class));
		return modelMapper.map(nominacionEntity, NominacionDTO.class);
	}

	/**
	 * Actualiza la nominacion con el id recibido en la URL con la información que se
	 * recibe en el cuerpo de la petición.
	 *
	 * @param nominacionId Identificador de la nominacion que se desea actualizar. Este debe ser
	 *               una cadena de dígitos.
	 * @param nominacion   {@link NominacionDTO} El nominacion que se desea guardar.
	 * @return JSON {@link NominacionDTO} - El nominacion guardada.
	 */
	@PutMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public NominacionDTO update(@PathVariable("id") Long id, @RequestBody NominacionDTO nominacionDTO)
			throws EntityNotFoundException, IllegalOperationException {
		NominacionEntity nominacionEntity = nominacionService.updateNominacion(id, modelMapper.map(nominacionDTO, NominacionEntity.class));
		return modelMapper.map(nominacionEntity, NominacionDTO.class);
	}

	/**
	 * Borra la nominacion con el id asociado recibido en la URL.
	 *
	 * @param nominacionId Identificador de la nominacion que se desea borrar. Este debe ser una
	 *               cadena de dígitos.
	 */
	@DeleteMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") Long id) throws EntityNotFoundException, IllegalOperationException {
		nominacionService.deleteNominacion(id);
	}
}
