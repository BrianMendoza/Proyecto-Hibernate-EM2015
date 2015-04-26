package bases2.brianmendoza.hibernate;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

/* Columnas embeddable en otras tablas
 * que representan una vigencia en el
 * tiempo dada por una fecha inicio
 * y una fecha final.
 * */
@Embeddable
public class Vigencia {

	/* Fecha inicio */
	@Column(name="Fecha_Inicio_Promocion", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date fechaInicio;
	
	/* Fecha fin */
	@Column(name="Fecha_Fin_Promocion", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date fechaFin;
	
	/* Generador de valores aleatorios.
	 * No es agregada a la tabla.
	 * */
	@Transient
	private Random random;

	/* Constructor de una vigencia aleatoria
	 * que puede tener un rango de 1 a 3 meses.
	 * */
	public Vigencia() {
		super();
		this.random = new Random();
		int year = 2015 + this.random.nextInt(4);
		int month = 1 + this.random.nextInt(12);
		GregorianCalendar gc = new GregorianCalendar(year, month, 1);
		@SuppressWarnings("static-access")
		int day = 1 + gc.getActualMaximum(gc.DAY_OF_MONTH);
		gc.set(year, month, day);
		this.fechaInicio = gc.getTime();
		/* El calendario avanza entre 1 y 3 meses */
		gc.add(Calendar.MONTH, 1 + this.random.nextInt(3));
		this.fechaFin = gc.getTime();
	}

	public Vigencia(Date fechaInicio, Date fechaFin) {
		this.fechaInicio = fechaInicio;
		this.fechaFin = fechaFin;
	}

	public Date getFechaInicio() {
		return fechaInicio;
	}

	public void setFechaInicio(Date fechaInicio) {
		this.fechaInicio = fechaInicio;
	}

	public Date getFechaFin() {
		return fechaFin;
	}

	public void setFechaFin(Date fechaFin) {
		this.fechaFin = fechaFin;
	}
}
