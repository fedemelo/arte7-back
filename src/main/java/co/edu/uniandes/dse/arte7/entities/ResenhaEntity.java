package co.edu.uniandes.dse.arte7.entities;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;
import uk.co.jemos.podam.common.PodamExclude;

@Getter
@Setter
@Entity
public class ResenhaEntity extends BaseEntity{
    private int estrellas;
    private int numCaracteres;
    private String texto;

    @PodamExclude
    @OneToMany(mappedBy = "critico", fetch = FetchType.EAGER)
    private UsuarioEntity user;
    

}
