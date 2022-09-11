<<<<<<< HEAD
package co.edu.uniandes.dse.arte7.entities;

import javax.persistence.MappedSuperclass;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


/**
 * Clase abstracta que representa un reconocimiento en general, ya sea un
 * permio ganado o una nominación.
 *
 * @author Federico Melo Barrero
 */

@Getter
@Setter
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class ReconocimientoEntity extends BaseEntity {

    private String nombre;
    private Integer anho;
    private String categoria;
}
=======
package co.edu.uniandes.dse.arte7.entities;

import javax.persistence.MappedSuperclass;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;


/**
 * Clase abstracta que representa un reconocimiento en general, ya sea un
 * permio ganado o una nominación.
 *
 * @author Federico Melo Barrero
 */

@Getter
@Setter
@MappedSuperclass
@EqualsAndHashCode(callSuper = true)
public abstract class ReconocimientoEntity extends BaseEntity {

    private String nombre;
    private Integer anho;
    private String categoria;
}
>>>>>>> origin/papigoti
