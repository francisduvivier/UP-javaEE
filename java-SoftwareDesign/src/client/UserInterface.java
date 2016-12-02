package client;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.SortedSet;

import system.controllers.AdministratorController;
import system.controllers.DoctorController;
import system.controllers.NurseController;
import system.controllers.SessionController;
import system.controllers.WarehouseManagerController;
import system.debug.Dump;
import system.exceptions.IllegalAccessException;
import system.exceptions.IllegalOperationException;
import system.exceptions.InsufficientWarehouseItemsException;
import system.exceptions.SchedulingException;
import system.repositories.MachineType;
import system.repositories.ResourceType;
import system.repositories.StaffType;
import system.results.medicaltestresults.ScanMatter;
import system.scheduling.Priority;
import system.time.TimeStamp;
import system.warehouse.MedicationItemType;

public class UserInterface {
	private static Scanner scanner;
	private static SessionController sessionController;
	public static void main(String[] args) {
		scanner = new Scanner(System.in);
	
		sessionController = new SessionController();
		mainMenu();
	}

	private static ICampus getCurrentCampus() {
		return sessionController.getCurrentCampus();
	}

	/**
	 * Hoofdmenu van de user-interface/systeem. Typ "1" in om naar het volgende
	 * menu (gebruiker kiezen) te geraken, of typ "2" om het systeem te
	 * verlaten.
	 */
	public static void mainMenu() {
		while (true) {
			printHeader();


			System.out.println("Select one of the following:\n");
			System.out.println("1. Login");
			System.out.println("2. Dump: show (HTML)");
			System.out.println("3. Exit");

			switch (readInt(3)) {
			case 1:
				login();
				break;
			case 2:
				showDump();
				break;
			case 3:
				exit();
				break;
			}
		}
	}

	/**
	 * Methode om af te drukken wie ingelogd is.
	 */
	private static void printHeader() {
		IStaffMember user = sessionController.getCurrentUser();
		System.out.println("== Logged in as "
				+ (user == null ? "No one" : user.toString()) + " ==");
	}

	/**
	 * Hulpmethode voor het lezen van geldige integers in een user-menu
	 * 
	 * @return geldige integer
	 */
	private static int readInt() {
		int menuEntry = -1;

		do {
			while (!scanner.hasNextInt()) {
				scanner.next();
			}
			menuEntry = scanner.nextInt();
		} while (menuEntry < 0);
		saveDump();
		return menuEntry;
	}

	/**
	 * Hulpmethode om te zien of de gebruiker een geldige integer heeft
	 * ingevoerd.
	 * @param maximum getal mogelijk als keuze
	 * @return geldige integer die de keuze voorstelt
	 */
	private static int readInt(int max) {
		int menuEntry = -1;

		do {
			while (!scanner.hasNextInt()) {
				scanner.next();
			}
			menuEntry = scanner.nextInt();
			if (menuEntry > max || menuEntry <= 0)
				System.out
						.println("Invalid choice - choose [1 - " + max + "]:");
		} while (menuEntry <= 0 || menuEntry > max);
		saveDump();
		return menuEntry;
	}

	/**
	 * Verlaat het hoofdmenu
	 */
	private static void exit() {
		System.exit(1);
	}

	/**
	 * Deze methode geeft de gebruiker de optie in te loggen als ofwel Doctor,
	 * ofwel Nurse, ofwel Hospital Administrator, ofwel Warehouse Manager.
	 */
	private static void login() {
		printHeader();

		int i;

		System.out.println("Select one of the following Staffmembers:\n");

		for (i = 0; i < sessionController.getAllStaff().size(); i++)
			System.out.println((i + 1)
					+ ". "
					+ sessionController.getAllStaff().get(i)
							.toString());

		System.out.println((i + 1) + ". Back");

		int np = readInt(i + 1);
		if (--np != i) {
			IStaffMember staffMember = sessionController.getAllStaff().get(np);
			int nc=0;
			int ic=0;
			if(sessionController.isHospitalStaff(staffMember)){
			System.out.println("Select one of the following Campuses:\n");
			for (ic = 0; ic < sessionController.getBigHospital().getNbHospitals(); ic++)
				System.out.println((ic + 1)
						+ ". "
						+ sessionController.getCampuses().get(ic).toString()
						);

			System.out.println((ic + 1) + ". Back");

			nc = readInt(ic + 1);
			}
			else{
			}
			if (--nc != ic || !sessionController.isHospitalStaff(staffMember)) {
				ICampus newCurrentCampus;
				if(sessionController.isHospitalStaff(staffMember))
				newCurrentCampus=sessionController.getCampuses().get(nc);
				else
				newCurrentCampus=sessionController.getCampusOf(staffMember);
				sessionController.login(staffMember, newCurrentCampus);
				ResourceType staffMemberType = sessionController
						.getCurrentUser().getResourceType();

				if (staffMemberType == StaffType.DOCTOR)
					doctorMenu(new DoctorController(sessionController));
				else if (staffMemberType == StaffType.NURSE)
					nurseMenu(new NurseController(sessionController));
				else if (staffMemberType == StaffType.HOSPITAL_ADMINISTRATOR)
					hospitalAdministratorMenu(new AdministratorController(
							sessionController));
				else if (staffMemberType == StaffType.WAREHOUSE_MANAGER)
					warehouseManagerMenu(new WarehouseManagerController(
							sessionController));
			}
		}

	}

	/**
	 * Menu om als administrator personeel of gereedschap toe te voegen
	 * 
	 * 		An instance of AdministratorController
	 */
	public static void hospitalAdministratorMenu(
			AdministratorController controller) {
		boolean logout = false;

		while (!logout) {
			printHeader();

			boolean campusInLockdown = controller.campusIsInLockdown();
			
			System.out.println("Select one of the following:\n");
			System.out.println("1. Add Hospital Staff");
			System.out.println("2. Add Hospital Equipment");
			System.out.println("3. Advance Time");
			if (campusInLockdown) {
				System.out.println("---LOCKDOWN------------------");
				System.out.println("4. Open Campus");
				System.out.println("5. Get List of People at Risk");
				System.out.println("-----------------------------");
				System.out.println("6. Log Out");
			} else 
				System.out.println("4. Log Out");

			switch (readInt(campusInLockdown?6:4)) {
			case 1:
				addHospitalStaff(controller);
				break;
			case 2:
				addHospitalEquipment(controller);
				break;
			case 3:
				advanceTime(controller);
				break;
			case 4:
				if (campusInLockdown) {
					controller.openCampusAfterLockdown();
					System.out.println("The campus is open again");
				} else 
					logout = logOut();
				break;
			case 5:
				if (campusInLockdown) {
					List<IScheduleResource> resources = controller.getListOfPeopleWhoLeft();
					
					for (int i = 0; i < resources.size(); i++)
						System.out.println("\t"+(i+1)+": "+resources.get(i));
				} else
					logout = logOut();
				break;
			case 6:
				logout = logOut();
				break;
			}
		}
	}

	/**
	 * Afmelden van user
	 */
	private static boolean logOut() {
		sessionController.logOut();
		return true;
	}

	/**
	 * Menu om ziekenhuisgereedschap toe te voegen als administrator
	 * 
	 * @param controller
	 * 		An instance of AdministratorController
	 */
	private static void addHospitalEquipment(AdministratorController controller) {
		printHeader();

		System.out.println("Select one of the following:\n");
		System.out.println("1. X-Ray Scanner");
		System.out.println("2. Blood Analyzer");
		System.out.println("3. Ultrasound Machine");
		System.out.println("4. Surgical Equipment");
		System.out.println("5. Back");

		switch (readInt(5)) {
		case 1:
			addMachine(MachineType.XRAY_SCANNER, controller);
			break;
		case 2:
			addMachine(MachineType.BLOOD_ANALYZER, controller);
			break;
		case 3:
			addMachine(MachineType.ULTRASOUND_MACHINE, controller);
			break;
		case 4:
			addMachine(MachineType.SURGICAL_EQUIPMENT, controller);
			break;
		}
	}

	/**
	 * Menu om een machine toe te voegen. De gebruiker (hospital administrator)
	 * kan een verdieping en kamer kiezen.
	 * 
	 * @param machineType
	 * 		The type of the new machine
	 * @param controller
	 * 		An instance of AdministratorController
	 */
	private static void addMachine(MachineType machineType,
			AdministratorController controller) {
		System.out.println("Floor:\n");
		int floor = readInt();

		System.out.println("Room:\n");
		int room = readInt();

		System.out.println("ID:\n");
		int ID = readInt();
		try {
			controller.addMachine(ID, floor, room, machineType);
		} catch (IllegalArgumentException e) {
			System.out.println("Something went wrong.");
		}

	}

	/**
	 * Menu om ziekenhuispersoneel toe te voegen als administrator
	 * 
	 * @param controller
	 * 		An instance of AdministratorController
	 */
	private static void addHospitalStaff(AdministratorController controller) {
		printHeader();

		System.out.println("Select one of the following:\n");
		System.out.println("1. Doctor");
		System.out.println("2. Nurse");
		System.out.println("3. Warehouse Manager");
		System.out.println("4. Back");

		switch (readInt(4)) {
		case 1:
			System.out.println("Please choose doctor type:");
			System.out.println("1. Switcher");
			System.out.println("2. Half campus 1 | Half campus 2");
			System.out.println("3. Half campus 2 | Half campus 1");
			int choice = readInt(3);
			addStaffMember(StaffType.DOCTOR, controller, choice);
			break;
		case 2:
			addStaffMember(StaffType.NURSE, controller, -1);
			break;
		case 3:
			addStaffMember(StaffType.WAREHOUSE_MANAGER, controller, -1);
			break;
		}
	}

	/**
	 * Menu om als hospital administrator een staffmember
	 * (nurse/doctor/warehouse manager) toe te voegen aan het systeem
	 * 
	 * @param staffMemberType
	 * 		The type of the new staff member
	 * @param controller
	 * 		An instance of AdministratorController
	 */
	private static void addStaffMember(ResourceType staffMemberType,
			AdministratorController controller, int doctorType) {

		System.out.println("Name:\n");
		scanner.nextLine();
		String name = scanner.nextLine();

		try {
			if (staffMemberType == StaffType.DOCTOR) {
				IStaffMember doctor;

				switch (doctorType) {
				case 1: 
					controller.addDoctor(name,true);
					break;
				case 2:
					doctor = controller.addDoctor(name);
					controller.addShift(doctor,sessionController.getCampuses().get(0),
							9,0,12,0);
					controller.addShift(doctor, sessionController.getCampuses().get(1), 
							12, 0, 17, 0);
					break;
				case 3: 
					doctor = controller.addDoctor(name);
					controller.addShift(doctor,sessionController.getCampuses().get(1),
							9,0,12,0);
					controller.addShift(doctor, sessionController.getCampuses().get(0), 
							12, 0, 17, 0);
					break;
				}
			} else if (staffMemberType == StaffType.NURSE) {
				controller.addNurse(name,9,0,17,0);
			} else if (staffMemberType == StaffType.WAREHOUSE_MANAGER) {
				controller.addWarehouseManager(name);
			}
		} catch (IllegalArgumentException e) {
			System.out.println("Something went wrong.");
		}
	}

	/**
	 * De tijd van het systeem vooruit zetten m.b.v. 
	 * de administrator controller.
	 * 
	 * @param controller
	 * 		An instance of AdministratorController
	 */
	private static void advanceTime(AdministratorController controller) {
		try {
			System.out.println("Please enters a new date and time using the following format 'dd/MM/yyyy HH:mm'");
			TimeStamp now = readDateTime();
			controller.advanceTime(now);			
			IStaffMember admin = sessionController.getCurrentUser();
			autoEnterResults(controller);
			autoMarkArrived();
			sessionController.logOut();
			sessionController.login(admin, null);
		} catch (IllegalAccessException accessException) {
			System.out.println(accessException.getMessage()+" to advance time.");
			logOut();
		}
	}

	/**
	 * Methode om gearriveerde plaster orders, meal orders, en
	 * medication item orders als gearriveerd te markeren
	 */
	private static void autoMarkArrived() {
		for (ICampus campus : sessionController.getCampuses()) {
			List<IStaffMember> managers = sessionController.getCampusStaffByType(campus, StaffType.WAREHOUSE_MANAGER);
			if (!managers.isEmpty()) {
				sessionController.logOut();
				sessionController.login(managers.get(0), campus);
				WarehouseManagerController warehouseController = new WarehouseManagerController(
						sessionController);

				while (!warehouseController.getArrivedPlasterOrders().isEmpty()) {
					markPlasterOrders(warehouseController,
							warehouseController.getArrivedPlasterOrders());
				}

				while (!warehouseController.getArrivedMealOrders().isEmpty()) {
					markMealOrders(warehouseController,
							warehouseController.getArrivedMealOrders());
				}

				while (!warehouseController.getAllArrivedMedicationItemOrders()
						.isEmpty()) {
					markMedicationItemOrders(warehouseController,
							warehouseController
									.getAllArrivedMedicationItemOrders());
				}
			}
		}

	}
	
	/**
	 * Methode om via de administrator controller automatisch
	 * resultaten (zowel medical test resultaten als
	 * treatment resultaten) als nurse in te voeren.
	 * @param controller
	 * 		An instance of AdministratorContrller
	 */
	private static void autoEnterResults(AdministratorController controller) {
		IStaffMember loggedInHA = sessionController.getCurrentUser();
		for (IStaffMember nurse : controller.getTreatmentNurses()) {		
			sessionController.logOut();
			sessionController.login(nurse, null);
			enterTreatmentResult(new NurseController(sessionController));
		}		
		sessionController.login(loggedInHA, null);
		for (IStaffMember nurse : controller.getMedicalTestNurses()) {		
			sessionController.logOut();
			sessionController.login(nurse, null);
			enterMedicalTestResult(new NurseController(sessionController));
		}
	}
	
	/**
	 * Methode om tijd in te geven (eerst datum, dan tijdstip)
	 * en om te zetten naar een geldige TimeStamp
	 * @return timeStamp
	 * 		een geldig object van de klasse TimeStamp
	 */
	private static TimeStamp readDateTime() {
		TimeStamp timeStamp = null;	
		boolean isTimeStamp = false;
			
		while (!isTimeStamp) {
			String date = scanner.next();
			String time = scanner.next();
			try {
				timeStamp = TimeStamp.parseStringToTimeStamp(date + " " + time);
				System.out.println(timeStamp);
				isTimeStamp = true;
			} catch(ParseException parseException) {
				System.out.println("Please use the specified date format");
			}
		}

		return timeStamp;
	}
	
	
	/**
	 * Menu om als verpleegster acties uit te voeren
	 * 
	 * @param controller
	 * 		An instance of NurseController
	 */
	public static void nurseMenu(NurseController controller) {
		boolean logout = false;

		while (!logout) {
			printHeader();

			System.out.println("Select one of the following:\n");
			System.out.println("1. Register Patient");
			System.out.println("2. Enter Medical Test Result");
			System.out.println("3. Enter Treatment Result");
			System.out.println("4. Log Out");

			switch (readInt(4)) {
			case 1:
				registerPatient(controller);
				break;
			case 2:
				enterMedicalTestResult(controller);
				break;
			case 3:
				enterTreatmentResult(controller);
				break;
			case 4:
				logout = logOut();
				break;
			}
		}
	}

	/**
	 * Methode om als nurse treatment resultaten in te voeren.
	 * Eerst selecteer je een treatment uit de lijst van
	 * onafgwerkte treatments en vervolgens wordt een
	 * methode opgeroepen waar je de details van die
	 * treatment kan invoeren.
	 * 
	 * @param controller
	 * 		an instance of NurseController
	 */
	private static void enterTreatmentResult(NurseController controller) {
		printHeader();

		int i;

		System.out.println("Select one of the following:\n");

		for (i = 0; i < controller.getUnfinishedTreatments().size(); i++)
			System.out.println((i + 1) + ". "
					+ controller.getUnfinishedTreatments().get(i).toString());

		System.out.println((i + 1) + ". Back");

		int n = readInt(i + 1);

		if (--n == i)
			return;
		else
			requestDetails(controller.getUnfinishedTreatments().get(n),
					controller);
	}

	/**
	 * Methode om als nurse medical test resultaten in te voeren.
	 * Eerst selecteer je een medical test uit de lijst van
	 * onafgwerkte medical tests en vervolgens wordt een
	 * methode opgeroepen waar je de details van die
	 * medical test kan invoeren.
	 * 
	 * @param controller
	 * 		an instance of NurseController
	 */
	private static void enterMedicalTestResult(NurseController controller) {
		printHeader();

		int i;

		System.out.println("Select one of the following:\n");

		for (i = 0; i < controller.getUnfinishedMedicalTests().size(); i++)
			System.out.println((i + 1) + ". "
					+ controller.getUnfinishedMedicalTests().get(i).toString());

		System.out.println((i + 1) + ". Back");

		int n = readInt(i + 1);

		if (--n == i)
			return;
		else
			requestDetails(controller.getUnfinishedMedicalTests().get(n),
					controller);
	}

	/**
	 * Hulpmethode om te bepalen met welk sub-sub-type van
	 * resultaten te maken hebben om vervolgens te beslissen
	 * welke methode we moeten oproepen om de details van de test
	 * in te voeren.
	 * 
	 * @param operation
	 * 		De handeling/operatie van de patient
	 * @param controller
	 * 		An instance of NurseController
	 */
	private static void requestDetails(IOperation operation,
			NurseController controller) {
		switch (operation.getEventType()) {
		case MEDICATION:
			requestDetailsMedicationResult(operation, controller);
			break;
		case CAST:
			requestDetailsCastResult(operation, controller);
			break;
		case SURGERY:
			requestDetailsSurgeryResult(operation, controller);
			break;
		case BLOOD_ANALYSIS:
			requestDetailsBloodAnalysisResult(operation,
					controller);
			break;
		case XRAY_SCAN:
			requestDetailsXRayScanResult(operation, controller);
			break;
		case ULTRASOUND_SCAN:
			requestDetailsUltrasoundScanResult(operation,
					controller);
			break;
		case APPOINTMENT:
			// dit event type heeft geen actie tot gevolg
			break;
		}
	}


	/**
	 * Menu om als verpleegster de details van een medicatie in te voeren Deze
	 * details zijn abnormalReaction (boolean) en een verslag (string)
	 * 
	 * @param medication
	 * 		The medication that has treated the patient 
	 * @param controller
	 * 		An instance of NurseController
	 */
	private static void requestDetailsMedicationResult(IOperation medication,
			NurseController controller) {
		System.out.println("Abnormal reaction? (type true or false):\n");
		boolean abnormalReaction = scanner.nextBoolean();
		System.out.println("Enter report:\n");
		scanner.nextLine();
		String report = scanner.nextLine();

		controller.registerMedicationResult(medication, abnormalReaction,
				report);
	}

	/**
	 * Menu om als verpleegster de details van een gips in te voeren Deze
	 * details gaan om een verslag (string)
	 * 
	 * @param cast
	 * 		The cast that has treated the patient 
	 * @param controller
	 * 		An instance of NurseController
	 */
	private static void requestDetailsCastResult(IOperation cast,
			NurseController controller) {
		System.out.println("Enter report:\n");
		scanner.nextLine();
		String report = scanner.nextLine();

		controller.registerCastResult(cast, report);
	}

	/**
	 * Menu om als verpleegster de details van een operatie in te voeren Deze
	 * details zijn een verslag (string) en een post-operatie-verzoring (string)
	 * 
	 * @param surgery
	 * 		The finished surgery
	 * @param controller
	 * 		An instance of NurseController
	 */
	private static void requestDetailsSurgeryResult(IOperation surgery,
			NurseController controller) {
		System.out.println("Enter report:\n");
		scanner.nextLine();
		String report = scanner.nextLine();
		System.out.println("Enter special aftercare:\n");
		scanner.nextLine();
		String specialAftercare = scanner.nextLine();		

		controller.registerSurgeryResult(surgery, report, specialAftercare);
	}

	/**
	 * Menu om als verpleegster de details van een blood analysis in te voeren
	 * Deze details zijn amountOfBloodWithdrawn (int), redCellCount (int),
	 * whiteCellCount (int) en plateletCount (int)
	 * 
	 * @param bloodAnalysis
	 * 		The finished blood analysis
	 * @param controller
	 * 		An instance of NurseController
	 */
	private static void requestDetailsBloodAnalysisResult(
			IOperation bloodAnalysis, NurseController controller) {
		System.out
				.println("Enter amount of blood withdrawn (higher than zero):\n");
		int amountOfBloodWithdrawn = readInt();

		System.out.println("Enter red cell count (higher than zero):\n");
		int redCellCount = readInt();

		System.out.println("Enter white cell count (higher than zero):\n");
		int whiteCellCount = readInt();

		System.out.println("Enter platelet count (higher than zero):\n");
		int plateletCount = readInt();

		controller.registerBloodAnalysisResult(bloodAnalysis,
				amountOfBloodWithdrawn, redCellCount, whiteCellCount,
				plateletCount);

	}

	/**
	 * Menu om als verpleegster de details van een medicatie in te voeren Deze
	 * details zijn scanInformation (string) en natureOfScannedMass (enum
	 * ScanMatter)
	 * 
	 * @param ultrasoundscan
	 * 		The finished ultrasound scan
	 * @param controller
	 * 		An instance of NurseController
	 */
	private static void requestDetailsUltrasoundScanResult(
			IOperation ultrasoundScan, NurseController controller) {
		scanner.nextLine();
		String scanInformation = scanner.nextLine();
		System.out.println("Choose nature of scanned mass:\n");
		System.out.println("1. Benign");
		System.out.println("2. Unknown");
		System.out.println("3. Malignant");

		ScanMatter scanMatter = null;
		int natureOfScannedMass = readInt(3);

		switch (natureOfScannedMass) {
		case 1:
			scanMatter = ScanMatter.BENIGN;
			break;
		case 2:
			scanMatter = ScanMatter.UNKNOWN;
			break;
		case 3:
			scanMatter = ScanMatter.MALIGNANT;
			break;
		}

		controller.registerUltrasoundScanResult(ultrasoundScan,
				scanInformation, scanMatter);
	}

	/**
	 * Menu om als verpleegster de details van een medicatie in te voeren Deze
	 * details zijn abnormalities (string) en nbImagesTaken (int)
	 * 
	 * @param xRayScan
	 * 		The finished X-ray scan
	 * @param controller
	 * 		An instance of NurseController
	 */
	private static void requestDetailsXRayScanResult(IOperation xRayScan,
			NurseController controller) {
		System.out.println("Enter abnormalities:\n");
		scanner.nextLine();
		String abnormalities = scanner.nextLine();

		System.out
				.println("Enter number of images taken (higher than zero):\n");
		int nbImagesTaken = readInt();

		controller.registerXRayScanResult(xRayScan, abnormalities,
				nbImagesTaken);
	}

	/**
	 * Menu om als verpleegster een patient te registreren Kiezen uit een
	 * lijst met oude patienten of hem als nieuwe patient toe te voegen
	 * Wijst een dokter toe aan een patient Scheduled de afspraak
	 * 
	 * @param controller
	 * 		An instance of NurseController
	 */
	private static void registerPatient(NurseController controller) {
		if (controller.getDoctors().isEmpty()) {
			System.out.println("There are no doctors in the hospital.");
			return;
		}

		printHeader();

		int i;

		System.out.println("Select one of the following:\n");
		
		for (i = 0; i < controller.getRegisteredPatients().size(); i++)
			System.out.println((i + 1) + ". "
					+ controller.getRegisteredPatients().get(i).toString());

		System.out.println((i + 1) + ". Add Patient");
		System.out.println((i + 2) + ". Back");

		int n = readInt(i + 2);

		String name=null;
		if (--n == i + 1)
			return;

		else if (n == i) {
			System.out.println("Name:\n");
			scanner.nextLine();
			name = scanner.nextLine();

			try {
				controller.registerNewPatient(name);
				System.out.println("Patient added: " + name);
			} catch (NullPointerException npe) {
				System.out.println("Please fill in the patient's name");
				return;
			} catch (IllegalArgumentException iae) {
				System.out.println("Patient already exists");
				return;
			} catch (IllegalOperationException ioe) {
				System.out.println(ioe.getMessage());
				return;
			} catch (InsufficientWarehouseItemsException iwie) {
			notEnoughFoodInformer(controller, name);
			return;
		}
		} else {

			try {
				controller.registerPatient(controller.getRegisteredPatients()
						.get(n));
			} catch (NullPointerException npe) {
				System.out.println("Something went wrong.");
				return;
			} catch (IllegalArgumentException iae) {
				System.out.println("Patient is already registered.");
				return;
			} catch (InsufficientWarehouseItemsException iwie) {
				notEnoughFoodInformer(controller, name);
				return;
			}
		}
		
		int j;
		System.out.println("Select one of the following Doctors for an appointment:\n");

		for (j = 0; j < controller.getDoctors().size(); j++)
			System.out.println((j + 1) + ". "
					+ controller.getDoctors().get(j).toString());

		int m = readInt(j);

		try {
			controller.scheduleAppointment(controller.getDoctors().get(--m));
			System.out.println("Appointment scheduled.");
		} catch (SchedulingException e) {
			System.out.println("Something went wrong.");
		}
	}

	private static void notEnoughFoodInformer(NurseController controller,
			String name) {
		System.out.println("Not enough food in " + getCurrentCampus());
		for(ICampus possibleCampus:controller.getCampusesWithFood(name))
			System.out.println("There is enough food in "+ possibleCampus);
	}

	/**
	 * Menu om als dokter acties uit te voeren
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	public static void doctorMenu(DoctorController controller) {
		boolean logout = false;

		while (!logout) {
			printHeader();

			System.out.println("Select one of the following:\n");
			System.out.println("1. Consult Patient File");
			System.out.println("2. Approve Diagnosis");
			System.out.println("3. Undo/Redo Previous Action");
			System.out.println("4. Log Out");

			switch (readInt(4)) {
			case 1:
				consultPatientFile(controller);
				break;
			case 2:
				approveDiagnosisMenu(controller);
				break;
			case 3:
				undoRedoAction(controller);
				break;
			case 4:
				logout = logOut();
				break;
			}
		}
	}

	/**
	 * Menu om als dokter een diagnose goed of af te keuren
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void approveDiagnosisMenu(DoctorController controller) {
		printHeader();

		int i;

		System.out.println("Select one of the following:\n");

		for (i = 0; i < controller.getSecondOpinionDiagnoses().size(); i++)
			System.out.println((i + 1) + ". "
					+ controller.getSecondOpinionDiagnoses().get(i).toString());

		System.out.println((i + 1) + ". Back");

		int n = readInt(i + 1);

		if (--n == i)
			return;
		else {
			IDiagnosis diagnosis = controller.getSecondOpinionDiagnoses().get(n);
			controller.consultPatientFile(diagnosis.getPatient());
			continueApproveDiagnosisMenu(controller, diagnosis);
			controller.closePatientFile();
		}
	}

	/**
	 * Menu om als dokter een diagnose goed te keuren (deel 2)
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 * @param diagnosis
	 * 		The diagnosis to be approved
	 */
	private static void continueApproveDiagnosisMenu(
			DoctorController controller, IDiagnosis diagnosis) {
		printHeader();
		patientHeader(controller);

		System.out.println("Diagnosis Details:");
		System.out.println(controller.getDiagnosisDetails(diagnosis));
		System.out.println();
		System.out.println("Select one of the following:\n");
		System.out.println("1. Review Previous Results");
		System.out.println("2. Approve Diagnosis");
		System.out.println("3. Do NOT Approve Diagnosis");

		switch (readInt(3)) {
		case 1:
			reviewPreviousResults(controller, diagnosis);
			break;
		case 2:
			approveDiagnosis(controller, diagnosis);
			break;
		case 3:
			denyDiagnosis(controller, diagnosis);
			break;
		}

	}

	/**
	 * Methode om als dokter een nieuwe diagnose te stellen
	 * voor een oude diagnose.
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 * @param diagnosis
	 * 		De up-te-daten diagnose
	 */
	private static void denyDiagnosis(DoctorController controller,
			IDiagnosis diagnosis) {
		System.out.println("Please enter the new diagnosis");
		String newDetails = scanner.next();
		controller.denyDiagnosis(diagnosis, newDetails);
	}

	/**
	 * Methode om als dokter een diagnose goed te keuren.
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 * @param diagnosis
	 * 		De goed te keuren diagnose
	 */
	private static void approveDiagnosis(DoctorController controller,
			IDiagnosis diagnosis) {
		controller.approveDiagnosis(diagnosis);
	}

	/**
	 * Menu om als dokter een diagnose te bekijken
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void reviewPreviousResults(DoctorController controller,
			IDiagnosis diagnosis) {
		// for (Result result : controller.getResults(diagnosis
		// .getPatient())) {
		// System.out.println(result.toString());
		// }
		//
		// System.out.println("Select one of the following:\n");
		// System.out.println("1. Back");
		//
		// switch (readInt()) {
		// case 1:
		// continueApproveDiagnosisMaybe(doctor, diagnosis);
		// break;
		// }
	}

	/**
	 * Methode om als dokter ��n van zijn acties ongedaan 
	 * te maken of te herdoen. Een lijst verschijnt en hij
	 * kan ��n van de laatste 20 acties kiezen om ongedaan
	 * te maken.
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void undoRedoAction(DoctorController controller) {
		printHeader();
		
		System.out.println("Select one of the following:\n");
		System.out.println("1. Undo");
		System.out.println("2. Redo");
		switch (readInt(2)) {
		case 1:
			undoAction(controller);
			break;
		case 2:
			redoAction(controller);
			break;
		}
	}
	
	
	/**
	 * Ongedaan maken.
	 * 
	 * @param controller
	 */
	private static void undoAction(DoctorController controller) {
		int i;

		System.out.println("Select one of the following:\n");
		for (i = 0; i < controller.getUndoCommands().size(); i++)
			System.out.println((i + 1) + ". "
					+ controller.getUndoCommands().get(i));

		System.out.println((i + 1) + ". Back");

		int n = readInt(i + 1);

		if (--n == i)
			return;
		else {
			try {
				controller.undoCommand(n);
				System.out.println("Action undone.");
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
		}
	}
	
	/**
	 * Terug opnieuw uitvoeren.
	 * 
	 * @param controller
	 */
	private static void redoAction(DoctorController controller) {
		int i;

		System.out.println("Select one of the following:\n");
		for (i = 0; i < controller.getRedoCommands().size(); i++)
			System.out.println((i + 1) + ". "
					+ controller.getRedoCommands().get(i));

		System.out.println((i + 1) + ". Back");

		int n = readInt(i + 1);

		if (--n == i)
			return;
		else {
			try {
				controller.redoCommand(n);
			} catch (Exception e) {
				System.out.println(e.getMessage());
			}
			System.out.println("Action redone.");
		}
	}

	/**
	 * Menu om als dokter een patient file te consulteren uit een lijst van
	 * patient files
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void consultPatientFile(DoctorController controller) {
		printHeader();

		System.out.println("Select one of the following:\n");

		int i;
		for (i = 0; i < controller.getNonDischargedPatients().size(); i++)
			System.out.println((i + 1) + ". "
					+ controller.getNonDischargedPatients().get(i).toString());

		System.out.println((i + 1) + ". Back");
		int n = readInt(i + 1);

		if (--n == i)
			return;
		else {
			controller.consultPatientFile(controller.getNonDischargedPatients()
					.get(n));

			patientFileMenu(controller);
		}
	}

	/**
	 * Methode om af te drukken welke patient
	 * de dokter behandelt.
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void patientHeader(DoctorController controller) {
		System.out.println("== Working on "
				+ (controller.getSelectedPatient() == null ? "No one" : controller.getSelectedPatient().toString()) + " ==");
	}

	/**
	 * Menu om als dokter voor een patie�nt een actie uit te voeren
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void patientFileMenu(DoctorController controller) {
		boolean exitPatient = false;

		while (!exitPatient) {
			printHeader();
			patientHeader(controller);

			System.out.println("Select one of the following:\n");
			System.out.println("1. Enter Diagnosis");
			System.out.println("2. Order Medical Test");
			System.out.println("3. Prescribe Treatment");
			System.out.println("4. Patient Schedule");
			System.out.println("5. Discharge Patient");
			System.out.println("6. Back");

			switch (readInt(6)) {
			case 1:
				enterDiagnosis(controller);
				break;
			case 2:
				orderMedicalTest(controller);
				break;
			case 3:
				chooseDiagnosis(controller);
				break;
			case 4:
				showSchedule(controller);
				break;
			case 5:
				try {
					dischargePatient(controller);
					exitPatient = exitPatient(controller);
				} catch (IllegalOperationException ioe) {
					System.out.println(ioe.getMessage());
				}
				break;
			case 6:
				exitPatient = exitPatient(controller);
				break;
			}
		}
	}

	/**
	 * Methode om als dokter een patient file
	 * te verlaten.
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 * @return true
	 * 			Patient file verlaten
	 */
	private static boolean exitPatient(DoctorController controller) {
		controller.closePatientFile();
		return true;
	}

	/**
	 * Menu om een afspraak met patient te tonen.
	 * Druk 1 om terug te keren.
	 * 
	 * @param controller
	 * 		An instance of DocterController
	 */
	private static void showSchedule(DoctorController controller) {
		System.out.println(controller.getPatientSchedule().toString());
		System.out.println("1. Back");

		readInt(1);
	}

	/**
	 * Menu om als dokter een diagnose uit een lijst
	 * van diagnosissen te kiezen. Wanneer een diagnose gekozen is
	 * wordt een methode opgeroepen om een treatment voor
	 * die diagnose voor te schrijven.
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void chooseDiagnosis(DoctorController controller) {
		int i;

		System.out.println("Select one of the following:\n");

		for (i = 0; i < controller.getDiagnoses().size(); i++)
			System.out.println((i + 1) + ". "
					+ controller.getDiagnoses().get(i).toString());

		System.out.println((i + 1) + ". Back");

		int n = readInt(i + 1);

		if (--n == i)
			return;
		else
			prescribeTreatment(controller, controller.getDiagnoses().get(n));
	}

	/**
	 * Menu om als dokter de geselecteerde patient te ontslaan uit het
	 * ziekenhuis. Kies 1 om de patient effectief te ontslaan, of kies
	 * 2 om terug te keren naar het vorige menu.
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void dischargePatient(DoctorController controller) {
		System.out.println("1. Discharge " + controller.getSelectedPatient().getName());
		System.out.println("2. Back");
		int n = readInt();
		if (n == 1) {
			controller.dischargePatient();
			System.out.println(controller.getSelectedPatient().getName() + " has been discharged.");
			return;
		}
		else if (n == 2) {
			return;
		}
		else
			dischargePatient(controller);
	}

	/**
	 * Menu om de prioriteit van de scheduleEvent te vragen.
	 */
	private static Priority getPriority() {
		System.out.println("Priority:");
		System.out.println("1. Normal");
		System.out.println("2. Urgent");
		if (readInt(2) == 1)
			return Priority.NORMAL;
		else return Priority.URGENT;
	}
	
	/**
	 * Menu om als dokter een behandeling te kiezen voor een
	 * diagnose. Kies uit medicatie, gips of operatie.
	 * Kies 4 om terug te keren.
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void prescribeTreatment(DoctorController controller,
			IDiagnosis diagnosis) {
		System.out.println("Select one of the following:\n");
		System.out.println("1. Medication");
		System.out.println("2. Cast");
		System.out.println("3. Surgery");
		System.out.println("4. Back");

		switch (readInt(4)) {
		case 1:
			requestDetailsMedication(controller, diagnosis);
			break;
		case 2:
			requestDetailsCast(controller, diagnosis);
			break;
		case 3:
			requestDetailsSurgery(controller, diagnosis);
			break;
		case 4:
			break;
		}
	}

	/**
	 * Menu om als dokter de details van een medicatie in te voeren Deze details
	 * zijn description (string) en sensitive (boolean)
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void requestDetailsMedication(DoctorController controller,
			IDiagnosis diagnosis) {
		String description = "";
		while (description.length() <= 0) {
			System.out.println("Enter description:\n");
			description = scanner.next();
		}
		System.out.println("Sensitive? (type true or false):\n");
		boolean sensitive = scanner.nextBoolean();

		List<MedicationItemType> medicationItems = new ArrayList<MedicationItemType>();
		boolean done = false;
		while (!done) {
			int i;
			System.out.println("Select the medication:");

			for (i = 0; i < MedicationItemType.values().length; i++)
				System.out
						.println((i + 1)
								+ ". "
								+ MedicationItemType.values()[i].toString()
								+ " : "
								+ (medicationItems.contains(MedicationItemType
										.values()[i]) ? "X" : ""));

			System.out.println((i + 1) + ". Done");

			int n = readInt(i + 1);

			if (--n == i)
				done = true;
			else {
				if (medicationItems.contains(MedicationItemType.values()[n]))
					medicationItems.remove(MedicationItemType.values()[n]);
				else
					medicationItems.add(MedicationItemType.values()[n]);
			}
		}

		try {
			controller.prescribeMedication(diagnosis, getPriority(), description, sensitive,
					medicationItems);
			if (diagnosis.needsApproval())
				System.out
						.println("Treatment is stored, but can't be scheduled yet. The treatment's diagnose needs a second opinion.");
			else
				System.out.println("Medication is scheduled.");
		} catch (InsufficientWarehouseItemsException e) {
			System.out.println("Insufficient medication in warehouse.");
		} catch(SchedulingException se){
			System.out.println("Het is onmogelijk om dit te schedulen./n" +
					"De nodige resources zijn niet aanwezig in het ziekenhuis");
		};
	}

	/**
	 * Menu om als dokter de details van een gips in te voeren Deze details zijn
	 * bodyPart (string) en durationInDays (int)
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void requestDetailsCast(DoctorController controller,
			IDiagnosis diagnosis) {
		System.out.println("Enter body part:\n");
		scanner.nextLine();
		String bodyPart = scanner.nextLine();

		System.out.println("Enter duration in days (higher than zero):\n");
		int durationInDays = readInt();

		try {
			controller.prescribeCast(diagnosis, getPriority(), bodyPart, durationInDays);

			if (diagnosis.needsApproval())
				System.out
						.println("Treatment is stored, but can't be scheduled yet. The treatment's diagnose needs a second opinion.");
			else
				System.out.println("Cast is scheduled.");
		} catch (InsufficientWarehouseItemsException e) {
			System.out.println("Insufficient plaster in warehouse.");
		}catch(SchedulingException se){
			System.out.println("Het is onmogelijk om dit te schedulen./n" +
					"De nodige resources zijn niet aanwezig in het ziekenhuis");
		}
	}

	/**
	 * Menu om als dokter de details van een operatie in te voeren Deze details
	 * zijn description (string)
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void requestDetailsSurgery(DoctorController controller,
			IDiagnosis diagnosis) {
		System.out.println("Enter description:\n");
		scanner.nextLine();
		String description = scanner.next();
		
		try {
			controller.prescribeSurgery(diagnosis, getPriority(), description);

			if (diagnosis.needsApproval())
				System.out
						.println("Treatment is stored, but can't be scheduled yet. The treatment's diagnose needs a second opinion.");
			else
				System.out.println("Surgery is scheduled.");
		} catch (InsufficientWarehouseItemsException e) {
			System.out.println("Insufficient items in warehouse.");
		}catch(SchedulingException se){
			System.out.println("It is not possible to schedule this.\n" +
					"Insufficient resources in the warehouse.");
		}
	}

	/**
	 * Menu om als dokter de diagnose van een patient in te voeren
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void enterDiagnosis(DoctorController controller) {
		System.out.println("Second opinion required?\n");
		System.out.println("1. Yes");
		System.out.println("2. No");

			switch (readInt(2)) {
			case 1:
				requestSecondOpinion(controller);
				break;
			case 2:
				registerDiagnosis(controller);
				break;
			}
	}
	
	private static int askDiagnosisThreat() {
		System.out.println("Enter the threat level of the diagnosis\n");
		System.out.println("1. Low");
		System.out.println("2. Medium");
		System.out.println("3. High");
		return readInt(3);
	}

	/**
	 * Menu om als dokter een diagnose voor een patient te registreren en een
	 * tweede dokter toe te voegen voor de diagnose te controleren
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void registerDiagnosis(DoctorController controller) {
		System.out.println("Description:\n");
		scanner.nextLine();
		String description = scanner.nextLine();
		
		controller.enterDiagnosis(description,askDiagnosisThreat());
	}

	/**
	 * Menu om als dokter een tweede opinie te vragen aan een
	 * dokter uit een lijst van dokters.
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void requestSecondOpinion(DoctorController controller) {
		int i;

		System.out.println("Select one of the following:\n");

		for (i = 0; i < controller.getDoctors().size(); i++)
			System.out.println((i + 1) + ". " + controller.getDoctors().get(i));

		System.out.println((i + 1) + ". Back");

		int n = readInt(i + 1);

		if (--n == i)
			return;
		else {
			System.out.println("Description:\n");
			String description = scanner.next();

			controller.enterDiagnosis(controller.getDoctors().get(n),
					description,askDiagnosisThreat());
		}
	}

	/**
	 * Menu om als dokter een medische test te bestellen voor een patient
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void orderMedicalTest(DoctorController controller) {
		System.out.println("Select one of the following:\n");
		System.out.println("1. X-Ray Scan");
		System.out.println("2. Ultrasound Scan");
		System.out.println("3. Blood Analysis");
		System.out.println("4. Back");

		switch (readInt(4)) {
		case 1:
			requestDetailsXRayScan(controller);
			break;
		case 2:
			requestDetailsUltrasoundScan(controller);
			break;
		case 3:
			requestDetailsBloodAnalysis(controller);
			break;
		case 4:
			break;
		}
	}

	/**
	 * Menu om als dokter de details van een bloed analyse in te voeren Details
	 * zijn focus (string), en numberOfAnalyses (int)
	 * 
	 * @param contoller
	 * 		An instance of DoctorController
	 */
	private static void requestDetailsBloodAnalysis(DoctorController controller) {
		System.out.println("Enter focus:\n");
		
		scanner.nextLine();
		String focus = scanner.nextLine();

		System.out.println("Enter number of analyses (higher than zero):\n");
		int numberOfAnalyses = readInt();
		try{
		controller.orderBloodAnalysis(getPriority(), focus, numberOfAnalyses);
		}catch(SchedulingException se){
			System.out.println("Het is onmogelijk om dit te schedulen./n" +
					"De nodige resources zijn niet aanwezig in het ziekenhuis");
		};
		System.out.println("Blood Analysis scheduled.");
	}

	/**
	 * Menu om als dokter de details van een ultrasoundscan in te voeren Details
	 * zijn focus (string), recordVideo (boolean), recordImages (boolean)
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void requestDetailsUltrasoundScan(DoctorController controller) {
		System.out.println("Enter focus:\n");
		
		scanner.nextLine();
		String focus = scanner.nextLine();
		System.out.println("Record video? (type true or false):\n");
		boolean recordVideo = scanner.nextBoolean();
		System.out.println("Record Images? (type true or false):\n");
		boolean recordImages = scanner.nextBoolean();
		try{
		controller.orderUltrasoundScan(getPriority(),focus, recordVideo, recordImages);
		}catch(SchedulingException se){
			System.out.println("Het is onmogelijk om dit te schedulen./n" +
					"De nodige resources zijn niet aanwezig in het ziekenhuis");
		};
		System.out.println("Ultrasound scan scheduled.");
	}

	/**
	 * Menu om als dokter de details van een X-Rayscan in te voeren Details zijn
	 * bodyPart (string), nbOfNeededImages (int), levelOfZoom (int)
	 * 
	 * @param controller
	 * 		An instance of DoctorController
	 */
	private static void requestDetailsXRayScan(DoctorController controller) {
		System.out.println("Enter bodyPart:\n");
		scanner.nextLine();
		String bodyPart = scanner.nextLine();

		System.out
				.println("Enter number of needed images (higher than zero):\n");
		int numberOfImagesNeeded = readInt();

		System.out.println("Enter level of zoom (1-3):\n");
		int zoomlevel = readInt(3);
		try{
		controller.orderXRayScan(getPriority(),bodyPart, numberOfImagesNeeded, zoomlevel);
		}catch(SchedulingException se){
			System.out.println("Het is onmogelijk om dit te schedulen./n" +
					"De nodige resources zijn niet aanwezig in het ziekenhuis");
		};
		System.out.println("Xray scan scheduled.");
	}

	/**
	 * Menu om als Warehouse Manager acties uit te voeren. Een Warehouse Manager
	 * kan de stock van het warenhuis vullen, de tijd vooruit zetten en de lijst
	 * van bestellingen weergeven.
	 * 
	 * @param controller
	 * 		An instance of WarehouseController
	 */
	public static void warehouseManagerMenu(WarehouseManagerController controller) {
		boolean logout = false;
		
		while (!logout) {
			System.out.println("Select one of the following:\n");
			System.out.println("1. Fill Stock in Warehouse");
			System.out.println("2. List Orders");
			System.out.println("3. Log Out");
		
			switch (readInt(3)) {
			case 1:
				fillStock(controller);
				break;
			case 2:
				listOrders(controller);
				break;
			case 3:
				logout = logOut();
				break;
			}
		}
	}

	/**
	 * Menu om als warehouse manager de stock te vullen. De gebruiker kiest een
	 * categorie waarin bestellingen staan die gearriveerd zijn, maar nog niet
	 * geregistreerd.
	 * 
	 * @param controller
	 * 		An instance of WarehouseController
	 */
	public static void fillStock(WarehouseManagerController controller) {
		boolean outStock = false;
		
		while (!outStock) {
			System.out.println("Select one of the following:\n");
			System.out.println("1. Plaster");
			System.out.println("2. Medication Items");
			System.out.println("3. Meal");
			System.out.println("4. Back");

			switch (readInt(4)) {
			case 1:
				markArrivedPlasterOrders(controller);
				break;
			case 2:
				markArrivedMedicationItemsOrders(controller);
				break;
			case 3:
				markArrivedMealOrders(controller);
				break;
			case 4:
				outStock = true;
				break;
			}
		}
	}

	/**
	 * Selecteer de bestelling die gearriveerd is, vul eventueel de vervaldatum
	 * voor de bestelling in, en plaats de bestelling in de stock.
	 * 
	 * @param controller 
	 * 		An instance of WarehouseController
	 */
	public static void markArrivedPlasterOrders(WarehouseManagerController controller) {
		try {
			SortedSet<IStockOrder> orders = controller.getArrivedPlasterOrders();
			
			if (orders.isEmpty()) {
				System.out.println("There are no orders in the system\n");
			} else {
				markPlasterOrders(controller, orders);
			}
		} catch (IllegalAccessException accessException) {
			System.out.println("You need to be logged in as a warehouse manager\n");
			logOut();
		}
	}

	/**
	 * Menu om als warehouse manager plaster order te markeren
	 * als gearriveerd en te plaatsen in de stock.
	 * De warehouse manager heeft de keuze om een plaster
	 * order te kiezen uit de lijst of om terug te keren.
	 * 
	 * @param controller
	 * 		An instance of WarehouseController
	 * @param orders
	 * 		De lijst van plaster orders
	 */
	private static void markPlasterOrders(WarehouseManagerController controller,
			SortedSet<IStockOrder> orders) {
		System.out.println("Select one of the following:\n");
		int i = 0;
		for (; i < orders.size(); i++) {
			System.out.println((i + 1) + ". " + orders.toArray()[i]);
		}
		System.out.println((++i) + ". Back");

		int plasterOrderID = readInt()-1;
		if (plasterOrderID != i-1) {
			IStockOrder selectedOrder = (IStockOrder) orders.toArray()[plasterOrderID];
			controller.processArrivedOrder(selectedOrder,null);
			System.out.println("Stock items have been placed in the warehouse.\n");
		}
	}

	/**
	 * Selecteer een medicatie-item dat gearriveerd is, vul zijn vervaldatum in,
	 * en plaats het item in de stock
	 * 
	 * @param controller
	 * 		An instance of WarehouseController
	 */
	public static void markArrivedMedicationItemsOrders(
			WarehouseManagerController controller) {
		System.out.println("Select one of the following:\n");
		System.out.println("1. Aspirin");
		System.out.println("2. Vitamins");
		System.out.println("3. Activated carbon");
		System.out.println("4. Sleeping tablets");
		System.out.println("5. Misc");
		System.out.println("6. Back");
		
		MedicationItemType type = null;

		switch (readInt(6)) {
		case 1:
			type = MedicationItemType.ASPIRIN;
			break;
		case 2:
			type = MedicationItemType.VITAMINS;
			break;
		case 3:
			type = MedicationItemType.ACTIVATED_CARBON;
			break;
		case 4:
			type = MedicationItemType.SLEEPING_TABLETS;
			break;
		case 5:
			type = MedicationItemType.MISC;
			break;
		case 6:
			fillStock(controller);
			break;
		}

		if (type != null) {
			try {
				SortedSet<IStockOrder> orders = controller
						.getArrivedMedicationItemOrders(type);

				if (orders.isEmpty()) {
					System.out.println("There are no orders in the system\n");
					fillStock(controller);
				} else {
					markMedicationItemOrders(controller, orders);
					fillStock(controller);
				}
			} catch (IllegalAccessException accessException) {
				System.out.println("You need to be logged in as a warehouse manager\n");
				logOut();
			}
		}
	}

	/**
	 * Menu om als warehouse manager medicatie-item order te markeren
	 * als gearriveerd en te plaatsen in de stock.
	 * De warehouse manager heeft de keuze om een medicatie-item
	 * order te kiezen uit de lijst of om terug te keren.
	 * 
	 * @param controller
	 * 		An instance of WarehouseController
	 * @param orders
	 * 		De lijst van medicatie-item orders
	 */
	private static void markMedicationItemOrders(
			WarehouseManagerController controller,
			SortedSet<IStockOrder> orders) {
		System.out.println("Select one of the following:\n");
		int i = 0;
		for (; i < orders.size(); i++) {
			System.out
					.println((i + 1) + ". " + orders.toArray()[i]);
		}
		System.out.println((++i) + ". Back");

		int medicationItemOrderID = readInt()-1;
		if (medicationItemOrderID != i-1) {
			IStockOrder selectedOrder = (IStockOrder) orders
					.toArray()[medicationItemOrderID];
			System.out.println("Please enter expiration date. (\"YYYY/MM/DD\")");
			TimeStamp expirationDate = readDate();
			controller.processArrivedOrder(selectedOrder,expirationDate);
			System.out
					.println("Stock items have been placed in the warehouse.\n");
		}
	}
	
	/**
	 * Selecteer een maaltijd die gearriveerd gearriveerd is, vul zijn
	 * vervaldatum in, en plaats de maaltijd in de stock
	 * 
	 * @param controller
	 * 		An instance of WarehouseController
	 */
	public static void markArrivedMealOrders(WarehouseManagerController controller) {
		try {
			SortedSet<IStockOrder> orders = controller.getArrivedMealOrders();
			if (orders.isEmpty()) {
				System.out.println("There are no orders in the system\n");
			} else {
				markMealOrders(controller, orders);
			}
		} catch (IllegalAccessException accessException) {
			System.out.println("You need to be logged in as a warehouse manager\n");
			logOut();
		}
	}

	/**
	 * Menu om als warehouse manager meal order te markeren
	 * als gearriveerd en te plaatsen in de stock.
	 * De warehouse manager heeft de keuze om een meal
	 * order te kiezen uit de lijst of om terug te keren.
	 * 
	 * @param controller
	 * 		An instance of WarehouseController
	 * @param orders
	 * 		De lijst van meal orders
	 */
	private static void markMealOrders(WarehouseManagerController controller,
			SortedSet<IStockOrder> orders) {
		System.out.println("Select one of the following:\n");
		int i = 0;
		for (; i < orders.size(); i++) {
			System.out.println((i+1) + ". " + orders.toArray()[i]);
		}
		System.out.println((++i) + ". Back");

		int mealOrderID = readInt()-1;
		if (mealOrderID != i-1) {
			IStockOrder selectedOrder = (IStockOrder) orders.toArray()[mealOrderID];
			System.out.println("Please enter expiration date in the following format 'dd/MM/yyyy'");
			TimeStamp expirationDate = readDate();
			controller.processArrivedOrder(selectedOrder,expirationDate);
			System.out.println("Stock items have been placed in the warehouse.\n");
		}
	}

	/**
	 * Hulpmethode om een datum in te lezen.
	 * 
	 * @return Geldige time stamp indien datum correct ingevoerd
	 */
	private static TimeStamp readDate() {
		TimeStamp timeStamp = null;	
		boolean isTimeStamp = false;
			
		while (!isTimeStamp) {
			String date = scanner.next();
			try {
				timeStamp = TimeStamp.parseDateToTimeStamp(date);
				isTimeStamp = true;
			} catch(Exception parseException) {
				System.out.println("Please use the specified date format");
			}
		}

		return timeStamp;
	}
	
	/**
	 * Menu om als warehouse manager de lijst van bestellingen weer te geven
	 * 
	 * @param controller
	 * 		An instance of WarehouseController
	 */
	public static void listOrders(WarehouseManagerController controller) {
		boolean outListOrders = false;
		while (!outListOrders) {
			System.out.println("Select one of the following:\n");
			System.out.println("1. View latest plaster orders");
			System.out.println("2. View latest medication orders");
			System.out.println("3. View latest meal orders");
			System.out.println("4. Back");

			switch (readInt(4)) {
			case 1:
				viewLatestPlasterOrders(controller);
				break;
			case 2:
				viewLatestMedicationItemOrders(controller);
				break;
			case 3:
				viewLatestMealOrders(controller);
				break;
			case 4:
				outListOrders = true;
				break;
			}
		}
	}

	/**
	 * Menu waarin de laatste 20 plaster orders in de stock
	 * getoond worden. Wanneer je op enter drukt, keer je terug.
	 * 
	 * @param controller
	 * 		An instance of WarehouseController
	 */
	public static void viewLatestPlasterOrders(WarehouseManagerController controller) {
		try {
			SortedSet<IStockOrder> orders = controller
					.getLatestPlasterOrders(20);
			if (orders.isEmpty()) {
				System.out.println("There are no orders in the system\n");
			} else {
				for (IStockOrder order : orders) {
					System.out.println(order);
				}
			}

			System.out.println("Press enter to go back...\n");
			scanner.nextLine();
			scanner.nextLine();
		} catch (IllegalAccessException accessException) {
			System.out
					.println("You need to be logged in as a warehouse manager\n");
			logOut();
		} catch (IllegalArgumentException argumentException) {
			System.out.println("Something went wrong...\n");
		}
	}

	/**
	 * Menu waarin je gevraagd wordt een subtype van
	 * medicatie-item te kiezen. Eenmaal gekozen wordt
	 * een methode opgeroepen waar de laatste 20
	 * orders van dat subtype in de stock afgedrukt worden. 
	 * Wanneer je 6 kiest, keer je terug.
	 * 
	 * @param controller
	 * 		An instance of WarehouseController
	 */
	public static void viewLatestMedicationItemOrders(
			WarehouseManagerController controller) {
		System.out.println("Select one of the following:\n");
		System.out.println("1. View latest orders of aspirin");
		System.out.println("2. View latest orders of vitamins");
		System.out.println("3. View latest orders of activated carbon");
		System.out.println("4. View latest orders of sleeping tablets");
		System.out.println("5. View latest orders of misc");
		System.out.println("6. Back");

		MedicationItemType type = null;

		switch (readInt(6)) {
		case 1:
			type = MedicationItemType.ASPIRIN;
			break;
		case 2:
			type = MedicationItemType.VITAMINS;
			break;
		case 3:
			type = MedicationItemType.ACTIVATED_CARBON;
			break;
		case 4:
			type = MedicationItemType.SLEEPING_TABLETS;
			break;
		case 5:
			type = MedicationItemType.MISC;
			break;
		case 6:
			break;
		}

		if (type != null) {
			try {
				SortedSet<IStockOrder> orders = controller
						.getLatestMedicationItemOrders(type, 20);
				if (orders.isEmpty()) {
					System.out.println("There are no orders in the system\n");
				} else {
					for (IStockOrder order : orders) {
						System.out.println(order);
					}
				}

				System.out.println("Press enter to go back...\n");
				scanner.nextLine();
				scanner.nextLine();
			} catch (IllegalAccessException accessException) {
				System.out
						.println("You need to be logged in as a warehouse manager\n");
				logOut();
			} catch (IllegalArgumentException argumentException) {
				System.out.println("Something went wrong...\n");
			}
		}
	}

	/**
	 * Menu waarin de laatste 20 meal orders in de stock
	 * getoond worden. Wanneer je op enter drukt, keer je terug.
	 * 
	 * @param controller
	 * 		An instance of WarehouseController
	 */
	// 2000 LINES!!!!
	public static void viewLatestMealOrders(WarehouseManagerController controller) {
		try {
			SortedSet<IStockOrder> orders = controller.getLatestMealOrders(20);
			if (orders.isEmpty()) {
				System.out.println("There are no orders in the system\n");
			} else {
				for (IStockOrder order : orders) {
					System.out.println(order);
				}
			}

			System.out.println("Press enter to go back...\n");
			scanner.nextLine();
			scanner.nextLine();
		} catch (IllegalAccessException accessException) {
			System.out
					.println("You need to be logged in as a warehouse manager\n");
			logOut();
		} catch (IllegalArgumentException argumentException) {
			System.out.println("Something went wrong...\n");
		}
	}
	
	/**
	 * Deze methode zet de huidige dump om naar een HTML bestand.
	 * Nadat men de bestandsnaam ingeeft (zonder extensie) zal het bestand
	 * automatisch geopend worden.
	 */
	public static void showDump() {
		Dump dump = new Dump(sessionController.getBigHospital());
		String filename = "dump";
		dump.writeDumpHtml(filename);
		File document = new File(filename+".html");
		Desktop dt = Desktop.getDesktop();
	    try {
			dt.open(document);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Deze methode zet de huidige dump om naar een HTML bestand.
	 * Nadat men de bestandsnaam ingeeft (zonder extensie) zal het bestand
	 * automatisch geopend worden.
	 */
	public static void saveDump() {
		Dump dump = new Dump(sessionController.getBigHospital());
		String filename = "dump";
		dump.writeDumpHtml(filename);
		//File document = new File(filename+".html");

	}
	
}
