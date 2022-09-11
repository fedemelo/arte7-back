package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uniandes.dse.arte7.entities.NominacionEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.NominacionRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class NominacionService {

    @Autowired
    NominacionRepository nominacionRepository;

    /** Obtención de todos los géneros */
    @Transactional
    public List<NominacionEntity> getNominaciones() {
        log.info("Estamos buscando todas los nominaciones de las películas ... ");
        return nominacionRepository.findAll();
    }
    
    /** Creación de un nuevo género */
    @Transactional
    public NominacionEntity createNominacion (NominacionEntity nominacion){
        log.info("Se está creando una nueva nominación... ");
        return nominacionRepository.save(nominacion);
    }

    /** Obtención de un género específico por ID */
    @Transactional
    public NominacionEntity getNominacion(Long nominacionId) throws EntityNotFoundException{
        log.info("Estamos buscando la nominación con id = {0}", nominacionId);
        Optional < NominacionEntity > NominacionEntity = nominacionRepository.findById(nominacionId);
        if (NominacionEntity.isEmpty())
            throw new EntityNotFoundException("No se ha encontrado esta nominación");
        log.info("Finaliza la consulta de la nominación con id = {0}", nominacionId);
        return NominacionEntity.get();
    }

    /** Actualización de un género por ID */
    @Transactional
    public NominacionEntity updateNominacion(Long nominacionId, NominacionEntity nominacion) throws EntityNotFoundException {
        log.info("Se actualizará la nominación con la id = {0}", nominacionId);
        Optional < NominacionEntity > NominacionEntity = nominacionRepository.findById(nominacionId);
        if (NominacionEntity.isEmpty())
            throw new EntityNotFoundException("No se ha encontrado este género");
        log.info("Finaliza la actualización de la nominación id = {0}", nominacionId);
        nominacion.setId(nominacionId);
        return nominacionRepository.save(nominacion);
    }

    /** Eliminación de un género */
    @Transactional
    public void deleteNominacion(Long nominacionId) throws IllegalOperationException, EntityNotFoundException {
        log.info("Inicia proceso de borrar la nominación con id = {0}", nominacionId);
        Optional < NominacionEntity > NominacionEntity = nominacionRepository.findById(nominacionId);
        if (NominacionEntity.isEmpty())
            throw new EntityNotFoundException("No existe una nominación con esta Id");

        List<PeliculaEntity> peliculas = NominacionEntity.get().getPeliculas();
        if (!peliculas.isEmpty())
            throw new IllegalOperationException("No se puede eliminar esta nominación dado que tiene alguna pelicula asociada a este");

        nominacionRepository.deleteById(nominacionId);
        log.info("Finaliza la eliminación de la nominación con id = {0}", nominacionId);
    }
}
