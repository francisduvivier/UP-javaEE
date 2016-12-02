package system.treatments;
import system.patients.Diagnosis;
import system.repositories.MachineType;
import system.scheduling.EventType;
import system.scheduling.Priority;
import system.time.TimeDuration;
import system.warehouse.Warehouse;
import annotations.SystemAPI;

/**
 * Deze klasse is een subklasse van Treatment
 * en stelt een operatie voor.
 * 
 * @invar description != null
 * @invar diagnosis != null
 * 
 * @author SWOP Team 10
 */
public class Surgery extends Treatment {
	/**
	 * Een constante die de duur van de operatie bijhoudt
	 */
	private static final TimeDuration DURATION = TimeDuration.hours(3);
	/**
	 * Een variabele die de operatie beschrijft
	 */
	private String description;
	
	/**
	 * Constructor voor Surgery
	 * 
	 * @param diagnosis
	 *        De diagnose op basis waarvan de operatie wordt uitgevoerd
	 * @param description
	 *        De beschrijving van de operatie
	 */
	public Surgery(Diagnosis diagnosis, String description) {
		super(diagnosis, DURATION, EventType.SURGERY,Priority.NORMAL);
		neededResources.add(MachineType.SURGICAL_EQUIPMENT);
		setDescription(description);
	}

	public Surgery(Diagnosis diagnosis, Priority priority, String description) {
		super(diagnosis, DURATION, EventType.SURGERY,priority);
		neededResources.add(MachineType.SURGICAL_EQUIPMENT);
		setDescription(description);
	}
	/**
	 * Getter voor de beschrijving van de operatie
	 * 
	 * @return description
	 *         De beschrijving van de operatie
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setter voor de beschrijving van de operatie
	 * 
	 * @param description
	 *        De beschrijving van de operatie
	 * @pre description != null && description != ""
	 * 		| de beschrijving moet verschillend zijn van null of de lege string
	 * @post this.getDescription() == description
	 * @throws NullPointerException
	 *         Als de opgegeven beschrijving null of een lege string is
	 */
	public void setDescription(String description) throws NullPointerException {
		if (description == null || description.equals(""))
			throw new NullPointerException("Beschrijving is null.");
		this.description = description;
	}
	
	/**
	 * toString methode voor Surgery.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Priority: "+this.getPriority().toString()+
		" - Description: " + description;
	}
	
	/**
	 * Methode die aangeeft of deze behandeling gepland kan worden
	 * 
	 * @return true
	 *         Kan altijd gepland worden. Er zijn geen medicijnen of gips nodig.
	 */
	@Override
	public boolean canBeScheduled(Warehouse warehouse) {
		return true;
	}
	
	@Override
	public void updateWarehouse(Warehouse warehouse, boolean inverse) {
		return;
	}
}
