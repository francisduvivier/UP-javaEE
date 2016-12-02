package system.repositories;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import system.scheduling.ScheduleResource;
import system.staff.StaffMember;

/**
 * Deze klasse houdt al het personeel van het ziekenhuis bij
 * 
 * @author SWOP Team 10
 */
public abstract class StaffRepository implements ResourceRepository {
	/**
	 * Lijst van al het personeel van het ziekenhuis
	 */
	protected final List<StaffMember> staff;

	/**
	 * Constructor van StaffRepository
	 */
	public StaffRepository() {
		staff = new ArrayList<StaffMember>();
	}

	/**
	 * Getter voor het ziekenhuispersoneel
	 * 
	 * @return staff
	 *         De verzameling van ziekenhuispersoneel
	 */
	public List<StaffMember> getStaff() {
		return Collections.unmodifiableList(this.staff);
	}

	/**
	 * Methode om een bepaald soort personeelslid aan het ziekenhuis toe te voegen
	 * 
	 * @param type
	 *        Het soort toe te voegen personeelslid
	 * @param name
	 *        De naam van het toe te voegen personeelslid
	 */
	public StaffMember addStaffMember(StaffType type, String name) {
		StaffMember staffMember = type.createStaffMember(name);
		staff.add(staffMember);
		return staffMember;
	}
	
	/**
	 * Getter voor een lijst van een bepaald soort personeelsleden van het ziekenhuis
	 * 
	 * @param type
	 *        Het soort toe te voegen personeelslid
	 * @return members
	 *         De lijst van personeelsleden
	 */
	public List<StaffMember> getStaffMembers(StaffType type) {
		List<StaffMember> members = new ArrayList<StaffMember>();
		
		for (StaffMember member : staff)
			if (member.getResourceType() == type)
				members.add(member);
		
		return members;
	}
	
	/**
	 * Nodig voor het plannen
	 * Methode die een collectie van personeelsleden van een bepaald type teruggeeft
	 * 
	 * @param type
	 *        Het type personeelslid
	 * @return resourceList
	 *         De verzameling personeelsleden van het opgegeven type
	 */
	@Override
	public List<ScheduleResource> getResources(ResourceType type) {
		List<ScheduleResource> resourceList = new ArrayList<ScheduleResource>();
		for (StaffMember thisStaff : staff) {
			if (thisStaff.getResourceType().equals(type))
				resourceList.add(thisStaff);
		}
		return resourceList;
	}
}