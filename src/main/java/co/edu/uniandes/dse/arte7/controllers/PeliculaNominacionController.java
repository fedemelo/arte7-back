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
import co.edu.uniandes.dse.arte7.services.PeliculaNominacionService;


@RestController
@RequestMapping("/peliculas")


public class PeliculaNominacionController {
    
    @Autowired
	private PeliculaNominacionService peliculaNominacionService;

	@Autowired
	private ModelMapper modelMapper;

    @PostMapping(value = "/{peliculaId}/nominaciones/{nominacionId}")
	@ResponseStatus(code = HttpStatus.OK)
	public NominacionDetailDTO addNominacion(@PathVariable("nominacionId") Long nominacionId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException {
            NominacionEntity nominacionEntity = peliculaNominacionService.addNominacion(peliculaId, nominacionId);
	        return modelMapper.map(nominacionEntity, NominacionDetailDTO.class);
	}

    @GetMapping(value = "{peliculaId}/nominaciones/{nominacionId}")
	@ResponseStatus(code = HttpStatus.OK)
	public NominacionDetailDTO getNominacion(@PathVariable("nominacionId") Long nominacionId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException, IllegalOperationException {
                NominacionEntity nominacionEntity = peliculaNominacionService.getNominacion(peliculaId, nominacionId);
		return modelMapper.map(nominacionEntity, NominacionDetailDTO.class);
	}

    @PutMapping(value = "/{peliculaId}/nominaciones")
	@ResponseStatus(code = HttpStatus.OK)
	public List<NominacionDetailDTO> addNominaciones(@PathVariable("peliculaId") Long peliculaId, @RequestBody List<NominacionDTO> nominaciones)
			throws EntityNotFoundException {
                List<NominacionEntity> entities = modelMapper.map(nominaciones, new TypeToken<List<NominacionEntity>>() {
		}.getType());
		List<NominacionEntity> nominacionesList = peliculaNominacionService.replaceNominaciones(peliculaId, entities);
		return modelMapper.map(nominacionesList, new TypeToken<List<NominacionDetailDTO>>() {
		}.getType());
	}

    @GetMapping(value = "/{peliculaId}/nominaciones")
	@ResponseStatus(code = HttpStatus.OK)
	public List<NominacionDetailDTO> getNominaciones(@PathVariable("peliculaId") Long peliculaId) throws EntityNotFoundException {
		List<NominacionEntity> nominacionEntity = peliculaNominacionService.getNominaciones(peliculaId);
		return modelMapper.map(nominacionEntity, new TypeToken<List<NominacionDetailDTO>>() {
		}.getType());
	}

    @DeleteMapping(value = "/{peliculaId}/nominaciones/{nominacionId}")
	@ResponseStatus(code = HttpStatus.NO_CONTENT)
	public void removeNominacion(@PathVariable("nominacionId") Long nominacionId, @PathVariable("peliculaId") Long peliculaId)
			throws EntityNotFoundException {
            peliculaNominacionService.removeNominacion(peliculaId, nominacionId);
	}

}