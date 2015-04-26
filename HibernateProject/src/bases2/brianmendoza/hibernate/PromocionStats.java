package bases2.brianmendoza.hibernate;

/* Clase contenedora de resultados
 * de una consulta a la Base de Datos.
 * No representa ninguna tabla o columna.
 * */
public class PromocionStats {
	
	/* id de la promocion */
	private int idPromo;
	
	/* Contador */
	private long count;

	public PromocionStats(int idPromo, long count) {
		super();
		this.idPromo = idPromo;
		this.count = count;
	}

	public int getIdPromo() {
		return idPromo;
	}

	public void setIdPromo(int idPromo) {
		this.idPromo = idPromo;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
	
}
