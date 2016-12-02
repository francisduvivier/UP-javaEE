package system.controllers;

import system.controllers.DoctorController.OrderMedicalTestController;
import system.medicaltests.UltrasoundScan;
import system.patients.Patient;
import system.scheduling.Priority;
/**
 * Deze commando klasse dient voor het aanvragen van een Ultrasound Scan test voor een
 * patient.
 * @author SWOP Team 10
 */
class OrderUltrasoundScanCommand extends OrderMedicalTestCommand {

	/**
	 * Constructor van OrderUltraSoundScanCommand
	 * 
	 * @param orderMedicalTestController
	 * 		Een instantie van OrderMedicalTestController
	 * @param patient
	 * 		Patient waarvoor de test besteld moet worden
	 * @param focus
	 * 		Focus van de medische test
	 * @param recordVideo
	 * 		Boolean die dicteert of een video moet opgenomen worden
	 * @param recordImages
	 * 		Boolean die dicteert of afbeeldingen getrokken moeten worden
	 */
	OrderUltrasoundScanCommand(
			OrderMedicalTestController orderMedicalTestController,
			Patient patient, String focus, boolean recordVideo,
			boolean recordImages) {
		super(orderMedicalTestController,new UltrasoundScan(patient, focus, recordVideo,
				recordImages));
	}
	/**
	 * Zelfde met priority.
	 */
	OrderUltrasoundScanCommand(
			OrderMedicalTestController orderMedicalTestController,
			Patient patient, Priority priority,
			String focus, boolean recordVideo, boolean recordImages) {
		super(orderMedicalTestController,new UltrasoundScan(patient, priority, 
				focus, recordVideo,recordImages));
	}
}
