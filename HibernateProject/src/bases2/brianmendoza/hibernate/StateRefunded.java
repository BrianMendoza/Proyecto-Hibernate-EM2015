package bases2.brianmendoza.hibernate;

import java.io.Serializable;

import org.hibernate.SessionFactory;

/* Clase concreta de StateShare
 * que representa el estado de la
 * factura cuando ha sido reembolsada
 * por los shares.
 * */
public class StateRefunded implements StateShare, Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public void refund(Factura fac, SessionFactory sessionFactory) {
		
		System.out.println("Ya ha sido reembolsado por esta compra");
		
	}

}
