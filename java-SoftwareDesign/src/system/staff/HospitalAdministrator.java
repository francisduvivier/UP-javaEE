package system.staff;

import system.repositories.StaffType;
import annotations.SystemAPI;

/**
 * Deze klasse is een subklasse van StaffMember en stelt de
 * ziekenhuisadministrator voor.
 * 
 * @author SWOP Team 10
 */
public class HospitalAdministrator extends StaffMember {
	/**
	 * Constructor voor HospitalAdministrator
	 * 
	 * @param name
	 *            De naam van de ziekenhuisadministrator
	 */
	public HospitalAdministrator(String name) {
		super(name, StaffType.HOSPITAL_ADMINISTRATOR);
	}

	/**
	 * Een toString voor HospitalAdministrator.
	 */
	@Override
	@SystemAPI
	public String toString() {
		return "Hospital Administrator: " + super.getName();
	}
}
