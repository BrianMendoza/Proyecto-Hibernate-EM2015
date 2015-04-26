package bases2.brianmendoza.hibernate;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.Table;
import javax.persistence.Transient;

/* Tabla que representa la informacion
 * de login de todos los usuarios registrados
 * en la pagina. Es superclase de Persona y Empresa.
 * */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(name="Usuarios")
public abstract class Usuario {

	/* Username del usuario */
	@Id
	@Column(nullable=false)
	private String username;
	
	/* Contrasena del usuario */
	@Column(nullable=false)
	private String password;
	
	/* Generador de valores aleatorios.
	 * No es almacenada en la tabla.
	 * */
	@Transient
	protected Random random;
	
	/* Generador de valores aleatorios.
	 * No es almacenada en la tabla.
	 * */
	@Transient
	private SecureRandom secRandom;

	/* Constructor para crear un nuevo usuario. */
	public Usuario() {
		super();
		this.random = new Random();
		this.secRandom = new SecureRandom();
		this.username = "tmp" + random.nextInt(20000);
		this.password = generatePassword();
	}

	/* Generador de una contrasena aleatoria. */
	private String generatePassword() {
		return new BigInteger(130, this.secRandom).toString(32);
	}
	
	public String getUsername() {
		return username;
	}

	protected void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
}
