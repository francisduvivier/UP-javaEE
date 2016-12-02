package system.controllers.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import client.IStockOrder;

import system.time.TimeStamp;
import system.warehouse.MedicationItemType;
import system.warehouse.StockOrder;
import system.warehouse.Warehouse;
import system.warehouse.stock.MedicationItemStock;
import system.warehouse.stock.StockType;
import testsuite.TestWithControllers;

/**
 * FillWarehouseStockTest.java is een test-case voor volgende use-cases: - Fill
 * Warehouse Stock
 * 
 * Fill Warehouse Stock is verwerkt in AdministratorController.java.
 */

public class WarehouseStockTest extends TestWithControllers {
	private Warehouse warehouse1;
	/**
	 * Initialiseert telkens de test.
	 * 
	 * @throws PatientIDNotFoundException
	 *             De patientID kan niet gevonden worden in de repository.
	 */
	@Override
	@Before
	public void testInit() {
		super.testInit();
		warehouse1 = getCampus(0).getWarehouse();
	}

	/**
	 * We testen of we plaster orders correct aan het warenhuis kunnen
	 * toevoegen.
	 */
	@Test
	public void testFillWarehouseStockWithPlasterOrders() {
		sessionController.login(testC1WarehouseManager1, getCampus(0));
		int initialStockAmount = warehouse1.getStockList().getDefaultStock(StockType.PLASTER)
				.getStockSize();
		int initialAmountOfOrders = warehouse1.getStockList().getDefaultStock(StockType.PLASTER)
				.getLatestOrders(Integer.MAX_VALUE).size();
		int amountToRemove = 8;
		for (int i = 0; i < amountToRemove; i++)
			warehouse1.getStockList().getDefaultStock(StockType.PLASTER).removeWarehouseItem();
		SortedSet<IStockOrder> orders = new TreeSet<IStockOrder>();
		orders.addAll(warehouse1.getStockList().getDefaultStock(StockType.PLASTER)
				.getLatestOrders(Integer.MAX_VALUE));
		int newOrderAmount = 0;
		for (IStockOrder order : orders)
			newOrderAmount += order.getAmount();		
		assertEquals(initialAmountOfOrders + amountToRemove, newOrderAmount);
		orders = warehouseController.getLatestPlasterOrders(Integer.MAX_VALUE);
		newOrderAmount = 0;
		for (IStockOrder order : orders)
			newOrderAmount += order.getAmount();		
		assertEquals(initialAmountOfOrders + amountToRemove, newOrderAmount);
		
		int newStockAmount = warehouse1.getStockList().getDefaultStock(StockType.PLASTER).getStockSize();
		assertEquals(initialStockAmount - amountToRemove, newStockAmount);

		int amountArrived=doArrivals(orders);
		checkPlasterArrivals(amountArrived);
		processArrivals(orders, null);
		newStockAmount = warehouse1.getStockList().getDefaultStock(StockType.PLASTER).getStockSize();
		
		assertEquals(amountToRemove,amountArrived);
		//Na de arrivals moet de stock weer aan zijn originele pijl zitten
		assertEquals(initialStockAmount,
				newStockAmount);
	}

	/**
	 * @param amountArrived
	 */
	private void checkPlasterArrivals(int amountArrived) {
		int arrivedOrders=0;
		for(IStockOrder order:warehouseController.getArrivedPlasterOrders())
			arrivedOrders+=order.getAmount();
		assertEquals(amountArrived, arrivedOrders);
		
		arrivedOrders=0;
		for(StockOrder order:warehouse1.getStockList().getDefaultStock(StockType.PLASTER).getArrivedOrders())
			arrivedOrders+=order.getAmount();
		assertEquals(amountArrived, arrivedOrders);
	}

	/**
	 * We testen of we medication item orders correct aan het warenhuis kunnen
	 * toevoegen.
	 */
	@Test
	public void testFillWarehouseStockWithMedicationItemOrders() {
		sessionController.login(testC1WarehouseManager1,getCampus(0));
		List<MedicationItemType> types = Arrays.asList(MedicationItemType
				.values());

		HashMap<MedicationItemType, Integer> initialStockAmounts = new HashMap<MedicationItemType, Integer>();
		HashMap<MedicationItemType, Integer> initialOrderAmounts = new HashMap<MedicationItemType, Integer>();

		for (MedicationItemType type : types) {
			initialStockAmounts.put(type, warehouse1
					.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getStockSize(type));
			initialOrderAmounts.put(
					type,
					warehouse1.getStockList().getCompositeStock(StockType.MEDICATION_ITEM)
							.getLatestOrders(Integer.MAX_VALUE, type).size());
		}

		int amountToRemove = MedicationItemStock.MAX_CAPACITY_MEDICATION_ITEMS / 2 + 1;
		int allArrived=0;
		for (MedicationItemType type : types) {
			for (int i = 0; i < initialStockAmounts.get(type) / 2 + 1; i++) {
				warehouse1.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).removeWarehouseItem(
						type);
			}

			int newStockAmount = warehouse1.getStockList().getCompositeStock(StockType.MEDICATION_ITEM)
					.getStockSize(type);
			assertEquals(initialStockAmounts.get(type) - amountToRemove,
					newStockAmount);

			SortedSet<IStockOrder> orders = new TreeSet<IStockOrder>();
			orders.addAll(warehouse1
					.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getLatestOrders(
							Integer.MAX_VALUE, type));
			int newOrderAmount = 0;
			for (IStockOrder order : orders)
				newOrderAmount += order.getAmount();		
			assertEquals(amountToRemove + initialOrderAmounts.get(type),
					newOrderAmount);
			
			orders = warehouseController.getLatestMedicationItemOrders(type,
					Integer.MAX_VALUE);
			newOrderAmount = 0;
			for (IStockOrder order : orders)
				newOrderAmount += order.getAmount();		
			assertEquals(amountToRemove + initialOrderAmounts.get(type),
					newOrderAmount);
			int amountArrived = doArrivals(orders);
			allArrived+=amountArrived;
			checkMedArrivals(amountArrived, type);
			
		}
		
		//We kijken of het totaal aantal aangekomen medication items klopt
		int allArrivedFromController=0;
		SortedSet<IStockOrder> allOrdersList=warehouseController.getAllArrivedMedicationItemOrders();
		for(IStockOrder order:allOrdersList)
			allArrivedFromController+=order.getAmount();
		assertEquals(allArrived, allArrivedFromController);
		//We kijken hieronder of de arrived orders goed geprocessed worden.
		for(MedicationItemType type:MedicationItemType.values()){
			SortedSet<IStockOrder> orders = new TreeSet<IStockOrder>();
			orders.addAll(warehouse1.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getArrivedOrdersByType(type));
			int amountArrived=0;
			assertEquals(1, orders.size());
			amountArrived+=orders.first().getAmount();
			assertEquals(amountArrived, amountToRemove);
			
			processArrivals(orders, getFutureTime(30));
			int newStockAmount = warehouse1.getStockList().getCompositeStock(StockType.MEDICATION_ITEM)
					.getStockSize(type);
			//Nu kijken we of na het processen we weer het originele aantal hebben
			assertTrue(initialStockAmounts.get(type)==newStockAmount);

		}
		sessionController.logOut();

	}

	/**
	 * @param amountArrived
	 * @param type
	 */
	private void checkMedArrivals(int amountArrived, MedicationItemType type) {
		int arrivedOrders=0;
		for(IStockOrder order:warehouseController.getArrivedMedicationItemOrders(type))
			arrivedOrders+=order.getAmount();
		assertEquals(amountArrived, arrivedOrders);
		arrivedOrders=0;
		for(StockOrder order:warehouse1.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getArrivedOrdersByType(type))
			arrivedOrders+=order.getAmount();
		assertEquals(amountArrived, arrivedOrders);
	}


	/**
	 * We testen of we meal orders correct aan het warenhuis kunnen toevoegen.
	 */
	@Test
	public void testFillWarehouseStockWithMealOrders() {
		sessionController.login(testC1WarehouseManager1, getCampus(0));
		int initialStockAmount = warehouse1.getStockList().getDefaultStock(StockType.MEAL).getStockSize();

		int amountToRemove = 115;
		for (int i = 0; i < amountToRemove; i++)
			warehouse1.getStockList().getDefaultStock(StockType.MEAL).removeWarehouseItem();
		sessionController.login(testHospitalAdmin, getCampus(0));
		adminController.advanceTime(getFutureTime(1.01));
		SortedSet<IStockOrder> orders = new TreeSet<IStockOrder>();
		orders.addAll(warehouse1.getStockList().getDefaultStock(StockType.MEAL)
				.getLatestOrders(Integer.MAX_VALUE));
		int newOrderAmount = 0;
		for (IStockOrder order : orders)
			newOrderAmount += order.getAmount();
		//15*3*2*nbPatients-nbStock-nBtoArrive
		assertEquals(15+1*6-2-0, newOrderAmount);
		
		sessionController.login(testC1WarehouseManager1, getCampus(0));
		orders = warehouseController.getLatestMealOrders(Integer.MAX_VALUE);
		newOrderAmount = 0;
		for (IStockOrder order : orders)
			newOrderAmount += order.getAmount();
		//15*3*2*nbPatients-nbStock-nBtoArrive
		assertEquals(15+1*6-2-0, newOrderAmount);
		
		int newStockAmount = warehouse1.getStockList().getDefaultStock(StockType.MEAL).getStockSize();
		int nBUsedIn1Day=3*1;//There is only 1 patient
		assertEquals(initialStockAmount - amountToRemove - nBUsedIn1Day, newStockAmount);

		int amountArrived = doArrivals(orders);
		checkMealArrivals(amountArrived);
		processArrivals(orders, getFutureTime(30));

		newStockAmount = warehouse1.getStockList().getDefaultStock(StockType.MEAL).getStockSize();

		assertEquals(initialStockAmount - amountToRemove - nBUsedIn1Day + amountArrived,
				newStockAmount);
	}

	/**
	 * @param amountArrived
	 */
	private void checkMealArrivals(int amountArrived) {
		int arrivedOrders=0;
		for(IStockOrder order:warehouseController.getArrivedMealOrders())
			arrivedOrders+=order.getAmount();
		assertEquals(amountArrived, arrivedOrders);
		
		arrivedOrders=0;
		for(StockOrder order:warehouse1.getStockList().getDefaultStock(StockType.MEAL).getArrivedOrders())
			arrivedOrders+=order.getAmount();
		assertEquals(amountArrived, arrivedOrders);
	}
	

	/**
	 * @param amountArrived
	 * @param orders
	 */
	private int doArrivals(SortedSet<IStockOrder> orders) {
		int amountArrived=0; 
		for (IStockOrder order:orders) {
			((StockOrder) order).getArrivalEvent().execute();
			amountArrived+=order.getAmount();
		}
		return amountArrived;
	}
	
	/**
	 * @param amountArrived
	 * @param orders
	 */
	private void processArrivals(SortedSet<IStockOrder> orders, TimeStamp expirationDate) {
		for (IStockOrder order:orders) {
			warehouseController.processArrivedOrder(order, expirationDate);
		}
	}

}
