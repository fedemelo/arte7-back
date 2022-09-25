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
import co.edu.uniandes.dse.arte7.services.NominacionPeliculaService;


@RestController
@RequestMapping("/nominaciones")


public class NominacionPeliculaController {
    
    @Autowired
	private NominacionPeliculaService nominacionPeliculaService;

	@Autowired
	private ModelMapper modelMapper;

    @PostMapping(value = "/{nominacionId}/peliculas/{peliculaId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PeliculaDetailDTO addPelicula(@PathVariable("peliculaId") Long peliculaId, @PathVariable("nominacionId") Long nominacionId)
			throws EntityNotFoundException {
            PeliculaEntity peliculaEntity = nominacionPeliculaService.addPelicula(nominacionId, peliculaId);
	return modelMapper.map(peliculaEntity, PeliculaDetailDTO.class);
	}

    @GetMapping(value = "{nominacionId}/peliculas/{peliculaId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PeliculaDetailDTO getPelicula(@PathVariable("peliculaId") Long peliculaId, @PathVariable("nominacionId") Long nominacionId)
			throws EntityNotFoundException, IllegalOperationException {
            PeliculaEntity peliculaEntity = nominacionPeliculaService.getPelicula(nominacionId, peliculaId);
		return modelMapper.map(peliculaEntity, PeliculaDetailDTO.class);
	}

    @PutMapping(value = "/{nominacionId}/peliculas")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PeliculaDetailDTO> addPeliculas(@PathVariable("nominacionId") Long nominacionId, @RequestBody List<PeliculaDTO> peliculas)
			throws EntityNotFoundException {
                List<PeliculaEntity> entities = modelMapper.map(peliculas, new TypeToken<List<PeliculaEntity>>() {
		}.getType());
		List<PeliculaEntity> peliculasList = nominacionPeliculaService.replacePeliculas(nominacionId, entities);
		return modelMapper.map(peliculasList, new TypeToken<List<PeliculaDetailDTO>>() {
		}.getType());
	}

    @GetMapping(value = "/{nominacionId}/peliculas")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PeliculaDetailDTO> getPeliculas(@PathVariable("nominacionId") Long nominacionId) throws EntityNotFoundException {
		List<PeliculaEntity> peliculaEntity = nominacionPeliculaService.getPeliculas(nominacionId);
		return modelMapper.map(peliculaEntity, new TypeToken<List<PeliculaDetailDTO>>() {
		}.getType());
	}

    @DeleteMapping(value = "/{nominacionId}/peliculas/{peliculaId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removePelicula(@PathVariable("peliculaId") Long peliculaId, @PathVariable("nominacionId") Long nominacionId)
			throws EntityNotFoundException {
            nominacionPeliculaService.removePelicula(nominacionId, peliculaId);
	}

}