package co.edu.uniandes.dse.arte7.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uniandes.dse.arte7.entities.GeneroEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.ErrorMessage;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.GeneroRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j

public class PeliculaGeneroService {
    
    @Autowired
    private PeliculaRepository peliculaRepository;
    
    @Autowired 
    private GeneroRepository generoRepository;


    /**Asociar un género a una película con sus ID */
    @Transactional
    public GeneroEntity addGenero(Long generoId, Long peliculaId) throws EntityNotFoundException{
        log.info("Se asociará un género a la película con id={0}", peliculaId);
        Optional < GeneroEntity > generoEntity = generoRepository.findById(generoId);
        Optional < PeliculaEntity > peliculaEntity = peliculaRepository.findById(peliculaId);

        if (generoEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);

        if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

            if (!generoEntity.get().getPeliculas().contains(peliculaEntity.get())){
                 generoEntity.get().getPeliculas().add(peliculaEntity.get());
                }
        log.info("Se le ha asociado un género a la película con id = {0}", peliculaId);
        return generoEntity.get();
    }

     /** Actualización de películas de un género */
     @Transactional
     public List < GeneroEntity > updateGeneros(Long peliculaId, List < GeneroEntity > generos) throws EntityNotFoundException {
         log.info("Se reemplazarán los géneros asociados con la película con id = {0}", peliculaId);
         for (GeneroEntity genero: generos) {
                addGenero(genero.getId(),peliculaId);
         }
         log.info("Finaliza proceso de reemplazar los géneros asociadas a la película con id = {0}", peliculaId);
         return generos;
     }

    /**Obtener los géneros de una película */
    @Transactional
    public List < GeneroEntity > getGeneros(Long peliculaId) throws EntityNotFoundException {
        log.info("Se consultarán todos los géneros de la película con id = {0}", peliculaId);
        Optional < PeliculaEntity > peliculaEntity = peliculaRepository.findById(peliculaId);
        if (peliculaEntity.isEmpty()){
            throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);
            }

        List < GeneroEntity > generos = generoRepository.findAll();
        List < GeneroEntity > generoList = new ArrayList < > ();

        for (GeneroEntity b: generos) {
            if (b.getPeliculas().contains(peliculaEntity.get())) {
                generoList.add(b);
            }
        }
        log.info("Se han consultado todos los géneros de la película con id = {0}", peliculaId);
        return generoList;
    }

    /** Obtención de un género de una pekícula dados los IDs */
    @Transactional
    public GeneroEntity getGenero(Long generoId, Long peliculaId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de consultar un género con id = {0} de la película con id = " + generoId, peliculaId);
        Optional < GeneroEntity > generoEntity = generoRepository.findById(generoId);
        Optional < PeliculaEntity > peliculaEntity = peliculaRepository.findById(peliculaId);

        if (generoEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);

        if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

        log.info("Se ha consultado del género con id = {0} una película con id = " + generoId, peliculaId);
        if (peliculaEntity.get().getGeneros().contains(generoEntity.get()))
            return generoEntity.get();

        throw new IllegalOperationException("Este género no esta asociado a la película");
    }

   

    /** Desasociar una película de un génro dadas las IDs */
    @Transactional
    public void removeGenero(Long generoId, Long peliculaId) throws EntityNotFoundException {
        log.info("Inicia proceso de borrar un género de la película con id = {0}", peliculaId);
        Optional < GeneroEntity > generoEntity = generoRepository.findById(generoId);
        if (generoEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);

        Optional< PeliculaEntity > peliculaEntity = peliculaRepository.findById(peliculaId);
        if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

        generoEntity.get().getPeliculas().remove(peliculaEntity.get());
        log.info("Finaliza proceso de borrar un género de la película con id = {0}", peliculaId);
    }

}
