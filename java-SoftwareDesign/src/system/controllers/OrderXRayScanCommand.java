package system.controllers;

import system.controllers.DoctorController.OrderMedicalTestController;
import system.medicaltests.XRayScan;
import system.patients.Patient;
import system.scheduling.Priority;
/**
 * Deze commando klasse dient voor het aanvragen van een Ultrasound Scan test voor een
 * patient.
 * @author SWOP Team 10
 */
class OrderXRayScanCommand extends OrderMedicalTestCommand {

	/**
	 * Constructor van OrderXRayScanCommand
	 * 
	 * @param orderMedicalTestController
	 * 		Een instantie van OrderMedicalTestController
	 * @param patient
	 * 		Respectievelijke patient
	 * @param bodyPart
	 * 		Welk lichaamsdeel de scan moet krijgen
	 * @param numberOfImagesNeeded
	 * 		Hoeveel afbeeldingen genomen moeten worden
	 * @param zoomlevel
	 * 		Tot welk leven ingezoomd moet worden
	 */
	OrderXRayScanCommand(OrderMedicalTestController orderMedicalTestController,
			Patient patient, String bodyPart, int numberOfImagesNeeded,
			int zoomlevel) {
		super(orderMedicalTestController,new XRayScan(patient, bodyPart, numberOfImagesNeeded,
				zoomlevel));
		}
	
	/**
	 * Zelfde met priority
	 */
	OrderXRayScanCommand(OrderMedicalTestController orderMedicalTestController,
			Patient patient, Priority priority, 
			String bodyPart, int numberOfImagesNeeded, int zoomlevel) {
		super(orderMedicalTestController,new XRayScan(patient, priority, 
				bodyPart, numberOfImagesNeeded,zoomlevel));
		}
}
