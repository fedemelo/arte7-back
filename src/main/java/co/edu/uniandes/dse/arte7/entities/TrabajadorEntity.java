package co.edu.uniandes.dse.arte7.entities;

import javax.persistence.MappedSuperclass;

import lombok.Getter;
import lombok.Setter;

import java.awt.image.BufferedImage;
import java.time.LocalDateTime;

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
    private LocalDateTime fechaNacimiento;
    private String biografia;
}
