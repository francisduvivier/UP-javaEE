package system.warehouse.tests;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;

import org.junit.Test;

import system.time.TimeStamp;
import system.warehouse.Meal;


public class MealTest {	
	@Test
	public void testMealConstructor() {
		TimeStamp timeStamp = new TimeStamp(2012, 0, 1, 11, 14);
		 Meal testMeal = new Meal(timeStamp);
		TimeStamp expirationDate = testMeal.getExpirationDate();
		assertEquals(2012, expirationDate.get(Calendar.YEAR));
		assertEquals(0, expirationDate.get(Calendar.MONTH));
		assertEquals(1, expirationDate.get(Calendar.DAY_OF_MONTH));
		assertEquals(11, expirationDate.get(Calendar.HOUR));
		assertEquals(14, expirationDate.get(Calendar.MINUTE));
	}	
}
