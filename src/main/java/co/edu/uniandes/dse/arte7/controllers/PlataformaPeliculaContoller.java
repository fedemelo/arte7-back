package co.edu.uniandes.dse.arte7.controllers;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.services.PlataformaPeliculaService;

@RestController
@RequestMapping("/plataformas")
public class PlataformaPeliculaContoller {

    @Autowired
    private PlataformaPeliculaService plataformaPeliculaService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping(value = "/{plataformaId}/peliculas/{peliculaId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PeliculaDTO addPelicula(@PathVariable("peliculaId") Long plataformaId, @PathVariable("peliculaId") Long peliculalId) 
        throws co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException {
            PeliculaEntity peliculaEntity = plataformaPeliculaService.addPelicula(peliculalId, plataformaId);
            return modelMapper.map(peliculaEntity, PeliculaDTO.class);
	}
    
    @GetMapping(value = "/{plataformaId}/peliculas")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PeliculaDetailDTO> getPeliculas(@PathVariable("plataformaId") Long plataformaId) throws co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException {
		List<PeliculaEntity> peliculaList = plataformaPeliculaService.getPeliculas(plataformaId);
		return modelMapper.map(peliculaList, new TypeToken<List<PeliculaDetailDTO>>() {
		}.getType());
	}

	@GetMapping(value = "/{plataformaId}/peliculas/{peliculaId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PeliculaDetailDTO getPelicula(@PathVariable("plataformaId") Long plataformaId, @PathVariable("peliculaId") Long peliculaId)
			throws IllegalOperationException, co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException {
		PeliculaEntity peliculaEntity = plataformaPeliculaService.getPelicula(plataformaId, peliculaId);
		return modelMapper.map(peliculaEntity, PeliculaDetailDTO.class);
	}

	@PutMapping(value = "/{plataformaId}/peliculas")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PeliculaDetailDTO> replacePeliculas(@PathVariable("plataformaId") Long plataformasId,
			@RequestBody List<PeliculaDetailDTO> peliculas) throws co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException {
		List<PeliculaEntity> peliculasList = modelMapper.map(peliculas, new TypeToken<List<PeliculaEntity>>() {
		}.getType());
		List<PeliculaEntity> result = plataformaPeliculaService.replacePeliculas(plataformasId, peliculasList);
		return modelMapper.map(result, new TypeToken<List<PeliculaDetailDTO>>() {
		}.getType());
	}
}

