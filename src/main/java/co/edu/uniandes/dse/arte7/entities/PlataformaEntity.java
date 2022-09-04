package co.edu.uniandes.dse.arte7.entities;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.FetchType;


import lombok.Getter;
import lombok.Setter;


import uk.co.jemos.podam.common.PodamExclude;

@Getter
@Setter
@Entity
public class PlataformaEntity extends BaseEntity{

    private String nombre;
    private String url;

    @PodamExclude
    @ManyToMany
    private List<PeliculaEntity> peliculas = new ArrayList<>();
 
}   