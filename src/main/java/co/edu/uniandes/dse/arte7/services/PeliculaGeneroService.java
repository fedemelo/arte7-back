package co.edu.uniandes.dse.arte7.services;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import co.edu.uniandes.dse.arte7.entities.NominacionEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.repositories.nominacionRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j

public class PeliculanominacionService {
    
    @Autowired
    PeliculaRepository peliculaRepository;
    
    @Autowired 
    nominacionRepository nominacionRepository;


    @Transactional
    public List<NominacionEntity> getnominacions(){
        log.info("Estamos consultando los géneros de la película con la id ={0} ... ");
        return peliculaRepository.findAll();

    }

}
