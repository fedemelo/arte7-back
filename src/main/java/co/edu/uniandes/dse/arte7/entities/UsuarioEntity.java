package co.edu.uniandes.dse.arte7.entities;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import uk.co.jemos.podam.common.PodamExclude;

@Getter
@Setter
@Entity
@EqualsAndHashCode(callSuper = true)
public class UsuarioEntity extends BaseEntity {

    @PodamExclude
    @OneToMany
    private ResenhaEntity resenha;

}
