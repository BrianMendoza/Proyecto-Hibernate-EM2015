package bases2.brianmendoza.hibernate;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.Transient;

/* Columnas embeddable en otras tablas
 * que representan una ubicacion geografica
 * mediante coordenadas de latitud y longitud.
 * */
@Embeddable
public class Coordenadas {

	/* Latitud de la ubicacion geografica. */
	@Column(nullable=false)
	private double latitud;
	
	/* Latitud de la ubicacion geografica. */
	@Column(nullable=false)
	private double longitud;
	
	/* Minimo predeterminado de la latitud.
	 * No es agregada a la tabla.
	 * */
	@Transient
	private double minLat = -90.00;
	
	/* Maximo predeterminado de la latitud.
	 * No es agregada a la tabla.
	 * */
	@Transient
	private double maxLat = 90.00;
	
	/* Minimo predeterminado de la longitud.
	 * No es agregada a la tabla.
	 * */
	@Transient
	private double minLon = 0.00;
	
	/* Maximo predeterminado de la longitud.
	 * No es agregada a la tabla.
	 * */
	@Transient
	private double maxLon = 180.00;
	
	/* Constructor de una latitud y longitud aleatoria */
	public Coordenadas() {
		this.latitud = minLat + (double)(Math.random() * ((maxLat - minLat) + 1));
		this.longitud = minLon + (double)(Math.random() * ((maxLon - minLon) + 1));
	}

	public double getLatitud() {
		return latitud;
	}

	public void setLatitud(double latitud) {
		this.latitud = latitud;
	}

	public double getLongitud() {
		return longitud;
	}

	public void setLongitud(double longitud) {
		this.longitud = longitud;
	}

	
}
