package bases2.brianmendoza.hibernate;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

/* Clase que permite transformar un
 * objeto de la clase StateShare a un
 * tipo SQL que la Base de Datos entienda
 * y viceversa.
 * */
public class StateType implements UserType {

	@Override
	/* Implementacion del metodo assemble*/
	public Object assemble(Serializable arg0, Object arg1)
			throws HibernateException {
		return arg0;
	}

	@Override
	/* Copia profunda del objeto StateShare */
	public Object deepCopy(Object arg0) throws HibernateException {
		Object obj = null;
		if (arg0==null)
			return null;
		/* Si el objeto no es de la clase StateNoRefund o StateRefunded no se puede copiar */
		if (! (arg0.getClass() == StateNoRefund.class || arg0.getClass() == StateRefunded.class))
			throw new UnsupportedOperationException("Can't convert "+arg0.getClass());
		/* Si es un StateNoRefund devuelve un objeto nuevo de la misma clase */
		if (arg0.getClass() == StateNoRefund.class)
			obj = new StateNoRefund();
		/* Si es un StateRefunded devuelve un objeto nuevo de la misma clase */
		if (arg0.getClass() == StateRefunded.class)
			obj =  new StateRefunded();
		return obj;
	}

	@Override
	/* Implementacion del metodo disassemble */
	public Serializable disassemble(Object arg0) throws HibernateException {
		if (! (arg0.getClass() == StateNoRefund.class || arg0.getClass() == StateRefunded.class))
			throw new UnsupportedOperationException("Can't convert "+arg0.getClass());
		if (arg0.getClass() == StateNoRefund.class)
			return new StateNoRefund();
		if (arg0.getClass() == StateRefunded.class)
			return new StateRefunded();
		return null;
	}

	@Override
	public boolean equals(Object arg0, Object arg1) throws HibernateException {
		return arg0.equals(arg1);
	}

	@Override
	public int hashCode(Object arg0) throws HibernateException {
		return arg0.hashCode();
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	/* Metodo que permite pasar de un varchar de SQL a un objeto StateShare */
	public Object nullSafeGet(ResultSet arg0, String[] arg1,
			SessionImplementor arg2, Object arg3) throws HibernateException,
			SQLException {
		String value = arg0.getString(arg1[0]);
		if (value==null)
			return null;
		if (value.equals("NOREFUND"))
			return new StateNoRefund();
		if (value.equals("REFUNDED"))
			return new StateRefunded();
		return null;
	}

	@Override
	/* Metodo que permite pasar de un objeto StateShare a un varchar de SQL */
	public void nullSafeSet(PreparedStatement arg0, Object arg1, int arg2,
			SessionImplementor arg3) throws HibernateException, SQLException {
		if (arg1==null) {
			arg0.setNull(arg2, Types.VARCHAR);
			return;
		}
		String att = null;
		if (arg1.getClass() == StateNoRefund.class)
			att = "NOREFUND";
		if (arg1.getClass() == StateRefunded.class)
			att = "REFUNDED";
		if (! (arg1.getClass() == StateNoRefund.class || arg1.getClass() == StateRefunded.class))
			throw new UnsupportedOperationException("Can't convert "+arg1.getClass());
		arg0.setString(arg2, att);
	}

	@Override
	public Object replace(Object arg0, Object arg1, Object arg2)
			throws HibernateException {
		return arg0;
	}

	@SuppressWarnings("rawtypes")
	@Override
	/* Clase retornada */
	public Class returnedClass() {
		return StateShare.class;
	}

	@Override
	/* Tipos de SQL generados */
	public int[] sqlTypes() {
		return new int[] {Types.VARCHAR};
	}

}
