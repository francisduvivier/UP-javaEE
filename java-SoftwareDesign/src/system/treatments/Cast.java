package system.treatments;

import system.patients.Diagnosis;
import system.scheduling.EventType;
import system.scheduling.Priority;
import system.time.TimeDuration;
import system.warehouse.Plaster;
import system.warehouse.Warehouse;
import system.warehouse.stock.StockType;
import system.warehouse.stock.condition.HasPlasterCondition;
import annotations.SystemAPI;


/**
 * Deze klasse is een subklasse van de klasse Treatment 
 * en stelt een gipsverband voor.
 * 
 * @invar bodyPart != null
 * @invar durationInDays >= 1
 * 
 * @author SWOP Team 10
 */
public class Cast extends Treatment {
	/**
	 * Een constante die de duur van het plaatsen van een gipsverband bijhoudt
	 */
	private static final TimeDuration DURATIONOFCREATION = TimeDuration.hours(2);
	/**
	 * Variabele die het lichaamsdeel waarrond het gipsverband wordt geplaatst, voorstelt.
	 */
	private String bodyPart;
	/**
	 * Variabele die aangeeft hoelang de patient het gipsverband moet dragen.
	 */
	private int durationInDays;

	/**
	 * Constructor voor Cast
	 * 
	 * @param diagnosis
	 *        De diagnose op basis waarop van het gipsverband is geplaatst
	 * @param bodyPart
	 *        Het lichaamsdeel waarrond het gipsverband is geplaatst
	 * @param durationInDays
	 *        Het aantal dagen dat de patient het gipsverband moet dragen
	 */
	public Cast(Diagnosis diagnosis, String bodyPart, int durationInDays) {
		super(diagnosis, DURATIONOFCREATION,EventType.CAST, Priority.NORMAL);
		setBodyPart(bodyPart);
		setDurationInDays(durationInDays);
	}
	
	/**
	 * Constructor voor Cast met een prioriteit
	 * 
	 * @param diagnosis
	 * 		  De diagnose op basis waarop van het gipsverbend is geplaatst
	 * @param priority
	 * 		  De prioriteit van de cast
	 * @param bodyPart
	 * 		  Het lichaamsdeel waarrond het gipsverband is geplaatst
	 * @param durationInDays
	 * 		  Het aantal dagen dat de patient het gipsverband moet dragen
	 */
	public Cast(Diagnosis diagnosis, Priority priority, String bodyPart, int durationInDays) {
		super(diagnosis, DURATIONOFCREATION,EventType.CAST, priority);
		setBodyPart(bodyPart);
		setDurationInDays(durationInDays);
	}

	/**
	 * Getter voor duur van het dragen van het gipsverband
	 * 
	 * @return durationInDays
	 *         Het aantal dagen dat de patient het gipsverband moet dragen
	 */
	public int getKeepDurationInDays() {
		return durationInDays;
	}

	/**
	 * Setter voor de duur van het dragen van het gipsverband 
	 * 
	 * @param durationInDays
	 *        Het aantal dagen dat de patient het gipsverban
	 * @pre durationInDays >= 1
	 * @post this.getDurationInDays() == durationInDays
	 * @throws IllegalArgumentException
	 *         Als de opgegeven duur niet groter is dan 0
	 */
	public void setDurationInDays(int durationInDays)
			throws IllegalArgumentException {
		if (durationInDays >= 1)
			this.durationInDays = durationInDays; // We nemen aan dat een dokter voor minstens 1 dag voorschrijft
		else
			throw new IllegalArgumentException();
	}

	/**
	 * Setter voor het gebroken lichaamsdeel
	 * 
	 * @param bodyPart
	 *        Het lichaamsdeel waarrond het gipsverband is geplaatst
	 * @pre bodyPart != null && bodyPart != ""
	 * 		| het meegegeven lichaamsdeel moet een waarde hebben verschillend van de lege string of null
	 * @post this.getBodyPart() == bodyPart
	 * @throws NullPointerException
	 *         Als de waarde van de parameter bodyPart null of een lege string is
	 */
	public void setBodyPart(String bodyPart) throws NullPointerException{
		if (bodyPart == null || bodyPart.equals(""))
			throw new NullPointerException("Body part is null.");
		this.bodyPart = bodyPart;
	}

	/**
	 * Een toString methode voor Cast.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Priority: "+this.getPriority().toString()+
		" - Body Part: " + bodyPart + " - Duration: " + durationInDays;
	}
	
	/**
	 * Methode die aangeeft of deze behandeling gepland kan worden
	 * 
	 * @return true
	 *         Als er nog gips in het magazijn is
	 *         false
	 *         Als er geen gips meer in het magazijn is
	 */
	@Override
	public boolean canBeScheduled(Warehouse warehouse) {
		return (warehouse.getStockList().conditionIsTrue(new HasPlasterCondition()));
	}

	/**
	 * Een methode om het warenhuis te updaten met de nieuw.
	 * 
	 * @param warehouse
	 * 		  Het te updaten warenhuis
	 * @param inverse
	 * 		  Booleanse waarde om ongedaan te maken
	 */
	@Override
	public void updateWarehouse(Warehouse warehouse, boolean inverse) {
		if (inverse)
			warehouse.getStockList().getDefaultStock(StockType.PLASTER).addWarehouseItem(new Plaster());
		else
			warehouse.getStockList().getDefaultStock(StockType.PLASTER).removeWarehouseItem();
	}

	/**
	 * Getter voor de body part van een cast
	 * 
	 * @return bodyPart
	 */
	public String getBodyPart() {
		return this.bodyPart;
	}
}
