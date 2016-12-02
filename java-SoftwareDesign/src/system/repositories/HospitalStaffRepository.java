package system.repositories;

import java.util.List;

import system.exceptions.IllegalOperationException;
import system.staff.StaffMember;

/**
 * Deze klasse houdt de staff members van het ziekenhuis bij.
 */

public class HospitalStaffRepository extends StaffRepository {
	private boolean hasHospitalAdministrator;
	
	/**
	 * Een constructor voor HospitalStaffRepository.
	 */
	public HospitalStaffRepository() {
		this.addStaffMember(StaffType.HOSPITAL_ADMINISTRATOR, "Tom Holvoet");
		hasHospitalAdministrator = true;
	}
	
	/**
	 * Methode om een personeelslid toe te voegen aan de HospitalStaffRepository.
	 * 
	 * 
	 * @param name
	 *        De naam van het personeelslid
	 * @result het toegevoegde personeelslid
	 * @throws IllegalOperationException
	 *         Deze methode gooit een IllegalOperationException wanneer het personeelslid van het type Nurse of WarehouseManager is
	 *         aangezien deze soorten personeelsleden tot een specifieke campus behoren en niet op andere campussen kunnen worden ingezet
	 *         of wanneer men een ziekenhuisadministrator wil toevoegen maar er al een bestaat
	 */	
	@Override
	public StaffMember addStaffMember(StaffType type, String name) {
		if (type.isCampusStaff()) {
			throw new IllegalOperationException();
		}
		if (type == StaffType.HOSPITAL_ADMINISTRATOR && hasHospitalAdministrator) {
			throw new IllegalOperationException();
		}
		
		return super.addStaffMember(type, name);
	}
	
	/**
	 * Getter voor een lijst van een bepaald soort personeelsleden van de HospitalStaffRepository.
	 * 
	 * @param type
	 *        Het soort toe te voegen personeelslid
	 * @return members
	 *         De lijst van personeelsleden
	 * @throws IllegalOperationException
	 * 		   Deze methode gooit een IllegalOperationException wanneer het personeelslid van het type Nurse of WarehouseManager is
	 *         aangezien deze soorten personeelsleden tot een specifieke campus behoren en niet op andere campussen kunnen worden ingezet 
	 */
	@Override
	public List<StaffMember> getStaffMembers(StaffType type) {
		if (type.isCampusStaff()) {
			throw new IllegalOperationException();
		}
		
		return super.getStaffMembers(type);
	}
}
