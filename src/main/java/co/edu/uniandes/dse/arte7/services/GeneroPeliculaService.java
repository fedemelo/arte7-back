package co.edu.uniandes.dse.arte7.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

public class GeneroPeliculaService {
    
    @Autowired 
    private GeneroRepository generoRepository;

    @Autowired
    private PeliculaRepository peliculaRepository;

    /**Asociar una película a un género con sus ID 
     * @throws EntityNotFoundException
     * */

    @Transactional
    public PeliculaEntity addPelicula(Long generoId, Long peliculaId) throws  EntityNotFoundException
    {
        log.info("Se asociará una película al género con id={0}", generoId);
        Optional < GeneroEntity > generoEntity = generoRepository.findById(generoId);
        Optional < PeliculaEntity > peliculaEntity = peliculaRepository.findById(peliculaId);

        if (generoEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);

        if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

        generoEntity.get().getPeliculas().add(peliculaEntity.get());
        log.info("Se le ha asociado una película al género con id = {0}", generoId);
        return peliculaEntity.get();
    }

    /**Obtener las películas de un género */
    @Transactional
    public List < PeliculaEntity > getPeliculas(Long generoId) throws EntityNotFoundException {
        log.info("Se consultarán todos las películas del género con id = {0}", generoId);
        Optional < GeneroEntity > generoEntity = generoRepository.findById(generoId);
        if (generoEntity.isEmpty()){
            throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);
            }
        List < PeliculaEntity > peliculas = peliculaRepository.findAll();
        List < PeliculaEntity > peliculaList = new ArrayList <> ();

        for (PeliculaEntity b: peliculas) {
            if (generoEntity.get().getPeliculas().contains(b)) {
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

        if (generoEntity.isEmpty()){
            throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);
        }
        if (peliculaEntity.isEmpty()){
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
        }
        log.info("Se ha consultado una película con id = {0} del género con id = " + peliculaId, generoId);
        if (generoEntity.get().getPeliculas().contains(peliculaEntity.get())){
            return peliculaEntity.get();
        }
        throw new IllegalOperationException("La película no esta asociada a este género");
    }

    @Transactional
	public List<PeliculaEntity> updatePeliculas(Long generoId, List<PeliculaEntity> peliculas) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las peliculas asociadas al actor con id = {0}", generoId);
        for (PeliculaEntity pelicula: peliculas) {
            addPelicula(generoId, pelicula.getId());
        }
		log.info("Finaliza proceso de reemplazar las peliculas asociadas al actor con id = {0}", generoId);
		return peliculas;
    }

    /** Desasociar una película de un génro dadas las IDs */
    @Transactional
    public void removePelicula(Long generoId, Long peliculaId) throws EntityNotFoundException {
        log.info("Inicia proceso de borrar un libro del genero con id = {0}", generoId);
        Optional < GeneroEntity > generoEntity = generoRepository.findById(generoId);
        if (generoEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.GENERO_NOT_FOUND);

        Optional < PeliculaEntity > peliculaEntity = peliculaRepository.findById(peliculaId);
        if (peliculaEntity.isEmpty())
            throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

        peliculaEntity.get().getGeneros().remove(generoEntity.get());
        log.info("Finaliza proceso de borrar una película del genero con id = {0}", generoId);
    }
}
 
    
