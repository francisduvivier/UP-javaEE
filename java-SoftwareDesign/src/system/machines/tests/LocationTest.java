package system.machines.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import system.machines.Location;


/**
 * Unit Test Location.java
 *
 */

public class LocationTest {

	private Location location;
	
	@Test
	public void testLocationConstructor() {
		location = new Location(0, 12);
		assertEquals(location.getFloor(), 0);
		assertEquals(location.getRoom(), 12);
	}
	
	@Test (expected = IllegalArgumentException.class)
	public void testInvalidLocationConstructor() {
		location = new Location(0, -1);
	}
		
}
