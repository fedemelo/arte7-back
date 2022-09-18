package co.edu.uniandes.dse.arte7.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrabajadorDTO {
    private String nombre;
    private String fotografia;  //*Se puso String en este tipo para que por lo pornto las pruebas corran, toca preguntarle a José como seria poniendo una imange */
    private String nacionalidad;
    private Date fechaNacimiento;
    private String biografia;
}
