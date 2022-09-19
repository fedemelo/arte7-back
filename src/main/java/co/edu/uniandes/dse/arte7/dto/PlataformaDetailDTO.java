package co.edu.uniandes.dse.arte7.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class PlataformaDetailDTO extends PlataformaDTO{

    private List<PeliculaDTO> resenhaDTOs = new ArrayList<>();
    
}
