package system.machines.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import system.machines.Identifier;


/**
 * Unit Test Identifier.java
 *
 */

public class IdentifierTest {

	private Identifier ID;
	
	@Before
	public void testInit() {
		ID = new Identifier(12);
	}
	
	@Test
	public void testEquals(){
		ID = new Identifier(12);
		Identifier ID2 = new Identifier(15);
		assertFalse(ID.equals(ID2));
		Identifier ID3 = new Identifier(12);
		assertTrue(ID.equals(ID3));
	}
	
	@Test
	public void testToString() {
		ID = new Identifier(12);
		assertEquals(ID.toString(), "12");
	}
}
