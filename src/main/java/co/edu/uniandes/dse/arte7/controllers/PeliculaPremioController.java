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

import co.edu.uniandes.dse.arte7.dto.PremioDTO;
import co.edu.uniandes.dse.arte7.dto.PremioDetailDTO;
import co.edu.uniandes.dse.arte7.entities.PremioEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.services.PeliculaPremioService;


@RestController
@RequestMapping("/peliculas")


public class PeliculaPremioController {
    
    @Autowired
	private PeliculaPremioService peliculaPremioService;

	@Autowired
	private ModelMapper modelMapper;

    @PostMapping(value = "/{peliculaId}/premios/{premioId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PremioDetailDTO addPremio(@PathVariable("premioId") Long premioId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException {
            PremioEntity premioEntity = peliculaPremioService.addPremio(peliculaId, premioId);
	        return modelMapper.map(premioEntity, PremioDetailDTO.class);
	}

    @GetMapping(value = "{peliculaId}/premios/{premioId}")
	@ResponseStatus(code = HttpStatus.OK)
	public PremioDetailDTO getPremio(@PathVariable("premioId") Long premioId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException, IllegalOperationException {
                PremioEntity premioEntity = peliculaPremioService.getPremio(peliculaId, premioId);
		return modelMapper.map(premioEntity, PremioDetailDTO.class);
	}

    @PutMapping(value = "/{peliculaId}/premios")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PremioDetailDTO> addPremios(@PathVariable("peliculaId") Long peliculaId, @RequestBody List<PremioDTO> premios)
			throws EntityNotFoundException {
                List<PremioEntity> entities = modelMapper.map(premios, new TypeToken<List<PremioEntity>>() {
		}.getType());
		List<PremioEntity> premiosList = peliculaPremioService.replacePremios(peliculaId, entities);
		return modelMapper.map(premiosList, new TypeToken<List<PremioDetailDTO>>() {
		}.getType());
	}

    @GetMapping(value = "/{peliculaId}/premios")
	@ResponseStatus(code = HttpStatus.OK)
	public List<PremioDetailDTO> getPremios(@PathVariable("peliculaId") Long peliculaId) throws EntityNotFoundException {
		List<PremioEntity> premioEntity = peliculaPremioService.getPremios(peliculaId);
		return modelMapper.map(premioEntity, new TypeToken<List<PremioDetailDTO>>() {
		}.getType());
	}

    @DeleteMapping(value = "/{peliculaId}/premios/{premioId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removePremio(@PathVariable("premioId") Long premioId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException {
            peliculaPremioService.removePremio(peliculaId, premioId);
	}

}