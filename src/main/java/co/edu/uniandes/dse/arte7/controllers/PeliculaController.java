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
import co.edu.uniandes.dse.arte7.services.PeliculaService;

/**
 * Clase que implementa el recurso "books".
 *
 * @author Mariana Ruiz
 */
@RestController
@RequestMapping("/peliculas")
public class PeliculaController {
    
    @Autowired
	private PeliculaService peliculaService;

	@Autowired
	private ModelMapper modelMapper;

    /**
	 * Busca y devuelve todas las peliculas que existen en la aplicacion.
	 *
	 * @return JSONArray {@link PeliculaDetailDTO} - Las peliculas encontradas en la
	 *         aplicación. Si no hay ninguna retorna una lista vacía.
	 */
	@GetMapping
	@ResponseStatus(code = HttpStatus.OK)
	public List<PeliculaDetailDTO> findAll() {
		List<PeliculaEntity> peliculas = peliculaService.getPeliculas();
		return modelMapper.map(peliculas, new TypeToken<List<PeliculaDetailDTO>>() {
		}.getType());
	}

    /**
	 * Busca la pelicula con el id asociado recibido en la URL y la devuelve.
	 *
	 * @param peliculaId Identificador de la pelicula que se esta buscando. Este debe ser una
	 *               cadena de dígitos.
	 * @return JSON {@link PeliculaDetailDTO} - La pelicula buscada
	 */
	@GetMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public PeliculaDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
		PeliculaEntity peliculaEntity = peliculaService.getPelicula(id);
		return modelMapper.map(peliculaEntity, PeliculaDetailDTO.class);
	}

    
	/**
	 * Crea una nueva pelicula con la informacion que se recibe en el cuerpo de la
	 * petición y se regresa un objeto identico con un id auto-generado por la base
	 * de datos.
	 *
	 * @param pelicula {@link PeliculaDTO} - La película que se desea guardar.
	 * @return JSON {@link PeliculaDTO} - La pelicula guardada con el atributo id
	 *         autogenerado.
	 */
	@PostMapping
	@ResponseStatus(code = HttpStatus.CREATED)
	public PeliculaDTO create(@RequestBody PeliculaDTO peliculaDTO) throws IllegalOperationException, EntityNotFoundException {
		PeliculaEntity peliculaEntity = peliculaService.createPelicula(modelMapper.map(peliculaDTO, PeliculaEntity.class));
		return modelMapper.map(peliculaEntity, PeliculaDTO.class);
	}

    /**
	 * Actualiza la pelicula con el id recibido en la URL con la información que se
	 * recibe en el cuerpo de la petición.
	 *
	 * @param peliculaId Identificador de la pelicula que se desea actualizar. Este debe ser
	 *               una cadena de dígitos.
	 * @param pelicula   {@link PeliculaDTO} La pelicula que se desea guardar.
	 * @return JSON {@link PeliculaDTO} - La pelicula guardada.
	 */
	@PutMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public PeliculaDTO update(@PathVariable("id") Long id, @RequestBody PeliculaDTO peliculaDTO)
			throws EntityNotFoundException {
		PeliculaEntity peliculaEntity = peliculaService.updatePelicula(id, modelMapper.map(peliculaDTO, PeliculaEntity.class));
		return modelMapper.map(peliculaEntity, PeliculaDTO.class);
	}

    /**
	 * Borra la película con el id asociado recibido en la URL.
	 *
	 * @param peliculaId Identificador de la pelicula que se desea borrar. Este debe ser una
	 *               cadena de dígitos.
	 */
	@DeleteMapping(value = "/{id}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") Long id) throws EntityNotFoundException, IllegalOperationException {
		peliculaService.deletePelicula(id);
	}
    
}
