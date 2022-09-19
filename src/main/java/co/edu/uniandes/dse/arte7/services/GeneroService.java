package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uniandes.dse.arte7.entities.GeneroEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.ErrorMessage;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.GeneroRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class GeneroService {

    @Autowired
    GeneroRepository generoRepository;

    /** Obtención de todos los géneros */
    @Transactional
    public List<GeneroEntity> getGeneros() {
        log.info("Estamos buscando todos los generos de las películas ... ");
        return generoRepository.findAll();
    }
    
    /** Creación de un nuevo género */
    @Transactional
    public GeneroEntity createGenero (GeneroEntity genero)  throws EntityNotFoundException{
        log.info("Se está creando un nuevo género... ");
        return generoRepository.save(genero);
    }

    /** Obtención de un género específico por ID */
    @Transactional
    public GeneroEntity getGenero(Long generoId) throws EntityNotFoundException{
        log.info("Estamos buscando el género con id = {0}", generoId);
        Optional < GeneroEntity > generoEntity = generoRepository.findById(generoId);
        if (generoEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);
        log.info("Finaliza la consulta del género con id = {0}", generoId);
        return generoEntity.get();
    }

    /** Actualización de un género por ID */
    @Transactional
    public GeneroEntity updateGenero(Long generoId, GeneroEntity genero) throws EntityNotFoundException {
        log.info("Se actualizará el género con la id = {0}", generoId);
        Optional < GeneroEntity > generoEntity = generoRepository.findById(generoId);
        if (generoEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);
        log.info("Finaliza la actualización del género id = {0}", generoId);
        genero.setId(generoId);
        return generoRepository.save(genero);
    }

    /** Eliminación de un género */
    @Transactional
    public void deleteGenero(Long generoId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de borrar el género con id = {0}", generoId);
        Optional < GeneroEntity > generoEntity = generoRepository.findById(generoId);
        if (generoEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);

        List<PeliculaEntity> peliculas = generoEntity.get().getPeliculas();
        if (!peliculas.isEmpty())
            throw new IllegalOperationException("No se puede eliminar este género dado que tiene alguna pelicula asociada a este");

        generoRepository.deleteById(generoId);
        log.info("Finaliza la eliminación del género con id = {0}", generoId);
    }
}

