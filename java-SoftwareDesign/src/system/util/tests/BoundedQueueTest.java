package system.util.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import system.util.BoundedQueue;

public class BoundedQueueTest {
	
	@Test
	public void testAll() {
		BoundedQueue<Integer> q = new BoundedQueue<Integer>(5);
		assertEquals(q.dequeue(),null);
		assertEquals(q.peek(), null);
		q.enqueue(0);
		assertEquals(q.dequeue(), new Integer(0));
		for (int i = 1; i < 10; i++)
			q.enqueue(i);
		assertEquals(q.size(), 5);
		assertEquals(q.peek(), new Integer(5));
		int n = 5;
		String s = "";
		for (int i : q) {
			assertEquals(i, n++);
			s += i + " ";
		}
		assertEquals(s, q.toString());
		assertEquals(q.get(1), new Integer(6));
		q.remove(6);
		q.remove(9);
		q.remove(8);
		q.remove(5);
		q.remove(7);
		assertTrue(q.isEmpty());
		assertEquals(0, q.size());
	}
}
