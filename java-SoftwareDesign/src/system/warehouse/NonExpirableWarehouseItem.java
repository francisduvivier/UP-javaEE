package system.warehouse;

import system.time.TimeStamp;

/**
 * Een klasse die een order voorstelt
 * 
 * @author SWOP Team 10
 */
public class NonExpirableWarehouseItem extends WarehouseItem {

	/**
	 * Initialisatie van een item dat niet vervalt
	 */
	public NonExpirableWarehouseItem() {
		super(TimeStamp.END_OF_DAYS);
	}	
}
