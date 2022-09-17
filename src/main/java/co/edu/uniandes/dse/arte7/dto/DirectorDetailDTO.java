package co.edu.uniandes.dse.arte7.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter

public class DirectorDetailDTO extends DirectorDTO {

    private List<PeliculaDTO> peliculas = new ArrayList<>();

}