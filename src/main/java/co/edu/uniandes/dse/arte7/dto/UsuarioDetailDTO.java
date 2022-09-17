package co.edu.uniandes.dse.arte7.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UsuarioDetailDTO extends UsuarioDTO {
    
    private List<PeliculaDTO> pelicualas = new ArrayList<>();

}