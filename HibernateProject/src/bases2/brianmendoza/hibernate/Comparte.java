package bases2.brianmendoza.hibernate;

import java.io.Serializable;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.Table;

/* Tabla que almacena las promociones
 * compartidas por un usuario de la 
 * pagina con otros usuarios de la
 * pagina.
 * */
@Entity
@Table(name="Promociones_Compartidas")
public class Comparte {

	/* Clave compuesta de la clase Comparte.
	 * Necesario que los tres elementos formen
	 * parte de la clave dado que si alguno
	 * no lo es se imponen restricciones
	 * indeseadas a la accion de compartir.
	 * */
	@SuppressWarnings("serial")
	@Embeddable
	public static class ComparteID implements Serializable{
		
		/* Id de la persona que
		 * comparte la promocion
		 * */
		private String userEnvia;
		
		/* Id de la persona que
		 * recibe la accion de
		 * compartir.
		 * */
		private String userRecibe;
		
		/* Id de la promocion 
		 * compartida.
		 * */
		private int idPromo;

		public ComparteID() {}

		public ComparteID(String userEnvia, String userRecibe, int idPromo) {
			super();
			this.userEnvia = userEnvia;
			this.userRecibe = userRecibe;
			this.idPromo = idPromo;
		}

		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + idPromo;
			result = prime * result
					+ ((userEnvia == null) ? 0 : userEnvia.hashCode());
			result = prime * result
					+ ((userRecibe == null) ? 0 : userRecibe.hashCode());
			return result;
		}

		public boolean equals(Object obj) {
			if (obj != null && obj instanceof ComparteID) {
				ComparteID that = (ComparteID) obj;
				return this.userEnvia.equals(that.userEnvia) &&
						(this.idPromo == that.idPromo) &&
						this.userRecibe.equals(that.userRecibe);
			} else {
				return false;
			}
		}

		public String getUserEnvia() {
			return userEnvia;
		}

		public void setUserEnvia(String userEnvia) {
			this.userEnvia = userEnvia;
		}

		public String getUserRecibe() {
			return userRecibe;
		}

		public void setUserRecibe(String userRecibe) {
			this.userRecibe = userRecibe;
		}

		public int getIdPromo() {
			return idPromo;
		}

		public void setIdPromo(int idPromo) {
			this.idPromo = idPromo;
		}
		
	}
	
	/* ID compuesto */
	@EmbeddedId
	private ComparteID comparteID = new ComparteID();
	
	/* Relacion muchos a uno con la
	 * persona que envia el share.
	 * */
	@MapsId("userEnvia")
	@ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name = "Persona_Que_Envia", nullable=false, insertable=false, updatable=false)
	private Persona personaEnvia;
	
	/* Relacion muchos a uno con la
	 * persona que recibe el share.
	 * */
	@MapsId("userRecibe")
	@ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name = "Persona_Que_Recibe", nullable=false, insertable=false, updatable=false)
	private Persona personaRecibe;
	
	/* Relacion muchos a uno con la
	 * promocion que es compartida.
	 * */
	@MapsId("idPromo")
	@ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name = "Promocion_Compartida", nullable=false, insertable=false, updatable=false)
	private Promocion promocionCompartida;
	
	/* Booleano que indica si el share
	 * resulto en una compra o no.
	 * */
	@Column(name="Fue_Comprado", nullable = false)
	private boolean fueComprado;

	public Comparte() {}

	public Comparte(Persona personaEnvia, Persona personaRecibe, Promocion promocionCompartida) {
		super();
		//Set fields
		this.personaEnvia = personaEnvia;
		this.personaRecibe = personaRecibe;
		this.promocionCompartida = promocionCompartida;
		this.fueComprado = false;
		
		// Set identifier values
		this.comparteID.userEnvia = personaEnvia.getUsername();
		this.comparteID.userRecibe = personaRecibe.getUsername();
		this.comparteID.idPromo = promocionCompartida.getIdPromo();
		
		//Referential integrity
		personaEnvia.getSharesEnviados().add(this);
		personaRecibe.getSharesRecibidos().add(this);
		promocionCompartida.getShares().add(this);
	}

	public ComparteID getComparteID() {
		return comparteID;
	}

	protected void setComparteID(ComparteID comparteID) {
		this.comparteID = comparteID;
	}

	public Persona getPersonaEnvia() {
		return personaEnvia;
	}

	public void setPersonaEnvia(Persona personaEnvia) {
		this.personaEnvia = personaEnvia;
	}

	public Persona getPersonaRecibe() {
		return personaRecibe;
	}

	public void setPersonaRecibe(Persona personaRecibe) {
		this.personaRecibe = personaRecibe;
	}

	public Promocion getPromocionCompartida() {
		return promocionCompartida;
	}

	public void setPromocionCompartida(Promocion promocionCompartida) {
		this.promocionCompartida = promocionCompartida;
	}

	public boolean isFueComprado() {
		return fueComprado;
	}

	public void setFueComprado(boolean fueComprado) {
		this.fueComprado = fueComprado;
	}
	
}
