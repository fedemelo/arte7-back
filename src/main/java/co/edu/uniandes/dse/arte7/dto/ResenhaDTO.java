package co.edu.uniandes.dse.arte7.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class ResenhaDTO {
    
    private Long id;
    private Integer estrellas;
    private Integer numCaracteres;
    private String texto;

    private UsuarioDTO critico;
    private PeliculaDTO pelicula;
    
}
