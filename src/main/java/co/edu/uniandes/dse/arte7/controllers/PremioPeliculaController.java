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
import co.edu.uniandes.dse.arte7.services.PremioPeliculaService;


@RestController
@RequestMapping("/premios")


public class PremioPeliculaController {
    
    @Autowired
	private PremioPeliculaService premioPeliculaService;

	@Autowired
	private ModelMapper modelMapper;

    @PostMapping(value = "/{premioId}/peliculas/{peliculaId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PeliculaDetailDTO addPelicula(@PathVariable("peliculaId") Long peliculaId, @PathVariable("premioId") Long premioId)
			throws EntityNotFoundException {
            PeliculaEntity peliculaEntity = premioPeliculaService.addPelicula(premioId, peliculaId);
	return modelMapper.map(peliculaEntity, PeliculaDetailDTO.class);
	}

    @GetMapping(value = "{premioId}/peliculas/{peliculaId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PeliculaDetailDTO getPelicula(@PathVariable("peliculaId") Long peliculaId, @PathVariable("premioId") Long premioId)
			throws EntityNotFoundException, IllegalOperationException {
            PeliculaEntity peliculaEntity = premioPeliculaService.getPelicula(premioId, peliculaId);
		return modelMapper.map(peliculaEntity, PeliculaDetailDTO.class);
	}

    @PutMapping(value = "/{premioId}/peliculas")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PeliculaDetailDTO> addPeliculas(@PathVariable("premioId") Long premioId, @RequestBody List<PeliculaDTO> peliculas)
			throws EntityNotFoundException {
                List<PeliculaEntity> entities = modelMapper.map(peliculas, new TypeToken<List<PeliculaEntity>>() {
		}.getType());
		List<PeliculaEntity> peliculasList = premioPeliculaService.addPeliculas(premioId, entities);
		return modelMapper.map(peliculasList, new TypeToken<List<PeliculaDetailDTO>>() {
		}.getType());
	}

    @GetMapping(value = "/{premioId}/peliculas")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PeliculaDetailDTO> getPeliculas(@PathVariable("premioId") Long premioId) throws EntityNotFoundException {
		List<PeliculaEntity> peliculaEntity = premioPeliculaService.getPeliculas(premioId);
		return modelMapper.map(peliculaEntity, new TypeToken<List<PeliculaDetailDTO>>() {
		}.getType());
	}

    @DeleteMapping(value = "/{premioId}/peliculas/{peliculaId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removePelicula(@PathVariable("peliculaId") Long peliculaId, @PathVariable("premioId") Long premioId)
			throws EntityNotFoundException {
            premioPeliculaService.removePelicula(premioId, peliculaId);
	}

}
