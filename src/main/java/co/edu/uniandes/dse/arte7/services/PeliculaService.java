package co.edu.uniandes.dse.arte7.services;

import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;

import org.modelmapper.spi.ErrorMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uniandes.dse.arte7.entities.ActorEntity;
import co.edu.uniandes.dse.arte7.entities.DirectorEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.ActorRepository;
import co.edu.uniandes.dse.arte7.repositories.GeneroRepository;
import co.edu.uniandes.dse.arte7.repositories.NominacionRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import co.edu.uniandes.dse.arte7.repositories.PlataformaRepository;
import co.edu.uniandes.dse.arte7.repositories.PremioRepository;
import co.edu.uniandes.dse.arte7.repositories.ResenhaRepository;
import co.edu.uniandes.dse.arte7.repositories.DirectorRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PeliculaService {

    @Autowired
    PeliculaRepository peliculaRepository;

    @Autowired
    ResenhaRepository resehnaRepository;

    @Autowired
    PlataformaRepository platadormaRepository;

    @Autowired
    GeneroRepository generoRepository;

    @Autowired
    NominacionRepository nominacionRepository;

    @Autowired
    PremioRepository premioRepository;

    @Autowired
    ActorRepository actorRepository;

    @Autowired
    DirectorRepository directorRepository;

    @Transactional
    public PeliculaEntity createPelicula(PeliculaEntity peliculaEntity) throws EntityNotFoundException, IllegalOperationException {

        log.info("Creando pelicual.");

        //validaciones de las entradas.
        //Se decido solo verificar estas cosas porque una pelicula puede que no tenga premios, nominacion, plataforma ni resenham.
        if (peliculaEntity.getActores() == null){
            throw new IllegalOperationException("Los actores no son validos.");
        }
        
        if (peliculaEntity.getGeneros() == null){
            throw new IllegalOperationException("El genero no es valido.");
        }

        if (peliculaEntity.getPoster() == null){
            throw new IllegalOperationException("El poster no es valido.");
        }

        if (peliculaEntity.getDirectores() == null){
            throw new IllegalOperationException("El director no es valido.");
        }


        //verficiacion actores y directores.
        //Esta decision implica que en momento de la carga se debera instanciar primero actores y directores.
        List<ActorEntity> actores = peliculaEntity.getActores(); 

        Iterator iteratorA = actores.iterator();
        

        while (iteratorA.hasNext()){
            ActorEntity actor = (ActorEntity) iteratorA.next();
            if (actorRepository.findById(actor.getId()) == null){
                throw new EntityNotFoundException("Actor no encontrado.");
            }
        }
        List<DirectorEntity> directores = peliculaEntity.getDirectores(); 

        Iterator iteratorD = directores.iterator();
        

        while (iteratorD.hasNext()){
            ActorEntity director = (ActorEntity) iteratorD.next();
            if (actorRepository.findById(director.getId()) == null){
                throw new EntityNotFoundException("Director no encontrado.");
            }
        }
        log.info("Se termino de cargar el libro.");
        return peliculaRepository.save(peliculaEntity);
    }

    public List<PeliculaEntity> getPeliculas(){
        log.info("Inicia proceso de consultar todos las peliculas.");
        return peliculaRepository.findAll();
    }

    public PeliculaEntity getPelicula(long peliculaID) throws EntityNotFoundException{
        log.info("Inicia el proceso de buscar una pelicula por id={0}", peliculaID);
    
        Optional <PeliculaEntity> peliculaE = peliculaRepository.findById(peliculaID);
        if (peliculaE.isEmpty()){
            throw new EntityNotFoundException("Pelicula no encontrada.");
        }
        log.info("Termino el proceso de consultar la pelicula de id={0}", peliculaID);
        return peliculaE.get();
    }

    public PeliculaEntity updateEntity(Long peliculaId, PeliculaEntity pelicula ) throws EntityNotFoundException, IllegalOperationException{

        log.info("Inicia proceso de actualizar el con id={0}", peliculaId);
        
    }

}