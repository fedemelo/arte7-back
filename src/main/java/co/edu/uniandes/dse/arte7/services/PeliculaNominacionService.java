package co.edu.uniandes.dse.arte7.services;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import co.edu.uniandes.dse.arte7.entities.NominacionEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.exceptions.EntityNotFoundException;
import co.edu.uniandes.dse.arte7.exceptions.ErrorMessage;
import co.edu.uniandes.dse.arte7.exceptions.IllegalOperationException;
import co.edu.uniandes.dse.arte7.repositories.NominacionRepository;
import co.edu.uniandes.dse.arte7.repositories.PeliculaRepository;
import lombok.extern.slf4j.Slf4j;

   
																		 
  
								
   

@Slf4j
@Service
public class PeliculaNominacionService {

    @Autowired
	private PeliculaRepository peliculaRepository;

	@Autowired
	private NominacionRepository nominacionRepository;

	   
												  
   
																 
																   
																	 
	
	@Transactional
	public NominacionEntity addNominacion(Long peliculaId, Long nominacionId) throws EntityNotFoundException {
		log.info("Inicia proceso de asociarle una nominacion a la pelicula con id = {0}", peliculaId);
		
								 
																		

		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

			

		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);
		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.NOMINACION_NOT_FOUND);

		if(!nominacionEntity.get().getPeliculas().contains(peliculaEntity.get())){
			nominacionEntity.get().getPeliculas().add(peliculaEntity.get());
		}
		
   
 
		log.info("Termina proceso de asociarle una nominacion a la pelicula con id = {0}", peliculaId);
		
		return nominacionEntity.get();
	}

	
																					  
			   
   
															   
																					
					
	
	@Transactional
	public List<NominacionEntity> getNominaciones(Long peliculaId) throws EntityNotFoundException {
		log.info("Inicia proceso de consultar todos las nominaciones de la pelicula con id = {0}", peliculaId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);

			List<NominacionEntity> nominaciones = nominacionRepository.findAll();
		List<NominacionEntity> nominacionList = new ArrayList<>();

		for (NominacionEntity p : nominaciones) {
			if (p.getPeliculas().contains(peliculaEntity.get())) {
				nominacionList.add(p);
			}
		}
		log.info("Termina proceso de consultar todos las nominaciones de la pelicula con id = {0}", peliculaId);
		return nominacionList;
	}

	
																				  
   
																 
																   
													   
	
	@Transactional
	public NominacionEntity getNominacion(Long peliculaId, Long nominacionId)
			throws EntityNotFoundException, IllegalOperationException {
		log.info("Inicia proceso de consultar una nominacion de la pelicula con id = {0}", peliculaId);
		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.NOMINACION_NOT_FOUND);

		if (peliculaEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
		
		if (nominacionEntity.get().getPeliculas().contains(peliculaEntity.get()))
			return nominacionEntity.get();
log.info("Termina proceso de consultar una nominacion de la pelicula con id = {0}", peliculaId);
		throw new IllegalOperationException("La nominacion no se encuentra asociada a la pelicula.");
	}

	@Transactional
	
																			   
   
															   
																					 
							  
																					
	
	public List<NominacionEntity> replaceNominaciones(Long peliculaId, List<NominacionEntity> nominaciones) throws EntityNotFoundException {
		log.info("Inicia proceso de reemplazar las peliculas asociadas al actor con id = {0}", peliculaId);

		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);
		
		if (peliculaEntity.isEmpty()){
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);}
			
		for (NominacionEntity premi: nominaciones) {
  

											
		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(premi.getId());
		
		if (nominacionEntity.isEmpty())
			throw new EntityNotFoundException(ErrorMessage.NOMINACION_NOT_FOUND);

		
		if(!nominacionEntity.get().getPeliculas().contains(peliculaEntity.get())){
			nominacionEntity.get().getPeliculas().add(peliculaEntity.get());
		}
			}
		log.info("Finaliza proceso de reemplazar las peliculas asociadas al actor con id = {0}", peliculaId);

		return getNominaciones(peliculaId);
    }



	@Transactional
	
																
   
																 
																   
	
   
 
	
   
	 
	   
 
	public void removeNominacion(Long peliculaId, Long nominacionId) throws EntityNotFoundException {
		log.info("Inicia proceso de borrar una nominacion de la pelicula con id = {0}", peliculaId);
		Optional<NominacionEntity> nominacionEntity = nominacionRepository.findById(nominacionId);
		Optional<PeliculaEntity> peliculaEntity = peliculaRepository.findById(peliculaId);

		if (nominacionEntity.isEmpty()){
			throw new EntityNotFoundException(ErrorMessage.NOMINACION_NOT_FOUND);
	}
		if (peliculaEntity.isEmpty()){
			throw new EntityNotFoundException(ErrorMessage.PELICULA_NOT_FOUND);
		}
		
        
		nominacionEntity.get().getPeliculas().remove(peliculaEntity.get());
		
		log.info("Termina proceso de borrar una nominacion de la pelicula con id = {0}", peliculaId);
	}
}
