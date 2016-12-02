package system.warehouse.stock;

import java.util.List;
import java.util.NavigableSet;
import java.util.SortedSet;

import system.patients.ObserverAction;
import system.time.Time;
import system.util.Pair;
import system.warehouse.StockOrder;
import system.warehouse.WarehouseItem;

/**
 * Een klasse die een voorraad van items voorstelt
 * 
 * @author SWOP Team 10
 */
public abstract class DefaultStock extends Stock {
	
	/**
	 * De geplaatste orders die nog niet verwerkt zijn. Een order kan Arrived
	 * zijn maar is pas verwerkt nadat de items ervan zijn toegevoegd aan de
	 * warehouse en hij uit deze lijst gehaald is.
	 */
	protected NavigableSet<StockOrder> orders;
	
	/**
	 * Initialisatie van een voorraad
	 * 
	 * @param campus
	 *        De campus waartoe het magazijn waarin de voorraad wordt bewaard, behoort
	 */
	public DefaultStock(Time hospitalTime, StockType type) {
		super(hospitalTime, type);
	}
	
	/**
	 * Een methode om een item uit de voorraad te halen
	 *
	 * @return item
	 *         als er nog items zijn
	 *         null
	 *         als er geen items meer zijn
	 */
	public abstract WarehouseItem removeWarehouseItem();
	
	/**
	 * Getter voor het aantal items in voorraad
	 * 
	 * @return nb
	 *         Het aantal items in voorraad
	 */
	public abstract int getStockSize();
	
	/**
	 * Getter voor alle orders
	 * 
	 * @return orders
	 *         Alle orders
	 */
	public abstract SortedSet<StockOrder> getOrders();
	
	/**
	 * Getter voor het aantal items dat besteld is
	 * 
	 * @return nb
	 *         Het totaal aantal items dat besteld is 
	 */
	public int getNbItemsAlreadyOrdered() {
		return getNbItemsAlreadyOrdered(getOrders());
	}
	
	/**
	 * Getter voor de orders die gearriveerd moeten zijn 
	 * 
	 * @return orders
	 *         De orders die gearriveerd moeten zijn
	 */
	public SortedSet<StockOrder> getArrivedOrders() {
		return filterArrivedOrders(getOrders());
	}

	/**
	 * Getter voor de meest recent geplaatste orders
	 * 
	 * @param amount
	 *        Het aantal geplaatste orders
	 * @return latestOrders
	 *         De de meest recent geplaatste orders
	 * @throws IllegalArgumentException
	 *         Als het opgevraagde aantal niet groter is dan 0
	 */
	public SortedSet<StockOrder> getLatestOrders(int amount) throws IllegalArgumentException {
		return getLatestOrders(amount, getOrders());
	}
	/**
	 * Doet alle nodige dingen voor het bestellen van WarehouseItems
	 * @param items de items die besteld moeten worden
	 */
	protected void makeOrder(List<WarehouseItem> items) {
		StockOrder order = new StockOrder(this, getNow(), items);
		orders.add(order);
		setChanged();
		notifyObservers(new Pair<StockOrder, ObserverAction>(order, ObserverAction.ADD));
	}

}
