package co.edu.uniandes.dse.arte7.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.co.jemos.podam.common.PodamExclude;
/**
 * Clase que representa un director en la persistencia
 *
 * @author Federico Melo Barrero
 */

@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
public class DirectorEntity extends TrabajadorEntity {

    @PodamExclude
    @ManyToMany
    private List<PeliculaEntity> peliculas = new ArrayList<>();
}
