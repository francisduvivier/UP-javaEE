package system.warehouse.stock;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import system.patients.ObserverAction;
import system.time.Time;
import system.time.TimeStamp;
import system.util.BoundedQueue;
import system.util.Pair;
import system.warehouse.Plaster;
import system.warehouse.StockOrder;
import system.warehouse.WarehouseItem;

/**
 * Een klasse die een order voorstelt
 * 
 * @author SWOP Team 10
 */
public class PlasterStock extends DefaultStock {
	/**
	 * Constante die de maximum capaciteit aan gips van het magazijn voorstelt
	 */
	public static final int MAX_CAPACITY_PLASTER = 8;
	/**
	 * Het gips dat opgeslagen is in het ziekenhuis
	 */
	private BoundedQueue<Plaster> plasters;
	
	/**
	 * Initialisatie van de voorraad aan gips
	 * 
	 * @param campus
	 *        De campus waartoe het magazijn waarin de voorraad wordt bewaard, behoort
	 */
	public PlasterStock(Time hospitalTime) {
		super(hospitalTime,StockType.PLASTER);
		
		plasters = new BoundedQueue<Plaster>(MAX_CAPACITY_PLASTER);
		orders = new TreeSet<StockOrder>();
		
		this.fillStock();
	}

	/**
	 * Methode om een plaster toe te voegen aan het warenhuizen.
	 * 
	 * @param item
	 * 			Toe te voegen plaster
	 */
	@Override
	public void addWarehouseItem(WarehouseItem item) {
		plasters.enqueue((Plaster) item);
	}

	/**
	 * Methode om een plaster te verwijderen.
	 * 
	 * @pre aantal plasters in warenhuis > 0
	 * 		| plasters > 0
	 * 
	 * @return item
	 *         als er nog plasters zijn
	 *         null
	 *         als er geen plasters meer zijn
	 */
	@Override
	public WarehouseItem removeWarehouseItem() {
		if (!plasters.isEmpty()) {
			WarehouseItem item = plasters.dequeue();

			int amount = MAX_CAPACITY_PLASTER - plasters.size();
			amount -= getNbItemsAlreadyOrdered();
			
			if (amount > 0) {
				List<WarehouseItem> items = new ArrayList<WarehouseItem>();
				
				while (amount-- > 0) {
					Plaster plaster = new Plaster();
					items.add(plaster);
				}
				
				makeOrder(items);
			}
			
			return item;
		} else {
			return null;
		}
	}

	/**
	 * Een methode om een plaster order van de stock te verwijderen.
	 * 
	 * @param order
	 *        Te verwijderen stock order
	 */
	@Override
	public void removeStockOrder(StockOrder order) {
		orders.remove(order);
	}

	/**
	 * Een methode om de stock te vullen met plasters.
	 */
	@Override
	public void fillStock() {
		for (int i = 0; i < MAX_CAPACITY_PLASTER; i++) {
			plasters.enqueue(new Plaster());
		}
	}

	/**
	 * Een methode om het aantal plasters in de stock op te vragen.
	 * 
	 * @return plasters.size()
	 * 		   Het aantal plasters
	 */
	@Override
	public int getStockSize() {
		return plasters.size();
	}

	/**
	 * Een methode om een gesorteerde set van de plaster orders op te vragen.
	 * 
	 * @return orders
	 * 		   Gesorteerde set van orders
	 */
	@Override
	public SortedSet<StockOrder> getOrders() {
		return Collections.unmodifiableSortedSet(orders);
	}
	
	/**
	 * De primitieve operatie van de ConcreteClass van het Template Method patroon
	 * 
	 * @param before
	 *        De vorige huidige tijd
	 */
	@Override
	protected void updateStockFromTime(TimeStamp before) {
	}
	
	/**
	 * Een methode om te bepalen of er nog gips in voorraad is
	 * 
	 * @return true 
	 *         Als er nog gips in voorraad is
	 *         false
	 *         Als er geen gips in voorraad meer is
	 */
	public boolean hasPlaster() {
		return (this.getStockSize() > 0);
	}

	@Override
	public void cancelAllOrders() {
		for (StockOrder order : orders) {
			setChanged();
			notifyObservers(new Pair<StockOrder, ObserverAction>(order, ObserverAction.REMOVE));
		}
		orders.clear();
	}
}