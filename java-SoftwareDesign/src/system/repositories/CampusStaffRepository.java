package system.repositories;

import java.util.List;

import system.exceptions.IllegalOperationException;
import system.staff.StaffMember;

/**
 * Deze klasse houdt alle campus staff members bij.
 * 
 */

public class CampusStaffRepository extends StaffRepository {
	
	/**
	 * Methode om een personeelslid toe te voegen aan de CampusStaffRepository.
	 * 
	 * 
	 * @param 	name
	 *        	De naam van het personeelslid
	 * @param 	type
	 * 			Het type staff member
	 * @result 	het toegevoegde personeelslid
	 * @throws 	IllegalOperationException
	 *         	Deze methode gooit een IllegalOperationException wanneer het personeelslid van het type Doctor of HospitalAdministrator is
	 *         	aangezien deze soorten personeelsleden niet tot een specifieke campus behoren 
	 */	
	@Override
	public StaffMember addStaffMember(StaffType type, String name) {
		if (!type.isCampusStaff()) {
			throw new IllegalOperationException();
		}
		
		return super.addStaffMember(type, name);
	}
	
	/**
	 * Getter voor een lijst van een bepaald soort personeelsleden van de CampusStaffRepository.
	 * 
	 * @param type
	 *        Het soort toe te voegen personeelslid
	 * @return members
	 *         De lijst van personeelsleden
	 * @throws IllegalOperationException
	 * 		   Deze methode gooit een IllegalOperationException wanneer het personeelslid van het type Doctor of HospitalAdministrator is
	 *         aangezien deze soorten personeelsleden niet tot een specifieke campus behoren 
	 */
	@Override
	public List<StaffMember> getStaffMembers(StaffType type) {
		if (!type.isCampusStaff()) {
			throw new IllegalOperationException();
		}
		
		return super.getStaffMembers(type);
	}
}
