package co.edu.uniandes.dse.arte7.entities;

import javax.persistence.MappedSuperclass;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import co.edu.uniandes.dse.arte7.podam.DateStrategy;
import lombok.Getter;
import lombok.Setter;
import uk.co.jemos.podam.common.PodamStrategyValue;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * Clase abstracta que representa un trabajador que participó en la creación
 * de una película en la persistencia
 *
 * @author Federico Melo Barrero
 */

@Getter
@Setter
@MappedSuperclass
public abstract class TrabajadorEntity extends BaseEntity {

    private String nombre;
    private BufferedImage fotografia;
    private String nacionalidad;

    @Temporal(TemporalType.DATE)
    @PodamStrategyValue(DateStrategy.class)
    private Date fechaNacimiento;

    private String biografia;
}
