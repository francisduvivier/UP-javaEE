package system.controllers;

import system.controllers.DoctorController.OrderMedicalTestController;
import system.medicaltests.MedicalTest;


/**
 * Deze commando klasse dient voor het aanvragen van een medische test voor een
 * patient.
 * @Invar De MedicalTest mag niet null zijn
 * @Invar De OrderMedicalTestController mag niet null zijn.
 * @author SWOP Team 10
 */
public abstract class OrderMedicalTestCommand implements Command {
	protected final OrderMedicalTestController orderMedicalTestController;
	protected final MedicalTest medicalTest;
	
	/**
	 * Constructor van OrderMedicalTestCommand
	 * @Pre orderMedicalTestController\=null
	 * @Pre diagnosis\=null
	 * @param orderMedicalTestController
	 * 		Een instantie van OrderMedicalTestController
	 * @param medicalTest
	 * 		De medische test die georderd moet worden.
	 */
	protected OrderMedicalTestCommand(OrderMedicalTestController orderMedicalTestController, MedicalTest medicalTest) {
		if (orderMedicalTestController==null) throw new NullPointerException("De orderMedicalTestController is null");
		if (medicalTest==null) throw new NullPointerException("De MedicalTest is null");
		this.orderMedicalTestController = orderMedicalTestController;
		this.medicalTest=medicalTest;
	}
	/**
	 * Uitvoeren bestellen medische test.
	 */
	@Override
	public void execute() {
		orderMedicalTestController.orderMedicalTest(this.medicalTest);
	}

	/**
	 * Ongedaan maken van reeds bestelde medische test.
	 */
	@Override
	public void undo() {
		orderMedicalTestController.unOrderMedicalTest(this.medicalTest);
	}

	/**
	 * Herdoen van bestellen medische test die ongedaan gemaakt werd.
	 */
	@Override
	public void redo() {
		orderMedicalTestController.reOrderMedicalTest(this.medicalTest);
	}

}
