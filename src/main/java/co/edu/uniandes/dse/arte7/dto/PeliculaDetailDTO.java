package co.edu.uniandes.dse.arte7.dto;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PeliculaDetailDTO extends PeliculaDTO{
    
    private List<PlataformaDTO> plataformas = new ArrayList<>();
    private List<GeneroDTO> generos = new ArrayList<>();
    private List<NominacionDTO> nominaciones = new ArrayList<>();
    private List<PremioDTO> premios = new ArrayList<>();
    private List<ActorDTO> actores = new ArrayList<>();
    private List<DirectorDTO> directores = new ArrayList<>();
    private List<ResenhaDTO> resenhas = new ArrayList<>();

}
