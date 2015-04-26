package bases2.brianmendoza.hibernate;

import java.io.Serializable;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

/* Clase concreta de StateShare
 * que representa el estado de la
 * factura cuando no ha sido reembolsada
 * todavia.
 * */
public class StateNoRefund implements StateShare, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	/* Este metodo solo se ejecutara cuando haya que hacer la transicion
	 * de NotRefunded a Refunded */
	public void refund(Factura fac, SessionFactory sessionFactory) {

		Session session = sessionFactory.openSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			
			int monto = fac.getMontoPagado();
			
			System.out.println("Sera reembolsado " + monto + " en dinero virtual");
			
			/* Selecciona el username del que compra el vale asociado a la factura */
			String hql = "SELECT v.idVale.username " + 
						 "FROM Vale v " + 
						 "WHERE v.idVale.idFactura = :id_fac";
			Query query = session.createQuery(hql);
			query.setParameter("id_fac",fac.getIdFactura());
			
			String user = (String)query.uniqueResult();
			
			/* Selecciona el objeto Persona con ese username */
			hql = "FROM Persona p WHERE p.username = :usr";
			query = session.createQuery(hql);
			query.setParameter("usr",user);
			
			Persona persona = (Persona)query.uniqueResult();
			
			/* Se le reembolsa el monto en dinero virtual */
			persona.setDineroVirtual(persona.getDineroVirtual() + monto);
			
			session.merge(persona);
			
			/* Se actualiza el state a reembolsado */
			fac.setState(new StateRefunded());
			
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
