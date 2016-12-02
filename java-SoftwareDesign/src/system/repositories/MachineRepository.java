package system.repositories;

import java.util.ArrayList;
import java.util.List;

import system.exceptions.IllegalAccessException;
import system.machines.Identifier;
import system.machines.Location;
import system.machines.Machine;
import system.scheduling.ScheduleResource;


/**
 * Deze klasse houdt alle machines van het ziekenhuis bij.
 * 
 * @author SWOP Team 10
 */
public class MachineRepository implements ResourceRepository {
	/**
	 * Lijst van alle machines van het ziekenhuis
	 */
	private final List<Machine> machines;

	/**
	 * Constructor van MachineRepository
	 */
	public MachineRepository() {
		machines = new ArrayList<Machine>();
	}
	
	/**
	 * Getter voor de verzameling van ziekenhuismachines
	 * 
	 * @return machineRepository
	 *         Collectie van machines van het ziekenhuis
	 */
	public List<Machine> getMachines() {
		return this.machines;
	}
	
	/**
	 * Methode om een machine aan het ziekenhuis toe te voegen
	 * 
	 * @param ID
	 *        De ID van de machine
	 * @param floor
	 *        De verdieping waarop de machine staat
	 * @param room
	 *        De kamer waarin de machine staat
	 * @param machineType
	 *        Het type machine
	 */
	public void addMachine(Identifier ID, int floor, int room, MachineType machineType) {
		if (isExistingMachineID(ID))
			throw new IllegalArgumentException(
			"ID staat al in machine repository.");
		Location location = new Location(floor, room);
		Machine machine = new Machine(ID, location, machineType);
		machines.add(machine);
	}

	/**
	 * Nodig voor het plannen
	 * Methode die een collectie van machines van een bepaald type teruggeeft
	 * 
	 * @param type
	 *        Het type machine
	 * @return resourceList
	 *         De verzameling machines van het opgegeven type
	 */
	@Override
	public List<ScheduleResource> getResources(ResourceType type) throws NullPointerException{
		if (type == null)
			throw new NullPointerException("Type is null.");
		List<ScheduleResource> resourceList = new ArrayList<ScheduleResource>();
		for (Machine thisMachine : machines) {
			if (thisMachine.getResourceType().equals(type))
				resourceList.add(thisMachine);
		}
		return resourceList;
	}
	
	/**
	 * Kijkt of er nog geen machine in de hospital bestaat met de opgegven ID.
	 * 
	 * @param ID
	 *            de op te zoeken ID
	 * @return true als de ID al voorkomt, anders false.
	 */
	public boolean isExistingMachineID(Identifier ID) throws IllegalAccessException {
		for (Machine machine : getMachines()) {
			if (ID.equals(machine.getID()))
				return true;
		}
		return false;
	}
}
