package system.controllers;

import system.controllers.DoctorController.OrderMedicalTestController;
import system.medicaltests.BloodAnalysis;
import system.patients.Patient;
import system.scheduling.Priority;

class OrderBloodAnalysisCommand extends OrderMedicalTestCommand {

	/**
	 * Constructor van BloodAnalysisCommand
	 * 
	 * @param orderMedicalTestController
	 *        Een instantie van OrderMedicalTestController
	 * @param patient
	 *        Patient waarop de test uitgevoerd moet worden
	 * @param focus
	 *        Focus van de medische test
	 * @param numberOfAnalyses
	 *        Het aantal analyses dat uitgevoerd moet worden
	 */
	OrderBloodAnalysisCommand(
			OrderMedicalTestController orderMedicalTestController,
			Patient patient, String focus, int numberOfAnalyses) {
		super(orderMedicalTestController,  new BloodAnalysis(patient, focus, numberOfAnalyses));

	}
	/**
	 * Zelfde met priority.
	 */
	OrderBloodAnalysisCommand(
			OrderMedicalTestController orderMedicalTestController,
			Patient patient, Priority priority,
			String focus, int numberOfAnalyses) {
		super(orderMedicalTestController,  new BloodAnalysis(patient, priority, focus, numberOfAnalyses));

	}
}
