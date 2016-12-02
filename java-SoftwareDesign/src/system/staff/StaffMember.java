package system.staff;

import client.IStaffMember;
import system.campus.CampusId;
import system.repositories.ResourceType;
import system.repositories.StaffType;
import system.scheduling.Schedule;
import system.scheduling.ScheduleResource;
import system.scheduling.ShiftSchedule;
import system.time.TimeStamp;
import annotations.SystemAPI;

/**
 * Deze klasse stelt een personeelslid van het ziekenhuis voor
 * 
 * @invar name != null && name != ""
 * 
 * @author SWOP Team 10
 */
public abstract class StaffMember implements ScheduleResource, IStaffMember {
	/**
	 * Variabele die de naam van het personeelslid voorstelt. 
	 * De naam van elk personeelslid is uniek.
	 */
	private String name;
	/**
	 * Variabele die het type van personeelslid teruggeeft
	 */
	private final StaffType staffType;
	/**
	 * Variabele die het uurrooster van het personeelslid voorstelt
	 */
	private final ShiftSchedule schedule;
	
	private final ShiftTable shiftTable;
	
	/**
	 * Constructor van StaffMember
	 * 
	 * @param name
	 *        De naam van het personeelslid
	 * @param staffType
	 *        Het type van het personeelslid
	 * @throws NullPointerException
	 *         Als de opgegeven naam null of een lege string is
	 */
	public StaffMember(String name, StaffType staffType) throws NullPointerException {
		this.shiftTable = new ShiftTable();
		this.schedule = new ShiftSchedule(this.shiftTable);
		if (name == null || name.equals(""))
			throw new NullPointerException("Naam is null.");
		this.name = name;
		this.staffType = staffType;
	}

	/**
	 * Getter voor de naam van het personeelslid
	 * 
	 * @return name
	 *         De naam van het personeelslid
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Een toString voor StaffMember.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return name;
	}

	/**
	 * Een methode om het stafftype te krijgen.
	 */
	@Override
	public ResourceType getResourceType() {
		return this.staffType;
	}
	
	/**
	 * Getter voor de schedule van de staff member
	 */
	@Override
	public Schedule getSchedule() {
		return this.schedule;
	}
	
	/**
	 * Getter voor de shiftTable van de staff member
	 * @return shiftTable van de staff member
	 */
	public ShiftTable getShiftTable() {
		return this.shiftTable;
	}
	
	/**
	 * Methode om een shift toe te voegen aan de shiftTable van de staff member
	 * @param campusId
	 * 			Campus waar de shift doorgaat
	 * @param shift
	 * 			Shift die plaatsvindt
	 */
	public void addShift(CampusId campusId, Shift shift) {
		this.shiftTable.add(campusId, shift);
	}
	
	/**
	 * Geeft waar terug als dit personeelslid aanwezig was op de gegeven campus
	 * tussen timeStart en timeStop. Maakt op dit moment gebruik van de shiften
	 * maar er kunnen in principe ook andere parameters in rekening worden gebracht
	 * zoals het schedule.
	 * 
	 * @param id
	 * 			De campus waarop dit personeelslid aanwezig zou moeten zijn
	 * @param timeStart
	 * 			De begintijdvan waar we moeten zien
	 * @param timeStop
	 * 			De eindtijd tot waar we moeten zien
	 * @return
	 * 			waar als het personeelslid op een moment tussen timeStart en
	 * 			timeStop aanwezig was op de campus met CampusId id
	 */
	public boolean wasOnCampus(CampusId id, TimeStamp timeStart, TimeStamp timeStop) {
		TimeStamp working = this.getShiftTable().firstAvailable(timeStart, id);
		
		return (this.getShiftTable().isWorking(timeStart, id) ||
				working != null && 
				working.compareTo(timeStop) <= 0);
	}
}
