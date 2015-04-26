package bases2.brianmendoza.hibernate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.criterion.Restrictions;
import org.joda.time.DateTime;
import org.joda.time.Days;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;

/* Clase donde la Base de Datos es
 * poblada con datos y se realizan
 * consultas sobre ella.
 * */
public class Main {

	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws InterruptedException {
		
		/* Se insertaran 50 empresas y 50 personas */
		int numDatos = 50;
		
		/* Configuracion del SessionFactory */
		Configuration configuration = new Configuration().configure();
		StandardServiceRegistryBuilder builder = new StandardServiceRegistryBuilder().
		applySettings(configuration.getProperties());
		SessionFactory sessionFactory = configuration.buildSessionFactory(builder.build());
		
		/* Se abre el session */
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		
		/* Listas que almacenaran resultados de queries */
		List<Persona> listaPersonas = null;
		
		List<Promocion> listaPromociones = null;
		
		List<Empresa> listaEmpresas = null;
		
		/* Listas que almacenaran usernames y nombres de empresas ya utilizados */
		List<String> usersTomados = new ArrayList<String>();
		
		List<String> nombresTomados = new ArrayList<String>();
		
		int numPersonas = 0;
		int numPromociones = 0;
		
		/* Se guardara el indice de la persona con mayor cantidad de
		 * compras para que el query sea mas interesante. */
		int maxCompras = 0;
		int indexPersonaConMasCompras = 0;
		
		Random random = new Random();
		
		try {
			tx = session.beginTransaction();
			

			/* Poblar con Personas(a su vez TDCs y Cuentas Sociales)
			 * y Empresas(a su vez Promociones, y a estos fechas puntuales y enlaces)
			 * */
			for (int i = 0; i < numDatos; i++) {
				Empresa e = new Empresa(usersTomados, nombresTomados);
				session.save(e);

				Persona p = new Persona(usersTomados);
				session.save(p);
			}
			
			/* Los datos creados son guardados en la Base de Datos como tal */
			session.flush();
			
			listaPersonas = session.createCriteria(Persona.class).list();
			listaPromociones = session.createCriteria(Promocion.class).list();
			listaEmpresas = session.createCriteria(Empresa.class).list();
			numPersonas = listaPersonas.size();
			numPromociones = listaPromociones.size();
			int probTendraCompras = 100;
			
			/* Por cada persona se ve si tendra vales o no, y luego
			 * cuantos vales seran creados para el.
			 * */
			for (int i = 0; i < numPersonas; i++) {
				/* Se crean vales para la persona basada en una probabilidad
				 * para garantizar que al menos uno tendra vales */
				if (random.nextInt(101) < probTendraCompras) {
					probTendraCompras = probTendraCompras - 5;
					
					/* Cuantos vales tendra esta persona */
					int numCompras = 1 + random.nextInt(50);
					
					if (numCompras > maxCompras) {
						indexPersonaConMasCompras = i;
						maxCompras = numCompras;
					}
					
					Persona persona = listaPersonas.get(i);
					
					/* Se crea una lista de todas las tarjetas de credito de esta persona */
					List<TDC> listaTDCs = session.createCriteria(TDC.class)
							.add(Restrictions.like("dueno_tarjeta", persona))
							.list();
					int numTarjetas = listaTDCs.size();
					
					/* Se crean las compras */
					for (int j = 0; j < numCompras; j++) {
						int index = random.nextInt(numPromociones);
						
						/* Se obtiene una promocion aleatoria para comprar */
						Promocion promocion = listaPromociones.get(index);
						Factura factura = new Factura(promocion.getMontoOfertado());
						
						/* Se determina si sera comprada con una tarjeta de credito o no, y de ser
						 * asi con una tarjeta aleatoria de las que tiene la persona */
						if (factura.isCompraTDC()) {
							if (numTarjetas > 0) {
								int indexTDC = random.nextInt(numTarjetas);
								TDC tarjeta = listaTDCs.get(indexTDC);
								factura.setTarjeta(tarjeta);
							} else {
								factura.setCompraDineroVirtual(true);
								factura.setCompraTDC(false);
							}
						}
						
						/* La fecha de la factura es colocada dentro del rango
						 * de vigencia de la promocion, para mantener coherencia */
						int diasDeVigenciaPromo = Days.daysBetween
								(new DateTime(promocion.getVigencia().getFechaInicio()),
								 new DateTime(promocion.getVigencia().getFechaFin()))
							 .getDays();
						GregorianCalendar gc = new GregorianCalendar();
				        gc.setTime(promocion.getVigencia().getFechaInicio());
				        gc.add(Calendar.DAY_OF_MONTH, random.nextInt(diasDeVigenciaPromo));
				        factura.setFecha_compra(gc.getTime());
						session.save(factura);
						
						/* Se determinan los atributosdel vale a ser creado */
						boolean seraRegalo = random.nextBoolean();
						Date fechaEnvio = null;
						String email = null;
						
						/* Si es regalo, email debe ser de otro usuario y una fecha posterior.
						 * Si no es regalo, email es de la persona que compra y fecha es nula.
						 * */
						if (seraRegalo) {
							int indexOtherPersona = random.nextInt(numPersonas);
							
							/* No puede regalarselo a si mismo */
							while (indexOtherPersona == i) {
								indexOtherPersona = random.nextInt(numPersonas);
							}
							
							email = listaPersonas.get(indexOtherPersona).getEmail();
							
							/* Se determina una fecha de envio aleatoria entre la fecha de compra
							 * y la fecha de finalizacion de la promocion, para mantener coherencia */
					        gc.setTime(factura.getFecha_compra());
					        int diasEntreCompraFin = Days.daysBetween
					        								(new DateTime(factura.getFecha_compra()),
					        								 new DateTime(promocion.getVigencia().getFechaFin()))
					        							 .getDays();
					        
					        /* Si la fecha de compra es anterior a la fecha fin, se determina una
					         * fecha aleatoria entre los dos. Si es igual a la fecha fin entonces
					         * la fecha de envio es la fecha fin. */
					        if (diasEntreCompraFin != 0) {
					        	gc.add(Calendar.DAY_OF_MONTH, random.nextInt(diasEntreCompraFin));
						        fechaEnvio = gc.getTime();
					        } else {
					        	fechaEnvio = promocion.getVigencia().getFechaFin();
					        }
						} else {
							/* Caso que no es un regalo, el email es de la persona que compra */
							email = persona.getEmail();
						}
						Vale v = new Vale(seraRegalo, email, fechaEnvio, random.nextBoolean(),
											persona, promocion, factura);
						session.save(v);
					}
				}
			}
			
			/* Los datos creados son guardados en la Base de Datos como tal */
			session.flush();
			
			/* Para las personas que hayan comprado promociones, se realizaran
			 * un numero aleatorio de shares de esa persona con otras personas
			 * registradas en la pagina. */
	        for (int i = 0; i < numPersonas; i++) {
	        	Persona personaEnvia = listaPersonas.get(i);
	        	
	        	/* Se consulta los vales asociada a esta persona */
	        	List<Vale> listaVales = session.createCriteria(Vale.class)
						.add(Restrictions.like("persona", personaEnvia))
						.list();
        		int numVales = listaVales.size();
	        	
        		/* Solo puede compartir una promocion que haya comprado */
	        	if (numVales > 0) {
	        		/* Diccionario que permite repeticiones de clave donde se almacenaran
	        		 * que promociones ha compartido con cuales usuarios, para evitar
	        		 * repeticiones. Clave k: idPromo, Valor v: indice de la persona con
	        		 * la cual compartio la promocion */
	        		Multimap<Integer,Integer> sharesRealizados = ArrayListMultimap.create();
	        		
	        		for (int j = 0; j < numVales; j++) {
	        			Vale vale = listaVales.get(j);
	        			Promocion promocionCompartida = vale.getPromocion();
	        			int idPromo = promocionCompartida.getIdPromo();
	        			
	        			/* Decide si compartira esta promocion o no, y si todavia quedan usuarios
	        			 * con los cuales no la ha compartido todavia */
	        			if (random.nextBoolean() && sharesRealizados.get(idPromo).size() < numPersonas-1) {
							/* Numero aleatorio de shares que realizara para esta promocion */
							int numShares = 1 + random.nextInt(6);
							
							for (int k = 0; k < numShares; k++) {
								int index = random.nextInt(numPersonas);
								
								/* No puede compartir consigo mismo ni con personas con los cuales
								 * ya haya compartido esta promocion */
								while (index == i || sharesRealizados.get(idPromo).contains(index)) {
									index = random.nextInt(numPersonas);
								}
								
								/* Se actualiza el diccionario y se almacena el nuevo share en la BD*/
								sharesRealizados.put(idPromo, index);
								Persona personaRecibe = listaPersonas.get(index);
								Comparte comparte = new Comparte(personaEnvia,personaRecibe,promocionCompartida);
								session.save(comparte);
							}
						}
	        		}
	        	}
	        }
	        
	        session.flush();
	        session.clear();
	        
			session.close();
			
			/* Fin de la seccion de carga de datos */
			
			/* Comienzo de la seccion de queries */
			
			
			/* Se realizaran queries sobre cuantos vales tiene una persona
			 * (y los datos de cada uno de esos vales), y de cuantas compras
			 * sumadas entre todas sus promociones tiene una empresa.
			 * Para visualizar el contraste se realizaran sobre la persona con
			 * mas vales, una persona aleatoria, la empresa con mayor cantidad
			 * de compras y una empresa aleatoria. */
			
			Persona personaMasCompras = listaPersonas.get(indexPersonaConMasCompras);
			
			personaMasCompras.queryVales(sessionFactory);
			
			int indexPersonaAleatoria = random.nextInt(numPersonas);
			
			while (indexPersonaAleatoria == indexPersonaConMasCompras)
				indexPersonaAleatoria = random.nextInt(numPersonas);
			
			Persona personaPruebaAleatoria = listaPersonas.get(indexPersonaAleatoria);
			
			personaPruebaAleatoria.queryVales(sessionFactory);
			
			Empresa empresaPopular = getEmpresaMasPopular(sessionFactory);
			
			int indexEmpresaPopular = listaEmpresas.indexOf(empresaPopular);
			
			int indexEmpresaAleatoria = random.nextInt(listaEmpresas.size());
			
			while (indexEmpresaPopular == indexEmpresaAleatoria)
				indexEmpresaAleatoria = random.nextInt(listaEmpresas.size());
						
			empresaPopular.queryCompararClientes(sessionFactory);
			
			Empresa empresaPruebaAleatoria = listaEmpresas.get(indexEmpresaAleatoria);
			
			empresaPruebaAleatoria.queryCompararClientes(sessionFactory);
			
			/* Se realizaran operaciones que demuestren el comportamiento
			 * dinamico del modelo. Se cancelara la promocion mas popular,
			 * se expirara la promocion menos popular y se realizara un
			 * reembolso para un usuario que haya realizado shares y se
			 * hayan dado 3 compras a partir de esos shares.
			 * */
							
			cancelPromoPopular(sessionFactory);
			
			expirarPromocion(sessionFactory);
			
			refundPorShares(sessionFactory);
			
			tx.commit();
		}
		catch (Exception e) {
			if (tx!=null)
				tx.rollback();
			e.printStackTrace();
		}
		finally {
			if (session.isOpen())
				session.close();
			sessionFactory.close();
		}
	}

	/* Metodo para realizar un query HQL para obtener la empresa con mayor
	 * cantidad de compras en la Base de Datos
	 * */
	private static Empresa getEmpresaMasPopular(SessionFactory sessionFactory) {
		
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		Empresa res = null;
		
		try {
			tx = session.beginTransaction();
			
			/* Se utiliza el contenedor EmpresaStats para almacenar los resultados del query */
			String hql = "SELECT new bases2.brianmendoza.hibernate.EmpresaStats(p.empresa.username, COUNT (*)) " +
							"FROM Vale v " +
							"LEFT OUTER JOIN v.promocion as p " +
							"WHERE v.idVale.idPromo = p.idPromo " +
							"GROUP BY p.empresa.username " + 
							"ORDER BY COUNT(*) DESC";
			Query query = session.createQuery(hql);
			@SuppressWarnings("unchecked")
			List<EmpresaStats> results = query.list();
			
			/* Como los resultados estan ordenados en forma descendente, el primero es el mas popular */
			String userPopular = results.get(0).getUsername();
			
			/* Se obtiene el objeto Empresa que tiene mayor cantidad de compras */
			hql = "FROM Empresa e WHERE e.username = :pop_emp";
			query = session.createQuery(hql);
			query.setParameter("pop_emp",userPopular);
			
			@SuppressWarnings("unchecked")
			List<Empresa> results2 = query.list();
			
			res = results2.get(0);
			
			tx.commit();
		} catch (HibernateException e) {
			if (tx!=null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}	
		return res;
	}

	/* Metodo para para cancelar la promocion que
	 * haya sido comprada la mayor cantidad de veces
	 * */
	@SuppressWarnings("unchecked")
	private static void cancelPromoPopular(SessionFactory sessionFactory) {
		
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		Promocion res = null;
		
		try {
			tx = session.beginTransaction();
			
			/* Se utiliza el contenedor PromocionStats para almacenar los resultados del query */
			String hql = "SELECT new bases2.brianmendoza.hibernate.PromocionStats" +
						    "(v.idVale.idPromo, COUNT (v.idVale.idPromo)) " +
							"FROM Vale v " +
							"GROUP BY v.idVale.idPromo " + 
							"ORDER BY COUNT(v.idVale.idPromo) DESC";
			Query query = session.createQuery(hql);
			
			List<PromocionStats> results = query.list();
			
			/* Como los resultados estan ordenados en forma descendente, el primero es el mas popular */
			int promoPopular = results.get(0).getIdPromo();
			
			/* Se obtiene el objeto Promocion que tiene mayor cantidad de compras */
			hql = "FROM Promocion p WHERE p.idPromo = :pop_promo";
			query = session.createQuery(hql);
			query.setParameter("pop_promo",promoPopular);
			
			List<Promocion> results2 = query.list();
			
			res = results2.get(0);
			int montoOfertado = res.getMontoOfertado();
			
			/* Se obtienen las personas que seran afectadas por el cancelamiento */
			hql = "SELECT DISTINCT p FROM Persona p " +
				  "LEFT OUTER JOIN p.vales as v " +
				  "WHERE v.utilizado = false AND " +
				  		"v.idVale.idPromo = :pop_promo";
			query = session.createQuery(hql);
			query.setParameter("pop_promo",promoPopular);
			
			List<Persona> afectados = query.list();
			
			/* Se almacenaran para la impresion los usernames, el dinero antes del cancelamiento
			 * y la cantidad de vales del usuario que fueron afectados por el cancelamiento */
			String[] usernames = new String[afectados.size()];
			int[] dineroAntes = new int[afectados.size()];
			Long[] numVales = new Long[afectados.size()];
			
			int i = 0;
			
			/* Se prepara una consulta para obtener, por usuario, la cantidad de vales afectados */
			hql = "SELECT COUNT(*) FROM Vale v " +
				  "WHERE v.utilizado = false AND " +
					  	"v.idVale.idPromo = :pop_promo AND " +
					  	"v.idVale.username = :usr";
			
			/* Se obtienen los usernames, dinero virtual antes del cancelamiento y la cantidad de
			 * vales de ese usuario que fueron afectados */
			for (Persona p : afectados) {
				query = session.createQuery(hql);
				query.setParameter("pop_promo",promoPopular);
				query.setParameter("usr",p.getUsername());
				Long count = (Long)query.uniqueResult();
				numVales[i] = count;
				usernames[i] = p.getUsername();
				dineroAntes[i] = p.getDineroVirtual();
				i++;
			}
			
			System.out.println("-------------------------------------------");
			System.out.println("");
			System.out.println("Se cancelara la promocion " + promoPopular);
			System.out.println("Monto de la promocion: " + montoOfertado);
			System.out.println("");
			
			/* Se cambia el estado a cancelado */
			res.setStatus(Status.CANCELADO);
			session.flush();

			/* Se refrescan los objetos para que reflejen los cambios */
			for (Persona p : afectados) {
				session.refresh(p);
			}

			/* Se almacenara el dinero virtual despues del reembolso para la impresion */
			int[] dineroDespues = new int[afectados.size()];
			
			i = 0;
			
			/* Se obtiene el dinero virtual actualizado para cada persona */
			for (Persona p : afectados) {
				dineroDespues[i] = p.getDineroVirtual();
				i++;
			}
			
			i = 0;
			
			/* Se imprimen por pantalla los resultados de la prueba */
			while (i < afectados.size()) {
				System.out.println("Persona afectada: " + usernames[i]);
				System.out.println("Numero de vales afectados: " + numVales[i]);
				System.out.println("Dinero virtual antes:" + dineroAntes[i]);
				System.out.println("Dinero virtual despues:" + dineroDespues[i]);
				System.out.println("");
				i++;
			}
			
			tx.commit();
		} catch (HibernateException e) {
			if (tx!=null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}	
	}
	
	/* Metodo para expirar la promocion menos popular
	 * */
	@SuppressWarnings("unchecked")
	private static void expirarPromocion(SessionFactory sessionFactory) {
		
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		Promocion res = null;
		
		try {
			tx = session.beginTransaction();
			
			/* Se utiliza el contenedor PromocionStats para almacenar los resultados del query */
			String hql = "SELECT new bases2.brianmendoza.hibernate.PromocionStats" +
						    "(v.idVale.idPromo, COUNT (v.idVale.idPromo)) " +
							"FROM Vale v " +
							"GROUP BY v.idVale.idPromo " + 
							"ORDER BY COUNT(v.idVale.idPromo) DESC";
			Query query = session.createQuery(hql);
			
			List<PromocionStats> results = query.list();
			
			/* Como los resultados estan ordenados en forma descendente, el ultimo es el menos popular */
			int lastPromo = results.get(results.size()-1).getIdPromo();
			
			/* Se obtiene el objeto Promocion que tiene menor cantidad de compras */
			hql = "FROM Promocion p WHERE p.idPromo = :pop_promo";
			query = session.createQuery(hql);
			query.setParameter("pop_promo",lastPromo);
			
			List<Promocion> results2 = query.list();
			
			res = results2.get(0);
			
			/* Se obtiene la fecha final original y el status actual */
			Date fechaExp = res.getVigencia().getFechaFin();
			String status = res.getStatus().toString();
			
			System.out.println("-------------------------------------------");
			System.out.println("");
			System.out.println("Promocion a expirar: Promocion " + lastPromo);
			System.out.println("");
			System.out.println("Fecha final antes de modificar: " + fechaExp);
			System.out.println("");
			System.out.println("Status: " + status);
			
			/* Se obtiene la fecha de ayer */
			Date ayer = (new DateTime()).minusDays(1).toDate();
			
			/* Se cambia la fecha final por la fecha de ayer */
			res.setVigencia(new Vigencia(res.getVigencia().getFechaInicio(),ayer));
			
			session.flush();
			
			session.refresh(res);
			
			/* Se obtienen los nuevos valores */
			fechaExp = res.getVigencia().getFechaFin();
			status = res.getStatus().toString();

			System.out.println("");
			System.out.println("Fecha final despues de modificar: " + fechaExp);
			System.out.println("");
			System.out.println("Status: " + status);
			System.out.println("");
			System.out.println("-------------------------------------------");
			
			tx.commit();
		} catch (HibernateException e) {
			if (tx!=null)
				tx.rollback();
			e.printStackTrace();
		} finally {
			session.close();
		}	
	}
	
	/* Metodo que selecciona una factura al azar y prueba
	 * el cambio de estado de NoRefund a Refunded, que como
	 * consecuencia reembolsa al usuario el monto que pago.
	 * */
	@SuppressWarnings("unchecked")
	private static void refundPorShares(SessionFactory sessionFactory) {
		Session session = sessionFactory.openSession();
		Transaction tx = null;
		Random random = new Random();
		
		try {
			tx = session.beginTransaction();
			
			/* Selecciona todas las facturas */
			String hql = "FROM Factura";
			Query query = session.createQuery(hql);
			
			List<Factura> results = query.list();
			
			/* Escoge una factura al azar */
			Factura factura = results.get(random.nextInt(results.size()));
			
			/* Se obtiene la persona que vera su dinero reembolsado */
			hql = "SELECT v.persona FROM Vale v WHERE v.idVale.idFactura = :fac";
			query = session.createQuery(hql);
			query.setParameter("fac",factura.getIdFactura());
			
			Persona persona = (Persona) query.uniqueResult();
			
			System.out.println("");
			System.out.println("Persona a reembolsar por shares: " + persona.getUsername());
			System.out.println("");
			System.out.println("Dinero virtual primer chequeo: " + persona.getDineroVirtual());
			System.out.println("");
			System.out.println("Factura elegida: " + factura.getIdFactura());
			System.out.println("");	
			System.out.println("Numero de shares resultantes en compras: " + factura.getNumSharesACompras());
			System.out.println("");
			
			/* Primer chequeo, por defecto numero de shares resultantes en compras es cero
			 * asi que deberia salir en pantalla que no le corresponde un reembolso */
			factura.checkRefund(sessionFactory);
			System.out.println("");
			
			/* Se cambia manualmente el numero de shares resultantes en compras a 3 para
			 * probar la funcionalidad de que el estado se cambie efectivamente */
			System.out.println("Cambiando numero de shares resultantes en compras a 3");
			System.out.println("");
			factura.setNumSharesACompras(3);
			System.out.println("Numero de shares resultantes en compras: " + factura.getNumSharesACompras());
			System.out.println("");
			
			/* Segundo chequeo, como el numero de shares resultantes en compras es >= 3
			 * entonces el estado deberia cambiar automaticamente y el dinero reembolsado */
			factura.checkRefund(sessionFactory);
			System.out.println("");
			
			session.refresh(persona);
			
			System.out.println("Dinero virtual segundo chequeo: " + persona.getDineroVirtual());
			System.out.println("");
			System.out.println("Intentando reembolsar de nuevo");
			System.out.println("");
			
			/* Tercer chequeo, como ya fue reembolsado deberia salir en pantalla que
			 * la persona ya fue reembolsada por esa factura */
			factura.checkRefund(sessionFactory);
			System.out.println("");
			
			session.refresh(persona);
			
			System.out.println("Dinero virtual tercer chequeo: " + persona.getDineroVirtual());
			System.out.println("");
			
			session.flush();
			
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