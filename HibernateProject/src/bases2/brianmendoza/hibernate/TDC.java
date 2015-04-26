package bases2.brianmendoza.hibernate;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/* Tabla que almacena los datos
 * de las tarjetas de credito 
 * asociados a todas las personas
 * regitradas en la pagina.
 * */
@Entity
@Table(name="Tarjetas_De_Credito")
public class TDC {

	/* Numero de la tarjeta de credito */
	@Id
	@Column(name="Numero_De_Tarjeta", nullable=false, length=16)
	private String nroTarjeta;
	
	/* Nombre del tarjetahabiente */
	@Column(name="Tarjetahabiente", nullable=false)
	private String tarjetahabiente;
	
	/* Fecha de expiracion de la tarjeta */
	@Column(name="Fecha_De_Expiracion", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date fechaExpiracion;
	
	/* Relacion uno a muchos de una tarjeta
	 * de credito con las facturas de las 
	 * compras que han sido realizadas
	 * con ella.
	 * */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="tarjeta", orphanRemoval=true)
	private Set<Factura> facturas = new HashSet<Factura>();
	
	/* Relacion muchos a uno de tarjetas de
	 * credito asociados con una persona
	 * registrada en la pagina.
	 * */
	@ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name = "Dueno_Tarjeta", nullable=false)
	private Persona dueno_tarjeta;
	
	/* Generador de valores aleatorios.
	 * No es agregado a la tabla.
	 * */
	@Transient
	private Random random;
	
	/* Constructor de una tarjeta de credito
	 * aleatoria para sera asociada con una
	 * persona registrada.
	 * */
	public TDC() {
		super();
		this.random = new Random();
		this.nroTarjeta = generateNroTarjeta();
		this.tarjetahabiente = "tmp" + random.nextInt(10000);
		this.fechaExpiracion = generateFechaExp();
	}

	/* Generador de una fecha de expiracion aleatoria
	 * entre el 2016 y el 2025.
	 * */
	private Date generateFechaExp() {
		
		String[] rangoMonth = {"01", "02", "03", "04", "05", "06",
							   "07", "08", "09", "10", "11", "12"};
		String[] rangoYear = {"16", "17", "18", "19", "20", "21",
							  "22", "23", "24", "25"};
		
		int index = this.random.nextInt(rangoMonth.length);
		String mes = rangoMonth[index];
		index = this.random.nextInt(rangoYear.length);
		String year = rangoYear[index];
		String newFecha = mes + "/" + year;
		
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/yy");
		simpleDateFormat.setLenient(false);
		Date expiry = new Date();
		try {
			expiry = simpleDateFormat.parse(newFecha);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return expiry;
	}

	/* Generador de un numero de tarjeta
	 * de credito aleatorio.
	 * */
	private String generateNroTarjeta() {
		
		/*Posibles primeros seis digitos de la tarjeta, que determinan los demas numeros*/
		String[] binArray = {"513241", "533874", "533810", "535305", "513597", 
							 "448523", "492985", "471653", "448543", "455654"};
		int index = this.random.nextInt(binArray.length);
		String bin = binArray[index];
		/*Generar los demas numeros de una tarjeta con 16 digitos*/
		return 	randomNumeroTarjeta(bin,16);
	}
	
	 
	private String randomNumeroTarjeta(String bin, int length) {

		/* Numero de digitos aleatorios a determinar es el resultado
		 * de length - bin - 1, el 1 representando el ultimo digito
		 * de la tarjeta que es calculada mediante el algoritmo de Luhn.
		 * */
		int cantidadNumRandom = length - (bin.length() + 1);
		StringBuffer buffer = new StringBuffer(bin);
		for (int i = 0; i < cantidadNumRandom; i++) {
			int num = this.random.nextInt(10);
			buffer.append(num);
		}
		/*Ejecutar el algoritmon de Luhn para obtener el ultimo digito*/
		int ultNum = this.genUltimoNumero(buffer.toString());
		buffer.append(ultNum);
		return buffer.toString();
	}

	/* Algoritmo de Luhn para determinar
	 * el ultimo digito de la tarjeta de credito.
	 * */
	private int genUltimoNumero(String tarjetaIncompleta) {

		int suma = 0;
		int j = (tarjetaIncompleta.length() + 1) % 2;
		for (int i = 0; i < tarjetaIncompleta.length(); i++) {

			int num = Integer.parseInt(tarjetaIncompleta.substring(i, (i + 1)));
			if ((i % 2) == j) {
				num = num * 2;
				if (num > 9) {
					num = (num / 10) + (num % 10);
				}
			}
			suma += num;
		}

		/*El ultimo digito es el necesario para hacer la suma un multiplo de 10*/
		int mod = suma % 10;
		int ultDigito = ((mod == 0) ? 0 : 10 - mod);

		return ultDigito;
	} 
	
	public String getNroTarjeta() {
		return nroTarjeta;
	}
	protected void setNroTarjeta(String nroTarjeta) {
		this.nroTarjeta = nroTarjeta;
	}
	public String getTarjetahabiente() {
		return tarjetahabiente;
	}
	public void setTarjetahabiente(String tarjetahabiente) {
		this.tarjetahabiente = tarjetahabiente;
	}
	public Date getFechaExpiracion() {
		return fechaExpiracion;
	}
	public void setFechaExpiracion(Date fechaExpiracion) {
		this.fechaExpiracion = fechaExpiracion;
	}

	public Set<Factura> getFacturas() {
		return facturas;
	}

	public void setFacturas(Set<Factura> facturas) {
		this.facturas = facturas;
	}

	public Persona getDueno_tarjeta() {
		return dueno_tarjeta;
	}

	public void setDueno_tarjeta(Persona dueno_tarjeta) {
		this.dueno_tarjeta = dueno_tarjeta;
	}
}
