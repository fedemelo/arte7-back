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
import co.edu.uniandes.dse.arte7.services.PremioService;


@RestController
@RequestMapping("/premios")

public class PremioController {
   
    @Autowired
    private PremioService premioService;

    @Autowired
    private ModelMapper modelMapper;

    @GetMapping
    @ResponseStatus(code = HttpStatus.OK)
    public List<PremioDetailDTO> findAll() {
        List<PremioEntity> premios =premioService.getPremios();
        return modelMapper.map(premios, new TypeToken<List<PremioDetailDTO>>() {}.getType());
    }

    @GetMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public PremioDetailDTO findOne(@PathVariable("id") Long id) throws EntityNotFoundException {
        PremioEntity PremioEntity = premioService.getPremio(id);
        return modelMapper.map(PremioEntity, PremioDetailDTO.class);
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public PremioDTO create(@RequestBody PremioDTO premioDTO) throws IllegalOperationException {
        PremioEntity premioEntity = premioService.createPremio(modelMapper.map(premioDTO, PremioEntity.class));
        return modelMapper.map(premioEntity, PremioDTO.class);
     }

    @PutMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.OK)
    public PremioDTO update(@PathVariable("id") Long id, @RequestBody PremioDTO PremioDTO) throws EntityNotFoundException, IllegalOperationException {
        PremioEntity PremioEntity = premioService.updatePremio(id, modelMapper.map(PremioDTO, PremioEntity.class));
        return modelMapper.map(PremioEntity, PremioDTO.class);
    }

  

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public void delete(@PathVariable("id") Long id) throws EntityNotFoundException, IllegalOperationException {
        premioService.deletePremio(id);
    }

}
