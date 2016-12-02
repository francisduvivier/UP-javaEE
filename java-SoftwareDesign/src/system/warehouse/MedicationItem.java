package system.warehouse;

import system.time.TimeStamp;

/**
 * Een klasse die een medicijn voorstelt
 * 
 * @author SWOP Team 10
 */
public class MedicationItem extends ExpirableWarehouseItem {	
	/**
	 * Een variabele die het type medicijn voorstelt
	 */
	private MedicationItemType type;

	/**
	 * Initialisatie van het medicijn
	 * 
	 * @param expirationDate
	 *        De vervaldatum van het medicijn als TimeStamp
	 * @param type
	 *        Het type medicijn als MedicationItemType
	 */
	public MedicationItem(TimeStamp expirationDate, CompositeType type) {
		super(expirationDate);
		
		this.type = (MedicationItemType) type;
	}
	
	/**
	 * Getter voor het type medicijn
	 * 
	 * @return type
	 *         Het type medicijn als MedicationItemType
	 */
	public MedicationItemType getType() {
		return type;
	}
}