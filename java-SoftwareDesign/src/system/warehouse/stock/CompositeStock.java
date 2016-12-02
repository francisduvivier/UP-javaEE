package system.warehouse.stock;

import java.util.SortedSet;

import system.time.Time;
import system.warehouse.CompositeType;
import system.warehouse.StockOrder;
import system.warehouse.WarehouseItem;

/**
 * Een klasse die een samengestelde voorraad voorstelt. De samengestelde voorraad
 * kan onderscheden worden door een enum die CompositeType implementeert. 
 *
 */

public abstract class CompositeStock extends Stock {

	/**
	 * Maakt een nieuwe samengestelde stock aan.
	 * 
	 * @param hospitalTime
	 * 			Tijd in de hospital
	 * @param type
	 * 			Type van deze stock
	 */
	public CompositeStock(Time hospitalTime, StockType type) {
		super(hospitalTime, type);
	}
	
	/**
	 * Verwijdert een warehouse item.
	 * 
	 * @param type
	 * 			Verwijdert dit type
	 * @return
	 * 			Het verwijderde item
	 */
	public abstract WarehouseItem removeWarehouseItem(CompositeType type);
	
	/**
	 * 
	 * @param type
	 * 			grootte van de voorraad van dit type
	 * @return
	 * 			de grootte van de voorraad
	 */
	public abstract int getStockSize(CompositeType type);
	
	/**
	 * 
	 * @param type
	 * 			het type bestellingen
	 * @return
	 * 			de bestellingen van bovenstaand type
	 */
	public abstract SortedSet<StockOrder> getOrders(CompositeType type);
	
	/**
	 * 
	 * @return
	 * 		De gearriveerde bestellingen
	 * 			
	 */
	public abstract SortedSet<StockOrder> getAllArrivedOrders();
	/**
	 * Getter voor de meest recent geplaatste orders van een bepaald type medicijnen
	 * 
	 * @param amount
	 *        Het aantal geplaatste orders
	 * @param type
	 *        Het type samengesteld item
	 * @return latestOrders
	 *         De de meest recent geplaatste orders van het opgegeven type medicijnen
	 * @throws IllegalArgumentException
	 *         Als het opgevraagde aantal niet groter is dan 0
	 */
	public SortedSet<StockOrder> getLatestOrders(int amount, CompositeType type) throws IllegalArgumentException {
		return getLatestOrders(amount, getOrders(type));
	}
	/**
	 * Getter voor de orders van een bepaald type medicijnen die gearriveerd moeten zijn 
	 * 
	 * @param type
	 *        Het type samengesteld item
	 * @return orders
	 *         De orders van het opgegeven type medicijnen die gearriveerd moeten zijn
	 */
	public SortedSet<StockOrder> getArrivedOrdersByType(CompositeType type) {
		return filterArrivedOrders(getOrders(type));
	}
	/**
	 * Getter voor het aantal bestelde medicijnen van een bepaald type
	 * 
	 * @param type
	 *        Het type samengesteld item waarvan het totaal aantal bestelde samengestelde items wordt gevraagd
	 * @return nb
	 *         Het totaal aantal bestelde medicijnen van het gevraagde type
	 */
	public int getNbItemsAlreadyOrdered(CompositeType type) {
		return getNbItemsAlreadyOrdered(getOrders(type));
	}
	
}
