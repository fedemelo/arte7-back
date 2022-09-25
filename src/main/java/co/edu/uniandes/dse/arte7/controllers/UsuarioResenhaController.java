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

import co.edu.uniandes.dse.arte7.dto.ResenhaDTO;
import co.edu.uniandes.dse.arte7.entities.ResenhaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.services.UsuarioResenhaService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioResenhaController {

	@Autowired
	private UsuarioResenhaService usuarioResenhaService;

	@Autowired
	private ModelMapper modelMapper;

	/**
	 * Guarda un libro dentro de una usuario con la informacion que recibe el la
	 * URL. Se devuelve el libro que se guarda en la usuario.
	 *
	 * @param usuarioId Identificador de la usuario que se esta actualizando.
	 *                    Este debe ser una cadena de dígitos.
	 * @param resenhaId      Identificador del libro que se desea guardar. Este debe
	 *                    ser una cadena de dígitos.
	 * @return JSON {@link ResenhaDTO} - El libro guardado en la usuario.
	 */
	@PostMapping(value = "/{usuarioId}/resenhas/{resenhaId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResenhaDTO addResenha(@PathVariable("usuarioId") Long usuarioId, @PathVariable("resenhaId") Long resenhalId) throws EntityNotFoundException {
		ResenhaEntity resenhaEntity = usuarioResenhaService.addResenha(resenhalId, usuarioId);
		return modelMapper.map(resenhaEntity, ResenhaDTO.class);
	}

	/**
	 * Busca y devuelve todos los libros que existen en la usuario.
	 *
	 * @param usuarioId Identificador de la usuario que se esta buscando. Este
	 *                    debe ser una cadena de dígitos.
	 * @return JSONArray {@link ResenhaDetailDTO} - Los libros encontrados en la
	 *         usuario. Si no hay ninguno retorna una lista vacía.
	 */
	@GetMapping(value = "/{usuarioId}/resenhas")
	@ResponseStatus(code = HttpStatus.OK)
	public List<ResenhaDTO> getResenhas(@PathVariable("usuarioId") Long usuarioId) throws EntityNotFoundException {
		List<ResenhaEntity> ResenhaList = usuarioResenhaService.getResenhas(usuarioId);
		return modelMapper.map(ResenhaList, new TypeToken<List<ResenhaDTO>>() {}.getType());
	}

	/**
	 * Busca el libro con el id asociado dentro de la usuario con id asociado.
	 *
	 * @param usuarioId Identificador de la usuario que se esta buscando. Este
	 *                    debe ser una cadena de dígitos.
	 * @param ResenhaId      Identificador del libro que se esta buscando. Este debe
	 *                    ser una cadena de dígitos.
	 * @return JSON {@link ResenhaDetailDTO} - El libro buscado
	 */
	@GetMapping(value = "/{usuarioId}/resenhas/{resenhaId}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResenhaDTO getResenha(@PathVariable("usuarioId") Long usuarioId, @PathVariable("resenhaId") Long resenhaId)
			throws EntityNotFoundException, IllegalOperationException {
		ResenhaEntity resenhaEntity = usuarioResenhaService.getResenha(usuarioId, resenhaId);
		return modelMapper.map(resenhaEntity, ResenhaDTO.class);
	}

	/**
	 * Remplaza las instancias de Resenha asociadas a una instancia de usuario
	 *
	 * @param usuarioId Identificador de la usuario que se esta remplazando.
	 *                    Este debe ser una cadena de dígitos.
	 * @param Resenhas       JSONArray {@link ResenhaDTO} El arreglo de libros nuevo para
	 *                    la usuario.
	 * @return JSON {@link ResenhaDetailDTO} - El arreglo de libros guardado en la
	 *         usuario.
	 */
	@PutMapping(value = "/{usuarioId}/resenhas")
	@ResponseStatus(code = HttpStatus.OK)
	public List<ResenhaDTO> replaceResenhas(@PathVariable("usuarioId") Long usuariosId,
			@RequestBody List<ResenhaDTO> resenhas) throws EntityNotFoundException {
        
		List<ResenhaEntity> resenhasList = modelMapper.map(resenhas, new TypeToken<List<ResenhaEntity>>() {
		}.getType());
		List<ResenhaEntity> result = usuarioResenhaService.replaceResenhas(usuariosId, resenhasList);
		return modelMapper.map(result, new TypeToken<List<ResenhaDTO>>() {
		}.getType());
	}
}
