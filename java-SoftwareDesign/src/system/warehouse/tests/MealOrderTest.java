package system.warehouse.tests;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.SortedSet;

import org.junit.Before;
import org.junit.Test;

import system.controllers.AdministratorController;
import system.repositories.StaffType;
import system.time.TimeStamp;
import system.warehouse.Meal;
import system.warehouse.StockOrder;
import system.warehouse.WarehouseItem;
import system.warehouse.stock.StockType;


public class MealOrderTest extends StockOrderTest {

	@Before
	public void testInit() {
	 super.testInit();
	}
	
	@Test
	public void testMealOrderConstructor() {
		TimeStamp timeStamp = new TimeStamp(2012, 0, 1, 11, 14);
		StockOrder mealOrder = new StockOrder(warehouse.getStockList().getDefaultStock(StockType.MEAL), timeStamp, generateMeals(5));
		
		assertEquals(mealOrder.getAmount(), 5);
		
		TimeStamp orderDate = mealOrder.getOrderDate();
		assertEquals(orderDate.get(Calendar.YEAR), 2012);
		assertEquals(orderDate.get(Calendar.MONTH), 0);
		assertEquals(orderDate.get(Calendar.DAY_OF_MONTH), 1);
		assertEquals(orderDate.get(Calendar.HOUR_OF_DAY), 11);
		assertEquals(orderDate.get(Calendar.MINUTE), 14);
		
		TimeStamp arrivalDate = mealOrder.getArrivalDate();
		assertEquals(arrivalDate.get(Calendar.YEAR), 2012);
		assertEquals(arrivalDate.get(Calendar.MONTH), 0);
		assertEquals(arrivalDate.get(Calendar.DAY_OF_MONTH), 3);
		assertEquals(arrivalDate.get(Calendar.HOUR), 6);
		assertEquals(arrivalDate.get(Calendar.MINUTE), 0);
	}
	
	private List<WarehouseItem> generateMeals(int i) {
		TimeStamp timeStamp = getFutureTime(30);
		List<WarehouseItem> list=new ArrayList<WarehouseItem>();
		for (int j = 0; j < i; j++) 
			list.add(new Meal(timeStamp));
		return list;
	}

	@Test
	public void testArrive() {
		int amountToRemove = 7;

		SortedSet<StockOrder> orders = setupOrders(amountToRemove);
				int amountArrived = doArrivals(orders);
		checkMealArrivals(amountArrived);
	}

	@Test
	public void testProcessing(){
		int initialStockAmount = warehouse.getStockList().getDefaultStock(StockType.MEAL).getStockSize();
		int amountToRemove = 7;

		SortedSet<StockOrder> orders = setupOrders(amountToRemove);
		
		int amountArrived=doArrivals(orders);
		processArrivals(orders, getFutureTime(30));
		int newStockAmount = warehouse.getStockList().getDefaultStock(StockType.MEAL).getStockSize();

		assertEquals(initialStockAmount - amountToRemove + amountArrived,
				newStockAmount);
		
	}


	@Test (expected = IllegalArgumentException.class)
	public void testInvalid1MealOrderConstructor() {
		new StockOrder(null, getFutureTime(0),generateMeals(4));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid2MealOrderConstructor() {
		new StockOrder(warehouse.getStockList().getDefaultStock(StockType.MEAL),null,generateMeals(4));
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalid3MealOrderConstructor() {
	new StockOrder(warehouse.getStockList().getDefaultStock(StockType.MEAL),getFutureTime(0),null);
	}
	
	/**
	 * @param amountArrived
	 */
	private void checkMealArrivals(int amountArrived) {
		int arrivedOrders=0;
		for(StockOrder order:warehouse.getStockList().getDefaultStock(StockType.MEAL).getArrivedOrders())
			arrivedOrders+=order.getAmount();
		assertEquals(amountArrived, arrivedOrders);
	}
	
	private SortedSet<StockOrder> setupOrders(int amountToRemove) {
		for (int i = 0; i < amountToRemove; i++)
			warehouse.getStockList().getDefaultStock(StockType.MEAL).removeWarehouseItem();
		AdministratorController adminController=new AdministratorController(sessionController);
		sessionController.login(hospital.getStaffRepository().getStaffMembers(StaffType.HOSPITAL_ADMINISTRATOR).get(0), sessionController.getCurrentCampus());
		adminController.advanceTime(getFutureTime(1.1));
		SortedSet<StockOrder> orders = warehouse.getStockList().getDefaultStock(StockType.MEAL)
				.getLatestOrders(Integer.MAX_VALUE);
		return orders;
	}


	
}
