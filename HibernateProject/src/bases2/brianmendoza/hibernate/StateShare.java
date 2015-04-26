package bases2.brianmendoza.hibernate;

import org.hibernate.SessionFactory;

/* Interface utilizado para el patron
 * State que representa el estado de
 * la factura: sin reembolso o reembolsado.
 * */
public interface StateShare {
	public void refund(Factura fac, SessionFactory sessionFactory);
}
