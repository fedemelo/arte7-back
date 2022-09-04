package co.edu.uniandes.dse.arte7.entities;

import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToMany;

import lombok.Data;
import uk.co.jemos.podam.common.PodamExclude;


public class ResenhaEntity extends BaseEntity{
    private int estrellas;
    private int numCaracteres;
    private String texto;

    @PodamExclude
    @OneToMany(mappedBy = "critico", fetch = FetchType.EAGER)
    private usuarioEntity user;
    

}
