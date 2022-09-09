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
 * Clase que representa un premio (ganado) en la persistencia
 *
 * @author Federico Melo Barrero
 */

@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
public class PremioEntity extends ReconocimientoEntity {

    @PodamExclude
    @ManyToMany
    private List<PeliculaEntity> peliculas = new ArrayList<>();
}
