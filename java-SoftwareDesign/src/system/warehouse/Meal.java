package system.warehouse;

import system.time.TimeStamp;

/**
 * Een klasse die een maaltijd voorstelt
 * 
 * @author SWOP Team 10
 */
public class Meal extends ExpirableWarehouseItem {
	
	/**
	 * Initialisatie van de maaltijd met een vervaldatum
	 * 
	 * @param expirationDate
	 *        De vervaldatum van de maaltijd als TimeStamp
	 */
	public Meal(TimeStamp expirationDate) {
		super(expirationDate);
	}
}