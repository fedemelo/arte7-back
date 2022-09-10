package co.edu.uniandes.dse.arte7.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uniandes.dse.arte7.entities.ActorEntity;

@Repository
public interface ActorRepository extends JpaRepository<ActorEntity, Long> {
    List<ActorEntity> findByNombre(String name);
}
