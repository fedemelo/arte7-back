package co.edu.uniandes.dse.arte7.dto;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TrabajadorDTO {
    private String nombre;
    private String fotografia; 
    private String nacionalidad;
    private Date fechaNacimiento;
    private String biografia;
}
