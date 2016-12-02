
package system.warehouse.stock;

import java.util.Collections;
import java.util.Observable;
import java.util.Observer;
import java.util.SortedSet;
import java.util.TreeSet;

import system.time.Time;
import system.time.TimeStamp;
import system.warehouse.StockOrder;
import system.warehouse.WarehouseItem;

/**
 * Een klasse die een order voorstelt
 * 
 * @author SWOP Team 10
 */
public abstract class Stock extends Observable {
	/**
	 * Een variabele die de huidige tijd behoudt
	 */
	protected TimeStamp now;
	
	private final StockType type;
	
	/**
	 * Initialisatie van een voorraad
	 * 
	 * @param campus
	 *        De campus waartoe het magazijn waarin de voorraad wordt bewaard, behoort
	 */
	public Stock(Time hospitalTime, StockType type) {	
		hospitalTime.addObserver(new TimeObserver());
		this.now = hospitalTime.getTime();
		this.type = type;
	}
	
	/**
	 * Getter voor de huidige tijd
	 * 
	 * @return now 
	 *         De huidige tijd als TimeStamp
	 */
	protected TimeStamp getNow() {
		return now;
	}
	
	/** 
	 * Een methode om een item aan de voorraad toe te voegen
	 *
	 * @param item
	 *        Het toe te voegen item
	 */
	public abstract void addWarehouseItem(WarehouseItem item);
	/**
	 * Een methode om het order te markeren als aangekomen
	 * 
	 * @param order
	 *        Het aangekomen order
	 */
	public abstract void removeStockOrder(StockOrder order);
	/**
	 * Een methode om de voorraad volledig op te vullen
	 */
	public abstract void fillStock();

	/**
	 * Getter voor het aantal items dat met behulp van de opgegeven orders besteld is
	 * 
	 * @param orders
	 *        De orders waarvan het totaal aantal items moet worden bepaald
	 * @return nb
	 *         Het totaal aantal items dat met behulp van de opgegeven orders besteld is 
	 */
	protected int getNbItemsAlreadyOrdered(SortedSet<StockOrder> orders) {
		int nb = 0;

		for (StockOrder order : orders) {
				nb += order.getAmount();
		}

		return nb;
	}	
	
	/**
	 * Een methode om de gearriveerde orders uit een collectie orders te filteren
	 * 
	 * @param allOrders
	 * 		  De collectie orders die gefilterd dient te worden
	 * @return arrivedOrders
	 *         De gefilterde collectie bestaande uit gearriveerde orders
	 */
	protected SortedSet<StockOrder> filterArrivedOrders(SortedSet<StockOrder> allOrders) {
		SortedSet<StockOrder> arrivedOrders = new TreeSet<StockOrder>();

		for (StockOrder order : allOrders) {
			if (order.isArrived()) {
				arrivedOrders.add(order);
			}
		}

		return Collections.unmodifiableSortedSet(arrivedOrders);
	}

	/**
	 * Een methode om de x laatst geplaatste orders uit een collectie orders te halen
	 * 
	 * @param amount
	 *        Het aantal orders dat uit de collectie dient gehaald te worden
	 * @param orders
	 *        De collectie orders waaruit de meest recent geplaatste orders moeten gehaald worden
	 * @return latestOrders
	 * 	       De resulterende collectie bestaande uit het gevraagde aantal laatst geplaatste orders
	 * @throws IllegalArgumentException
	 *         Als het opgegeven aantal niet groter is dan nul
	 */
	protected SortedSet<StockOrder> getLatestOrders(int amount, SortedSet<StockOrder> orders) throws IllegalArgumentException {
		if (amount <= 0) {
			throw new IllegalArgumentException("Amount has to be greater than 0");
		}
		
		SortedSet<StockOrder> latestOrders = new TreeSet<StockOrder>();

		for (StockOrder order:orders) {
			if (!order.isArrived()) {
				latestOrders.add(order);
			}
		}
		return latestOrders;
	}
	
	public abstract void cancelAllOrders();
	
	/**
	 * De primitieve operatie van de AbstractClass van het Template Method patroon
	 * 
	 * @param before
	 *        De vorige huidige tijd
	 */
	protected abstract void updateStockFromTime(TimeStamp before);
	
	/**
	 * Een methode die opgeroepen wordt als de tijd vooruitgaat
	 * 
	 * @param arg
	 *        Een object van TimeStamp dat de nieuwe huidige tijd voorstelt
	 */
	protected void updateFromTime(Object arg) {
		try {
			TimeStamp before = now;
			now = (TimeStamp) arg;
			
			updateStockFromTime(before);
		} catch (ClassCastException e) {
			throw new ClassCastException("Invalid argument");
		}
	}
	
	/**
	 * De ConcreteObserver klasse met update methode voor de implementatie van het Observer patroon
	 */
	private class TimeObserver implements Observer {
		@Override
		public void update(Observable o, Object arg) {
			updateFromTime(arg);
		}
	}
	
	public final StockType getType() {
		return this.type;
	}
}