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
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.services.GeneroPeliculaService;

public class GeneroPeliculaController {

    @RestController
    @RequestMapping("/generos")
    public class GeneroPeliculaContoller {

        @Autowired
        private GeneroPeliculaService generoPeliculaService;

        @Autowired
        private ModelMapper modelMapper;

        @PostMapping(value = "/{generoId}/peliculas/{peliculaId}")
        @ResponseStatus(code = HttpStatus.OK)
        public PeliculaDTO addPelicula(@PathVariable("peliculaId") Long generoId, @PathVariable("peliculaId") Long peliculalId) 
            throws EntityNotFoundException, IllegalOperationException {
                PeliculaEntity peliculaEntity = generoPeliculaService.addPelicula(peliculalId, generoId);
                return modelMapper.map(peliculaEntity, PeliculaDTO.class);
        }
        
        @GetMapping(value = "/{generoId}/peliculas")
        @ResponseStatus(code = HttpStatus.OK)
        public List<PeliculaDetailDTO> getPeliculas(@PathVariable("generoId") Long generoId) throws EntityNotFoundException {
            List<PeliculaEntity> peliculaList = generoPeliculaService.getPeliculas(generoId);
            return modelMapper.map(peliculaList, new TypeToken<List<PeliculaDetailDTO>>() {
            }.getType());
        }

        @GetMapping(value = "/{generoId}/peliculas/{peliculaId}")
        @ResponseStatus(code = HttpStatus.OK)
        public PeliculaDetailDTO getPelicula(@PathVariable("generoId") Long generoId, @PathVariable("peliculaId") Long peliculaId)
                throws IllegalOperationException, EntityNotFoundException {
            PeliculaEntity peliculaEntity = generoPeliculaService.getPelicula(generoId, peliculaId);
            return modelMapper.map(peliculaEntity, PeliculaDetailDTO.class);
        }

        @PutMapping(value = "/{generoId}/peliculas")
        @ResponseStatus(code = HttpStatus.OK)
        public List<PeliculaDetailDTO> replacePeliculas(@PathVariable("generoId") Long generosId,
                @RequestBody List<PeliculaDetailDTO> peliculas) throws EntityNotFoundException {
            List<PeliculaEntity> peliculasList = modelMapper.map(peliculas, new TypeToken<List<PeliculaEntity>>() {
            }.getType());
            List<PeliculaEntity> result = generoPeliculaService.updatePeliculas(generosId, peliculasList);
            return modelMapper.map(result, new TypeToken<List<PeliculaDetailDTO>>() {
            }.getType());
        }
    }

}
