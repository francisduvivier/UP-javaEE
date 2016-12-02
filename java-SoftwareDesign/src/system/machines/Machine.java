package system.machines;

import system.repositories.MachineType;
import system.repositories.ResourceType;
import system.scheduling.Schedule;
import system.scheduling.ScheduleResource;

/**
 * Deze klasse is de superklasse van alle soorten machines. Alle machines zijn
 * resources die gebruikt kunnen worden door de Scheduler. Deze implementeren
 * daarom de interface ScheduleResources
 * 
 * @invar ID != null
 * @invar location != null
 * @invar machineType != null
 * 
 * @author SWOP Team 10
 */
public class Machine implements ScheduleResource {
	/**
	 * De locatie van de machine
	 * 
	 * @invar location != null
	 */
	private final Location location;
	/**
	 * Het type machine
	 * 
	 * @invar machineType != null
	 */
	private final MachineType machineType;
	/**
	 * De id van de machine
	 * 
	 * @invar ID != null
	 */
	private final Identifier ID;
	/**
	 * Het uurrooster van de machine
	 */
	private final Schedule schedule;
	
	/**
	 * Constructor voor Machine
	 * 
	 * @param ID
	 *        De id van de machine
	 * @param location
	 *        De locatie van de machine
	 * @param machineType
	 *        Het type machine
	 *        
	 * @pre ID != null
	 * 		De ID moet verschillend zijn van null
	 * @post getId() == ID
	 * @pre location != null
	 * 		De locatie moet verschillend zijn van null
	 * @post getLocation() == location
	 * @pre machineType != null
	 * 		Machinetype moet verschillend zijn van null
	 * @post getMachineType == machineType
	 * 
	 * @throws NullPointerException
	 *         Als de waarde van een van de drie parameters null is
	 */
	public Machine(Identifier ID, Location location, MachineType machineType) throws NullPointerException {
		this.schedule = new Schedule();
		if (ID == null)
			throw new NullPointerException("ID is null.");
		this.ID = ID;
		if (location == null)
			throw new NullPointerException("Location is null.");
		this.location = location;
		if (machineType == null)
			throw new NullPointerException("Resource type is null.");
		this.machineType = machineType;
	}

	/**
	 * Getter voor de id van de machine
	 * 
	 * @return ID
	 *         De id van de machine
	 */
	public Identifier getID() {
		return this.ID;
	}
	
	/**
	 * Getter voor de locatie van de machine
	 * 
	 * @return location
	 *         De locatie van de machine
	 */
	public Location getLocation() {
		return location;
	}

	/**
	 * Een methode om het resourcetype terug te krijgen.
	 * 
	 * @return machineType
	 * 		   Resourcetype is van type machine
	 */
	@Override
	public ResourceType getResourceType() {
		return this.machineType;
	}
	
	/**
	 * Een methode om het schema van een machine te krijgen
	 * 
	 * @return schedule
	 * 		   De planning van een machine
	 */
	@Override
	public Schedule getSchedule() {
		return this.schedule;
	}
}
