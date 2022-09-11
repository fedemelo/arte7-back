package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.entities.PlataformaEntity;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.PlataformaRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PlataformaService {
    
    @Autowired
    PlataformaRepository plataformaRepository;


    @Transactional
    public PlataformaEntity createPlataforma(PlataformaEntity plataformaEntity) throws IllegalOperationException{

        log.info("Creando plataforma.");
        
        if(plataformaEntity.getNombre() == null){
            throw new IllegalOperationException("El nombre no es valido.");
        }

        if(plataformaEntity.getUrl() == null){
            throw new IllegalOperationException("La Url no es valida.");
        }

        log.info("Se termino de cargar el libro.");
        return plataformaRepository.save(plataformaEntity);
    }

    @Transactional
    public List<PlataformaEntity> getPlataformas(){
        log.info("Inicia proceso de consultar todos las plataformas.");
        return plataformaRepository.findAll();
    }

    @Transactional
    public PlataformaEntity getPlataforma(long plataformaID) throws EntityNotFoundException{
        log.info("Inicia el proceso de buscar una plataforma por id={0}", plataformaID);
    
        Optional <PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataformaID);
        if (plataformaEntity.isEmpty()){
            throw new EntityNotFoundException("Plataforma no encontrada.");
        }
        log.info("Termino el proceso de consultar la plataforma de id={0}", plataformaID);
        return plataformaEntity.get();
    }

    @Transactional
    public PlataformaEntity updatePlataforma(Long plataformaId, PlataformaEntity plataforma) throws EntityNotFoundException {
        log.info("Inicia proceso de actualizar una plataforma con id = {0}", plataformaId);
        Optional<PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataformaId);
        if (plataformaEntity.isEmpty())
                throw new EntityNotFoundException("Plataforma no encontrada.");

        plataforma.setId(plataformaId);
        log.info("Termina proceso de actualizar una plataforma con id = {0}", plataformaId);
        return plataformaRepository.save(plataforma);
    }

    @Transactional
    public void deletePlataforma(Long plataformaId)throws EntityNotFoundException, IllegalOperationException{
        log.info("Inicia proceso de borrado de la plataforma con id={0}", plataformaId);
        
        Optional<PlataformaEntity> plataformaEntity = plataformaRepository.findById(plataformaId);
        if (plataformaEntity.isEmpty()){
            throw new EntityNotFoundException("plataforma no encontrada.");
        }

        List<PeliculaEntity> peliculas = plataformaEntity.get().getPeliculas();

        if (!peliculas.isEmpty())
            throw new IllegalOperationException("No se borro la plataforma porque aun tiene peliculas asociadas.");

        log.info("Se borro la plataforma con id={0}", plataformaId);
    }
    
}
