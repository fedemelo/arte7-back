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
import co.edu.uniandes.dse.arte7.services.PlataformaPeliculaService;


@RestController
@RequestMapping("/plataformas")
public class PlataformaPeliculaController {

	@Autowired
	private PlataformaPeliculaService plataformaPeliculaService;

	@Autowired
	private ModelMapper modelMapper;

	//Asocia un plataforma existente a un a película
	@PostMapping(value = "/{plataformaId}/peliculas/{peliculaId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PeliculaDetailDTO addPelicula(@PathVariable("peliculaId") Long peliculaId, @PathVariable("plataformaId") Long plataformaId)
			throws EntityNotFoundException {
		PeliculaEntity peliculaEntity = plataformaPeliculaService.addPelicula(plataformaId, peliculaId);
		return modelMapper.map(peliculaEntity, PeliculaDetailDTO.class);
	}

	// Obtiene el generp de una pelicula con ID especifica
	@GetMapping(value = "/{plataformaId}/peliculas/{peliculaId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PeliculaDetailDTO getPelicula(@PathVariable("peliculaId") Long peliculaId, @PathVariable("plataformaId") Long plataformaId)
			throws EntityNotFoundException, IllegalOperationException {
		PeliculaEntity peliculaEntity = plataformaPeliculaService.getPelicula(plataformaId, peliculaId);
		return modelMapper.map(peliculaEntity, PeliculaDetailDTO.class);
	}

	// Actualiza el género de una pelicula
	@PutMapping(value = "/{plataformaId}/peliculas")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PeliculaDetailDTO> addPeliculas(@PathVariable("plataformaId") Long plataformaId, @RequestBody List<PeliculaDTO> peliculas)
			throws EntityNotFoundException {
		List<PeliculaEntity> entities = modelMapper.map(peliculas, new TypeToken<List<PeliculaEntity>>() {
		}.getType());
		List<PeliculaEntity> peliculasList = plataformaPeliculaService.replacePeliculas(plataformaId, entities);
		return modelMapper.map(peliculasList, new TypeToken<List<PeliculaDetailDTO>>() {
		}.getType());
	}

	// Obtiene todas las peliculas de un plataforma
	@GetMapping(value = "/{plataformaId}/peliculas")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PeliculaDetailDTO> getPeliculas(@PathVariable("plataformaId") Long plataformaId) throws EntityNotFoundException {
		List<PeliculaEntity> peliculaEntity = plataformaPeliculaService.getPeliculas(plataformaId);
		return modelMapper.map(peliculaEntity, new TypeToken<List<PeliculaDetailDTO>>() {
		}.getType());
	}

	//*Elimina la conexion entre un plataforma y una pelicula
	@DeleteMapping(value = "/{plataformaId}/peliculas/{peliculaId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removePelicula(@PathVariable("peliculaId") Long peliculaId, @PathVariable("plataformaId") Long plataformaId)
			throws EntityNotFoundException {
		plataformaPeliculaService.removePelicula(plataformaId, peliculaId);
	}
}