package system.warehouse;

import system.time.TimeStamp;

/**
 * Een klasse die een order voorstelt
 * 
 * @author SWOP Team 10
 */
public class ExpirableWarehouseItem extends WarehouseItem {

	/**
	 * Initialisatie van een item dat vervalt
	 * 
	 * @param expirationDate
	 *        De vervaldatum van het item
	 */
	public ExpirableWarehouseItem(TimeStamp expirationDate) {
		super(expirationDate);
	}
}
