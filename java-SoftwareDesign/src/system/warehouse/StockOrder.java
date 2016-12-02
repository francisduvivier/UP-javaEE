package system.warehouse;


import java.util.ArrayList;
import java.util.List;

import client.IStockOrder;


import system.scheduling.Event;
import system.time.TimeStamp;
import system.warehouse.stock.Stock;


/**
 * Een klasse die een order voorstelt
 * 
 * @author SWOP Team 10
 */
public class StockOrder implements Comparable<StockOrder>, IStockOrder {
	/**
	 * Een variabele die de locatie voorstelt waar de items van het order opgeslagen moeten worden
	 */
	private final Stock stockLocation;
	/**
	 * Een variabele die het aantal items in het order voorstelt
	 */
	private final int amount;
	/**
	 * Een variabele die de datum waarop het order geplaatst is voorstelt
	 */
	private final TimeStamp orderDate;
	/**
	 * Een variabele die de verwachte leverdatum van het order voorstelt
	 */
	private final TimeStamp arrivalDate;
	/**
	 * Een variabele die aangeeft of het order aangekomen ZOU MOETEN ZIJN
	 */
	private boolean arrived;
	/**
	 * De items die met behulp van het order besteld zijn
	 */
	private List<WarehouseItem> items;	
	/**
	 * Een object van de inner klasse ArrivalEvent die het aankomen van het order voorstelt
	 */
	private final ArrivalEvent arrivalEvent;
	
	/**
	 * Initialisatie van het order
	 * 
	 * @param stockLocation
	 *        Het locatie waar de items van het order opgeslagen moeten worden
	 * @param orderDate
	 *        De datum waarop het order werd geplaatst
	 * @param items
	 *        De bestelde items
	 */
	public StockOrder(Stock stockLocation, TimeStamp orderDate, List<WarehouseItem> items) throws IllegalArgumentException {
		if (stockLocation == null) throw new IllegalArgumentException("Invalid argument");
		if (orderDate == null) throw new IllegalArgumentException("Invalid argument");
		if (items == null) throw new IllegalArgumentException("Invalid argument");
		
		this.stockLocation = stockLocation;
		this.orderDate = orderDate;
		this.arrivalDate = orderDate.calcForwardTo(6, 0).calcForwardTo(6, 0);
		this.arrived = false;
		
		this.amount = items.size();

		this.items = new ArrayList<WarehouseItem>();
		this.items.addAll(items);
		
		this.arrivalEvent = new ArrivalEvent(this.getArrivalDate());
	}
	
	/**
	 * Een methode die, als het order aangekomen is, de items van het order
	 * bij de stock van het daarvoor voorziene magazijn plaatst
	 * 
	 * @param expirationDate
	 *        De vervaldatum van de items in het aangekomen order
	 * 
	 * @throws IllegalStateException 
	 *         als het order nog niet als aangekomen gemarkeerd is.
	 *         |!this.isArrived()
	 */
	public void processArrival(TimeStamp expirationDate)
			throws IllegalArgumentException {
		if (!this.isArrived())
			throw new IllegalStateException(
					"Om de aankomst van een order te verwerken moet hij eerst zelf weten dat hij aangekomen is,");
		
		if (isArrived()) {
			for (WarehouseItem item : items) {
				if (expirationDate != null)
					item.setExpirationDate(expirationDate);
				stockLocation.addWarehouseItem(item);
			}
		}
	}
	
	/**
	 * Getter voor de locatie waar de items van het order opgeslagen worden
	 * 
	 * @return stockLocation
	 *         Het magazijn waar de items van het order opgeslagen worden als Warehouse
	 */
	public Stock getStockLocation() {
		return stockLocation;
	}
	
	/**
	 * Getter voor het aantal bestelde items
	 * 
	 * @return amount
	 *         Het aantal bestelde items als int
	 */
	public int getAmount() {
		return amount;
	}
	
	/**
	 * Getter voor de datum waarop het order werd geplaatst
	 * 
	 * @return orderDate
	 *         De datum waarop het order werd geplaatst als TimeStamp
	 */
	public TimeStamp getOrderDate() {
		return orderDate;
	}
	
	/**
	 * Getter voor de verwachte leverdatum
	 * 
	 * @return arrivalDate
	 *         De verwachte leverdatum als TimeStamp
	 */
	public TimeStamp getArrivalDate() {
		return arrivalDate;
	}
	
	/**
	 * De methode die aangeeft of het order al ontvangen zou moeten zijn
	 * 
	 * @return true 
	 *         als het order ontvangen zou moeten zijn
	 * @return false 
	 *         als het order nog niet ontvangen zou moeten zijn
	 * 
	 */
	public boolean isArrived() {
		return arrived;
	}

	/**
	 * Setter om aan te geven of het order al ontvangen zou moeten zijn
	 * 
	 * @param arrived
	 *        true als het order ontvangen zou moeten zijn
	 *        false als het order nog niet ontvangen zou moeten zijn
	 */
	public void setArrived(boolean arrived) {
		this.arrived = arrived;
	}
	
	/**
	 * Getter voor de gebeurtenis van het aankomen van het order
	 * 
	 * @return een instantie van Event
	 */
	public Event getArrivalEvent() {
		return arrivalEvent;
	}
	
	@Override
	public int compareTo(StockOrder other) {
		if(this.getOrderDate().compareTo(other.getOrderDate())!=0)
		return this.getOrderDate().compareTo(other.getOrderDate());
		else return System.identityHashCode(this)-System.identityHashCode(other);
	}

	/**
	 * Een inner klasse die het aankomen van het order voorstelt 
	 */
	private class ArrivalEvent extends Event {
		/**
		 * Initialisatie van de gebeurtenis met de verwachte aankomstdatum
		 * 
		 * @param arrivalDate
		 *        De datum waarop het order zou moeten aankomen
		 */
		private ArrivalEvent(TimeStamp arrivalDate) {
			super(arrivalDate);
		}

		@Override
		public void execute() {
			StockOrder.this.setArrived(true);
		}	
		@Override
		public String toString() {
			return " Order of "+ getAmount();
		}
	}
}