package co.edu.uniandes.dse.arte7.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uniandes.dse.arte7.entities.ResenhaEntity;

@Repository
public interface ResenhaRepository extends JpaRepository<ResenhaEntity, Long> {

}

