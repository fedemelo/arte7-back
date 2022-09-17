package co.edu.uniandes.dse.arte7.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PeliculaDTO {
    
    private String nombre;
    private String poster;
    private Integer duracionSec;
    private String pais;
    private Date fechaEstreno;
    private String urlTrailer;
    private Integer vistas;
    private Double estrellasPromedio;

}
