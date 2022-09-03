package co.edu.uniandes.dse.arte7.entities;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;

import lombok.Getter;
import lombok.Setter;
import uk.co.jemos.podam.common.PodamExclude;

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

    @PodamExclude
    @OneToMany(mappedBy = "pelicula")
	private List<ResenhaEntity> resenhas = new ArrayList<>();

    @PodamExclude
	@ManyToMany(mappedBy = "peliculas", fetch = FetchType.LAZY)
	private List<PlataformaEntity> plataformas = new ArrayList<>();

    @PodamExclude
	@ManyToMany(mappedBy = "peliculas", fetch = FetchType.LAZY)
	private List<GeneroEntity> generos = new ArrayList<>();

    @PodamExclude
	@ManyToMany(mappedBy = "peliculas", fetch = FetchType.LAZY)
	private List<DirectorEntity> directores = new ArrayList<>();

    @PodamExclude
	@ManyToMany(mappedBy = "peliculas", fetch = FetchType.LAZY)
	private List<ActorEntity> actores = new ArrayList<>();

    @PodamExclude
	@ManyToMany(mappedBy = "peliculas", fetch = FetchType.LAZY)
	private List<PremioEntity> premios = new ArrayList<>();

    @PodamExclude
	@ManyToMany(mappedBy = "peliculas", fetch = FetchType.LAZY)
	private List<NominacionEntity> nominaciones = new ArrayList<>();

    

}
