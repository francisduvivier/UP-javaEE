package system.machines.tests;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import system.machines.Identifier;
import system.machines.Location;
import system.machines.Machine;
import system.repositories.MachineType;


/**
 * Unit Test Machine.java
 *
 */

public class MachineTest {

	private Machine machine;
	Location location = new Location(0, 12);
	Identifier ID = new Identifier(1);
	
	@Test
	public void testMachineConstructor() {
		machine = new Machine(ID, location, MachineType.BLOOD_ANALYZER);
		assertEquals(machine.getID(), ID);
		assertEquals(machine.getLocation(), location);
		assertEquals(machine.getResourceType(), MachineType.BLOOD_ANALYZER);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalidMachineConstructor() {
		machine = new Machine(null, location, MachineType.BLOOD_ANALYZER);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid2MachineConstructor() {
		machine = new Machine(ID, null, MachineType.BLOOD_ANALYZER);
	}
	
	@Test (expected = NullPointerException.class)
	public void testInvalid3MachineConstructor() {
		machine = new Machine(ID, location, null);
	}

}
