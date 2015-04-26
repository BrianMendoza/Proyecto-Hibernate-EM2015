package bases2.brianmendoza.hibernate;

/* Clase contenedora de resultados
 * de una consulta a la Base de Datos.
 * No representa ninguna tabla o columna.
 * */
public class EmpresaStats {
	
	/* Username de la empresa */
	private String username;
	
	/* Contador */
	private long count;

	public EmpresaStats(String username, long count) {
		super();
		this.username = username;
		this.count = count;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public long getCount() {
		return count;
	}

	public void setCount(long count) {
		this.count = count;
	}
	
}
