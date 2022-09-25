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
import co.edu.uniandes.dse.arte7.services.PeliculaGeneroService;


@RestController
@RequestMapping("/peliculas")


public class PeliculaGeneroController {
    
    @Autowired
	private PeliculaGeneroService peliculaGeneroService;

	@Autowired
	private ModelMapper modelMapper;

    @PostMapping(value = "/{peliculaId}/generos/{generoId}")
	@ResponseStatus(code = HttpStatus.OK)
	public GeneroDetailDTO addGenero(@PathVariable("generoId") Long generoId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException {
                GeneroEntity generoEntity = peliculaGeneroService.addGenero(peliculaId, generoId);
	        return modelMapper.map(generoEntity, GeneroDetailDTO.class);
	}

    @GetMapping(value = "{peliculaId}/generos/{generoId}")
	@ResponseStatus(code = HttpStatus.OK)
	public GeneroDetailDTO getGenero(@PathVariable("generoId") Long generoId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException, IllegalOperationException {
                GeneroEntity generoEntity = peliculaGeneroService.getGenero(peliculaId, generoId);
		return modelMapper.map(generoEntity, GeneroDetailDTO.class);
	}

    @PutMapping(value = "/{peliculaId}/generos")
	@ResponseStatus(code = HttpStatus.OK)
	public List<GeneroDetailDTO> addGeneros(@PathVariable("peliculaId") Long peliculaId, @RequestBody List<GeneroDTO> generos)
			throws EntityNotFoundException {
                List<GeneroEntity> entities = modelMapper.map(generos, new TypeToken<List<GeneroEntity>>() {
		}.getType());
		List<GeneroEntity> generosList = peliculaGeneroService.updateGeneros(peliculaId, entities);
		return modelMapper.map(generosList, new TypeToken<List<GeneroDetailDTO>>() {
		}.getType());
	}

    @GetMapping(value = "/{peliculaId}/generos")
	@ResponseStatus(code = HttpStatus.OK)
	public List<GeneroDetailDTO> getGeneros(@PathVariable("peliculaId") Long peliculaId) throws EntityNotFoundException {
		List<GeneroEntity> generoEntity = peliculaGeneroService.getGeneros(peliculaId);
		return modelMapper.map(generoEntity, new TypeToken<List<GeneroDetailDTO>>() {
		}.getType());
	}

    @DeleteMapping(value = "/{peliculaId}/generos/{generoId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeGenero(@PathVariable("generoId") Long generoId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException {
            peliculaGeneroService.removeGenero(peliculaId, generoId);
	}

}