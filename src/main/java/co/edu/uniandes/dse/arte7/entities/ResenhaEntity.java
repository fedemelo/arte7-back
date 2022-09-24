package co.edu.uniandes.dse.arte7.entities;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.co.jemos.podam.common.PodamExclude;

@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
public class ResenhaEntity extends BaseEntity{
    private Integer estrellas;
    private Integer numCaracteres;
    private String texto;

    @PodamExclude
    @ManyToOne
    private UsuarioEntity critico;

    @PodamExclude
    @ManyToOne
    private PeliculaEntity pelicula;

}
