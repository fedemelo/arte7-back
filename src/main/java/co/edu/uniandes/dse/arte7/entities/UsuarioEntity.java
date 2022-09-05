package co.edu.uniandes.dse.arte7.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;
import uk.co.jemos.podam.common.PodamExclude;

/**
 * Entidad que representa un usuario en la persistencia 
 */

@Getter
@Setter
@Entity
public class UsuarioEntity extends BaseEntity {

    private String username;

    @PodamExclude
    @OneToMany(mappedBy = "critico", fetch = FetchType.LAZY)
    private List<ResenhaEntity> resenhas = new ArrayList<>();

}
