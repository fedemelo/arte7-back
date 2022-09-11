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
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.GeneroRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Service
@Slf4j

public class GeneroPeliculaService {
    
    @Autowired 
    private GeneroRepository generoRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    /**Asociar una película a un género con sus ID */
    @Transactional
    public PeliculaEntity addPelicula(Long generoId, Long peliculaId) throws EntityNotFoundException{
        log.info("Se asociará una película al género con id={0}", generoId);
        Optional < GeneroEntity > generoEntity = generoRepository.findById(generoId);
        Optional < PeliculaEntity > peliculaEntity = peliculaRepository.findById(peliculaId);

        if (generoEntity.isEmpty())
            throw new EntityNotFoundException("Ningún género coincide con la ID digitada");

        if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException("Ninguna película coincide con la ID digitada");

        peliculaEntity.get().getGeneros().add(generoEntity.get());
        log.info("Se le ha asociado una película al género con id = {0}", generoId);
        return peliculaEntity.get();
    }

    /**Obtener las películas de un género */
    @Transactional
    public List < PeliculaEntity > getPeliculas(Long generoId) throws EntityNotFoundException {
        log.info("Se consultarán todos las películas del género con id = {0}", generoId);
        Optional < GeneroEntity > generoEntity = generoRepository.findById(generoId);
        if (generoEntity.isEmpty())
            throw new EntityNotFoundException("No hay ningún género con esta ID");

        List < PeliculaEntity > peliculas = peliculaRepository.findAll();
        List < PeliculaEntity > peliculaList = new ArrayList < > ();

        for (PeliculaEntity b: peliculas) {
            if (b.getGeneros().contains(generoEntity.get())) {
                peliculaList.add(b);
            }
        }
        log.info("Se han consultado todas las peliculas del genero con id = {0}", generoId);
        return peliculaList;
    }

    /** Obtención de una película de un género dados los IDs */
    @Transactional
    public PeliculaEntity getPelicula(Long generoId, Long peliculaId) throws EntityNotFoundException, IllegalOperationException {
        log.info("Inicia proceso de consultar la película con id = {0} del género con id = " + peliculaId, generoId);
        Optional < GeneroEntity > generoEntity = generoRepository.findById(generoId);
        Optional < PeliculaEntity > peliculaEntity = peliculaRepository.findById(peliculaId);

        if (generoEntity.isEmpty())
            throw new EntityNotFoundException("No hay ningún género con el ID digitado");

        if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException("No hay ninguna pelicula con el ID digitado");

        log.info("Se ha consultado una película con id = {0} del género con id = " + peliculaId, generoId);
        if (peliculaEntity.get().getGeneros().contains(generoEntity.get()))
            return peliculaEntity.get();

        throw new IllegalOperationException("La película no esta asociada a este género");
    }

    /** Actualización de películas de un género */
    @Transactional
    public List < PeliculaEntity > updatePeliculas(Long generoId, List < PeliculaEntity > peliculas) throws EntityNotFoundException {
        log.info("Se reemplazarán las películas asociados al género con id = {0}", generoId);
        Optional < GeneroEntity > generoEntity = generoRepository.findById(generoId);
        if (generoEntity.isEmpty())
            throw new EntityNotFoundException("No hay ningún género con el ID digitado");

        for (PeliculaEntity pelicula: peliculas) {
            Optional < PeliculaEntity > peliculaEntity = peliculaRepository.findById(pelicula.getId());
            if (peliculaEntity.isEmpty())
                throw new EntityNotFoundException("No hay ninguna película con el ID digitado");

            if (!peliculaEntity.get().getGeneros().contains(generoEntity.get()))
                peliculaEntity.get().getGeneros().add(generoEntity.get());
        }
        log.info("Finaliza proceso de reemplazar las películas asociadas al género con id = {0}", generoId);
        return peliculas;
    }

    /** Desasociar una película de un génro dadas las IDs */
    @Transactional
    public void removePelicula(Long generoId, Long peliculaId) throws EntityNotFoundException {
        log.info("Inicia proceso de borrar un libro del genero con id = {0}", generoId);
        Optional < GeneroEntity > generoEntity = generoRepository.findById(generoId);
        if (generoEntity.isEmpty())
            throw new EntityNotFoundException("No hay ningún género con el ID digitado");

        Optional < PeliculaEntity > peliculaEntity = peliculaRepository.findById(peliculaId);
        if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException("No hay ninguna película con el ID digitado");

        peliculaEntity.get().getGeneros().remove(generoEntity.get());
        log.info("Finaliza proceso de borrar una película del genero con id = {0}", generoId);
    }
}
 
    
