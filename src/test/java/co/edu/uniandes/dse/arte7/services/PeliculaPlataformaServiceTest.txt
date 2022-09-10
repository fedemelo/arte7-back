package co.edu.uniandes.dse.arte7.services;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import co.edu.uniandes.dse.arte7.entities.ActorEntity;
import co.edu.uniandes.dse.arte7.entities.DirectorEntity;
import co.edu.uniandes.dse.arte7.entities.PeliculaEntity;
import co.edu.uniandes.dse.arte7.entities.PlataformaEntity;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Transactional
@Import(PeliculaService.class)
public class PeliculaPlataformaServiceTest {

    @Autowired
    private PeliculaPlataformaService peliculaplataformaService;

    @Autowired
	private PlataformaService plataformaService;

    @Autowired
	private PeliculaService peliculaService;

    @Autowired
	private TestEntityManager entityManager;

    private PodamFactory factory = new PodamFactoryImpl();

	private List<PeliculaEntity> peliculaList = new ArrayList<>();

	private List<DirectorEntity> directorList = new ArrayList<>();

	private List<ActorEntity> actorList = new ArrayList<>();

    private List<PlataformaEntity> plataformaList = new ArrayList<>();

	/**
	 * Configuración inicial de la prueba.
	 */
	@BeforeEach
	void setUp() {
		clearData();
		insertData();
	}

	/**
	 * Limpia las tablas que están implicadas en la prueba.
	 */
	private void clearData() {
        entityManager.getEntityManager().createQuery("delete from PlataformaEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from DirectorEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from PeliculaEntity").executeUpdate();
		entityManager.getEntityManager().createQuery("delete from ActorEntity").executeUpdate();
	}

	/**
	 * Inserta los datos iniciales para el correcto funcionamiento de las pruebas.
	 */
	private void insertData() {
    
		for (int i = 0; i < 3; i++) {
			ActorEntity actorEntity = factory.manufacturePojo(ActorEntity.class);
			entityManager.persist(actorEntity);
			
			actorList.add(actorEntity);
		}

		for (int i = 0; i < 3; i++) {
			DirectorEntity directorEntity = factory.manufacturePojo(DirectorEntity.class);
			entityManager.persist(directorEntity);
			directorList.add(directorEntity);
		}

		for (int i = 0; i < 3; i++) {
			PeliculaEntity peliculaEntity = factory.manufacturePojo(PeliculaEntity.class);
			entityManager.persist(peliculaEntity);
			peliculaList.add(peliculaEntity);
		}

        for (int i = 0; i < 3; i++) {
			PlataformaEntity plataformaEntity = factory.manufacturePojo(PlataformaEntity.class);
			entityManager.persist(plataformaEntity);
			plataformaList.add(plataformaEntity);
		}


		PeliculaEntity peliculaEntity = peliculaList.get(0);
		peliculaEntity.setActores(actorList);
        peliculaEntity.setDirectores(directorList);
        peliculaEntity.setPlataformas(plataformaList);
		entityManager.persist(peliculaEntity);

        PlataformaEntity plataformaEntity = plataformaList.get(0);
		plataformaEntity.setPeliculas(peliculaList);
        entityManager.persist(plataformaEntity);


	}




}
