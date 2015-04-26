package bases2.brianmendoza.hibernate;

import java.util.Date;
import java.util.Random;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.hibernate.SessionFactory;
import org.hibernate.annotations.Type;

/* Tabla que almacena las facturas
 * asociadas a compras realizadas
 * por personas registradas 
 * en la pagina.
 * */
@Entity
@Table(name="Facturas")
public class Factura {

	/* ID de la factura */
	@Id @GeneratedValue
	@Column(name="Id_Factura", nullable=false)
	private int idFactura;
	
	/* Monto pagado en la compra */
	@Column(name="Monto_Pagado", nullable=false)
	private int montoPagado;
	
	/* Fecha que la compra fue realizada */
	@Column(name="Fecha_Compra", nullable=false)
	@Temporal(TemporalType.DATE)
	private Date fecha_compra;
	
	/* Representa si la compra fue con una tarjeta de credito */
	@Column(name="Es_Compra_TDC", nullable=false)
	private boolean compraTDC;

	/* Representa si la compra fue con dinero virtual */
	@Column(name="Es_Compra_Dinero_Virtual", nullable=false)
	private boolean compraDineroVirtual;
	
	/* Relacion muchos a uno de facturas con
	 * tarjetas de credito.
	 * */
	@ManyToOne(cascade = CascadeType.ALL, fetch=FetchType.LAZY)
	@JoinColumn(name = "Pagado_Con", nullable=true)
	private TDC tarjeta;
	
	/* Relacion uno a uno de la factura con
	 * el vale generado por la compra.
	 * */
	@OneToOne(cascade = CascadeType.ALL, mappedBy="factura", orphanRemoval=true, fetch=FetchType.LAZY)
	@JoinColumn(name="Vale_Asociado", nullable=false)
	private Vale vale;
	
	/* Entero que almacena el numero
	 * de compras realizadas por otros
	 * usuarios resultantes de un share
	 * de la promocion comprada por el
	 * usuario que realiza esta compra
	 * */
	@Column(name = "Compras_Resultantes_De_Shares")
	private int numSharesACompras;
	
	/* Estado de reembolso por shares.
	 * Si no le corresponde un reembolso
	 * todavia es StateNoRefund, si ya
	 * fue reembolsado es StateRefunded.
	 * */
	@Type(type="bases2.brianmendoza.hibernate.StateType")
	@Column(name="Estado_Reembolso")
	private StateShare state;
	
	/* Generador de valores aleatorios.
	 * No es agregado a la tabla.
	 * */
	@Transient
	private Random random;
	
	
	public Factura() {}
	
	/* Constructor de una factura con
	 * un monto pagado especifico.
	 * */
	public Factura(int montoPagado) {
		super();
		this.random = new Random();
		this.montoPagado = montoPagado;
		this.fecha_compra = new Date();
		this.compraTDC = this.random.nextBoolean();
		this.compraDineroVirtual = !this.compraTDC;
		this.numSharesACompras = 0;
		this.state = new StateNoRefund();
	}

	public int getIdFactura() {
		return idFactura;
	}
	protected void setIdFactura(int idFactura) {
		this.idFactura = idFactura;
	}
	public int getMontoPagado() {
		return montoPagado;
	}
	public void setMontoPagado(int montoPagado) {
		this.montoPagado = montoPagado;
	}
	public Date getFecha_compra() {
		return fecha_compra;
	}
	public void setFecha_compra(Date fecha_compra) {
		this.fecha_compra = fecha_compra;
	}
	public boolean isCompraTDC() {
		return compraTDC;
	}
	public void setCompraTDC(boolean compraTDC) {
		this.compraTDC = compraTDC;
	}
	public boolean isCompraDineroVirtual() {
		return compraDineroVirtual;
	}
	public void setCompraDineroVirtual(boolean compraDineroVirtual) {
		this.compraDineroVirtual = compraDineroVirtual;
	}
	public TDC getTarjeta() {
		return tarjeta;
	}
	public void setTarjeta(TDC tarjeta) {
		this.tarjeta = tarjeta;
		tarjeta.getFacturas().add(this);
	}
	public Vale getVale() {
		return vale;
	}
	public void setVale(Vale vale) {
		this.vale = vale;
	}

	public int getNumSharesACompras() {
		return numSharesACompras;
	}

	public void setNumSharesACompras(int numSharesACompras) {
		this.numSharesACompras = numSharesACompras;
	}

	public StateShare getState() {
		return state;
	}

	public void setState(StateShare state) {
		this.state = state;
	}
	
	/* Metodo que verifica si le corresponde un
	 * reembolso al usuario por generar 3 o mas
	 * compras a partir de los shares que haya realizado.
	 * */
	public void checkRefund(SessionFactory sf) {
		
		/* Como condicion debe tener 3 o mas compras
		 * generadas no haber sido reembolsado anteriormente.
		 * */
		if (this.numSharesACompras >= 3 &&
				this.state.getClass() == StateNoRefund.class)
			state.refund(this,sf);
		/* Solo puede ser reembolsado una vez */
		else if (this.state.getClass() == StateRefunded.class)
			System.out.println("Ya fue reembolsado");
		/* Si no cumple estas condiciones, no le corresponde reembolso */
		else
			System.out.println("No le corresponde reembolso");
    }
}
