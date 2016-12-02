package system.warehouse;

import system.time.TimeStamp;


/**
 * Een klasse die een order voorstelt
 * 
 * @author SWOP Team 10
 */
public abstract class WarehouseItem {
	/**
	 * Een variabele die de vervaldatum van het item voorstelt
	 */
	private TimeStamp expirationDate;

	/**
	 * Initialisatie van het item met een vervaldatum
	 * 
	 * @param expirationDate
	 *        De vervaldatum van het item als TimeStamp
	 */
	public WarehouseItem(TimeStamp expirationDate) {
		this.expirationDate = expirationDate;
	}

	/**
	 * Getter voor de vervaldatum van het item
	 * 
	 * @return expirationDate
	 *         De vervaldatum van het item als TimeStamp
	 */
	public TimeStamp getExpirationDate() {
		return expirationDate;
	}

	/**
	 * Setter voor de vervaldatum van het item
	 * 
	 * @return expirationDate
	 *         De vervaldatum van het item als TimeStamp
	 */
	public void setExpirationDate(TimeStamp expirationDate) {
		this.expirationDate = expirationDate;
	}
}
