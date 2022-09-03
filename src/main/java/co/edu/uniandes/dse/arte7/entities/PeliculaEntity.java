package co.edu.uniandes.dse.arte7.entities;

import java.util.Date;

import javax.persistence.Entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class PeliculaEntity extends BaseEntity{

    private String nombre; 
    private String poster;
    private Integer duracionSec;
    private String pais;
    private Date fechaEstreno;
    private String url;
    private Integer visitas;
    private Double estrellasPromedio;

    
    

}
