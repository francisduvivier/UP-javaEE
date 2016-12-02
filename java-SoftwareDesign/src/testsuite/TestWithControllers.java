package testsuite;

import client.IStaffMember;
import system.campus.Campus;
import system.campus.Hospital;
import system.controllers.AdministratorController;
import system.controllers.DoctorController;
import system.controllers.NurseController;
import system.controllers.SessionController;
import system.controllers.WarehouseManagerController;
import system.patients.Patient;
import system.repositories.MachineType;
import system.time.TimeDuration;
import system.time.TimeStamp;

/**
 * Deze klasse dient gebruikt te worden door testklasses die alle of de meeste
 * controllers nodig hebben en een hospital met daarin al 1 patient, 1 van elk
 * personeeltype en 1 van elke machine aanwezig.
 * 
 * @author SWOP groep 10
 * 
 */
//TODO zorgen dat de campussen bij nurse, patient en warehouse manager kloppen.
public abstract class TestWithControllers {
	protected IStaffMember testHospitalAdmin;
	protected SessionController sessionController;
	protected AdministratorController adminController;
	protected DoctorController doctorController;
	protected NurseController nurseController;
	protected WarehouseManagerController warehouseController;
	protected IStaffMember testC1Nurse1;
	protected IStaffMember testC2Nurse1;//The C stands for Campus
	protected IStaffMember testC1Doctor1;
	protected IStaffMember testC2Doctor1;// These doctors have are SWITCHERS
	protected IStaffMember testC1Doctor2;
	protected IStaffMember testC2Doctor2; // Begint in campus 1, dan 2
	protected IStaffMember testC1Doctor3;
	protected Patient testC1Patient1,testC2Patient1;
	protected IStaffMember testC1WarehouseManager1;
	protected IStaffMember testC2WarehouseManager1;
	protected Hospital hospital;
	
	/**
	 * Deze methode kan gebruikt worden om een standaard configuratie van een
	 * hospital te bekomen, dat wil zeggen dat staff, controllers, patienten en
	 * machines toegevoegd worden.
	 */
	protected void testInit() {
		sessionController = new SessionController();
		hospital = (Hospital) sessionController.getBigHospital();
		addStaffAndControllers();
		addMachines();
		addPatients();
	}
	
	protected void addPatients() {
		sessionController.login(testC1Nurse1, getCampus(0));
		testC1Patient1 = nurseController.registerNewPatient("The Campus 1 Patient");
		sessionController.logOut();
		sessionController.login(testC2Nurse1, getCampus(1));
		testC2Patient1 = nurseController.registerNewPatient("The Campus 2 Patient");
		sessionController.logOut();
	}
	/**
	 * Deze methode voegt de nodige machines aan de hospital toe
	 */
	
	protected void addMachines() {
		sessionController.login(testHospitalAdmin, getCampus(0));
		int i=1;
		for(MachineType machineType: MachineType.values()){
		adminController.addMachine(i++, 1, 1, machineType);
		}
		sessionController.logOut();
		sessionController.login(testHospitalAdmin, getCampus(1));
		for(MachineType machineType: MachineType.values()){
		adminController.addMachine(i++, 1, 1, machineType);
		}
		sessionController.logOut();
	}
	/**
	 * Deze  methode voegt de staff toe en instantieerd ook de controllers toe die hiervoor nodig zijn
	 * @pre hospital is niet null
	 * @post toegevoegd: een admin, 2 dokters, 2 nurses en 2 warehouse managers
	 * @post toegevoegd: sessionController en administratorController
	 */
	
	protected void addStaffAndControllers() {
		testHospitalAdmin = sessionController.getHospitalAdministrator();
		sessionController.login(testHospitalAdmin,getCampus(0));
		adminController = new AdministratorController(sessionController);
		testC1Doctor1 = adminController.addDoctor("The Campus 1 Doctor", true);
		testC1Doctor2 = adminController.addDoctor("Second Campus 1 Doctor");
		testC1Doctor3 = adminController.addDoctor("Third Campus 1 Doctor");
		adminController.addShift(testC1Doctor2,getCampus(0),9,0,12,0);
		adminController.addShift(testC1Doctor2,getCampus(1), 12, 0, 17, 0);
		adminController.addShift(testC1Doctor3, getCampus(0), 9, 0, 17, 0);
		
		testC1Nurse1 = adminController.addNurse("The Campus 1 Nurse",9,0,17,0);
		testC1WarehouseManager1=adminController.addWarehouseManager("The Campus 1 Warehouse Manager");
		sessionController.logOut();
		sessionController.login(testHospitalAdmin,getCampus(1));
		testC2Doctor1=adminController.addDoctor("The Campus 2 Doctor", true);
		testC2Doctor2 = adminController.addDoctor("Second Campus 2 Doctor");
		adminController.addShift(testC2Doctor2,getCampus(1),9,0,12,0);
		adminController.addShift(testC2Doctor2,getCampus(0), 12, 0, 17, 0);
		testC2Nurse1 = adminController.addNurse("The Campus 2 Nurse",9,0,17,0);
		testC2WarehouseManager1=adminController.addWarehouseManager("The Campus 2 Warehouse Manager");
		sessionController.logOut();
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController = new DoctorController(sessionController);
		sessionController.logOut();
		sessionController.login(testC1Nurse1, getCampus(0));
		nurseController = new NurseController(sessionController);
		sessionController.logOut();
		sessionController.login(testC1WarehouseManager1, getCampus(0));
		warehouseController=new WarehouseManagerController(sessionController);
		sessionController.logOut();
	}
	/**
	 * Geeft een campus terug als de index gegeven is. Deze mehtode dient om de leesbaarheid en compactheid van de tests te verbeteren.
	 * @param campusNumber
	 * @return
	 */
	
	protected Campus getCampus(int campusNumber) {
		return (Campus)sessionController.getCampuses().get(campusNumber);
	}

	/**
	 * @param nbOfDays
	 * 		The number of days more that should be added to the current time
	 * @return
	 * 		A timestamp of a day in the distant future
	 */
	
	public TimeStamp getFutureTime(double nbOfDays){
		return TimeStamp.addedToTimeStamp(TimeDuration.days((int)(nbOfDays*100)/100), 
				sessionController.getTime());
	}
}
