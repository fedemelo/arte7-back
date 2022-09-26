package co.edu.uniandes.dse.arte7.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import co.edu.uniandes.dse.arte7.dto.ResenhaDTO;
import co.edu.uniandes.dse.arte7.entities.ResenhaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.services.ResenhaUsuarioService;


@RestController
@RequestMapping("/resenhas")
public class ResenhaUsuarioController {

	@Autowired
	private ResenhaUsuarioService resehnaUsuarioService;

	@Autowired
	private ModelMapper modelMapper;

	
	@PutMapping(value = "/{resenhaId}/usuarios/{usuarioId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResenhaDTO replaceUsuario(@PathVariable("resenhaId") Long resenhaId, @PathVariable("usuarioId") Long usuarioId) throws EntityNotFoundException {
		ResenhaEntity resenhaEntity = resehnaUsuarioService.replaceUsuario(resenhaId, usuarioId);
		return modelMapper.map(resenhaEntity, ResenhaDTO.class);
	}
}