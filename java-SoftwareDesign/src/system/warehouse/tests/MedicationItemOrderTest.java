package system.warehouse.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;

import system.time.TimeStamp;
import system.warehouse.MedicationItem;
import system.warehouse.MedicationItemType;
import system.warehouse.StockOrder;
import system.warehouse.WarehouseItem;
import system.warehouse.stock.StockType;

public class MedicationItemOrderTest extends StockOrderTest {

	@Before
	public void testInit() {
		super.testInit();
	}

	@Test
	public void testMedicationItemOrderConstructor() {
		TimeStamp timeStamp = new TimeStamp(2012, 0, 1, 11, 14);

		for (MedicationItemType type : MedicationItemType.values()) {
			StockOrder medicationItemOrder = new StockOrder(
					warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM), timeStamp,
					generateMedItems(type, getFutureTime(30), 5));

			assertEquals(medicationItemOrder.getAmount(), 5);

			TimeStamp orderDate = medicationItemOrder.getOrderDate();
			assertEquals(orderDate.get(Calendar.YEAR), 2012);
			assertEquals(orderDate.get(Calendar.MONTH), 0);
			assertEquals(orderDate.get(Calendar.DAY_OF_MONTH), 1);
			assertEquals(orderDate.get(Calendar.HOUR), 11);
			assertEquals(orderDate.get(Calendar.MINUTE), 14);

			TimeStamp arrivalDate = medicationItemOrder.getArrivalDate();
			assertEquals(arrivalDate.get(Calendar.YEAR), 2012);
			assertEquals(arrivalDate.get(Calendar.MONTH), 0);
			assertEquals(arrivalDate.get(Calendar.DAY_OF_MONTH), 3);
			assertEquals(arrivalDate.get(Calendar.HOUR), 6);
			assertEquals(arrivalDate.get(Calendar.MINUTE), 0);
		}
	}

	private List<WarehouseItem> generateMedItems(MedicationItemType type,
			TimeStamp expirationDate, int amount) {
		List<WarehouseItem> medItems = new ArrayList<WarehouseItem>();
		for (int i = 0; i < amount; i++)
			medItems.add(new MedicationItem(expirationDate, type));
		return medItems;
	}
	
	@Test
	public void testArrive() {
		for (MedicationItemType type : MedicationItemType.values()) {
			int amountToRemove = 7;
			SortedSet<StockOrder> orders = setupOrders(amountToRemove, type);
			int amountArrived = doArrivals(orders);
			checkMedicationItemArrivals(amountArrived, type);
		}
	}

	@Test
	public void testProcessing(){
		for(MedicationItemType type: MedicationItemType.values()){

		int initialStockAmount = warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getStockSize(type);
		int amountToRemove = 7;
		
		SortedSet<StockOrder> orders = setupOrders(amountToRemove,type);
		
		int amountArrived=doArrivals(orders);
		processArrivals(orders, getFutureTime(30));
		int newStockAmount = warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getStockSize(type);

		assertEquals(initialStockAmount - amountToRemove + amountArrived,
				newStockAmount);
		}
		
	}


	@Test 
	public void testInvalid1MedicationItemOrderConstructor() {
		for(MedicationItemType type: MedicationItemType.values()){
			try{		
		new StockOrder(null, getFutureTime(30),generateMedItems(type,getFutureTime(30),6));
		fail("zou een exception gegooid moeten hebben");
		}catch (IllegalArgumentException e){}
				
		}
		
	}
	
	@Test 
	public void testInvalid2MedicationItemOrderConstructor() {
		for (MedicationItemType type : MedicationItemType.values()) {
			try {
				new StockOrder(warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM), null,
						generateMedItems(type, getFutureTime(30), 6));
				fail("zou een exception gegooid moeten hebben");
			} catch (IllegalArgumentException e) {}
		}
	}

	@Test (expected = IllegalArgumentException.class)
	public void testInvalid3MedicationItemOrderConstructor() {
	new StockOrder(warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM),getFutureTime(30),null);
	}
	
	/**
	 * @param amountArrived
	 */
	private void checkMedicationItemArrivals(int amountArrived, MedicationItemType type) {
		int arrivedOrders=0;
		for(StockOrder order:warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).getArrivedOrdersByType(type))
			arrivedOrders+=order.getAmount();
		assertEquals(amountArrived, arrivedOrders);
	}
	
	private SortedSet<StockOrder> setupOrders(int amountToRemove,MedicationItemType type) {
		for (int i = 0; i < amountToRemove; i++)
			warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM).removeWarehouseItem(type);

		SortedSet<StockOrder> orders = warehouse.getStockList().getCompositeStock(StockType.MEDICATION_ITEM)
				.getLatestOrders(Integer.MAX_VALUE,type);
		return orders;
	}

}
