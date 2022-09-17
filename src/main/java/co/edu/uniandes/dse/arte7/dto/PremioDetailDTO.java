package co.edu.uniandes.dse.arte7.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PremioDetailDTO extends PremioDTO {
    
    private List<PeliculaDTO> pelicualas = new ArrayList<>();

}
