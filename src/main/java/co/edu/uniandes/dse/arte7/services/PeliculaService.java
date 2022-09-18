package co.edu.uniandes.dse.arte7.services;


import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.ActorEntity;
import co.edu.uniandes.dse.arte7.entities.DirectorEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.entities.PlataformaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.ActorRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import co.edu.uniandes.dse.arte7.repositories.PlataformaRepository;
import co.edu.uniandes.dse.arte7.repositories.DirectorRepository;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PeliculaService {

    @Autowired
    PeliculaRepository peliculaRepository;

    @Autowired
    ActorRepository actorRepository;

    @Autowired
    DirectorRepository directorRepository;

    @Autowired
    PlataformaRepository plataformaRepository;

    @Transactional
    public PeliculaEntity createPelicula(PeliculaEntity peliculaEntity) throws EntityNotFoundException, IllegalOperationException {

        log.info("Creando pelicula.");

        //validaciones de las entradas.
        //Se decido solo verificar estas cosas porque una pelicula puede que no tenga premios, nominacion, plataforma ni resenhas.
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

        log.info("Se termino de cargar la pelicula.");
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

    @Transactional
    public PeliculaEntity updatePelicula(Long peliculaId, PeliculaEntity pelicula) throws EntityNotFoundException {
        log.info("Inicia proceso de actualizar una pelicula con id = {0}", peliculaId);
        Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
        if (peliculaEntity.isEmpty())
                throw new EntityNotFoundException("Pelicula no encontrada.");

        pelicula.setId(peliculaId);
        log.info("Termina proceso de actualizar una pelicula con id = {0}", peliculaId);
        return peliculaRepository.save(pelicula);
    }

    public void deletePelicula(Long peliculaId)throws EntityNotFoundException, IllegalOperationException{
        log.info("Inicia proceso de borrado de la pelicula con id={0}", peliculaId);
        
        Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
        if (peliculaEntity.isEmpty()){
            throw new EntityNotFoundException("Pelicula no encontrada.");
        }

        List<ActorEntity> actores = peliculaEntity.get().getActores();

        if (!actores.isEmpty())
                throw new IllegalOperationException("No se borro la pelicula porque aun tiene actores asociados.");

        List<DirectorEntity> directores = peliculaEntity.get().getDirectores();

        if (!directores.isEmpty())
            throw new IllegalOperationException("No se borro la pelicula porque aun tiene directores asociados.");
        
        List<PlataformaEntity> plataformas = peliculaEntity.get().getPlataformas();

        if (!plataformas.isEmpty())
            throw new IllegalOperationException("No se borro la pelicula porque aun tiene plataformas asociadas.");
        
        log.info("Se borro la pelicula con id={0}", peliculaId);

        peliculaRepository.deleteById(peliculaId);
    }

}
