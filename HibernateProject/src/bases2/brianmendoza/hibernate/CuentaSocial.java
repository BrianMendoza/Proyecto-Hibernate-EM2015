package bases2.brianmendoza.hibernate;

import java.io.Serializable;
import java.util.Random;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/* Tabla que almacena las cuentas sociales
 * asociadas a los usuarios registrados en
 * la pagina.
 * */
@SuppressWarnings("serial")
@Entity
@Table(name="Cuentas_Redes_Sociales")
public class CuentaSocial implements Serializable {

	/* Nombre de usuario en la red social.
	 * Parte de clave compuesta.
	 * */
	@Id
	@Column(name="Cuenta_Red_Social", nullable=false)
	private String userSocial;
	
	/* Nombre de la red social.
	 * Parte de clave compuesta.
	 * */
	@Id
	@Column(name="Red_Social", nullable=false)
	private String redSocial;
	
	/* Relacion muchos a uno con la persona
	 * que es duena de la cuenta social.
	 * */
	@ManyToOne(cascade = CascadeType.ALL)
	@JoinColumn(name = "Dueno_Cuenta", nullable=false, referencedColumnName="username")
	private Persona dueno;
	
	/* Generador de valores aleatorios.
	 * No es agregada a la tabla.
	 * */
	@Transient
	private Random random;
	
	public CuentaSocial() {
		this.random = new Random();
		this.userSocial = "tmp" + random.nextInt(10000);
		this.redSocial = "redTmp" + random.nextInt(10000);
	}

	/* Metodo que obtiene un username basado
	 * en los nombres y apellidos de la persona.
	 * */
	private String toUsername(String names, String apellidos) {
		String nameTrim = names.replaceAll("\\s", "");
		String apellidoTrim = apellidos.replaceAll("\\s", "");
		return removeLastChar(nameTrim) + removeLastChar(apellidoTrim) + random.nextInt(500);
	}
	
	private String removeLastChar(String s) {
	    if (s == null || s.length() == 0) {
	        return s;
	    }
	    return s.substring(0, s.length()-1);
	}

	public String getUserSocial() {
		return userSocial;
	}

	protected void setUserSocial(String names, String apellidos) {
		this.userSocial = toUsername(names,apellidos);
	}

	public String getRedSocial() {
		return redSocial;
	}

	protected void setRedSocial(String redSocial) {
		this.redSocial = redSocial;
	}

	public Persona getDueno() {
		return dueno;
	}

	protected void setDueno(Persona dueno) {
		this.dueno = dueno;
	}

	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((dueno == null) ? 0 : dueno.getUsername().hashCode());
		result = prime * result
				+ ((redSocial == null) ? 0 : redSocial.hashCode());
		result = prime * result
				+ ((userSocial == null) ? 0 : userSocial.hashCode());
		return result;
	}

	public boolean equals(Object obj) {
		if (obj != null && obj instanceof CuentaSocial) {
			CuentaSocial that = (CuentaSocial) obj;
			return this.redSocial.equals(that.redSocial) &&
					this.userSocial.equals(that.userSocial);
		} else {
			return false;
		}
	}
}
