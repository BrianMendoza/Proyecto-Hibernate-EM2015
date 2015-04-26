package bases2.brianmendoza.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.PrimaryKeyJoinColumn;
import javax.persistence.Table;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/* Tabla que almacena los datos
 * de las distintas empresas registradas
 * en la pagina de promociones. Subclase
 * de la clase Usuario.
 * */
@Entity
@PrimaryKeyJoinColumn(name="username")
@Table(name="Empresas")
public class Empresa extends Usuario {

	/* Nombre de la empresa.
	 * No es clave primaria pero debe
	 * ser unica.
	 * */
	@Column(name="Nombre_Empresa", nullable=false, unique=true)
	private String nombreEmpresa;
	
	/* Direccion de la empresa. */
	@Column(nullable=false)
	private String direccion;
	
	/* Telefono de la empresa. */
	@Column(nullable=false)
	private String telefono;
	
	/* Numero estimado de clientes
	 * de la empresa.
	 * */
	@Column(name="Nro_Estimado_Clientes", nullable=false)
	private int estimadoClientes;
	
	/* Relacion uno a muchos de la
	 * empresa con las promociones
	 * que ha lanzado.
	 * */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="empresa", orphanRemoval=true)
	private Set<Promocion> promocionesEmpresa = new HashSet<Promocion>();

	public Empresa() {}

	/* Constructor para una empresa aleatoria.
	 * Recibe como parametros una lista con
	 * usernames ya en uso y nombres de empresa
	 * ya en uso.
	 * */
	public Empresa(List<String> usersTomados, List<String> nombresTomados) {
		super();
		setUsername(genUsernameEmpresa(usersTomados));
		this.nombreEmpresa = generateNombreEmpresa(nombresTomados);
		this.direccion = generateDireccion();
		this.telefono = generateTelefono();
		this.estimadoClientes = 5000 - this.random.nextInt(4501);
		generatePromociones(this.random.nextInt(11));
	}

	/* Generador de un numero, especificado
	 * por su parametro, de promociones para
	 * asociar con la empresa.
	 * */
	private void generatePromociones(int numPromos) {
		for (int i = 0; i < numPromos; i++) {
			Promocion promo = new Promocion();
			promo.setEmpresa(this);
			promocionesEmpresa.add(promo);
		}
	}

	/* Generador de un username aleatorio
	 * para empresas. No utiliza uno que ya
	 * este en uso en la pagina.
	 * */
	private String genUsernameEmpresa(List<String> usersTomados) {
		String newUsername = "usrEmpresa" + String.valueOf(this.random.nextInt(10000));
		while (usersTomados.contains(newUsername)) {
			newUsername = "usrEmpresa" + String.valueOf(this.random.nextInt(10000));
		}
		usersTomados.add(newUsername);
		return newUsername;
	}
	
	/* Generador de un nombre para la empresa.
	 * No utiliza uno que ya este asociado a
	 * una empresa regitrada en la pagina.
	 * */
	private String generateNombreEmpresa(List<String> nombresTomados) {
		String name = randomNameEmpresa();
		while (nombresTomados.contains(name)) {
			name = randomNameEmpresa();
		}
		nombresTomados.add(name);
		return name;
	}
	
	/* Metodo que genera un nombre a partir
	 * de un conjunto de prefijos y sufijos.
	 * */
	private String randomNameEmpresa() {
		StringBuffer buffer = new StringBuffer();
		int index = 0;
		
		String[] prefijos = {"American", "U.S.", "Sun", "United",
				 "Fox", "Central", "British", "Euro",
				 "Virgin", "Universal", "Aeon", "Armax",
				 "Rosenkov", "Kassa", "Indian", "Electronic",
				 "Maximus", "Pegassi", "Aniplex", "Solar",
				 "Future", "International", "Trevor Phillips", "Vandelay",
				 "Roxxon", "Stark", "Daily", "Oscorp",
				 "Advanced", "Merryweather", "Vapid", "Mors Mutual",
				 "Betta", "Perseus", "Nagasaki", "Maibatsu",
				 "Hawk", "Xero", "Eris", "Atomic",
				 "Ultimax", "Avery", "Binary", "Conatix",
				 "New Dawn", "Sirta", "Baria", "Apex",
				 "Ariake", "Armali", "Serrice", "Synthetic Insights",
				 "Sky", "BT", "Altai", "T-GES",
				 "CAT6", "Eclipse", "Omega", "Ryuusei",
				 "AquaStructure", "Aldrin", "Haliat", "Nova"};

		String[] sufijos = {"Enterprises", "Technologies", "Industries",
						"Conglomerate", "Solutions", "Events",
						"Airlines", "& Sons", "Incorporated",
						"C.A.", "Ltd", "Group", "Delivery",
						"Systems", "P.L.C", "Telecom", "Trust",
						"Fabrications", "Applications", "Mechanics",
						"Laboratories", "Foundation", "Apparel",
						"Media", "Designs", "Foods", "Pharmaceuticals",
						"Trading", "Storage", "Rentals"};
		
		index = this.random.nextInt(prefijos.length);
		buffer.append(prefijos[index] + " ");
		index = this.random.nextInt(sufijos.length);
		buffer.append(sufijos[index]);
		return buffer.toString();
	}

	/* Generador de una direccion aleatoria
	 * para la empresa.
	 * */
	private String generateDireccion() {
		StringBuffer buffer = new StringBuffer("Calle ");
		buffer.append(100 - this.random.nextInt(100));
		buffer.append(", Caracas, Venezuela");
		return buffer.toString();
	}
	
	/* Generador de un telefono aleatorio
	 * para la empresa.
	 * */
	private String generateTelefono() {
		StringBuffer buffer = new StringBuffer("0212-");
		buffer.append(9 - this.random.nextInt(8));
		
		for (int i = 0; i < 6; i++) {
			int digit = this.random.nextInt(10);
			buffer.append(digit);
		}
		return buffer.toString();
	}

	public String getNombreEmpresa() {
		return nombreEmpresa;
	}

	public void setNombreEmpresa(String nombreEmpresa) {
		this.nombreEmpresa = nombreEmpresa;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public int getEstimadoClientes() {
		return estimadoClientes;
	}

	public void setEstimadoClientes(int estimadoClientes) {
		this.estimadoClientes = estimadoClientes;
	}

	public Set<Promocion> getPromocionesEmpresa() {
		return promocionesEmpresa;
	}

	public void setPromocionesEmpresa(Set<Promocion> promocionesEmpresa) {
		this.promocionesEmpresa = promocionesEmpresa;
	}

	/* Metodo que realiza un query
	 * a la Base de Datos sobre la
	 * empresa. Imprime por pantalla
	 * la cantidad de clientes esperados
	 * por la empresa y el numero actual
	 * de clientes que ha tenido en la
	 * pagina de promociones.
	 * */
	public void queryCompararClientes(SessionFactory sessionFactory) {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			
			/* Se cuentan todos los vales de
			 * todas las promociones que ofrece la
			 * empresa.
			 * */
			String hql = "SELECT COUNT (*) FROM Vale v " +
							"LEFT OUTER JOIN v.promocion as p " +
							"LEFT OUTER JOIN p.empresa as e " +
							"WHERE v.idVale.idPromo = p.idPromo " +
							"AND e.username = :username_empresa";
			Query query = session.createQuery(hql);
			query.setParameter("username_empresa",this.getUsername());
			Long count = (Long)query.uniqueResult();
			
			System.out.println("-----------------------------------------------------------------------");
			System.out.println("Resumen de comparacion de clientes de la empresa " + this.getNombreEmpresa());
			System.out.println("\tClientes esperados: " + this.getEstimadoClientes());
			System.out.println("\tClientes actuales: " + count);
			System.out.println("-----------------------------------------------------------------------");
			
			tx.commit();
		} catch (HibernateException e) {
			if (tx!=null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}
	}	
}
