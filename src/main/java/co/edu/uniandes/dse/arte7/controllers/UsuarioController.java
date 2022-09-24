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

import co.edu.uniandes.dse.arte7.dto.UsuarioDTO;
import co.edu.uniandes.dse.arte7.dto.UsuarioDetailDTO;
import co.edu.uniandes.dse.arte7.entities.UsuarioEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.services.UsuarioService;




@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private ModelMapper modelMapper;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public UsuarioDTO create(@RequestBody UsuarioDTO usuarioDTO) throws IllegalOperationException, EntityNotFoundException {
        UsuarioEntity usuarioEntity = usuarioService.createUsuario(modelMapper.map(usuarioDTO, UsuarioEntity.class));
        return modelMapper.map(usuarioEntity, UsuarioDTO.class);
     }

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<UsuarioDetailDTO> findAll() {
        List<UsuarioEntity> usuarios = usuarioService.getUsuarios();
        return modelMapper.map(usuarios, new TypeToken<List<UsuarioDetailDTO>>() {}.getType());
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public UsuarioDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        UsuarioEntity UsuarioEntity = usuarioService.getUsuario(id);
        return modelMapper.map(UsuarioEntity, UsuarioDetailDTO.class);
    }

    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public UsuarioDTO update(@PathVariable("id") Long id, @RequestBody UsuarioDTO UsuarioDTO) throws EntityNotFoundException, IllegalOperationException {
        UsuarioEntity UsuarioEntity = usuarioService.updateUsuario(id, modelMapper.map(UsuarioDTO, UsuarioEntity.class));
        return modelMapper.map(UsuarioEntity, UsuarioDTO.class);
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException, IllegalOperationException {
        usuarioService.deleteUsuario(id);
    }
}