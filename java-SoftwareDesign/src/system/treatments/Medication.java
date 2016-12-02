package system.treatments;

import java.util.ArrayList;
import java.util.List;

import system.patients.Diagnosis;
import system.scheduling.EventType;
import system.scheduling.Priority;
import system.time.TimeDuration;
import system.warehouse.MedicationItem;
import system.warehouse.MedicationItemType;
import system.warehouse.Warehouse;
import system.warehouse.stock.StockType;
import system.warehouse.stock.condition.HasMedicationItemsCondition;
import annotations.SystemAPI;

/**
 * Deze klasse is een subklasse van Treatment
 * en stelt een medicijnenkuur voor.
 * 
 * @invar description != null
 * @invar medicationItems != null
 * 
 * @author SWOP Team 10
 */
public class Medication extends Treatment {
	/**
	 * Variabele die de medicijnenkuur beschrijft
	 */
	private final String description;
	/**
	 * Variabele die aangeeft of het een medicijnenkuur is met zachte werking
	 */
	private final boolean sensitive;
	/**
	 * Verzameling van alle medicijnen die voor deze medicijnenkuur voorgeschreven zijn
	 */
	private final List<MedicationItemType> medicationItems;
	
	/**
	 * Constructor voor Medication
	 * 
	 * @param diagnosis
	 *        De diagnose op basis waarvan deze medicijnenkuur is voorgeschreven
	 * @param description
	 *        Een beschrijving voor de medicijnenkuur
	 * @param sensitive
	 *        De aard van de werking van de medicijnenkuur
	 * @param medicationItems
	 * 		  Lijst van medication items
	 * @throws NullPointerException
	 *         Als de opgegeven beschrijving null of een lege string is
	 */
	public Medication(Diagnosis diagnosis, String description, boolean sensitive,
			List<MedicationItemType> medicationItems) {
		super(diagnosis, TimeDuration.minutes(sensitive ? 20 : 10),EventType.MEDICATION,Priority.NORMAL);
		if (description == null || description.equals(""))
			throw new NullPointerException("Beschrijving is null.");
		if (medicationItems == null)
			throw new NullPointerException("MedicationItems zijn null.");
		this.description = description;
		this.sensitive = sensitive;
		this.medicationItems = medicationItems;
	}
	
	/**
	 * Initialisatie van een Medication object met prioriteit
	 * 
	 * @param diagnosis
	 * 			De diagnose op basis waarvan deze medicijnenkuur is voorgeschreven
	 * @param priority
	 * 			De prioriteit van de medicatie
	 * @param description
	 * 			Een beschrijving voor de medicijnenkuur
	 * @param sensitive
	 * 			De aard van de werking van de medicijnenkuur
	 * @param medicationItems
	 * 			Lijst van medication items
	 */
	public Medication(Diagnosis diagnosis, Priority priority, String description, boolean sensitive,
			List<MedicationItemType> medicationItems) {
		super(diagnosis, TimeDuration.minutes((sensitive ? 20 : 10)),EventType.MEDICATION,priority);
		if (description == null || description.equals(""))
			throw new NullPointerException("Beschrijving is null.");
		if (medicationItems == null)
			throw new NullPointerException("MedicationItems zijn null.");
		this.description = description;
		this.sensitive = sensitive;
		this.medicationItems = medicationItems;
	}
	
	/**
	 * Methode die aangeeft of de medicijnenkuur een zachte werking heeft
	 * 
	 * @return true
	 *         Als de medicijnenkuur een zachte werking heeft
	 *         false
	 *         Als de medicijnenkuur geen zachte werking heeft
	 */
	public boolean isSensitive(){
		return sensitive;
	}
	
	/**
	 * Getter voor de beschrijving van de medicijnenkuur
	 * 
	 * @return description
	 *         De beschrijving van de medicijnenkuur
	 */
	public String getDescription() {
		return description;
	}
	
	/**
	 * toString methode voor Medication
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Priority: "+this.getPriority().toString()+
		" - Description: " + description + " - Sensitive: " + sensitive;
	}
	
	/**
	 * Methode die aangeeft of deze behandeling gepland kan worden
	 * 
	 * @return true
	 *         Als de medicijnen in het magazijn aanwezig zijn
	 *         false
	 *         Als een of meerdere medicijnen niet meer in het magazijn aanwezig zijn 
	 */
	@Override
	public boolean canBeScheduled(Warehouse warehouse) {
		return (warehouse.getStockList().conditionIsTrue(new HasMedicationItemsCondition(this.medicationItems)));
	}
	
	/**
	 * Lijst van verwijderde medicatie items
	 */
	private List<MedicationItem> removedMedicationItems;
	
	/**
	 * Een methode om het warenhuis te updaten met de nieuwe medication items
	 */
	@Override
	public void updateWarehouse(Warehouse warehouse, boolean inverse) {
		if (inverse) {
			if (this.removedMedicationItems != null)
				for (MedicationItem item : removedMedicationItems)
					warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).addWarehouseItem(item);
		} else {
			this.removedMedicationItems = new ArrayList<MedicationItem>();
			for (MedicationItemType type : medicationItems) {
				MedicationItem item = (MedicationItem) warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).removeWarehouseItem(type);
				this.removedMedicationItems.add(item);
			}
		}
	}
}
