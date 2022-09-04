package co.edu.uniandes.dse.arte7.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import co.edu.uniandes.dse.arte7.entities.ResenhaEntity;

@Repository
public class ResenhaRepository extends JpaRepository<ResenhaEntity, Long> {

}

