package bases2.brianmendoza.hibernate;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/* Tabla que almacena todos los vales
 * generados por la compra de una promocion
 * por una persona registrada en la pagina.
 * */
@Entity
@Table(name="Vales")
public class Vale {
	
	/* Clave compuesta de la clase Vale. */
	@SuppressWarnings("serial")
	@Embeddable
	public static class ValeId implements Serializable {
		
		/* Username de la persona que compra */
		private String username;
		
		/* ID de la promocion comprada */
		private int idPromo;
		
		/* ID de la factura generada por la compra */
		private int idFactura;

		public ValeId() {}
		
		public ValeId(String username, int idPromo, int idFactura) {
			super();
			this.username = username;
			this.idPromo = idPromo;
			this.idFactura = idFactura;
		}

		public boolean equals(Object obj) {
			if (obj != null && obj instanceof ValeId) {
				ValeId that = (ValeId) obj;
				return this.username.equals(that.username) &&
						(this.idPromo == that.idPromo) &&
						(this.idFactura == that.idFactura);
			} else {
				return false;
			}
		}

		public int hashCode() {
			return username.hashCode() + idPromo + idFactura;
		}

		public String getUsername() {
			return username;
		}

		protected void setUsername(String username) {
			this.username = username;
		}

		public int getIdPromo() {
			return idPromo;
		}

		protected void setIdPromo(int idPromo) {
			this.idPromo = idPromo;
		}

		public int getIdFactura() {
			return idFactura;
		}

		protected void setIdFactura(int idFactura) {
			this.idFactura = idFactura;
		}
	}
	
	/* ID del vale */
	@EmbeddedId
	private ValeId idVale = new ValeId();
	
	/* Columna que indica si es un vale de regalo o no */
	@Column(name="Vale_De_Regalo", nullable=false)
	private boolean esRegalo;
	
	/* Columna que indica a que email es enviado el vale */
	@Column(name="email_Destinatario", nullable=false)
	private String emailDestinatario;
	
	/* Columna que indica que dia es enviado el vale, si es de regalo */
	@Column(name="Fecha_Envio_Regalo", nullable=true)
	@Temporal(TemporalType.DATE)
	private Date fechaEnvio;
	
	@Column(nullable=false)
	private boolean utilizado;
	
	/* Relacion muchos a uno de los vales
	 * con la persona que haya realizado
	 * la compra.
	 * */
	@MapsId("username")
	@ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name = "Persona_Que_Compra", nullable=false, insertable=false, updatable=false)
	private Persona persona;
	
	/* Relacion muchos a uno de los vales
	 * con la promocion que haya sido comprada.
	 * */
	@MapsId("idPromo")
	@ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name = "Promocion_Comprada", nullable=false, insertable=false, updatable=false)
	private Promocion promocion;
	
	/* Relacion uno a uno del vale
	 * con la factura generada por 
	 * la compra.
	 * */
	@MapsId("idFactura")
	@OneToOne(cascade = CascadeType.ALL, orphanRemoval=true, fetch=FetchType.LAZY)
	@JoinColumn(name = "Codigo_Factura", nullable=false, insertable=false, updatable=false)
	private Factura factura;

	public Vale() {}

	/* Constructor de un vale dado una
	 * persona, una promocion, una factura
	 * y los valores que tomaran sus atributos.
	 * */
	public Vale(boolean esRegalo, String emailDestinatario,
			Date fechaEnvio, boolean utilizado, Persona persona, 
			Promocion promocion, Factura factura) {
		super();
		//Set fields
		this.esRegalo = esRegalo;
		this.emailDestinatario = emailDestinatario;
		this.fechaEnvio = fechaEnvio;
		this.utilizado = utilizado;
		this.persona = persona;
		this.promocion = promocion;
		this.factura = factura;
		
		// Set identifier values
		this.idVale.username = persona.getUsername();
		this.idVale.idPromo = promocion.getIdPromo();
		this.idVale.idFactura = factura.getIdFactura();
		
		//Referential integrity
		factura.setVale(this);
		persona.getVales().add(this);
		promocion.getVales().add(this);
	}

	public ValeId getIdVale() {
		return idVale;
	}

	protected void setIdVale(ValeId idVale) {
		this.idVale = idVale;
	}

	public boolean isEsRegalo() {
		return esRegalo;
	}

	public void setEsRegalo(boolean esRegalo) {
		this.esRegalo = esRegalo;
	}

	public String getEmailDestinatario() {
		return emailDestinatario;
	}

	public void setEmailDestinatario(String emailDestinatario) {
		this.emailDestinatario = emailDestinatario;
	}

	public Date getFechaEnvio() {
		return fechaEnvio;
	}

	public void setFechaEnvio(Date fechaEnvio) {
		this.fechaEnvio = fechaEnvio;
	}

	public Persona getPersona() {
		return persona;
	}

	public void setPersona(Persona persona) {
		this.persona = persona;
	}

	public Promocion getPromocion() {
		return promocion;
	}

	public void setPromocion(Promocion promocion) {
		this.promocion = promocion;
	}

	public Factura getFactura() {
		return factura;
	}

	public void setFactura(Factura factura) {
		this.factura = factura;
	}
}
