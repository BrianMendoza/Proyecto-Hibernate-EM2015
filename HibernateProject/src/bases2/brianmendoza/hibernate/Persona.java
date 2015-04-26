package bases2.brianmendoza.hibernate;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
 * de las distintas personas registradas
 * en la pagina de promociones. Subclase
 * de la clase Usuario.
 * */
@Entity
@PrimaryKeyJoinColumn(name="username")
@Table(name="Personas")
public class Persona extends Usuario {

	/* Los nombres de la persona. */
	@Column(nullable=false)
	private String nombres;
	
	/* Los apellidos de la persona. */
	@Column(nullable=false)
	private String apellidos;
	
	/* El email de la persona. */
	@Column(nullable=false)
	private String email;
	
	/* La ubicacion geografica de
	 * la persona.
	 * */
	@Embedded
	@AttributeOverrides({
        @AttributeOverride(name="latitud", column=@Column(nullable=true)),
        @AttributeOverride(name="longitud", column=@Column(nullable=true))
    })
	private Coordenadas coordenadas;
	
	/* Relacion uno a muchos de la persona
	 * con las cuentas sociales asociadas
	 * a ella.
	 * */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="dueno", orphanRemoval=true)
	private Set<CuentaSocial> cuentasSocial = new HashSet<CuentaSocial>();
	
	/* Dinero virtual de la persona. */
	@Column(name="Dinero_Virtual", nullable=false)
	private int dineroVirtual;
	
	/* Opcion que indica si la persona
	 * desea que le sean enviados emails
	 * o no.
	 * */
	@Column(name="Opcion_Enviar_Correos", nullable=false)
	private boolean enviarCorreos;
	
	/* Frecuencia (en dias) de cuando
	 * se envian emails a la persona.
	 * Solo tiene un valor si 
	 * enviarCorreos == true.
	 * */
	@Column(name="Frecuencia_Dias_Correos", nullable=true)
	private Integer frecuenciaCorreos;
	
	/* Relacion uno a muchos de la persona
	 * con las tarjetas de credito asociadas
	 * a ella.
	 * */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="dueno_tarjeta", orphanRemoval=true)
	private Set<TDC> tarjetas = new HashSet<TDC>();
	
	/* Relacion uno a muchos de la persona
	 * con los vales asociados a ella.
	 * */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="persona", orphanRemoval=true)
	private Set<Vale> vales = new HashSet<Vale>();
	
	/* Relacion uno a muchos de la persona
	 * con los shares que ha enviado.
	 * */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="personaEnvia", orphanRemoval=true)
	private Set<Comparte> sharesEnviados = new HashSet<Comparte>();
	
	/* Relacion uno a muchos de la persona
	 * con los shares que ha recibido.
	 * */
	@OneToMany(cascade = CascadeType.ALL, mappedBy="personaRecibe", orphanRemoval=true)
	private Set<Comparte> sharesRecibidos = new HashSet<Comparte>();
	
	public Persona() {}

	/* Constructor para una persona aleatoria.
	 * Recibe como parametros una lista con
	 * usernames ya en uso.
	 * */
	public Persona(List<String> usersTomados) {
		super();
		setUsername(genUsernamePersona(usersTomados));
		this.nombres = generateNames();
		this.apellidos = generateApellidos();
		this.email = generateEmail(this.nombres);
		/*Coordenadas son opcionales*/
		if (this.random.nextBoolean())
			this.coordenadas = new Coordenadas();
		this.dineroVirtual = this.random.nextInt(2000);
		this.enviarCorreos = this.random.nextBoolean();
		/* frecuenciaCorreos solo puede tomar un valor
		 * si enviarCorreos == true.
		 * */
		if (this.enviarCorreos)
			this.frecuenciaCorreos = 1 + this.random.nextInt(30);
		/* Generar entre 0 y 3 tarjetas para la persona.*/
		generateTarjetas(this.random.nextInt(4));
		/*Tener cuentas sociales es opcional*/
		if (this.random.nextBoolean())
			generateCuentasSociales();
	}

	/* Metodo que genera entre 1 y 6
	 * cuentas sociales para la persona.
	 * */
	private void generateCuentasSociales() {
		String[] redes = {"Facebook", "Twitter", "Instagram",
				  		  "LinkedIn", "Google+", "Pinterest"};
		boolean seAgrega = true;
		for (int i = 0; i < redes.length; i++) {
			if (seAgrega) {
				CuentaSocial cuentaSoc = new CuentaSocial();
				/*Asigna un username en la red social basada en los nombres y los apellidos*/
				cuentaSoc.setUserSocial(this.nombres, this.apellidos);
				cuentaSoc.setRedSocial(redes[i]);
				cuentaSoc.setDueno(this);
				this.cuentasSocial.add(cuentaSoc);
				seAgrega = this.random.nextBoolean();
			}
		}	
	}

	/* Metodo que genera un numero determinado,
	 * establecido por su parametro, de tarjetas
	 * de credito para la persona.
	 * */
	private void generateTarjetas(int numTarjetas) {
		for (int i = 0; i < numTarjetas; i++) {
			TDC tdc = new TDC();
			tdc.setTarjetahabiente(this.nombres + " " + this.apellidos);
			tdc.setDueno_tarjeta(this);
			this.tarjetas.add(tdc);
		}
	}
	
	/* Generador de un username para la
	 * persona. Solo genera uno que no haya
	 * sido tomado previamente.
	 * */
	private String genUsernamePersona(List<String> usersTomados) {
		String newUsername = "usrPersona" + String.valueOf(this.random.nextInt(10000));
		while (usersTomados.contains(newUsername)) {
			newUsername = "usrPersona" + String.valueOf(this.random.nextInt(10000));
		}
		usersTomados.add(newUsername);
		return newUsername;
	}
	
	/* Generador de nombres aleatorios
	 * para la persona.
	 * */
	private String generateNames() {
		String[] nameArray = {"Daniel", "David", "Gabriel", "Benjamin", "Samuel",
							  "Lucas", "Angel", "Samuel", "Jose", "Adrian",
							  "Sebastian", "Xavier", "Juan", "Luis", "Diego",
							  "Oliver", "Carlos", "Jesus", "Alex", "Max", 
							  "Alejandro", "Antonio", "Miguel", "Victor", "Joel",
							  "Santiago", "Elias", "Ivan", "Oscar", "Leonardo",
							  "Isabella", "Olvia", "Alexis", "Sofia", "Victoria",
							  "Amelia", "Alexa", "Julia", "Camila", "Alexandra",
							  "Maya", "Andrea", "Ariana", "Maria", "Eva",
							  "Angelina", "Valeria", "Natalia", "Isabel", "Sara",
							  "Liliana", "Adriana", "Juliana", "Gabriela", "Daniela",
							  "Valentina", "Lila", "Vivian", "Nora", "Diana"};

		String[] inicialesArray = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
							  	   "J", "K", "L", "M", "N", "O", "P", "Q", "R",
							  	   "S", "T", "U", "V", "W", "X", "Y", "Z"};
		
		int index = this.random.nextInt(nameArray.length);
		String newNombre = nameArray[index];
		index = this.random.nextInt(inicialesArray.length);
		String inicialN = inicialesArray[index];
		
		return newNombre + " " + inicialN;
	}

	/* Generador de apellidos aleatorios
	 * para la persona.
	 * */
	private String generateApellidos() {
		String[] apellidoArray = {"Mendoza", "Garcia", "Gonzalez", "Fernandez",
				  				  "Lopez", "Rodriguez", "Martinez", "Sanchez",
				  				  "Perez", "Gomez", "Martin", "Jimenez",
				  				  "Ruiz", "Hernandez", "Diaz", "Moreno",
				  				  "Alvarez", "Munoz", "Romero", "Alonso",
				  				  "Gutierrez", "Navarro", "Torres", "Dominguez",
				  				  "Ramos", "Vazquez", "Acosta", "Benitez",
				  				  "Medina", "Castillo", };

		String[] inicialesArray = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
						  	   	   "J", "K", "L", "M", "N", "O", "P", "Q", "R",
						  	   	   "S", "T", "U", "V", "W", "X", "Y", "Z"};
		
		int index = this.random.nextInt(apellidoArray.length);
		String newApellido = apellidoArray[index];
		index = this.random.nextInt(inicialesArray.length);
		String inicialA = inicialesArray[index];
		
		return newApellido + " " + inicialA;
	}

	/* Generador de un email
	 * aleatorio para la persona.
	 * */
	private String generateEmail(String usr) {
		return usr.replaceAll("\\s", "") + this.random.nextInt(10000) + "@gmail.com";
	}

	public String getNombres() {
		return nombres;
	}

	public void setNombres(String nombres) {
		this.nombres = nombres;
	}

	public String getApellidos() {
		return apellidos;
	}

	public void setApellidos(String apellidos) {
		this.apellidos = apellidos;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Coordenadas getCoordenadas() {
		return coordenadas;
	}

	public void setCoordenadas(Coordenadas coordenadas) {
		this.coordenadas = coordenadas;
	}

	public Set<CuentaSocial> getCuentasSocial() {
		return cuentasSocial;
	}

	public void setCuentasSocial(Set<CuentaSocial> cuentasSocial) {
		this.cuentasSocial = cuentasSocial;
	}

	public int getDineroVirtual() {
		return dineroVirtual;
	}

	public void setDineroVirtual(int dineroVirtual) {
		this.dineroVirtual = dineroVirtual;
	}

	public boolean isEnviarCorreos() {
		return enviarCorreos;
	}

	public void setEnviarCorreos(boolean enviarCorreos) {
		this.enviarCorreos = enviarCorreos;
	}

	public int getFrecuenciaCorreos() {
		return frecuenciaCorreos;
	}

	public void setFrecuenciaCorreos(int frecuenciaCorreos) {
		this.frecuenciaCorreos = frecuenciaCorreos;
	}

	public Set<TDC> getTarjetas() {
		return tarjetas;
	}

	public void setTarjetas(Set<TDC> tarjetas) {
		this.tarjetas = tarjetas;
	}

	public Set<Vale> getVales() {
		return vales;
	}

	public void setVales(Set<Vale> vales) {
		this.vales = vales;
	}

	public Set<Comparte> getSharesEnviados() {
		return sharesEnviados;
	}


	public void setSharesEnviados(Set<Comparte> sharesEnviados) {
		this.sharesEnviados = sharesEnviados;
	}


	public Set<Comparte> getSharesRecibidos() {
		return sharesRecibidos;
	}


	public void setSharesRecibidos(Set<Comparte> sharesRecibidos) {
		this.sharesRecibidos = sharesRecibidos;
	}

	/* Metodo que realiza un query
	 * a la Base de Datos sobre la
	 * persona. Imprime por pantalla
	 * la cantidad de vales asociados
	 * a la persona y los datos de
	 * cada uno de ellos.
	 * */
	public void queryVales(SessionFactory sessionFactory) {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			String hql = "SELECT COUNT (*) FROM Vale v WHERE v.idVale.username = :username_persona";
			Query query = session.createQuery(hql);
			query.setParameter("username_persona",this.getUsername());
			Long count = (Long)query.uniqueResult();
			hql = "FROM Vale v WHERE v.idVale.username = :username_persona ";
			query = session.createQuery(hql);
			query.setParameter("username_persona",this.getUsername());
			@SuppressWarnings("unchecked")
			List<Vale> results = query.list();
			int i = 1;
			
			System.out.println("-----------------------------------------------------------------------");
			System.out.println("El usuario " + this.getUsername() + " posee " + count + " vales");
			for (Vale v : results) {
				System.out.println("-----------------------------------------------------------------------");
				System.out.println("Vale #" + i);
			    System.out.println("ID del Vale:");
			    System.out.println("\tUsername de persona que compro la promocion: " + v.getIdVale().getUsername());
			    System.out.println("\tID de promocion comprada: " + v.getIdVale().getIdPromo());
			    System.out.println("\tID de factura asociada a compra: " + v.getIdVale().getIdFactura());
			    System.out.println("Es un regalo?: " + (v.isEsRegalo() ? "si" : "no"));
			    System.out.println("Email al que sera enviado el vale: " + v.getEmailDestinatario());
			    System.out.println("Fecha en que sera enviado el vale: " + v.getFechaEnvio());
			    ++i;
			}
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
