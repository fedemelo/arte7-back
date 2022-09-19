package co.edu.uniandes.dse.arte7.exceptions;

public final class ErrorMessage {
    public static final String PELICULA_NOT_FOUND = "No se encuentra la pelicula con el id provisto.";
	public static final String ACTOR_NOT_FOUND = "No se encuentra el actor con el id provisto.";
	public static final String DIRECTOR_NOT_FOUND = "No se encuentra el director con el id provisto.";
	public static final String GENERO_NOT_FOUND = "No se ha encontrado este género";
	public static final String NOMINACION_NOT_FOUND = "No se ha encontrado esta nominación";
	public static final String PREMIO_NOT_FOUND = "No se encontró el premio buscado";
    public static final String RESENHA_NOT_FOUND = "No se encontro la resenha.";
    public static final String USUARIO_NOT_FOUND = "No se encuentra el usuario con el id provisto.";
    public static final String PLATAFORMA_NOT_FOUND = "No se encuentra la plataforma con el id provisto.";

	private ErrorMessage() {
		throw new IllegalStateException("Utility class");
	}
    
}
