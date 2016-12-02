package testsuite;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import system.patients.Patient;
import system.time.TimeDuration;
import system.time.TimeStamp;
import system.warehouse.MedicationItemType;
import client.IDiagnosis;
import client.IStockOrder;


/**
 * In deze testklasse doorlopen we een scenario waarin Warehouse een hoofdrol speelt.
 * Van elke categorie halen we orders binnen, plaatsen we ze in de stock
 * en tonen de laatste orders.
 * 
 * @author Team 10
 *
 */

public class Scenario3_warehouse extends TestWithControllers {

	Random random = new Random();
	
	@Override
	@Before
	public void testInit() {
		super.testInit();
		sessionController.login(testC1Nurse1, getCampus(0));
		sessionController.login(testC1WarehouseManager1, getCampus(0));
	}

	/**
	 * Dit scenario zal de levensloop van plaster orders simuleren.
	 */
	@Test
	public void scenario3_1() {
		// plaster orders
		orderPlasterOrders();
		arrivePlasterOrders();
		stockPlasterOrders();
	}
	
	/**
	 * Dit scenario zal de levensloop van medication item orders simuleren.
	 */
	@Test
	public void scenario3_2() {
		orderMedicationItemOrders();
		arriveMedicationItemOrders();
		stockMedicationItemOrders();
	}
	
	/**
	 * Dit scenario zal de levensloop van meal orders simuleren.
	 */
	@Test
	public void scenario3_3() {
		orderMealOrders();
	}
	
	/**
	 *  We schrijven alle casts in voorraad voor uit beide campussen.
	 *  Nadat een cast wordt voorgeschreven zal automatisch een order
	 *  geplaatst worden.
	 */
	private void orderPlasterOrders() {
		sessionController.login(testC1WarehouseManager1, getCampus(0));
		assertEquals(warehouseController.getArrivedPlasterOrders().size(), 0);
		
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis("Diagnosis test");
		IDiagnosis diagnosis = doctorController.getDiagnoses().get(0);
		for (int i = 1; i <= 8; i++) {
			doctorController.prescribeCast(diagnosis, ""+i, i);
			//assertEquals(sessionController.getCurrentCampus().getWarehouse().
					//getStock(PlasterStock.class).getOrders().size(), i); // na elke prescribe wordt succesvol een nieuwe plaster besteld
		}
		sessionController.login(testC2Doctor1, getCampus(1));
		for (int i = 1; i <= 8; i++) {
			doctorController.prescribeCast(diagnosis, ""+i, i);
//			assertEquals(sessionController.getCurrentCampus().getWarehouse().
//					getStock(PlasterStock.class).getOrders().size(), i);
		}		
	}
	
	/**
	 * We zetten de tijd vooruit zodat de orders kunnen aankomen.
	 */
	private void arrivePlasterOrders() {
		sessionController.login(testC1WarehouseManager1, getCampus(0));
		assertEquals(warehouseController.getArrivedPlasterOrders().size(), 0);
		advanceTime();
		sessionController.login(testC1WarehouseManager1, getCampus(0));
		assertEquals(warehouseController.getArrivedPlasterOrders().size(), 8);
		sessionController.login(testC2WarehouseManager1, getCampus(1));
		assertEquals(warehouseController.getArrivedPlasterOrders().size(), 8);
	}
	
	/**
	 * We plaatsen de gearriveerde plaster orders in de stock.
	 */
	private void stockPlasterOrders() {
		// campus 1
		sessionController.login(testC1WarehouseManager1, getCampus(0));
		//assertEquals(warehouseController.getCurrentCampus().getWarehouse().getDefaultStock(StockType.PLASTER).getStockSize(), 0);
		for (IStockOrder plasterOrder: warehouseController.getArrivedPlasterOrders())
			warehouseController.processArrivedOrder(plasterOrder, TimeStamp.END_OF_DAYS);
		//assertEquals(warehouseController.getCurrentCampus().getWarehouse().getDefaultStock(StockType.PLASTER).getStockSize(), 8);
		
		// campus 2
		sessionController.login(testC2WarehouseManager1, getCampus(1));
		//assertEquals(warehouseController.getCurrentCampus().getWarehouse().getDefaultStock(StockType.PLASTER).getStockSize(), 0);
		for (IStockOrder plasterOrder: warehouseController.getArrivedPlasterOrders())
			warehouseController.processArrivedOrder(plasterOrder, TimeStamp.END_OF_DAYS);
		//assertEquals(warehouseController.getCurrentCampus().getWarehouse().getDefaultStock(StockType.PLASTER).getStockSize(), 0);
	}
	
	/**
	 *  We schrijven alle medicatie items in voorraad voor uit beide campussen.
	 *  Nadat medicatie wordt voorgeschreven zal automatisch een order
	 *  geplaatst worden.
	 */
	private void orderMedicationItemOrders() {
		assertEquals(warehouseController.getAllArrivedMedicationItemOrders().size(), 0);
		
		sessionController.login(testC1Doctor1, getCampus(0));
		doctorController.consultPatientFile(testC1Patient1);
		doctorController.enterDiagnosis("Diagnosis test");
		IDiagnosis diagnosis = doctorController.getDiagnoses().get(0);
		for (int i = 1; i <= 10; i++) {
			List<MedicationItemType> medicationItems = new ArrayList<MedicationItemType>();
			medicationItems.add(MedicationItemType.ASPIRIN);
			doctorController.prescribeMedication(diagnosis, ""+i, true, medicationItems);
			if (i >= 6) {
				//assertEquals(sessionController.getCurrentCampus().getWarehouse().getCompositeStock(StockType.MEDICATION_ITEM).
				//getOrders(MedicationItemType.ASPIRIN).size(), i-5); 
			}
					// na elke prescribe wordt succesvol een nieuwe medication item besteld
		}
		sessionController.login(testC2Doctor1, getCampus(1));
		for (int i = 1; i <= 10; i++) {
			List<MedicationItemType> medicationItems = new ArrayList<MedicationItemType>();
			medicationItems.add(MedicationItemType.ASPIRIN);
			doctorController.prescribeMedication(diagnosis, ""+i, true, medicationItems);
			if (i >= 6){
				//assertEquals(sessionController.getCurrentCampus().getWarehouse().
				//getMedicationItemStock().getOrders(MedicationItemType.ASPIRIN).size(), i-5); 
					// na elke prescribe wordt succesvol een nieuwe medication item besteld
			}
		}		
	}
	
	/**
	 * We zetten de tijd vooruit zodat de orders kunnen aankomen.
	 */
	private void arriveMedicationItemOrders() {
		sessionController.login(testC1WarehouseManager1, getCampus(0));
		assertEquals(warehouseController.getAllArrivedMedicationItemOrders().size(), 0);
		advanceTime();
		sessionController.login(testC1WarehouseManager1, getCampus(0));
		assertEquals(warehouseController.getAllArrivedMedicationItemOrders().size(), 5);
		sessionController.login(testC2WarehouseManager1, getCampus(1));
		assertEquals(warehouseController.getAllArrivedMedicationItemOrders().size(), 5);
	}
	
	/**
	 * We plaatsen de gearriveerde medication item orders in de stock.
	 */
	private void stockMedicationItemOrders() {
		// campus 1
		sessionController.login(testC1WarehouseManager1, getCampus(0));
		//assertEquals(warehouseController.getCurrentCampus().getWarehouse().getDefaultStock(StockType.MEAL).getStockSize(), 0);
		for (IStockOrder medicationItemOrder: warehouseController.getArrivedMedicationItemOrders(MedicationItemType.ASPIRIN))
			warehouseController.processArrivedOrder(medicationItemOrder, TimeStamp.END_OF_DAYS);
		//assertEquals(warehouseController.getCurrentCampus().getWarehouse().getDefaultStock(StockType.MEAL).getStockSize(), 10);
		
		// campus 2
		sessionController.login(testC2WarehouseManager1, getCampus(1));
		//assertEquals(warehouseController.getCurrentCampus().getWarehouse().getDefaultStock(StockType.MEAL).getStockSize(), 0);
		for (IStockOrder medicationItemOrder: warehouseController.getArrivedMedicationItemOrders(MedicationItemType.ASPIRIN))
			warehouseController.processArrivedOrder(medicationItemOrder, TimeStamp.END_OF_DAYS);
		//assertEquals(warehouseController.getCurrentCampus().getWarehouse().getDefaultStock(StockType.MEAL).getStockSize(), 10);
	}
	
	/**
	 *  We schrijven alle meal items in voorraad voor uit beide campussen.
	 *  Nadat een meal wordt voorgeschreven zal automatisch een order
	 *  geplaatst worden.
	 */
	private void orderMealOrders() {
		sessionController.login(testC1WarehouseManager1, getCampus(0));
		assertEquals(warehouseController.getArrivedMealOrders().size(), 0);
		registerPatients();
		stockMealOrdersDayZero();
		sessionController.login(testHospitalAdmin, getCampus(0));
		adminController.advanceTime(TimeStamp.addedToTimeStamp(TimeDuration.days(1), sessionController.getTime()));
		stockMealOrdersDayOne();
		removePatients();
		sessionController.login(testHospitalAdmin, getCampus(0));
		adminController.advanceTime(TimeStamp.addedToTimeStamp(TimeDuration.days(1), sessionController.getTime()));
		stockMealOrdersDayTwo();
		sessionController.login(testC2Nurse1, getCampus(1));		
	}

	private void stockMealOrdersDayZero() {
		sessionController.login(testC1WarehouseManager1, getCampus(0));
	
		assertEquals(warehouseController.getNbMealInStock(), 120);
	}

	private void registerPatients() {
		sessionController.login(testC1Nurse1, getCampus(0));
		for (int i = 1; i <= 19; i++) {
			nurseController.registerNewPatient("Patient "+i);
		}
	}
	
	private void removePatients() {
		sessionController.login(testC1Nurse1, getCampus(0));

		for (Patient patient:nurseController.getRegisteredPatients()) {
			sessionController.login(testC1Doctor1, getCampus(0));
			doctorController.consultPatientFile(patient);
			doctorController.dischargePatient();
		}
	}

	/**
	 * We plaatsen de gearriveerde meal orders in de stock.
	 */
	private void stockMealOrdersDayOne() {
		sessionController.login(testC1WarehouseManager1, getCampus(0));
		assertEquals(warehouseController.getNbMealInStock(), 60);		

		stockMealOrders();
		
		assertEquals(warehouseController.getNbMealInStock(), 60);
	}
	
	private void stockMealOrdersDayTwo() {
		stockMealOrders();
		
		assertEquals(warehouseController.getNbMealInStock(), 135);
	}
	
	private void stockMealOrders() {
		sessionController.login(testC1WarehouseManager1, getCampus(0));
		for (IStockOrder mealOrder: warehouseController.getArrivedMealOrders())
			warehouseController.processArrivedOrder(mealOrder, new TimeStamp(2012, 2, 2, 2, 2));
	}

	/**
	 * De hospital administrator zet de tijd vooruit zodat we de
	 * resultaten kunnen verwerken
	 */
	private void advanceTime() {
		sessionController.login(testHospitalAdmin, getCampus(0));
        adminController.advanceTime(
        		TimeStamp.addedToTimeStamp(TimeDuration.days(2), 
        		sessionController.getTime()));
	}
	
}
