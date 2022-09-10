package co.edu.uniandes.dse.arte7.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.repositories.GeneroRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class GeneroPeliculaService {
    
    @Autowired 
    GeneroRepository generoRepository;

    @Autowired
    PeliculaRepository peliculaRepository;


    @Transactional
    public List<PeliculaEntity> getPeliculas(){
        log.info("Estamos consultando todas las películas con el género = {0} ... ");
        return peliculaList;
        

        }    
    }
