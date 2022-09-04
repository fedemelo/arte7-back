package co.edu.uniandes.dse.arte7.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uniandes.dse.arte7.entities.PremioEntity;

@Repository
public interface PremioRepository  extends JpaRepository<PremioEntity, Long> {
    
}
