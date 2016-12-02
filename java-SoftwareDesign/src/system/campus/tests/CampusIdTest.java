package system.campus.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import system.campus.CampusId;
import system.campus.Hospital;


/**
 * Unit Test CampusId.java
 */

public class CampusIdTest {

	Hospital hospital;
	
	@Before
	public void testInit() {
		hospital = new Hospital();
	}
	
	@Test
	public void testEquals() {
		CampusId campusId1a = new CampusId(hospital.getCampuses().get(0));
		CampusId campusId1b = new CampusId(hospital.getCampuses().get(0));
		CampusId campusId2 = new CampusId(hospital.getCampuses().get(1));
		assertTrue(campusId1a.equals(campusId1b));
		assertTrue(!campusId1a.equals(campusId2));
	}
	
	@Test
	public void testHashCode() {
		CampusId campusId = new CampusId(hospital.getCampuses().get(0));
		assertEquals(campusId.hashCode(), System.identityHashCode(hospital.getCampuses().get(0)));
	}
	
	@Test
	public void testToString() {
		CampusId campusId = new CampusId(hospital.getCampuses().get(0));
		assertEquals("CAMPUS ID: " + Long.toString(System.identityHashCode(hospital.getCampuses().get(0))), campusId.toString());
	}
	
}
