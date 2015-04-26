package bases2.brianmendoza.hibernate;

import java.util.Calendar;
import java.util.Date;
//import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
//import java.util.concurrent.TimeUnit;













import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
//import javax.persistence.OrderColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/* Tabla que almacena la informacion de
 * una promocion que ha sido ofertada en
 * la pagina.
 * */
@Entity
@Table(name="Promociones")
public class Promocion {

	/* ID de la promocion */
	@Id @GeneratedValue
	@Column(name="Id_Promocion", nullable=false)
	private int idPromo;
	
	/* Descripcion de la promocion */
	@Column(nullable=false)
	private String descripcion;
	
	/* Monto original del evento/servicio */
	@Column(name="Monto_Original", nullable=false)
	private int montoOriginal;
	
	/* Monto ofertado del evento/servicio */
	@Column(name="Monto_Ofertado", nullable=false)
	private int montoOfertado;
	
	/* Ubicacion geografica de la promocion */
	@Embedded
	private Coordenadas coordenadas;
	
	/* Vigencia de la promocion */
	@Embedded
	private Vigencia vigencia;
	
	/* Coleccion de fechas puntuales donde
	 * la promocion es valida.
	 * */
	@ElementCollection
	@CollectionTable(name="Fechas_Puntuales_Promocion", joinColumns=@JoinColumn(name="Promo_id"))
	@Column(name="fecha_puntual", nullable=false)
	@Temporal(TemporalType.DATE)
	private Set<Date> fechasPuntuales = new HashSet<Date>();
	
	/* Coleccion de urls de paginas asociadas
	 * a la promocion.
	 * */
	@ElementCollection
	@CollectionTable(name="Enlaces_Promociones", joinColumns=@JoinColumn(name="Promo_id"))
	@Column(name="url", nullable=false)
	private Set<String> enlaces = new HashSet<String>();
	
	/* Relacion muchos a uno de promociones con
	 * la empresa que los lanza.
	 * */
	@ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name = "Lanzado_Por", nullable=false)
	private Empresa empresa;
	
	/* Relacion uno a muchos de la promocion
	 * con los vales generados cuando la
	 * promocion es comprada via la pagina.
	 * */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="promocion", orphanRemoval=true)
	private Set<Vale> vales = new HashSet<Vale>();
	
	/* Relacion uno a muchos de la promocion
	 * con los distintos shares donde un usuario
	 * haya compartido la promocion.
	 * */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="promocionCompartida", orphanRemoval=true)
	private Set<Comparte> shares = new HashSet<Comparte>();
	
	/* Atributo que representa el status
	 * de la promocion. Puede ser DISPONIBLE,
	 * CANCELADO o EXPIRADO.
	 * */
	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private Status status;
	
	/* Generador de valores aleatorios.
	 * No es agregado a la tabla.
	 * */
	@Transient
	private Random random;

	/* Constructor de una promocion
	 * aleatoria para una empresa.
	 * */
	public Promocion() {
		super();
		this.random = new Random();
		this.descripcion = "Descripcion " + this.random.nextInt(10000);
		this.montoOriginal = 1 + this.random.nextInt(5000);
		this.montoOfertado = this.montoOriginal - this.random.nextInt(this.montoOriginal);
		this.coordenadas = new Coordenadas();
		this.vigencia = new Vigencia();
		this.status = Status.DISPONIBLE;
		/* Fechas puntuales son opcionales */
		if (this.random.nextBoolean())
			generateFechasPuntuales();
		/* Debe tener al menos un enlace */
		generateEnlaces(1 + this.random.nextInt(5));
	}
	
	/* Generador de un numero, especificado
	 * por su parametro, de urls aleatorios
	 * para la promocion.
	 * */
	private void generateEnlaces(int numEnlaces) {
		for (int i = 0; i < numEnlaces; i++)
			this.enlaces.add("www.pagina" + this.random.nextInt(10000) + ".com");
	}

	/* Generador de fechas puntuales aleatorias
	 * para la promocion, dentro del margen de
	 * su vigencia.
	 * */
	private void generateFechasPuntuales() {
		Calendar cal = Calendar.getInstance();
		cal.setTime(this.vigencia.getFechaInicio());
		int prob = 100;
		
		while (cal.getTime().before(this.vigencia.getFechaFin()) && prob > 0) {
		    cal.add(Calendar.DATE, 1);
		    /* Se agrega una nueva fecha en base a una
		     * probabilidad para que los datos sean mas
		     * "creibles". */
		    if (this.random.nextInt(101) < prob) {
		    	this.fechasPuntuales.add(cal.getTime());
		    	prob = prob - 5;
		    }
		}
	}

	public int getIdPromo() {
		return idPromo;
	}

	protected void setIdPromo(int idPromo) {
		this.idPromo = idPromo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public int getMontoOriginal() {
		return montoOriginal;
	}

	public void setMontoOriginal(int montoOriginal) {
		this.montoOriginal = montoOriginal;
	}

	public int getMontoOfertado() {
		return montoOfertado;
	}

	public void setMontoOfertado(int montoOfertado) {
		this.montoOfertado = montoOfertado;
	}

	public Coordenadas getCoordenadas() {
		return coordenadas;
	}

	public void setCoordenadas(Coordenadas coordenadas) {
		this.coordenadas = coordenadas;
	}

	public Vigencia getVigencia() {
		return vigencia;
	}

	public void setVigencia(Vigencia vigencia) {
		this.vigencia = vigencia;
	}

	public Set<Date> getFechasPuntuales() {
		return fechasPuntuales;
	}

	public void setFechasPuntuales(Set<Date> fechasPuntuales) {
		this.fechasPuntuales = fechasPuntuales;
	}

	public Empresa getEmpresa() {
		return empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}
	
	public Set<String> getEnlaces() {
		return enlaces;
	}

	public void setEnlaces(Set<String> enlaces) {
		this.enlaces = enlaces;
	}

	public Set<Vale> getVales() {
		return vales;
	}

	public void setVales(Set<Vale> vales) {
		this.vales = vales;
	}

	public Set<Comparte> getShares() {
		return shares;
	}

	public void setShares(Set<Comparte> shares) {
		this.shares = shares;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}
	
}
