package system.staff.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

import org.junit.Before;
import org.junit.Test;

import system.staff.WarehouseManager;


/**
 * Unit Test WarehouseManager.java
 *
 */

public class WarehouseManagerTest {
	private WarehouseManager testWarehouseManager1, testWarehouseManager2;

	@Before
	public void testInit() {	
		testWarehouseManager1 = new WarehouseManager("Alice");
		testWarehouseManager2 = new WarehouseManager("Bob");
	}
	
	@Test
	public void testDoctorConstructor() {
		assertEquals(testWarehouseManager1.getName(), "Alice");
		assertEquals(testWarehouseManager2.getName(), "Bob");
	}
	
	@Test
	public void testToString() {
		testWarehouseManager1 = new WarehouseManager("Alice");
		assertNotSame(testWarehouseManager1.toString().length(), 0);
	}
}
