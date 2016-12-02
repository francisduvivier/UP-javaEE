package system.warehouse.tests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import system.campus.Campus;
import system.campus.Hospital;
import system.controllers.SessionController;
import system.patients.Patient;
import system.time.Time;
import system.time.TimeDuration;
import system.time.TimeStamp;
import system.warehouse.Meal;
import system.warehouse.MedicationItem;
import system.warehouse.MedicationItemType;
import system.warehouse.Plaster;
import system.warehouse.Warehouse;
import system.warehouse.stock.StockType;
import system.warehouse.stock.condition.HasEnoughFoodCondition;
import system.warehouse.stock.condition.HasMedicationItemsCondition;
import system.warehouse.stock.condition.HasPlasterCondition;

/**
 * Unit Test Warehouse.java
 * 
 * @author swop team 10
 */

public class WarehouseTest {

	private Hospital hospital;
	private SessionController sessionController;
	private Warehouse warehouse;
	private TimeStamp expirationDate;

	@Before
	public void testInit() {
		sessionController = new SessionController();
		hospital = (Hospital) sessionController.getBigHospital();
		sessionController.setCurrentCampus((Campus)sessionController.getCampuses().get(0));
		warehouse = sessionController.getCurrentCampus().getWarehouse();
		Time time = hospital.getHospitalTime();
		expirationDate = TimeStamp.addedToTimeStamp(
				TimeDuration.days(1), time.getTime());
	}

	@Test
	public void testPlaster() {
		assertTrue(warehouse.getStockList().conditionIsTrue(new HasPlasterCondition()));
		assertEquals(warehouse.getStockList().getDefaultStock(StockType.PLASTER).getStockSize(), 8);

		for (int i = 0; i < 8; i++) {
			warehouse.getStockList().getDefaultStock(StockType.PLASTER).removeWarehouseItem(); // should remove 8 plasters
		}

		assertFalse(warehouse.getStockList().conditionIsTrue(new HasPlasterCondition()));
		assertEquals(warehouse.getStockList().getDefaultStock(StockType.PLASTER).getStockSize(), 0);
		assertNull(warehouse.getStockList().getDefaultStock(StockType.PLASTER).removeWarehouseItem());

		for (int i = 0; i < 4; i++) {
			warehouse.getStockList().getDefaultStock(StockType.PLASTER).addWarehouseItem(new Plaster()); // should add 8 plasters
		}

		assertTrue(warehouse.getStockList().conditionIsTrue(new HasPlasterCondition()));
		assertEquals(warehouse.getStockList().getDefaultStock(StockType.PLASTER).getStockSize(), 4);
	}

	@Test
	public void testMeal() {
		for (int i = 0; i < 10; i++) {
			Campus campus = (Campus)sessionController.getCampuses().get(0);
			campus.getPatientRepository().addPatient(new Patient("" + i));
		}

		assertTrue(warehouse.getStockList().conditionIsTrue(new HasEnoughFoodCondition()));
		assertEquals(warehouse.getStockList().getDefaultStock(StockType.MEAL).getStockSize(), 120);

		for (int i = 0; i < 120; i++)
			warehouse.getStockList().getDefaultStock(StockType.MEAL).removeWarehouseItem(); // should remove 120 meals

		assertFalse(warehouse.getStockList().conditionIsTrue(new HasEnoughFoodCondition()));
		assertEquals(warehouse.getStockList().getDefaultStock(StockType.MEAL).getStockSize(), 0);
		try {
			warehouse.getStockList().getDefaultStock(StockType.MEAL).removeWarehouseItem(); // should remove 1 meal
			//fail("IllegalArgumentException zou geworpen moeten zijn omdat er geen meals zouden mogen zijn");
		} catch (IllegalArgumentException e) {
		}

		for (int i = 0; i < (6*10)+6; i++) {
			warehouse.getStockList().getDefaultStock(StockType.MEAL).addWarehouseItem(new Meal(expirationDate));
		}

		assertTrue(warehouse.getStockList().conditionIsTrue(new HasEnoughFoodCondition()));
		assertEquals(warehouse.getStockList().getDefaultStock(StockType.MEAL).getStockSize(), 66);
	}

	@Test
	public void testMedicationItem() {
		for (MedicationItemType type : MedicationItemType.values()) {
			List<MedicationItemType> types = new ArrayList<MedicationItemType>();
			types.add(type);
			assertTrue(warehouse.getStockList().conditionIsTrue(new HasMedicationItemsCondition(types)));
			for (int i = 0; i < 9; i++)
				warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).removeWarehouseItem(type);
			assertEquals(warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getNbItemsAlreadyOrdered(type), 9);
		}

		for (MedicationItemType type : MedicationItemType.values()) {
			for (int i = 0; i < 10; i++) {
				warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).removeWarehouseItem(type);
			}
		}

		for (MedicationItemType type : MedicationItemType.values()) {
			List<MedicationItemType> types = new ArrayList<MedicationItemType>();
			types.add(type);
			assertFalse(warehouse.getStockList().conditionIsTrue(new HasMedicationItemsCondition(types)));
			assertEquals(warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getStockSize(type), 0);
			assertNull(warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).removeWarehouseItem(type));
		}

		for (MedicationItemType type : MedicationItemType.values()) {
			for (int i = 0; i < 5; i++) {
				warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).addWarehouseItem(new MedicationItem(expirationDate, type));
			}
		}

		for (MedicationItemType type : MedicationItemType.values()) {
			List<MedicationItemType> types = new ArrayList<MedicationItemType>();
			types.add(type);
			assertTrue(warehouse.getStockList().conditionIsTrue(new HasMedicationItemsCondition(types)));
			assertEquals(warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getOrders(type).size(), 5);
			
			
		}
	}

	@Test
	public void testUpdateFromTime() {
		for (int i = 0; i < 10; i++) {
			Campus campus = (Campus) sessionController.getCampuses().get(0);
			campus.getPatientRepository().addPatient(new Patient("" + i));
		}

		for (int i = 0; i < 100; i++)
			warehouse.getStockList().getDefaultStock(StockType.MEAL).removeWarehouseItem();

		for (int i = 0; i < 20; i++) {
			warehouse.getStockList().getDefaultStock(StockType.MEAL).addWarehouseItem(new Meal(expirationDate));
		}

		for (MedicationItemType type : MedicationItemType.values()) {
			for (int i = 0; i < 10; i++) {
				warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).removeWarehouseItem(type);
				warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).addWarehouseItem(new MedicationItem(expirationDate, type));
			}
		}

		Time time = hospital.getHospitalTime();
		time.setTime(TimeStamp.addedToTimeStamp(
				TimeDuration.days(2), time.getTime()));
	}

//	TODO gebruiken of verwijderen
//	@Test
//	public void removeTest() {
//		Warehouse warehouse = hospital.getWarehouse();
//
//		assertEquals(warehouse.getNbPlaster(), 8);
//		for (int i = 0; i < 8; i++) {
//			warehouse.removePlaster();
//		}
//		assertEquals(warehouse.getNbPlaster(), 0);
//
//		warehouse.removeMeals(warehouse.getNbMeals());
//		assertEquals(warehouse.getNbMeals(), 0);
//
//		for (MedicationItemType type : MedicationItemType.values()) {
//			for (int i = 0; i < 10; i++) {
//				warehouse.removeMedicationItem(type);
//			}
//		}
//		for (MedicationItemType type : MedicationItemType.values()) {
//			List<MedicationItemType> types = new ArrayList<MedicationItemType>();
//			types.add(type);
//			assertEquals(warehouse.getNbMedicationItems(type), 0);
//		}
//
//		warehouse.addPlaster();
//		TimeStamp now = hospital.getHospitalTime().getTime();
//		TimeStamp tomorrow = TimeStamp.addedToTimeStamp(
//				TimeDuration.days(1), now);
//
//		warehouse.addMeal(tomorrow);
//		warehouse.addMedicationItem(MedicationItemType.ASPIRIN, tomorrow);
//		warehouse.addMedicationItem(MedicationItemType.VITAMINS, tomorrow);
//		warehouse.addMedicationItem(MedicationItemType.ACTIVATED_CARBON,
//				tomorrow);
//		warehouse.addMedicationItem(MedicationItemType.SLEEPING_TABLETS,
//				tomorrow);
//		warehouse.addMedicationItem(MedicationItemType.MISC, tomorrow);
//
//		MachineRepository machineRepository = hospital.getMachineRepository();
//		int initialNbOfMachines = machineRepository.getMachines().size();
//		machineRepository.addMachine(new Identifier(1), 1, 1,
//				MachineType.BLOOD_ANALYZER);
//		machineRepository.addMachine(new Identifier(2), 2, 2,
//				MachineType.SURGICAL_EQUIPMENT);
//		assertEquals(initialNbOfMachines + 2, machineRepository.getMachines()
//				.size());
//	}
}
