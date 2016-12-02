package system.debug;


import client.IHospital;
import system.campus.Campus;
import system.campus.Hospital;
import system.io.CustomFile;
import system.machines.Machine;
import system.medicaltests.MedicalTest;
import system.patients.Diagnosis;
import system.patients.Patient;
import system.repositories.StaffType;
import system.results.Result;
import system.staff.StaffMember;
import system.treatments.Treatment;
import system.warehouse.MedicationItemType;
import system.warehouse.stock.StockType;
import annotations.SystemAPI;
@SystemAPI
public class Dump {

	private Hospital hospital;
	
	/**
	 * Initialisatie van een nieuwe Dump met gegeven Hospital
	 * 
	 * @param hospital2
	 * 			De gegeven hospital
	 */
	@SystemAPI
	public Dump(IHospital hospital2) {
		this.hospital = (Hospital) hospital2;
	}
	
	/**
	 * Een html-formaat van de dump wegschrijven naar een bestand.
	 * 
	 * @param filename
	 * 			De gewenste bestandsnaam (zonder extensie)
	 */
	@SystemAPI
	public void writeDumpHtml(String filename) {
		CustomFile.writeTxt(filename+".html", makeDumpHtml());
	}
	
	/**
	 * Dump omzetten naar een HTML-formaat
	 * 
	 * @return Een string bevattende de HTML-opmaak en -inhoud
	 */
	@SystemAPI
	public String makeDumpHtml() {
		String string = "<!DOCTYPE html>\n<html>\n" +
				"<head>\n<title>Dump - [SWOP][Groep 10]</title>\n" +
				"<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">\n" +
				"<style type=\"text/css\">\n" +
				"table {border: 1px solid #000;}\n" +
				"td {border: 1px solid #000;}\n" +
				"*, html {padding: 3px;}\n" +
				"h1 {margin: 0px 0 2px 0;}\n" +
				"h2 {margin: 10px 0px 2px 0px;}\n" +
				"h3 {margin: 2px 0px 2px 0px;}\n" +
				"h4 {margin: 2px 0px 2px 0px;}\n" +
				"hr {margin: 10px 0px 10px 0px;}\n" +
				".menu {position: fixed; top: 0px; background: #fff;}\n" +
				"</style>\n" +
				"</head>\n<body>\n"; 
		
		string += "<div class=menu>\n<a href=#hospitaltime>1. Hospital Time</a>\n<a href=#personeel>2. Hospital Personeel</a>\n<a href=#campussen>3. Campus Personeel</a>\n" +
				"<a href=#afspraken>4. Afspraken</a>\n<a href=#patienten>5. Patienten</a>\n<a href=#machines>6. Machines</a>\n" +
				"<a href=#warehouse>7. Warehouse</a>\n</div>\n";
		
		string += "<h1>Hospital Dump</h1>\n";
		string += "<h2 id=hospitaltime>1. Hospital Time</h2>\n" + "Hospital time: " + getBigHospital().getHospitalTime().getTime().toString() + "\n";
		
		string += "<hr />\n";
		
		string += "<h2 id=personeel>2. Hospital Personeel</h2>\n";
		
		string += "<h3>2.1. Doctors</h3>\n";
		string += "<table>\n";
		string += "<tr><th>Naam</th></tr>\n";
		for(StaffMember doctor: getBigHospital().getStaffRepository().getStaffMembers(StaffType.DOCTOR)) {
			string += "<tr><td>" + doctor.getName() + "</td></tr>" + "\n";
		}
		string += "</table>\n";
		
		string += "<h3>2.2.Hospital Administrator</h3>\n";
		string += "<table>\n";
		string += "<tr><th>Naam</th></tr>\n";
		string += "<tr><td>" + getBigHospital().getStaffRepository().getStaffMembers(StaffType.HOSPITAL_ADMINISTRATOR).get(0).getName() + "</td></tr>" + "\n";
		string += "</table>\n";
		
		string += "<hr />\n";
		
		string += "<h2 id=campussen>3. Campus Personeel</h2>";
		int campusNb = 1;
		for(Campus campus: getBigHospital().getCampuses()) {
			string += "<h3>3."+campusNb+". Campus " + campusNb + "</h3>\n";
			
			string += "<h4>3."+campusNb+".1. Nurses</h4>\n";
			string += "<table>\n";
			string += "<tr><th>Naam</th></tr>\n";
			for(StaffMember nurse: campus.getStaffRepository().getStaffMembers(StaffType.NURSE)) {
				string += "<tr><td>" + nurse.getName() + "</td></tr>" + "\n";
			}
			string += "</table>\n";
			
			
			string += "<h4>3."+campusNb+".2. Warehouse Managers</h4>\n";
			string += "<table>\n";
			string += "<tr><th>Naam</th></tr>\n";
			for(StaffMember warehouseManager: campus.getStaffRepository().getStaffMembers(StaffType.WAREHOUSE_MANAGER)) {
				string += "<tr><td>" + warehouseManager.getName() + "</td></tr>" + "\n";
			}
			string += "</table>\n";
			campusNb++;
		}
		
		string += "<hr />\n";
		
		string += "<h2 id=afspraken>4. Afspraken</h2>\n";
		
		string += "<h3>4.1. Doctors</h3>\n";
		string += "<table>\n";
		string += "<tr><th>Naam</th><th>Afspraak</th></tr>\n";
		for (StaffMember doctor: getBigHospital().getStaffRepository().getStaffMembers(StaffType.DOCTOR)) {
			string += "<tr><td>" + doctor.getName() + "</td><td><table>";
			string += "<tr><th>Begindatum</th><th>Einddatum</th><th>Prioriteit</th><th>Details</th></tr>\n";
			String[] appointments = doctor.getSchedule().scheduleToStringToArray();
			for (int i = 0; i < appointments.length; i++) {
				string += "<tr><td>" + getColumnFromAppointmentString(appointments[i], 0) + "</td><td>" + getColumnFromAppointmentString(appointments[i], 1) + "</td><td>" + getColumnFromAppointmentString(appointments[i], 2) + "</td><td>" + getColumnFromAppointmentString(appointments[i], 3) + "</td></tr>\n"; 			}
			string += "</table></td></tr>\n";
		}
		string += "</table>\n";
		
		string += "<h3>4.2. Nurses</h3>\n";
		string += "<table>\n";
		for(Campus campus: getBigHospital().getCampuses()) {
			string += "<tr><th>Naam</th><th>Afspraak</th></tr>\n";
			for(StaffMember nurse: campus.getStaffRepository().getStaffMembers(StaffType.NURSE)) {
				string += "<tr><td>" + nurse.getName() + "</td><td><table>";
				string += "<tr><th>Begindatum</th><th>Einddatum</th><th>Prioriteit</th><th>Details</th></tr>\n";

				String[] appointments = nurse.getSchedule().scheduleToStringToArray();
				for (int i = 0; i < appointments.length; i++) {
					string += "<tr><td>" + getColumnFromAppointmentString(appointments[i], 0) + "</td><td>" + getColumnFromAppointmentString(appointments[i], 1) + "</td><td>" + getColumnFromAppointmentString(appointments[i], 2) + "</td><td>" + getColumnFromAppointmentString(appointments[i], 3) + "</td></tr>\n";  
				}
				string += "</table></td></tr>\n";
			}
		}
		string += "</table>\n";
		
		string += "<h3>4.3. Patients</h3>\n";
		string += "<table>\n"; 
		for(Campus campus: getBigHospital().getCampuses()) {
			string += "<tr><th>Naam</th><th>Afspraak</th></tr>\n";
			for(Patient patient: campus.getPatientRepository().getRegisteredPatients()) {
				string += "<tr><td>" + patient.getName() + "</td><td><table>";
				string += "<tr><th>Begindatum</th><th>Einddatum</th><th>Prioriteit</th><th>Details</th></tr>\n";
				String[] appointments = patient.getSchedule().scheduleToStringToArray();
				for (int i = 0; i < appointments.length; i++) {
					string += "<tr><td>" + getColumnFromAppointmentString(appointments[i], 0) + "</td><td>" + getColumnFromAppointmentString(appointments[i], 1) + "</td><td>" + getColumnFromAppointmentString(appointments[i], 2) + "</td><td>" + getColumnFromAppointmentString(appointments[i], 3) + "</td></tr>\n"; 
				}
				string += "</table></td></tr>\n";
			}
		}
		string += "</table>\n";
		
		string += "<hr />\n";
		
		string += "<h2 id=patienten>5. Patienten</h2>\n";
		campusNb = 1;
		for(Campus campus: getBigHospital().getCampuses()) {
			string += "<h3>5." + (campusNb) + ". Campus " + (campusNb++) + "</h3>\n"; 
			
			for(Patient patient: campus.getPatientRepository().getRegisteredPatients()) {
				string += "<h4>5." + campus.getPatientRepository().getRegisteredPatients().indexOf(patient) + ". Patient " + patient.getName() + "</h4>\n";
				
				string += "<strong>5." + campus.getPatientRepository().getRegisteredPatients().indexOf(patient) + ".1. Diagnosissen</strong>\n";
				string += "<table>\n<tr><th>Beschrijving</th><th>Treatments</th></tr>";
				for (Diagnosis diagnosis: patient.getPatientFile().getDiagnoses()) {
					string += "<tr><td>" + diagnosis.getDescription() + "</td>";
					string += "<td><table>";
					if (diagnosis.getTreatments() != null) {
						for (Treatment treatment: diagnosis.getTreatments()) {
							string += "<tr><td>" + treatment.getEventType().toString() + "</td></tr>\n";
						}
					}
					string += "</table></td></tr>\n";
				}
				string += "</table>\n";
				
				string += "<strong>5." + campus.getPatientRepository().getRegisteredPatients().indexOf(patient) + ".2. Medical Tests</strong>\n";
				string += "<table><tr><th>Soort</th><th>Duur</th></tr>\n";
				if (patient.getPatientFile().getMedicalTests() != null) {
					for (MedicalTest medicalTest: patient.getPatientFile().getMedicalTests()) {
						string += "<tr><td>" + medicalTest.getEventType() + "</td><td>" + medicalTest.getDuration() + "</td></tr>\n";
					}
				}
				string += "</table>\n";
				
				string += "<strong>5." + campus.getPatientRepository().getRegisteredPatients().indexOf(patient) + ".3. Results</strong>\n";
				string += "<table><tr><th>Soort</th><th>Details</th></tr>\n";
				if (patient.getPatientFile().getResults() != null) {
					for (Result result: patient.getPatientFile().getResults()) {
						string += "<tr><td>" + result.getResultType().toString() + "</td><td>" + result.getDetails() + "</td></tr>\n";
					}
				}
				string += "</table>\n";
			}
			
		}
		
		string += "<hr />\n";
		
		string += "<h2 id=machines>6. Machines</h2>\n";
		campusNb = 1;
		for(Campus campus: getBigHospital().getCampuses()) {
			string += "<h3>6." + (campusNb) + ". Campus " + (campusNb++) + "</h3>\n"; 
			string += "<table>\n";
			string += "<tr><th>Machine ID</th><th>Soort</th><th>Verdieping</th><th>Kamer</th></tr>\n";
			if (campus.getMachineRepository().getMachines() != null) {
				for(Machine machine: campus.getMachineRepository().getMachines()) {
					string += "<tr><td>" + machine.getID().toString() + "</td><td>" + machine.getResourceType().toString() +  "</td><td>" + machine.getLocation().getFloor() + "</td><td>" + machine.getLocation().getRoom() + "</td></tr>\n";
				}
			}
			string += "</table>\n";
		}
		
		string += "<hr />\n";
		
		string += "<h2 id=warehouse>7. Warehouse</h2>\n";
		campusNb = 1;
		for(Campus campus: getBigHospital().getCampuses()) {
			string += "<h3>7." + campusNb + ". Campus " + campusNb++ + "</h3>\n"; 
			string += "<table>\n";
			string += "<tr><th>Soort</th><th>Aantal in voorraad</th></tr>\n";
				string += "<tr><td>Maaltijden</td><td>" + campus.getWarehouse().getStockList().getDefaultStock(StockType.MEAL).getStockSize() + "</td></tr>" + "\n";
				string += "<tr><td>Plaasters</td><td>" + campus.getWarehouse().getStockList().getDefaultStock(StockType.PLASTER).getStockSize() + "</td></tr>" + "\n";
				string += "<tr><td>'Activated carbon'</td><td>" + campus.getWarehouse().getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getStockSize(MedicationItemType.ACTIVATED_CARBON) + "</td></tr>" + "\n";
				string += "<tr><td>Aspirines</td><td>" + campus.getWarehouse().getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getStockSize(MedicationItemType.ASPIRIN) + "</td></tr>" + "\n";
				string += "<tr><td>Slaaptabletten</td><td>" + campus.getWarehouse().getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getStockSize(MedicationItemType.SLEEPING_TABLETS) + "</td></tr>" + "\n";
				string += "<tr><td>Vitaminen</td><td>" + campus.getWarehouse().getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getStockSize(MedicationItemType.VITAMINS) + "</td></tr>" + "\n";
				string += "<tr><td>Diversen</td><td>" + campus.getWarehouse().getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getStockSize(MedicationItemType.MISC) + "</td></tr>" + "\n";
			string += "</table>\n";
		}
		
		string += "</body>\n</html>";
		return string;
	}
	
	/**
	 * Een kolom verkrijgen uit een tabel
	 * 
	 * @param appointment
	 * 			De tabel bevattende enkele kolommen informatie
	 * @param i
	 * 			Het gewenste kolomnummer
	 * @return string met de inhoud van de gewenste kolom
	 */
	private String getColumnFromAppointmentString(String appointment, int i) {
		String[] columns = appointment.split(" - ");
		if (i >= columns.length) 
			return "";
		return columns[i];
	}
	
	/**
	 * Geeft de hospital weer waar de dump met werkt
	 * 
	 * @return hospital
	 */
	@SystemAPI
	public Hospital getBigHospital() {
		return hospital;
	}
}
