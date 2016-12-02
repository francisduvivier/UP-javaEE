package system.repositories;

import system.staff.Doctor;
import system.staff.HospitalAdministrator;
import system.staff.Nurse;
import system.staff.StaffMember;
import system.staff.WarehouseManager;
import annotations.SystemAPI;

/**
 * Deze enumklasse houdt de verschillende soorten personeelsleden bij.
 * 
 * @author SWOP Team 10
 */
@SystemAPI
public enum StaffType implements ResourceType {
	DOCTOR(false) {
		@Override
		public StaffMember createStaffMember(String name) {
			Doctor doctor = new Doctor(name);
			return doctor;
		}
	},
	HOSPITAL_ADMINISTRATOR(false) {
		@Override
		public StaffMember createStaffMember(String name) {
			HospitalAdministrator administrator = new HospitalAdministrator(name);
			return administrator;
		}
	},
	NURSE(true) {
		@Override
		public StaffMember createStaffMember(String name) {
			Nurse nurse = new Nurse(name);
			return nurse;
		}
	},
	WAREHOUSE_MANAGER(true) {
		@Override
		public StaffMember createStaffMember(String name) {
			WarehouseManager manager = new WarehouseManager(name);
			return manager;
		}
	};
	
	private final boolean campusStaff;
	
	private StaffType(boolean campusStaff) {
		this.campusStaff = campusStaff;
	}
	
	public final boolean isCampusStaff() {
		return campusStaff;
	}
	
	public abstract StaffMember createStaffMember(String name);
}
