package system.util.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;

import system.util.BoundedStack;


public class BoundedStackTest {

	private BoundedStack<String> testBoundedStack1, testBoundedStack2;
	
	@Before
	public void testBoundedStackConstructor() {
		testBoundedStack1 = new BoundedStack<String>(10);
		testBoundedStack2 = new BoundedStack<String>(40);
	}
	
	@Test
	public void testPush() {
		for (int i = 1; i <= 10; i++) 
			testBoundedStack1.push(i + " - Test");
		assertEquals(testBoundedStack1.size(), 10);
		assertEquals(testBoundedStack1.get(1), "2 - Test");
		assertEquals(testBoundedStack1.get(9), "10 - Test");
		// Hieronder wordt er getest of de eerste elementen vervangen worden
		// wanneer de capaciteit overschreden wordt.
		for (int i = 11; i <= 13; i++) 
			testBoundedStack1.push(i + " - Test");
		assertEquals(testBoundedStack1.get(9), "13 - Test");
		assertEquals(testBoundedStack1.get(0), "4 - Test");
		
	}
	
	@Test
	public void testRemove() {
		for (int i = 1; i <= 10; i++) 
			testBoundedStack1.push(i + " - Test");
		assertEquals(testBoundedStack1.get(0), "1 - Test");
		testBoundedStack1.remove(0);
		assertEquals(testBoundedStack1.get(0), "2 - Test");
		testBoundedStack1.remove(0);
		assertEquals(testBoundedStack1.get(0), "3 - Test");
		testBoundedStack1.remove("3 - Test");
		assertEquals(testBoundedStack1.get(0), "4 - Test");
		testBoundedStack1.remove(0);
		assertEquals(testBoundedStack1.get(0), "5 - Test");
		testBoundedStack1.remove("5 - Test");
		assertEquals(testBoundedStack1.get(0), "6 - Test");
		testBoundedStack1.remove(0);
		assertEquals(testBoundedStack1.get(0), "7 - Test");
		testBoundedStack1.remove("7 - Test");
		assertEquals(testBoundedStack1.get(0), "8 - Test");
		testBoundedStack1.remove(0);
		assertEquals(testBoundedStack1.get(0), "9 - Test");
		testBoundedStack1.remove("9 - Test");
		assertEquals(testBoundedStack1.get(0), "10 - Test");
		testBoundedStack1.remove(0);
		assertEquals(testBoundedStack1.size(), 0);
		assertTrue(testBoundedStack1.isEmpty());
	}
	
	@Test
	public void testPop() {
		for (int i = 1; i <= 10; i++) 
			testBoundedStack1.push(i + " - Test");
		assertEquals(testBoundedStack1.size(), 10);
		assertEquals(testBoundedStack1.get(9), "10 - Test");
		testBoundedStack1.pop();
		assertEquals(testBoundedStack1.size(), 9);
	}
	
	@Test
	public void testCopyStack() {
		for (int i = 1; i <= 10; i++) 
			testBoundedStack1.push(i + " - Test");
		testBoundedStack2 = testBoundedStack1.copy();
		assertNotSame(testBoundedStack1, testBoundedStack2);
	}
	
	@Test
	public void testIterator() {
		for (int i = 1; i <= 10; i++) 
			testBoundedStack1.push(i + " - Test");
		Iterator<String> testIterator = testBoundedStack1.iterator();
		for (int i = 1; i <= 10; i++)
			assertEquals(testIterator.next(), i + " - Test");
	}
}
